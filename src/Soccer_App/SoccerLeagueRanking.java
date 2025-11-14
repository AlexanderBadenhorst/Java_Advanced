package Soccer_App;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Reads match results from a text file and prints a league table.
 * Rules: win=3, draw=1, loss=0. Same points share the same rank.
 * Within a tie, teams are printed alphabetically (case-insensitive).
 *
 * Input line format:  TeamA <score>, TeamB <score>
 * Example:            Liverpool 3, ManchesterUnited 3
 */
public class SoccerLeagueRanking {

    /** Simple value object for display and sorting */
    static class Team implements Comparable<Team> {
        final String name;
        final int points;

        Team(String name, int points) {
            this.name = name;
            this.points = points;
        }

        /** Sort: points desc, then name asc, case-insensitive */
        @Override
        public int compareTo(Team other) {
            if (points != other.points) return Integer.compare(other.points, points);
            return String.CASE_INSENSITIVE_ORDER.compare(name, other.name);
        }

        @Override public String toString() { return name + ", " + points + " pts"; }
    }

    // Core storage the rubric asks for
    private final Map<String, Integer> teamScores = new HashMap<>();

    /** Public so GUI or tests can reuse the exact parsing and points logic */
    public void processMatch(String line) {
        // Split the two “sides” around the first comma
        String[] sides = line.split(",", 2);
        if (sides.length != 2) throw new IllegalArgumentException("Invalid match: " + line);

        String[] a = parseTeamAndScore(sides[0].trim()); // [name, score]
        String[] b = parseTeamAndScore(sides[1].trim());

        String teamA = a[0]; int goalsA = Integer.parseInt(a[1]);
        String teamB = b[0]; int goalsB = Integer.parseInt(b[1]);

        // Award points
        if (goalsA > goalsB) {
            addPoints(teamA, 3); addPoints(teamB, 0);
        } else if (goalsB > goalsA) {
            addPoints(teamA, 0); addPoints(teamB, 3);
        } else {
            addPoints(teamA, 1); addPoints(teamB, 1);
        }
    }

    /** Split "Team Name 4" into ["Team Name", "4"] with basic validation */
    private String[] parseTeamAndScore(String chunk) {
        int i = chunk.lastIndexOf(' ');
        if (i <= 0 || i == chunk.length() - 1)
            throw new IllegalArgumentException("Invalid team+score: " + chunk);
        String name = chunk.substring(0, i).trim();
        String score = chunk.substring(i + 1).trim();
        // Lightweight digit check to catch “FC  A” style mistakes
        if (!score.chars().allMatch(Character::isDigit))
            throw new IllegalArgumentException("Score is not a number: " + score);
        return new String[]{name, score};
    }

    private void addPoints(String team, int pts) {
        teamScores.put(team, teamScores.getOrDefault(team, 0) + pts);
    }

    /** Build a sorted list of teams for printing or GUI use */
    public List<Team> buildSortedTable() {
        List<Team> out = new ArrayList<>();
        for (Map.Entry<String, Integer> e : teamScores.entrySet()) {
            out.add(new Team(e.getKey(), e.getValue()));
        }
        Collections.sort(out);
        return out;
    }

    /** Print with shared ranks: 1,2,3,3,3,6 */
    public void printRankings() {
        List<Team> teams = buildSortedTable();

        int lastPoints = Integer.MIN_VALUE;
        int lastRank = 0;           // the rank number we print
        int position = 0;           // 1-based position in the sorted list

        System.out.println("Rankings from file:");
        for (Team t : teams) {
            position++;
            if (t.points != lastPoints) {
                lastPoints = t.points;
                lastRank = position;
            }
            System.out.println(lastRank + ". " + t);
        }
    }

    /** Read line-by-line from a file, skipping blanks */
    public void processFile(String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            for (String line; (line = br.readLine()) != null; ) {
                line = line.trim();
                if (!line.isEmpty()) processMatch(line);
            }
        }
    }

    public static void main(String[] args) {
        // Default to matches.txt in the project root when run from IntelliJ “play” button
        String path = (args.length >= 1) ? args[0] : "matches.txt";
        try {
            SoccerLeagueRanking league = new SoccerLeagueRanking();
            league.processFile(path);
            league.printRankings();
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
