package Chap12.Exercises.Page416;

import java.util.*;
import java.util.stream.*;

public class StreamPuzzle {
    public static void main(String[] args) {
        SongSearch songSearch = new SongSearch();
        songSearch.printTopFiveSongs();
        songSearch.search("The Beatles");
        songSearch.search("The Beach Boys");
    }
}

// ------------------------------------------------------------
// Handles song searching and top-5 filtering logic
// ------------------------------------------------------------
class SongSearch {
    private final List<Song> songs = new JukeboxData().getSongs();

    void printTopFiveSongs() {
        List<String> topFive = songs.stream()
                // MOST-played first
                .sorted(Comparator.comparingInt(Song::getTimesPlayed).reversed())
                .limit(5)
                .map(Song::getTitle)
                .collect(Collectors.toList());
        System.out.println(topFive);
    }

    void search(String artist) {
        Optional<Song> result = songs.stream()
                .filter(song -> song.getArtist().equals(artist)) // exact match (book behavior)
                .findFirst();

        System.out.println(result.map(Song::getTitle)
                .orElse("No songs found by: " + artist));
    }
}

// ------------------------------------------------------------
// Simple POJO class representing a song
// ------------------------------------------------------------
class Song {
    private final String title;
    private final String artist;
    private final int timesPlayed;

    public Song(String title, String artist, int timesPlayed) {
        this.title = title;
        this.artist = artist;
        this.timesPlayed = timesPlayed;
    }

    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public int getTimesPlayed() { return timesPlayed; }

    @Override
    public String toString() {
        return title + " by " + artist + " (" + timesPlayed + ")";
    }
}

// ------------------------------------------------------------
// Dataset tailored to match the book's expected output
// ------------------------------------------------------------
class JukeboxData {
    public List<Song> getSongs() {
        return Arrays.asList(
                new Song("Immigrant Song", "Led Zeppelin", 10),
                new Song("With a Little Help from My Friends", "The Beatles", 9),
                new Song("Hallucinate", "Dua Lipa", 8),
                new Song("Pasos de cero", "Pablo Alborán", 7),
                new Song("Cassidy", "Grateful Dead", 6),
                new Song("Good Vibrations", "Beach Boys", 5) // note: NOT "The Beach Boys"
        );
    }
}
