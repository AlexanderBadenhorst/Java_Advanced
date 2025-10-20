package Chap15.Page530_533;

import javax.sound.midi.*;              // For MIDI music functionality
import javax.swing.*;                   // For creating GUI components like buttons and checkboxes
import java.awt.*;                      // For layout and window design
import java.util.ArrayList;             // For using ArrayList to hold checkboxes
import static javax.sound.midi.ShortMessage.*; // Import MIDI message constants

public class BeatBox {

    private ArrayList<JCheckBox> checkboxList;  // List to store all the checkboxes for beats
    private Sequencer sequencer;                // MIDI sequencer to play the music
    private Sequence sequence;                  // Holds the musical sequence
    private Track track;                        // A track inside the sequence

    // Names of the instruments that will appear on the interface
    String[] instrumentNames = {
            "Bass Drum", "Closed Hi-Hat", "Open Hi-Hat", "Acoustic Snare",
            "Crash Cymbal", "Hand Clap", "High Tom", "Hi Bongo",
            "Maracas", "Whistle", "Low Conga", "Cowbell",
            "Vibraslap", "Low-mid Tom", "High Agogo", "Open Hi Conga"
    };

    // MIDI key numbers for each instrument
    int[] instruments = {
            35, 42, 46, 38, 49, 39, 50, 60,
            70, 72, 64, 56, 58, 47, 67, 63
    };

    // Main method: program starts here
    public static void main(String[] args) {
        new BeatBox().buildGUI();  // Create a BeatBox object and build the GUI
    }

    // Method to set up and display the graphical user interface
    public void buildGUI() {
        JFrame frame = new JFrame("Cyber BeatBox"); // Create the main window
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Exit program when window is closed

        BorderLayout layout = new BorderLayout(); // Use BorderLayout for organizing components
        JPanel background = new JPanel(layout);   // Main panel with BorderLayout
        background.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding

        Box buttonBox = new Box(BoxLayout.Y_AXIS); // Vertical box to hold control buttons

        // Create and add the Start button
        JButton start = new JButton("Start");
        start.addActionListener(e -> buildTrackAndStart()); // Start playing the beat when clicked
        buttonBox.add(start);

        // Create and add the Stop button
        JButton stop = new JButton("Stop");
        stop.addActionListener(e -> sequencer.stop()); // Stop playing the beat
        buttonBox.add(stop);

        // Create and add the Tempo Up button
        JButton upTempo = new JButton("Tempo Up");
        upTempo.addActionListener(e -> changeTempo(1.03f)); // Increase tempo by 3%
        buttonBox.add(upTempo);

        // Create and add the Tempo Down button
        JButton downTempo = new JButton("Tempo Down");
        downTempo.addActionListener(e -> changeTempo(0.97f)); // Decrease tempo by 3%
        buttonBox.add(downTempo);

        // Create a vertical box (Y-axis) to display instrument names
        Box nameBox = new Box(BoxLayout.Y_AXIS);

// Loop through each instrument name and add it to the name box as a label
        for (String instrumentName : instrumentNames) {
            JLabel instrumentLabel = new JLabel(instrumentName); // Create a label for the instrument
            instrumentLabel.setBorder(BorderFactory.createEmptyBorder(4, 1, 4, 1)); // Add some padding
            nameBox.add(instrumentLabel); // Add label to the name box
        }

// Add button controls to the right side of the main background panel
        background.add(BorderLayout.EAST, buttonBox);

// Add instrument name labels to the left side
        background.add(BorderLayout.WEST, nameBox);

// Add the background panel to the main window (frame)
        frame.getContentPane().add(background);

// Create a 16x16 grid layout: 16 instruments and 16 time steps
        GridLayout grid = new GridLayout(16, 16);
        grid.setVgap(1); // Small vertical gap between checkboxes
        grid.setHgap(2); // Small horizontal gap between checkboxes

// Create a panel to hold the grid of checkboxes
        JPanel mainPanel = new JPanel(grid);

// Add the checkbox grid panel to the center of the background
        background.add(BorderLayout.CENTER, mainPanel);

// Initialize the list to hold all 256 checkboxes (16x16)
        checkboxList = new ArrayList<>();

// Create and add each checkbox to the grid and the list
        for (int i = 0; i < 256; i++) {
            JCheckBox c = new JCheckBox(); // Create a new checkbox
            c.setSelected(false); // Start unchecked
            checkboxList.add(c); // Add to the list for later use
            mainPanel.add(c); // Add to the grid on screen
        }

// Set up the MIDI system for playing sounds
        setUpMidi();

// Set the size and position of the window
        frame.setBounds(50, 50, 300, 300);

// Resize the frame to fit all components neatly
        frame.pack();

// Make the window visible
        frame.setVisible(true);
    }

