package app.ui;

import app.model.Song;
import app.model.SongLibrary;
import app.persistence.LibraryStorage;
import app.util.ValidationException;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Main Swing window for managing the song library.
 * Demonstrates event listeners, layout managers, list models, and exception handling in a GUI.
 */
public class SongManagerFrame extends JFrame {
    // Core model and persistence collaborator
    private final SongLibrary library = new SongLibrary();
    private final LibraryStorage storage = new LibraryStorage(
            Path.of(System.getProperty("user.home"), ".songlib", "songs.ser"));

    // UI model + component that displays Song objects
    private final DefaultListModel<Song> listModel = new DefaultListModel<>();
    private final JList<Song> songList = new JList<>(listModel);

    // Input fields for new songs
    private final JTextField titleField = new JTextField();
    private final JTextField artistField = new JTextField();
    private final JSpinner yearSpinner = new JSpinner(new SpinnerNumberModel(2020, 1900, 2100, 1));
    private final JSpinner ratingSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 5, 1));
    private final JTextField filterField = new JTextField();

    // Sorting control
    private final JComboBox<String> sortCombo = new JComboBox<>(new String[]{
            "Title", "Artist", "Year, high to low", "Rating, high to low"
    });

    // Delete button is enabled only when a list item is selected
    private final JButton deleteBtn = new JButton("Delete");

    public SongManagerFrame() {
        super("Song Library");
        // Proper close behavior for a top-level window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(720, 520);
        setLocationRelativeTo(null); // Center on screen

        buildUi();     // Build and layout components
        wireEvents();  // Attach listeners for interaction

        // Load previously saved songs on startup.
        loadFromDisk();
    }

    /**
     * Builds the visual layout of the window using simple Swing containers and layout managers.
     */
    private void buildUi() {
        // Input form for song fields
        JPanel form = new JPanel(new GridLayout(2, 4, 8, 8));
        form.add(labeled("Title", titleField));
        form.add(labeled("Artist", artistField));
        form.add(labeled("Year", yearSpinner));
        form.add(labeled("Rating 1..5", ratingSpinner));

        // Main action buttons
        JButton addBtn = new JButton("Add");
        JButton saveBtn = new JButton("Save");
        JButton loadBtn = new JButton("Load");

        JPanel topBar = new JPanel(new BorderLayout(8, 8));
        JPanel left = new JPanel(new BorderLayout(8, 8));
        left.add(form, BorderLayout.CENTER);

        // Horizontal strip of buttons under the form
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttons.add(addBtn);
        buttons.add(saveBtn);
        buttons.add(loadBtn);

        // Delete starts disabled and lights up only with a selection
        deleteBtn.setEnabled(false);
        buttons.add(deleteBtn);

        left.add(buttons, BorderLayout.SOUTH);

        // Filter and sort controls on the right
        JPanel right = new JPanel(new GridLayout(2, 1, 8, 8));
        right.add(labeled("Filter text", filterField));
        right.add(labeled("Sort by", sortCombo));

        topBar.add(left, BorderLayout.CENTER);
        topBar.add(right, BorderLayout.EAST);

        // List configuration
        songList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(songList);

        // Frame layout, simple North + Center
        setLayout(new BorderLayout(8, 8));
        add(topBar, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        // Hook up button actions
        addBtn.addActionListener(e -> onAdd());
        saveBtn.addActionListener(e -> onSave());
        loadBtn.addActionListener(e -> loadFromDisk());
        deleteBtn.addActionListener(e -> onDelete());
    }

    /** Small helper to attach a label above any component. */
    private JPanel labeled(String label, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(4, 4));
        p.add(new JLabel(label), BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    /**
     * Wires listeners for sort, filter, and selection state changes.
     * Demonstrates DocumentListener and ListSelectionListener usage.
     */
    private void wireEvents() {
        // Resort whenever the choice changes
        sortCombo.addActionListener(e -> refreshList());

        // Re-filter on any text change
        filterField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { refreshList(); }
            public void removeUpdate(DocumentEvent e) { refreshList(); }
            public void changedUpdate(DocumentEvent e) { refreshList(); }
        });

        // Enable delete button only when a selection is finalized
        songList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                deleteBtn.setEnabled(!songList.isSelectionEmpty());
            }
        });
    }

    /**
     * Reads fields, validates by constructing a Song, adds to the model, and updates the view.
     * Any validation failure is reported to the user via a dialog.
     */
    private void onAdd() {
        try {
            String title = titleField.getText();
            String artist = artistField.getText();
            int year = (Integer) yearSpinner.getValue();
            int rating = (Integer) ratingSpinner.getValue();

            Song s = new Song(title, artist, year, rating); // May throw IllegalArgumentException
            library.add(s);
            clearForm();
            refreshList();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Validation error", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Deletes the currently selected song after user confirmation.
     */
    private void onDelete() {
        Song selected = songList.getSelectedValue();
        if (selected == null) return;

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Delete \"" + selected.getTitle() + "\" by " + selected.getArtist() + "?",
                "Confirm delete",
                JOptionPane.YES_NO_OPTION
        );
        if (choice != JOptionPane.YES_OPTION) return;

        library.remove(selected);
        refreshList();
        songList.clearSelection();
        deleteBtn.setEnabled(false);
    }

    /** Saves the current in-memory list to disk, with user feedback. */
    private void onSave() {
        handleUiErrors(() -> {
            storage.save(library.all());
            JOptionPane.showMessageDialog(this, "Saved successfully");
            return null;
        });
    }

    /**
     * Loads songs from disk and replaces the in-memory list.
     * Uses the helper that accepts a Callable so checked exceptions are allowed inside.
     */
    private void loadFromDisk() {
        handleUiErrors(() -> {
            SongLibrary loaded = storage.load();
            // Replace current displayed items
            removeAllFromModel();
            for (Song s : loaded.all()) {
                library.add(s);
            }
            refreshList();
            return null;
        });
    }

    /**
     * Rebuilds the JList based on the chosen sort and current filter text.
     * Uses stream operations to derive a new view of the underlying library.
     */
    private void refreshList() {
        List<Song> source;
        String choice = (String) sortCombo.getSelectedItem();

        if ("Artist".equals(choice)) {
            source = library.sortedByArtist();
        } else if ("Year, high to low".equals(choice)) {
            source = library.sortedByYearDesc();
        } else if ("Rating, high to low".equals(choice)) {
            source = library.sortedByRatingDesc();
        } else {
            source = library.sortedByTitle();
        }

        // Apply simple case-insensitive text filtering on title or artist
        String filter = filterField.getText().trim().toLowerCase();
        if (!filter.isEmpty()) {
            source = source.stream()
                    .filter(s -> s.getTitle().toLowerCase().contains(filter)
                            || s.getArtist().toLowerCase().contains(filter))
                    .toList();
        }

        // Push the derived list into the JList model
        removeAllFromModel();
        for (Song s : source) listModel.addElement(s);
    }

    /** Clears input fields and focuses title for quick data entry. */
    private void clearForm() {
        titleField.setText("");
        artistField.setText("");
        yearSpinner.setValue(2020);
        ratingSpinner.setValue(3);
        titleField.requestFocusInWindow();
    }

    /** Utility to empty the JList model in one call. */
    private void removeAllFromModel() {
        listModel.clear();
    }

    /**
     * Helper that executes a Callable and shows friendly dialogs for errors.
     * Callable<T> allows lambdas that throw checked exceptions like ValidationException.
     */
    private <T> T handleUiErrors(Callable<T> action) {
        try {
            return action.call();
        } catch (ValidationException ve) {
            JOptionPane.showMessageDialog(this, ve.getMessage(),
                    "I/O error", JOptionPane.ERROR_MESSAGE);
        } catch (RuntimeException re) {
            JOptionPane.showMessageDialog(this, "Unexpected error, " + re.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) { // Any other checked exception
            JOptionPane.showMessageDialog(this, "Error, " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
}
