package app;

import javax.swing.SwingUtilities;
import app.ui.SongManagerFrame;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SongManagerFrame().setVisible(true));
    }
}