    // This method sets up the MIDI sequencer used to play the beats
    private void setUpMidi() {
        try {
            sequencer = MidiSystem.getSequencer(); // Get a sequencer
            sequencer.open();                      // Open it for use
            sequence = new Sequence(Sequence.PPQ, 4); // Create a sequence with timing resolution
            track = sequence.createTrack();           // Create a track inside the sequence
            sequencer.setTempoInBPM(120);             // Set default tempo (beats per minute)
        } catch (Exception e) {
            e.printStackTrace(); // Print error details if setup fails
        }
    }

    // This method builds the MIDI track based on which checkboxes are selected and starts playing the sequence
    private void buildTrackAndStart() {
        int[] trackList;

        // Remove the existing track to avoid overlaps
        sequence.deleteTrack(track);
        track = sequence.createTrack(); // Create a new track

        // Loop through each of the 16 instruments
        for (int i = 0; i < 16; i++) {
            trackList = new int[16]; // Holds beat pattern for one instrument
            int key = instruments[i]; // Get the MIDI key for this instrument

            // Loop through 16 beats (columns)
            for (int j = 0; j < 16; j++) {
                JCheckBox jc = checkboxList.get(j + 16 * i); // Get checkbox at row i, column j

                if (jc.isSelected()) {
                    trackList[j] = key; // If selected, set the instrument to play here
                } else {
                    trackList[j] = 0; // Otherwise, no sound
                }
            }

            // Add this instrument's beat pattern to the track
            makeTracks(trackList);

            // Add a control change event to keep the sequencer responsive
            track.add(makeEvent(CONTROL_CHANGE, 1, 127, 0, 16));
        }

        // Add a program change at the end of the track (MIDI housekeeping)
        track.add(makeEvent(PROGRAM_CHANGE, 9, 1, 0, 15));

        try {
            sequencer.setSequence(sequence); // Load the sequence into the sequencer
            sequencer.setLoopCount(sequencer.LOOP_CONTINUOUSLY); // Play continuously
            sequencer.setTempoInBPM(120); // Set the tempo
            sequencer.start(); // Start playing
        } catch (Exception e) {
            e.printStackTrace(); // Handle any errors
        }
    }

    // This method changes the playback tempo by multiplying the current tempo with a given factor
    private void changeTempo(float tempoMultiplier) {
        float tempoFactor = sequencer.getTempoFactor(); // Get the current tempo
        sequencer.setTempoFactor(tempoFactor * tempoMultiplier); // Apply the multiplier
    }

    // This method adds note events to the track for a given instrument's beat pattern
    private void makeTracks(int[] list) {
        // Loop through 16 beats
        for (int i = 0; i < 16; i++) {
            int key = list[i]; // Get the instrument key for this beat

            if (key != 0) {
                // If a beat is present, add a NOTE_ON event at this beat (tick)
                track.add(makeEvent(NOTE_ON, 9, key, 100, i));

                // Add a NOTE_OFF event one tick later to stop the sound
                track.add(makeEvent(NOTE_OFF, 9, key, 100, i + 1));
            }
        }
    }

    // This helper method creates a MIDI event with the specified parameters
    public static MidiEvent makeEvent(int cmd, int chnl, int one, int two, int tick) {
        MidiEvent event = null;
        try {
            ShortMessage msg = new ShortMessage(); // MIDI message object
            msg.setMessage(cmd, chnl, one, two);   // Set the message (e.g., NOTE_ON, NOTE_OFF)
            event = new MidiEvent(msg, tick);      // Wrap it into a MIDI event at the given time (tick)
        } catch (Exception e) {
            e.printStackTrace(); // Print error if something goes wrong
        }
        return event; // Return the created MIDI event
    }
}