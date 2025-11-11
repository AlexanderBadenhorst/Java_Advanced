package Soccer_App;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class SoccerLeagueGUI extends JFrame {

    // Team class to store team information
    static class Team implements Comparable<Team> {
        String name;
        int points;

        public Team(String name, int points) {
            this.name = name;
            this.points = points;
        }

        @Override
        public int compareTo(Team other) {
            if (this.points != other.points) {
                return other.points - this.points;
            }
            return this.name.compareTo(other.name);
        }
    }

    // Data structures
    private HashMap<String, Integer> teamScores;
    private ArrayList<String> matchHistory;

    // GUI Components
    private JTextArea matchInputArea;
    private JTable rankingTable;
    private DefaultTableModel tableModel;
    private JTextArea historyArea;
    private JLabel statusLabel;

    public SoccerLeagueGUI() {
        teamScores = new HashMap<>();
        matchHistory = new ArrayList<>();

        setTitle("Soccer League Ranking Calculator");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Create main panels in the correct order
        createInputPanel();
        createRankingPanel();
        createBottomPanel();

        // Load sample data option
        loadSampleData();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void createInputPanel() {
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBorder(new TitledBorder("Match Input"));

        JLabel instructionLabel = new JLabel("<html><b>Enter match results</b> (one per line):<br>" +
                "Format: Team1 Score, Team2 Score<br>" +
                "Example: Liverpool 3, ManchesterUnited 3</html>");
        instructionLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        matchInputArea = new JTextArea(8, 40);
        matchInputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        matchInputArea.setLineWrap(true);
        matchInputArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(matchInputArea);

        inputPanel.add(instructionLabel, BorderLayout.NORTH);
        inputPanel.add(scrollPane, BorderLayout.CENTER);

        add(inputPanel, BorderLayout.NORTH);
    }

    private void createRankingPanel() {
        JPanel rankingPanel = new JPanel(new BorderLayout(5, 5));
        rankingPanel.setBorder(new TitledBorder("League Rankings"));

        String[] columnNames = {"Rank", "Team Name", "Points"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        rankingTable = new JTable(tableModel);
        rankingTable.setFont(new Font("Arial", Font.PLAIN, 13));
        rankingTable.setRowHeight(25);
        rankingTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));

        // Set column widths
        rankingTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        rankingTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        rankingTable.getColumnModel().getColumn(2).setPreferredWidth(80);

        JScrollPane tableScrollPane = new JScrollPane(rankingTable);
        rankingPanel.add(tableScrollPane, BorderLayout.CENTER);

        add(rankingPanel, BorderLayout.CENTER);
    }

    private void createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton processButton = new JButton("Process Matches");
        processButton.setFont(new Font("Arial", Font.BOLD, 14));
        processButton.setPreferredSize(new Dimension(160, 35));
        processButton.addActionListener(e -> processMatches());

        JButton clearButton = new JButton("Clear All");
        clearButton.setFont(new Font("Arial", Font.BOLD, 14));
        clearButton.setPreferredSize(new Dimension(120, 35));
        clearButton.addActionListener(e -> clearAll());

        JButton loadFileButton = new JButton("Load from File");
        loadFileButton.setFont(new Font("Arial", Font.BOLD, 14));
        loadFileButton.setPreferredSize(new Dimension(150, 35));
        loadFileButton.addActionListener(e -> loadFromFile());

        JButton sampleButton = new JButton("Load Sample");
        sampleButton.setFont(new Font("Arial", Font.BOLD, 14));
        sampleButton.setPreferredSize(new Dimension(140, 35));
        sampleButton.addActionListener(e -> loadSampleData());

        buttonPanel.add(processButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(loadFileButton);
        buttonPanel.add(sampleButton);

        // History panel
        JPanel historyPanel = new JPanel(new BorderLayout(5, 5));
        historyPanel.setBorder(new TitledBorder("Match History"));

        historyArea = new JTextArea(6, 40);
        historyArea.setEditable(false);
        historyArea.setFont(new Font("Monospaced", Font.PLAIN, 11));

        JScrollPane historyScrollPane = new JScrollPane(historyArea);
        historyPanel.add(historyScrollPane, BorderLayout.CENTER);

        // Status bar
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBorder(BorderFactory.createEtchedBorder());

        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        statusPanel.add(statusLabel);

        // Combine all bottom components
        bottomPanel.add(buttonPanel, BorderLayout.NORTH);
        bottomPanel.add(historyPanel, BorderLayout.CENTER);
        bottomPanel.add(statusPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void processMatches() {
        String input = matchInputArea.getText().trim();

        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter match results!",
                    "No Input",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Clear existing data
        teamScores.clear();
        matchHistory.clear();

        String[] lines = input.split("\n");
        int successCount = 0;

        for (String line : lines) {
            line = line.trim();
            if (!line.isEmpty()) {
                try {
                    processMatch(line);
                    matchHistory.add(line);
                    successCount++;
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                            "Error processing line: " + line + "\n" + e.getMessage(),
                            "Parse Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        updateRankingTable();
        updateHistoryArea();
        statusLabel.setText("Processed " + successCount + " match(es). " +
                teamScores.size() + " team(s) in league.");
    }

    private void processMatch(String matchLine) {
        String[] teams = matchLine.split(",");

        if (teams.length != 2) {
            throw new IllegalArgumentException("Invalid format. Expected: Team1 Score, Team2 Score");
        }

        String team1Data = teams[0].trim();
        String[] team1Parts = parseTeamAndScore(team1Data);
        String team1Name = team1Parts[0];
        int team1Score = Integer.parseInt(team1Parts[1]);

        String team2Data = teams[1].trim();
        String[] team2Parts = parseTeamAndScore(team2Data);
        String team2Name = team2Parts[0];
        int team2Score = Integer.parseInt(team2Parts[1]);

        if (team1Score > team2Score) {
            addPoints(team1Name, 3);
            addPoints(team2Name, 0);
        } else if (team2Score > team1Score) {
            addPoints(team1Name, 0);
            addPoints(team2Name, 3);
        } else {
            addPoints(team1Name, 1);
            addPoints(team2Name, 1);
        }
    }

    private String[] parseTeamAndScore(String teamData) {
        teamData = teamData.trim();
        int lastSpaceIndex = teamData.lastIndexOf(' ');

        if (lastSpaceIndex == -1) {
            throw new IllegalArgumentException("Invalid format: " + teamData);
        }

        String teamName = teamData.substring(0, lastSpaceIndex).trim();
        String scoreStr = teamData.substring(lastSpaceIndex + 1).trim();

        return new String[]{teamName, scoreStr};
    }

    private void addPoints(String teamName, int points) {
        teamScores.put(teamName, teamScores.getOrDefault(teamName, 0) + points);
    }

    private void updateRankingTable() {
        tableModel.setRowCount(0);

        ArrayList<Team> teams = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : teamScores.entrySet()) {
            teams.add(new Team(entry.getKey(), entry.getValue()));
        }

        Collections.sort(teams);

        int rank = 1;
        int previousPoints = -1;

        for (int i = 0; i < teams.size(); i++) {
            Team team = teams.get(i);

            if (team.points != previousPoints) {
                rank = i + 1;
                previousPoints = team.points;
            }

            tableModel.addRow(new Object[]{rank, team.name, team.points + " pts"});
        }
    }

    private void updateHistoryArea() {
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
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Match Results File");

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            try {
                BufferedReader reader = new BufferedReader(new FileReader(selectedFile));
                StringBuilder content = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }

                reader.close();

                matchInputArea.setText(content.toString());
                statusLabel.setText("Loaded file: " + selectedFile.getName());

            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                        "Error reading file: " + e.getMessage(),
                        "File Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadSampleData() {
        String sampleData = "Liverpool 3, ManchesterUnited 3\n" +
                "Tarantulas2 1, FC Awesome 0\n" +
                "Lions 1, FC Awesome 1\n" +
                "Tarantulas2 3, ManchesterUnited 1\n" +
                "Lions 4, Grouches 0";

        matchInputArea.setText(sampleData);
        statusLabel.setText("Sample data loaded. Click 'Process Matches' to calculate rankings.");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new SoccerLeagueGUI();
        });
    }
}