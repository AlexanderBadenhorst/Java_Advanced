package Soccer_App;
import java.io.*;
import java.util.*;

public class SoccerLeagueRanking {

    // Team class to store team information
    static class Team implements Comparable<Team> {
        String name;
        int points;

        public Team(String name, int points) {
            this.name = name;
            this.points = points;
        }

        // Compare teams by points (descending) then by name (alphabetically)
        @Override
        public int compareTo(Team other) {
            if (this.points != other.points) {
                return other.points - this.points; // Higher points first
            }
            return this.name.compareTo(other.name); // Alphabetical if tied
        }

        @Override
        public String toString() {
            return name + ", " + points + " pts";
        }
    }

    // HashMap to store team names and their points
    private HashMap<String, Integer> teamScores;

    public SoccerLeagueRanking() {
        teamScores = new HashMap<>();
    }

    // Process a single match result line
    public void processMatch(String matchLine) {
        // Split by comma to separate the two teams
        String[] teams = matchLine.split(",");

        if (teams.length != 2) {
            System.err.println("Invalid match format: " + matchLine);
            return;
        }

        // Parse team 1
        String team1Data = teams[0].trim();
        String[] team1Parts = parseTeamAndScore(team1Data);
        String team1Name = team1Parts[0];
        int team1Score = Integer.parseInt(team1Parts[1]);

        // Parse team 2
        String team2Data = teams[1].trim();
        String[] team2Parts = parseTeamAndScore(team2Data);
        String team2Name = team2Parts[0];
        int team2Score = Integer.parseInt(team2Parts[1]);

        // Calculate points based on match result
        if (team1Score > team2Score) {
            // Team 1 wins
            addPoints(team1Name, 3);
            addPoints(team2Name, 0);
        } else if (team2Score > team1Score) {
            // Team 2 wins
            addPoints(team1Name, 0);
            addPoints(team2Name, 3);
        } else {
            // Draw
            addPoints(team1Name, 1);
            addPoints(team2Name, 1);
        }
    }

    // Parse team name and score from a string like "Lions 4"
    private String[] parseTeamAndScore(String teamData) {
        teamData = teamData.trim();
        int lastSpaceIndex = teamData.lastIndexOf(' ');

        if (lastSpaceIndex == -1) {
            throw new IllegalArgumentException("Invalid team data: " + teamData);
        }

        String teamName = teamData.substring(0, lastSpaceIndex).trim();
        String scoreStr = teamData.substring(lastSpaceIndex + 1).trim();

        return new String[]{teamName, scoreStr};
    }

    // Add points to a team (or initialize if not exists)
    private void addPoints(String teamName, int points) {
        teamScores.put(teamName, teamScores.getOrDefault(teamName, 0) + points);
    }

    // Generate and print the ranking table
    public void printRankings() {
        // Convert HashMap to ArrayList of Team objects
        ArrayList<Team> teams = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : teamScores.entrySet()) {
            teams.add(new Team(entry.getKey(), entry.getValue()));
        }

        // Sort teams using the compareTo method
        Collections.sort(teams);

        // Print rankings with proper rank numbering
        int rank = 1;
        int previousPoints = -1;
        int teamsWithSameRank = 0;

        for (int i = 0; i < teams.size(); i++) {
            Team team = teams.get(i);

            // If points changed, update rank
            if (team.points != previousPoints) {
                rank = i + 1;
                previousPoints = team.points;
            }

            System.out.println(rank + ". " + team.toString());
        }
    }

    // Read matches from a file
    public void processFile(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;

        while ((line = reader.readLine()) != null) {
            if (!line.trim().isEmpty()) {
                processMatch(line);
            }
        }

        reader.close();
    }

    // Main method with sample usage
    public static void main(String[] args) {
//        SoccerLeagueRanking league = new SoccerLeagueRanking();
//
//        // Process sample data
//        String[] sampleMatches = {
//                "Liverpool 3, ManchesterUnited 3",
//                "Tarantulas2 1, FC Awesome 0",
//                "Lions 1, FC Awesome 1",
//                "Tarantulas2 3, ManchesterUnited 1",
//                "Lions 4, Grouches 0"
//        };
//
//        System.out.println("Processing matches...\n");
//        for (String match : sampleMatches) {
//            league.processMatch(match);
//        }
//
//        System.out.println("Final Rankings:");
//        league.printRankings();

        // Example of reading from a file

        try {
            SoccerLeagueRanking fileLeague = new SoccerLeagueRanking();
            fileLeague.processFile("matches.txt");
            System.out.println("\nRankings from file:");
            fileLeague.printRankings();
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }

    }
}