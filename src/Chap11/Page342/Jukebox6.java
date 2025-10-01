package Chap11.Page342;

import java.util.*;

public class Jukebox6 {
    public static void main(String[] args) {
        new Jukebox6().go();
    }

    public void go() {
        // Get a list of SongV3 objects from the mock factory
        List<SongV3> songList = MockSongs.getSongsV3();

        System.out.println("Original List: " + songList);

        // --- Sort by TITLE using a lambda expression ---
        // Instead of writing a separate Comparator class,
        // we declare the comparison logic inline.
        //
        // (one, two) -> one.getTitle().compareTo(two.getTitle())
        //   - "one" and "two" are the two SongV3 objects being compared
        //   - compareTo() comes from String, which knows alphabetical order
        //
        // This lambda is equivalent to new TitleCompare(), but much shorter.
        songList.sort((one, two) -> one.getTitle().compareTo(two.getTitle()));
        System.out.println("Sorted by Title: " + songList);

        // --- Sort by ARTIST using a lambda expression ---
        // Same idea, but now the lambda compares the artist field instead of the title.
        //
        // This replaces the need for a separate ArtistCompare class.
        songList.sort((one, two) -> one.getArtist().compareTo(two.getArtist()));
        System.out.println("Sorted by Artist: " + songList);
    }
}
