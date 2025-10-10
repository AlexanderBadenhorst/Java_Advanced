package Chap13.Page448;

// Import required MIDI classes from javax.sound.midi package
import javax.sound.midi.*;

// Import static constants for MIDI message types (NOTE_ON, NOTE_OFF)
import static javax.sound.midi.ShortMessage.*;

// Define a public class named MiniMiniMusicApp
public class MiniMiniMusicApp {

    // Main method: Entry point of the application
    public static void main(String[] args) {
        // Create an instance of MiniMiniMusicApp
        MiniMiniMusicApp mini = new MiniMiniMusicApp();

        // Call the play() method to start playing music
        mini.play();
    }

    // Method to create and play a short MIDI sequence
    public void play() {
        try {
            // Get a Sequencer object from the system (used to play MIDI sequences)
            Sequencer player = MidiSystem.getSequencer();

            // Open the sequencer to prepare it for use
            player.open();

            // Create a new MIDI Sequence with timing resolution (PPQ = Pulses Per Quarter note), 4 ticks per quarter note
            Sequence seq = new Sequence(Sequence.PPQ, 4);

            // Create a new Track in the sequence to hold MIDI events
            Track track = seq.createTrack();

            // Add Program Change message to set instrument before playing notes
            ShortMessage first = new ShortMessage();
            first.setMessage(192, 1, 85, 0);  // Program Change: channel 1, instrument 102 (e.g., SynthPad)
            MidiEvent changeInstrument = new MidiEvent(first, 0);  // At tick 0 (start)
            track.add(changeInstrument);

            // Create a NOTE_ON message (start playing a note)
            ShortMessage msg1 = new ShortMessage();
            msg1.setMessage(NOTE_ON, 1, 44, 100); // Channel 1, note 20, velocity 100

            // Create a MidiEvent from the NOTE_ON message, scheduled at tick 1
            MidiEvent noteOn = new MidiEvent(msg1, 1);
            track.add(noteOn); // Add the event to the track

            // Create a NOTE_OFF message (stop playing the note)
            ShortMessage msg2 = new ShortMessage();
            msg2.setMessage(NOTE_OFF, 1, 44, 100); // Same channel, note 44

            // Create a MidiEvent from the NOTE_OFF message, scheduled at tick 3
            MidiEvent noteOff = new MidiEvent(msg2, 20);
            track.add(noteOff); // Add the event to the track

            // Load the sequence into the sequencer
            player.setSequence(seq);

            // Start playing the sequence
            player.start();

        } catch (Exception e) {
            // Handle exceptions by printing stack trace
            e.printStackTrace();
        }
    }
}
