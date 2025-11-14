package Soccer_App;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.*;
import static javax.swing.JOptionPane.*;

/**
 * Soccer League Ranking Calculator (GUI)
 * Paste or load match lines, process them, and view a ranked table.
 * Ranking uses "standard competition ranking": 1,2,3,3,3,6
 * Sort order inside a tie is alphabetical by team name.
 *
 * Input line format:
 *   Team A 2, Team B 1
 */
public class SoccerLeagueGUI extends JFrame {

    /** Simple DTO for table rows. Comparable gives: points desc, then name asc (case-insensitive). */
    static class Team implements Comparable<Team> {
        final String name;
        final int points;
        Team(String n, int p) { name = n; points = p; }
        @Override public int compareTo(Team o) {
            if (points != o.points) return Integer.compare(o.points, points); // higher first
            return String.CASE_INSENSITIVE_ORDER.compare(name, o.name);       // A..Z inside tie
        }
    }

    // --- State -----------------------------------------------------
    private final Map<String, Integer> teamScores = new HashMap<>();
    private final java.util.List<String> matchHistory = new ArrayList<>();

    // --- UI --------------------------------------------------------
    private JTextArea matchInputArea;
    private JTable rankingTable;
    private DefaultTableModel tableModel;
    private JTextArea historyArea;
    private JLabel statusLabel;

