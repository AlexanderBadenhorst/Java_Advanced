package Chap11.Page333;

import java.util.*;

public class Jukebox4 {
    public static void main(String[] args) {
        new Jukebox4().go();
    }

    public void go() {
        // Build a List<SongV3> from the mock factory
        List<SongV3> songList = MockSongs.getSongsV3();
        System.out.println(songList); // 1) Unsorted (in insertion order)

        // 2) Sort using the element type’s *natural order*.
        // Because SongV3 implements Comparable<SongV3>, Collections.sort(list)
        // will call each element’s compareTo(). In our SongV3 class, compareTo()
        // compares titles, so this sorts BY TITLE.
        Collections.sort(songList);
        System.out.println(songList); // Sorted by title (SongV3.compareTo)

        // 3) Sort using an *external* Comparator.
        // When you pass a Comparator, the list’s compareTo() is ignored.
        // The Comparator's compare() decides the order instead.
        ArtistCompare artistCompare = new ArtistCompare();
        songList.sort(artistCompare); // Java 8+ convenience; same as Collections.sort(list, comparator)
        System.out.println(songList); // Sorted by artist (ArtistCompare.compare)
    }

}

// Comparator class to compare songs by artist
// Note: Elements do NOT need to implement Comparable when a Comparator is supplied;
// but if they do (like SongV3), that compareTo() is *not used* for this call.
class ArtistCompare implements Comparator<SongV3> {
    public int compare(SongV3 one, SongV3 two) {
        // Delegate to String’s compareTo(), which knows alphabetical order.
        // This is case-sensitive lexicographic comparison ("Cassidy" != "cassidy").
        return one.getArtist().compareTo(two.getArtist());
    }
}
