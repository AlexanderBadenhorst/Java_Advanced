package Appendix_A;

import javax.sound.midi.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.*;

import static javax.sound.midi.ShortMessage.*;


public class BeatBoxFinal {
    // GUI component to display list of incoming messages/sequences from other users
    private JList<String> incomingList;

    // Text area for user to type their message
    private JTextArea userMessage;

    // List to hold checkboxes representing beats
    private ArrayList<JCheckBox> checkboxList;

    // Vector to store incoming messages from other users
    private Vector<String> listVector = new Vector<>();

    // Map to store other users' sequences by their user ID/message
    private HashMap<String, boolean[]> otherSeqsMap = new HashMap<>();

    // Stores this user's name
    private String userName;

    // Keeps track of the sequence number for messages from this user
    private int nextNum;

    // For sending data to the server
    private ObjectOutputStream out;

    // For receiving data from the server
    private ObjectInputStream in;

    // MIDI sequencer components
    private Sequencer sequencer;
    private Sequence sequence;
    private Track track;

    // Array of instrument names to be displayed in GUI
    String[] instrumentNames = {"Bass Drum", "Closed Hi-Hat",
            "Open Hi-Hat", "Acoustic Snare", "Crash Cymbal", "Hand Clap",
            "High Tom", "Hi Bongo", "Maracas", "Whistle", "Low Conga",
            "Cowbell", "Vibraslap", "Low-mid Tom", "High Agogo",
            "Open Hi Conga"};

    // Corresponding MIDI instrument key numbers
    int[] instruments = {35, 42, 46, 38, 49, 39, 50, 60, 70, 72, 64, 56, 58, 47, 67, 63};

    public static void main(String[] args) {
        // Start the application with user name provided as command line argument
        new BeatBoxFinal().startUp(args[0]);  // args[0] is your user ID/screen name
    }

    public void startUp(String name) {
        userName = name;
        // open connection to the server
        try {
            // Attempt to connect to the server at localhost on port 4242
            Socket socket = new Socket("192.168.0.119", 4242);

            // Create output stream for sending data to server
            out = new ObjectOutputStream(socket.getOutputStream());

            // Create input stream for receiving data from server
            in = new ObjectInputStream(socket.getInputStream());

            // Run a separate thread to listen for incoming data from server
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(new RemoteReader());
        } catch (Exception ex) {
            // If connection fails, print message and allow local play only
            System.out.println("Couldn’t connect-you’ll have to play alone.");
        }

        // Initialize MIDI system
        setUpMidi();

        // Build the graphical user interface
        buildGUI();
    }
    public void buildGUI() {
        // Create the main application window
        JFrame frame = new JFrame("Cyber BeatBox");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Exit app on window close

        // Set up the main layout and panel with padding
        BorderLayout layout = new BorderLayout();
        JPanel background = new JPanel(layout);
        background.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding

        // Vertical box to hold control buttons and message area
        Box buttonBox = new Box(BoxLayout.Y_AXIS);

        // Start button - starts playback
        JButton start = new JButton("Start");
        start.addActionListener(e -> buildTrackAndStart()); // Play sequence
        buttonBox.add(start);

        // Stop button - stops playback
        JButton stop = new JButton("Stop");
        stop.addActionListener(e -> sequencer.stop());
        buttonBox.add(stop);

        // Increase tempo button
        JButton upTempo = new JButton("Tempo Up");
        upTempo.addActionListener(e -> changeTempo(1.03f)); // Increase tempo by 3%
        buttonBox.add(upTempo);

        // Decrease tempo button
        JButton downTempo = new JButton("Tempo Down");
        downTempo.addActionListener(e -> changeTempo(0.97f)); // Decrease tempo by 3%
        buttonBox.add(downTempo);

        // Send button - sends message and current beat pattern to server
        JButton sendIt = new JButton("sendIt");
        sendIt.addActionListener(e -> sendMessageAndTracks());
        buttonBox.add(sendIt);

        // Text area for user to type a message
        userMessage = new JTextArea();
        userMessage.setLineWrap(true); // Enable word wrapping
        userMessage.setWrapStyleWord(true);
        JScrollPane messageScroller = new JScrollPane(userMessage); // Scroll if text is long
        buttonBox.add(messageScroller);

        // JList to display incoming messages from other users
        incomingList = new JList<>();
        incomingList.addListSelectionListener(new MyListSelectionListener()); // React to selection
        incomingList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // One item at a time
        JScrollPane theList = new JScrollPane(incomingList); // Add scroll functionality
        buttonBox.add(theList);
        incomingList.setListData(listVector); // Initialize with empty data

        // Vertical box to display instrument labels
        Box nameBox = new Box(BoxLayout.Y_AXIS);
        for (String instrumentName : instrumentNames) {
            JLabel instrumentLabel = new JLabel(instrumentName); // Label for each instrument
            instrumentLabel.setBorder(BorderFactory.createEmptyBorder(4, 1, 4, 1)); // Spacing
            nameBox.add(instrumentLabel);
        }
        // Add the button box (controls, message area, etc.) to the right side of the layout
        background.add(BorderLayout.EAST, buttonBox);

        // Add the instrument name labels to the left side
        background.add(BorderLayout.WEST, nameBox);

        // Add the background panel (which holds everything) to the main frame
        frame.getContentPane().add(background);

        // Create a 16x16 grid layout for checkboxes (one per beat/instrument slot)
        GridLayout grid = new GridLayout(16, 16);
        grid.setVgap(1); // Vertical gap between checkboxes
        grid.setHgap(2); // Horizontal gap between checkboxes

        // Panel to hold all the checkboxes for the beat grid
        JPanel mainPanel = new JPanel(grid);
        background.add(BorderLayout.CENTER, mainPanel); // Add it to the center of the layout

        // Initialize checkbox list and add 256 checkboxes (16 instruments x 16 beats)
        checkboxList = new ArrayList<>();
        for (int i = 0; i < 256; i++) {
            JCheckBox c = new JCheckBox(); // New checkbox for one instrument-beat slot
            c.setSelected(false);          // Start unselected (no beat)
            checkboxList.add(c);           // Add to the list
            mainPanel.add(c);              // Add to the grid panel
        }

        // Set window position and size, then pack to fit components
        frame.setBounds(50, 50, 300, 300);
        frame.pack(); // Resize frame to fit contents
        frame.setVisible(true); // Show the window
    }

