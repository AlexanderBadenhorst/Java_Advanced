package Chap11.Page333;

import java.util.*;

// Simple factory to produce sample data for the demo
class MockSongs {
    public static List<String> getSongStrings() {
        return new ArrayList<>();
    }

    public static List<SongV3> getSongsV3() {
        List<SongV3> songs = new ArrayList<>();
        // title, artist, bpm
        songs.add(new SongV3("somersault", "zero 7", 147));
        songs.add(new SongV3("cassidy", "grateful dead", 158));
        songs.add(new SongV3("$10", "hitchhiker", 140));
        songs.add(new SongV3("havana", "cabello", 105));
        songs.add(new SongV3("Cassidy", "grateful dead", 158)); // Note different case in title
        songs.add(new SongV3("50 ways", "simon", 102));
        // List is intentionally mixed so we can see the effect of each sort
        return songs;
    }
}
