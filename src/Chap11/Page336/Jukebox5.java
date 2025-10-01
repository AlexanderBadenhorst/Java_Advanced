package Chap11.Page336;

import java.util.*;

public class Jukebox5 {
    public static void main(String[] args) {
        new Jukebox5().go();
    }

    public void go() {
        // Get a mock list of songs
        List<SongV3> songList = MockSongs.getSongsV3();
        System.out.println("Original List: " + songList);

        // --- Sort by TITLE using a Comparator ---
        // Instead of relying on SongV3.compareTo() (Comparable),
        // we now use a dedicated Comparator class: TitleCompare.
        // This keeps our design consistent: ALL sorting goes through Comparators.
        TitleCompare titleCompare = new TitleCompare();
        songList.sort(titleCompare);
        System.out.println("Sorted by Title: " + songList);

        // --- Sort by ARTIST using a Comparator ---
        // ArtistCompare compares SongV3 objects based on the artist string.
        // Again, String.compareTo() does the heavy lifting.
        ArtistCompare artistCompare = new ArtistCompare();
        songList.sort(artistCompare);
        System.out.println("Sorted by Artist: " + songList);

        // --- Sort by BPM using a Comparator ---
        // This time we compare the integer BPM values.
        // Integer.compare() is used for a clean, safe numeric comparison.
        BpmCompare bpmCompare = new BpmCompare();
        songList.sort(bpmCompare);
        System.out.println("Sorted by Bpm: " + songList);
    }
}

// Comparator for comparing SongV3 objects by TITLE.
// Uses String.compareTo() to alphabetize by song title.
class TitleCompare implements Comparator<SongV3> {
    public int compare(SongV3 one, SongV3 two) {
        return one.getTitle().compareTo(two.getTitle());
    }
}

// Comparator for comparing SongV3 objects by ARTIST.
// Again, String.compareTo() alphabetizes the artist names.
class ArtistCompare implements Comparator<SongV3> {
    public int compare(SongV3 one, SongV3 two) {
        return one.getArtist().compareTo(two.getArtist());
    }
}

// Comparator for comparing SongV3 objects by BPM (numeric).
// Integer.compare() is the safe way to compare two ints.
class BpmCompare implements Comparator<SongV3> {
    public int compare(SongV3 one, SongV3 two) {
        return Integer.compare(one.getBpm(), two.getBpm());
    }
}