    private void setUpMidi() {
        try {
            // Get and open the MIDI sequencer
            sequencer = MidiSystem.getSequencer();
            sequencer.open();

            // Create a new MIDI sequence with resolution of 4 ticks per beat
            sequence = new Sequence(Sequence.PPQ, 4);

            // Create a new track in the sequence
            track = sequence.createTrack();

            // Set initial tempo (BPM)
            sequencer.setTempoInBPM(120);
        } catch (Exception e) {
            e.printStackTrace(); // Print any errors that occur
        }
    }

    private void buildTrackAndStart() {
        ArrayList<Integer> trackList; // this will hold the instruments for each beat

        // Remove the existing track (clear previous notes)
        sequence.deleteTrack(track);
        track = sequence.createTrack(); // Create a new empty track

        // Loop through each instrument (row)
        for (int i = 0; i < 16; i++) {
            trackList = new ArrayList<>();
            int key = instruments[i]; // MIDI key for this instrument

            // Loop through 16 beats (columns)
            for (int j = 0; j < 16; j++) {
                // Get the checkbox corresponding to this instrument and beat
                JCheckBox jc = checkboxList.get(j + (16 * i));

                if (jc.isSelected()) {
                    trackList.add(key); // Add this instrument to the track at this beat
                } else {
                    trackList.add(null); // No instrument played at this beat
                }
            }

            // Create MIDI events from this track list
            makeTracks(trackList);

            // Add a controller event to keep beat aligned
            track.add(makeEvent(CONTROL_CHANGE, 1, 127, 0, 16));
        }

        // Add a program change event to ensure all 16 beats are processed
        track.add(makeEvent(PROGRAM_CHANGE, 9, 1, 0, 15));

        try {
            // Load the new sequence into the sequencer
            sequencer.setSequence(sequence);

            // Loop the sequence indefinitely
            sequencer.setLoopCount(sequencer.LOOP_CONTINUOUSLY);

            // Set the playback tempo
            sequencer.setTempoInBPM(120);

            // Start playback
            sequencer.start();
        } catch (Exception e) {
            e.printStackTrace(); // Print error if playback fails
        }
    }
    // Adjusts the tempo of the sequencer by multiplying the current tempo factor with the provided multiplier
    private void changeTempo(float tempoMultiplier) {
        float tempoFactor = sequencer.getTempoFactor();
        sequencer.setTempoFactor(tempoFactor * tempoMultiplier);
    }

