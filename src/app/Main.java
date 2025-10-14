package app;

import javax.swing.SwingUtilities;
import app.ui.SongManagerFrame;

/**
 * Application entry point.
 * Uses SwingUtilities.invokeLater to ensure all Swing code runs on the Event Dispatch Thread,
 * which is the correct thread for creating and updating Swing components.
 */
public class Main {
    public static void main(String[] args) {
        // Schedule GUI creation on the EDT to avoid threading bugs in Swing.
        SwingUtilities.invokeLater(() -> new SongManagerFrame().setVisible(true));
    }
}
