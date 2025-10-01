package Chap11.Page342;

import java.util.ArrayList;
import java.util.List;

// Mock factory that provides a hardcoded list of songs.
// Used so we always have consistent data for testing sorting.
class MockSongs {
    public static List<String> getSongStrings() {
        return new ArrayList<>();
    }

    public static List<SongV3> getSongsV3() {
        List<SongV3> songs = new ArrayList<>();
        songs.add(new SongV3("somersault", "zero 7", 147));
        songs.add(new SongV3("cassidy", "grateful dead", 158));
        songs.add(new SongV3("$10", "hitchhiker", 140));
        songs.add(new SongV3("havana", "cabello", 105));
        songs.add(new SongV3("Cassidy", "grateful dead", 158));
        songs.add(new SongV3("50 ways", "simon", 102));
        return songs;
    }
}