    // Sends the user message and current state of checkboxes (track data) to the server
    private void sendMessageAndTracks() {
        boolean[] checkboxState = new boolean[256]; // Stores the state of each checkbox
        for (int i = 0; i < 256; i++) {
            JCheckBox check = checkboxList.get(i);
            if (check.isSelected()) {
                checkboxState[i] = true; // Mark selected checkboxes as true
            }
        }
        try {
            out.writeObject(userName + nextNum++ + ": " + userMessage.getText()); // Send user message
            out.writeObject(checkboxState); // Send track state
        } catch (IOException e) {
            System.out.println("Terribly sorry. Could not send it to the server.");
            e.printStackTrace();
        }
        userMessage.setText(""); // Clear input field after sending
    }

    // List selection listener that loads and plays a selected sequence from the map
    public class MyListSelectionListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent lse) {
            if (!lse.getValueIsAdjusting()) { // Ensures only final selection is processed
                String selected = incomingList.getSelectedValue();
                if (selected != null) {
                    // Retrieve and load the selected sequence
                    boolean[] selectedState = otherSeqsMap.get(selected);
                    changeSequence(selectedState); // Update checkboxes to reflect selected sequence
                    sequencer.stop(); // Stop current playback
                    buildTrackAndStart(); // Rebuild track and start playback
                }
            }
        }
    }

    // Updates the checkbox list to match the state of the provided boolean array
    private void changeSequence(boolean[] checkboxState) {
        for (int i = 0; i < 256; i++) {
            JCheckBox check = checkboxList.get(i);
            check.setSelected(checkboxState[i]);
        }
    }

    // Creates and adds MIDI events (NOTE_ON and NOTE_OFF) to the track based on instrument list
    public void makeTracks(ArrayList<Integer> list) {
        for (int i = 0; i < list.size(); i++) {
            Integer instrumentKey = list.get(i);
            if (instrumentKey != null) {
                track.add(makeEvent(NOTE_ON, 9, instrumentKey, 100, i)); // Note ON event
                track.add(makeEvent(NOTE_OFF, 9, instrumentKey, 100, i + 1)); // Note OFF event
            }
        }
    }
    // Creates a MIDI event with the specified parameters and returns it
    public static MidiEvent makeEvent(int cmd, int chnl, int one, int two, int tick) {
        MidiEvent event = null;
        try {
            ShortMessage msg = new ShortMessage(); // Create a new MIDI short message
            msg.setMessage(cmd, chnl, one, two);   // Set the message with command, channel, and data bytes
            event = new MidiEvent(msg, tick);      // Create a MIDI event with the message and tick position
        } catch (Exception e) {
            e.printStackTrace(); // Print stack trace if there's an error creating the message or event
        }
        return event; // Return the created MIDI event (or null if creation failed)
    }
    // A Runnable class that listens for incoming data from the server and updates the GUI accordingly
    public class RemoteReader implements Runnable {
        public void run() {
            try {
                Object obj;
                // Continuously read objects from the server
                while ((obj = in.readObject()) != null) {
                    System.out.println("got an object from server");
                    System.out.println(obj.getClass()); // Print the type of object received

                    String nameToShow = (String) obj; // First object is expected to be a name or message string
                    boolean[] checkboxState = (boolean[]) in.readObject(); // Second object is the track state array

                    otherSeqsMap.put(nameToShow, checkboxState); // Store the received sequence in the map
                    listVector.add(nameToShow); // Add the name/message to the display list
                    incomingList.setListData(listVector); // Update the JList to show the new entry
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace(); // Print errors related to I/O or casting issues
            }
        }
    }
}
