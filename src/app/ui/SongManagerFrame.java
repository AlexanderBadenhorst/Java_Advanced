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

public class SongManagerFrame extends JFrame {
    private final SongLibrary library = new SongLibrary();
    private final LibraryStorage storage = new LibraryStorage(
            Path.of(System.getProperty("user.home"), ".songlib", "songs.ser"));

    private final DefaultListModel<Song> listModel = new DefaultListModel<>();
    private final JList<Song> songList = new JList<>(listModel);

    private final JTextField titleField = new JTextField();
    private final JTextField artistField = new JTextField();
    private final JSpinner yearSpinner = new JSpinner(new SpinnerNumberModel(2020, 1900, 2100, 1));
    private final JSpinner ratingSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 5, 1));
    private final JTextField filterField = new JTextField();

    private final JComboBox<String> sortCombo = new JComboBox<>(new String[]{
            "Title", "Artist", "Year, high to low", "Rating, high to low"
    });

    // new: delete button reference so we can enable/disable it
    private final JButton deleteBtn = new JButton("Delete");

    public SongManagerFrame() {
        super("Song Library");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(720, 520);
        setLocationRelativeTo(null);

        buildUi();
        wireEvents();

        // Try loading saved data, show exceptions in a friendly way
        loadFromDisk();
    }

    private void buildUi() {
        JPanel form = new JPanel(new GridLayout(2, 4, 8, 8));
        form.add(labeled("Title", titleField));
        form.add(labeled("Artist", artistField));
        form.add(labeled("Year", yearSpinner));
        form.add(labeled("Rating 1..5", ratingSpinner));

        JButton addBtn = new JButton("Add");
        JButton saveBtn = new JButton("Save");
        JButton loadBtn = new JButton("Load");

        JPanel topBar = new JPanel(new BorderLayout(8, 8));
        JPanel left = new JPanel(new BorderLayout(8, 8));
        left.add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttons.add(addBtn);
        buttons.add(saveBtn);
        buttons.add(loadBtn);

        // new: delete button, starts disabled
        deleteBtn.setEnabled(false);
        buttons.add(deleteBtn);

        left.add(buttons, BorderLayout.SOUTH);

        JPanel right = new JPanel(new GridLayout(2, 1, 8, 8));
        right.add(labeled("Filter text", filterField));
        right.add(labeled("Sort by", sortCombo));

        topBar.add(left, BorderLayout.CENTER);
        topBar.add(right, BorderLayout.EAST);

        songList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(songList);

        setLayout(new BorderLayout(8, 8));
        add(topBar, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        // Action wiring
        addBtn.addActionListener(e -> onAdd());
        saveBtn.addActionListener(e -> onSave());
        loadBtn.addActionListener(e -> loadFromDisk());
        deleteBtn.addActionListener(e -> onDelete());
    }

    private JPanel labeled(String label, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(4, 4));
        p.add(new JLabel(label), BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    private void wireEvents() {
        sortCombo.addActionListener(e -> refreshList());

        filterField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { refreshList(); }
            public void removeUpdate(DocumentEvent e) { refreshList(); }
            public void changedUpdate(DocumentEvent e) { refreshList(); }
        });

        // new: enable Delete only when a song is selected
        songList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                deleteBtn.setEnabled(!songList.isSelectionEmpty());
            }
        });
    }

    private void onAdd() {
        try {
            String title = titleField.getText();
            String artist = artistField.getText();
            int year = (Integer) yearSpinner.getValue();
            int rating = (Integer) ratingSpinner.getValue();

            Song s = new Song(title, artist, year, rating);
            library.add(s);
            clearForm();
            refreshList();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Validation error", JOptionPane.WARNING_MESSAGE);
        }
    }

    // new: delete handler
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

    private void onSave() {
        handleUiErrors(() -> {
            storage.save(library.all());
            JOptionPane.showMessageDialog(this, "Saved successfully");
            return null;
        });
    }

    private void loadFromDisk() {
        handleUiErrors(() -> {
            SongLibrary loaded = storage.load();
            // Replace current items
            removeAllFromModel();
            for (Song s : loaded.all()) {
                library.add(s);
            }
            refreshList();
            return null;
        });
    }

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

        String filter = filterField.getText().trim().toLowerCase();
        if (!filter.isEmpty()) {
            source = source.stream()
                    .filter(s -> s.getTitle().toLowerCase().contains(filter)
                            || s.getArtist().toLowerCase().contains(filter))
                    .toList();
        }

        removeAllFromModel();
        for (Song s : source) listModel.addElement(s);
    }

    private void clearForm() {
        titleField.setText("");
        artistField.setText("");
        yearSpinner.setValue(2020);
        ratingSpinner.setValue(3);
        titleField.requestFocusInWindow();
    }

    private void removeAllFromModel() {
        listModel.clear();
    }

    // Helper that accepts callables so checked exceptions are allowed
    private <T> T handleUiErrors(Callable<T> action) {
        try {
            return action.call();
        } catch (ValidationException ve) {
            JOptionPane.showMessageDialog(this, ve.getMessage(),
                    "I/O error", JOptionPane.ERROR_MESSAGE);
        } catch (RuntimeException re) {
            JOptionPane.showMessageDialog(this, "Unexpected error, " + re.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) { // any other checked exception
            JOptionPane.showMessageDialog(this, "Error, " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
}