    public SoccerLeagueGUI() {
        super("Soccer League Ranking Calculator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 700);
        setLayout(new BorderLayout(10, 10));

        add(buildInputPanel(),   BorderLayout.NORTH);
        add(buildRankingPanel(), BorderLayout.CENTER);
        add(buildBottomPanel(),  BorderLayout.SOUTH);

        loadSampleData();          // prefill for the demo / recording
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // === Panels ====================================================

    private JPanel buildInputPanel() {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setBorder(new TitledBorder("Match Input"));

        JLabel help = new JLabel("""
                <html><b>Enter match results</b> one per line.<br>
                Format: Team1 Score, Team2 Score<br>
                Example: Liverpool 3, ManchesterUnited 3</html>
                """);
        help.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        matchInputArea = new JTextArea(8, 40);
        matchInputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        matchInputArea.setLineWrap(true);
        matchInputArea.setWrapStyleWord(true);

        p.add(help, BorderLayout.NORTH);
        p.add(new JScrollPane(matchInputArea), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildRankingPanel() {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setBorder(new TitledBorder("League Rankings"));

        tableModel = new DefaultTableModel(new String[]{"Rank", "Team Name", "Points"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        rankingTable = new JTable(tableModel);
        rankingTable.setFont(new Font("Arial", Font.PLAIN, 13));
        rankingTable.setRowHeight(25);
        rankingTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        rankingTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        rankingTable.getColumnModel().getColumn(1).setPreferredWidth(220);
        rankingTable.getColumnModel().getColumn(2).setPreferredWidth(80);

        p.add(new JScrollPane(rankingTable), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildBottomPanel() {
        JPanel root = new JPanel(new BorderLayout(5,5));

        // Buttons
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttons.add(makeButton("Process Matches", 160, e -> processMatches()));
        buttons.add(makeButton("Clear All", 120, e -> clearAll()));
        buttons.add(makeButton("Load from File", 150, e -> loadFromFile()));
        buttons.add(makeButton("Load Sample", 140, e -> loadSampleData()));

        // History
        JPanel historyPanel = new JPanel(new BorderLayout(5,5));
        historyPanel.setBorder(new TitledBorder("Match History"));
        historyArea = new JTextArea(6, 40);
        historyArea.setEditable(false);
        historyArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        historyPanel.add(new JScrollPane(historyArea), BorderLayout.CENTER);

        // Status
        JPanel status = new JPanel(new FlowLayout(FlowLayout.LEFT));
        status.setBorder(BorderFactory.createEtchedBorder());
        statusLabel = new JLabel("Ready");
        status.add(statusLabel);

        root.add(buttons, BorderLayout.NORTH);
        root.add(historyPanel, BorderLayout.CENTER);
        root.add(status, BorderLayout.SOUTH);
        return root;
    }

    private JButton makeButton(String text, int width, java.awt.event.ActionListener onClick) {
        JButton b = new JButton(text);
        b.setFont(new Font("Arial", Font.BOLD, 14));
        b.setPreferredSize(new Dimension(width, 35));
        b.addActionListener(onClick);
        return b;
    }

    // === Actions ===================================================

    /** Parse all lines, compute scores, then refresh the table and history. */
    private void processMatches() {
        String input = matchInputArea.getText().trim();
        if (input.isEmpty()) {
            showMessageDialog(this, "Please enter match results!", "No Input", WARNING_MESSAGE);
            return;
        }

        teamScores.clear();
        matchHistory.clear();

        int ok = 0, bad = 0;
        for (String raw : input.split("\\R")) {
            String line = raw.trim();
            if (line.isEmpty()) continue;
            try {
                processMatch(line);
                matchHistory.add(line);
                ok++;
            } catch (Exception ex) {
                bad++;
            }
        }

        refreshRankingTable();
        refreshHistory();
        statusLabel.setText("Processed " + ok + " match(es). " + teamScores.size()
                + " team(s). Errors: " + bad + ".");
        if (bad > 0) {
            showMessageDialog(this,
                    "Some lines could not be parsed. Please check the format.",
                    "Parse Warnings",
                    INFORMATION_MESSAGE);
        }
    }

    /** Parse one line, update points according to win, draw, loss. */
    private void processMatch(String matchLine) {
        String[] sides = matchLine.split(",", 2);
        if (sides.length != 2)
            throw new IllegalArgumentException("Expected: Team1 Score, Team2 Score");

        String[] a = parseTeamAndScore(sides[0].trim());
        String[] b = parseTeamAndScore(sides[1].trim());

        String teamA = a[0]; int goalsA = Integer.parseInt(a[1]);
        String teamB = b[0]; int goalsB = Integer.parseInt(b[1]);

        if (goalsA > goalsB) { addPoints(teamA, 3); addPoints(teamB, 0); }
        else if (goalsB > goalsA) { addPoints(teamA, 0); addPoints(teamB, 3); }
        else { addPoints(teamA, 1); addPoints(teamB, 1); }
    }

    /** Split "Team Name 4" into [name, "4"], validate score numeric. */
    private String[] parseTeamAndScore(String chunk) {
        int i = chunk.lastIndexOf(' ');
        if (i <= 0 || i == chunk.length() - 1)
            throw new IllegalArgumentException("Invalid: " + chunk);
        String name = chunk.substring(0, i).trim();
        String score = chunk.substring(i + 1).trim();
        if (!score.chars().allMatch(Character::isDigit))
            throw new IllegalArgumentException("Score is not a number: " + score);
        return new String[]{name, score};
    }

    private void addPoints(String team, int pts) {
        teamScores.put(team, teamScores.getOrDefault(team, 0) + pts);
    }

    /** Build rows and assign shared ranks 1,2,3,3,3,6. */
    private void refreshRankingTable() {
        tableModel.setRowCount(0);

        java.util.List<Team> teams = new ArrayList<>();
        for (Map.Entry<String,Integer> e : teamScores.entrySet()) {
            teams.add(new Team(e.getKey(), e.getValue()));
        }
        Collections.sort(teams);

        int lastPoints = Integer.MIN_VALUE;
        int lastRank = 0;
        int position = 0;

        for (Team t : teams) {
            position++;
            if (t.points != lastPoints) {
                lastPoints = t.points;
                lastRank = position;
            }
            tableModel.addRow(new Object[]{lastRank, t.name, t.points + " pts"});
        }
    }

    private void refreshHistory() {
        historyArea.setText("");
        for (int i = 0; i < matchHistory.size(); i++) {
            historyArea.append((i + 1) + ". " + matchHistory.get(i) + "\n");
        }
    }

    private void clearAll() {
        matchInputArea.setText("");
        teamScores.clear();
        matchHistory.clear();
        tableModel.setRowCount(0);
        historyArea.setText("");
        statusLabel.setText("Cleared all data.");
    }

    private void loadFromFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Match Results File");
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                StringBuilder sb = new StringBuilder();
                for (String line; (line = br.readLine()) != null; ) {
                    sb.append(line).append("\n");
                }
                matchInputArea.setText(sb.toString());
                statusLabel.setText("Loaded file: " + f.getName());
            } catch (IOException ex) {
                showMessageDialog(this, "Error reading file: " + ex.getMessage(),
                        "File Error", ERROR_MESSAGE);
            }
        }
    }

    private void loadSampleData() {
        String sample = """
                Liverpool 3, ManchesterUnited 3
                Tarantulas2 1, FC Awesome 0
                Lions 1, FC Awesome 1
                Tarantulas2 3, ManchesterUnited 1
                Lions 4, Grouches 0
                """;
        matchInputArea.setText(sample.trim());
        statusLabel.setText("Sample data loaded. Click 'Process Matches' to calculate rankings.");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}
            new SoccerLeagueGUI();
        });
    }
}
