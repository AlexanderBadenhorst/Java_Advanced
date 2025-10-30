package JavaSongLibraryApp.model;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * In-memory collection of songs with convenience query methods.
 * Keeps a private, mutable List and exposes read-only or copied views to callers.
 */
public class SongLibrary {
    // Internal storage. We keep it private to protect invariants.
    private final List<Song> songs = new ArrayList<>();

    /** Adds a song to the library. */
    public void add(Song s) { songs.add(s); }

    /**
     * Removes a song from the library.
     * Returns true if the item was found and removed.
     */
    public boolean remove(Song s) { return songs.remove(s); }

    /**
     * Read-only view of the list to prevent outside code from modifying internal state.
     * Callers can iterate, but cannot add or remove.
     */
    public List<Song> all() { return Collections.unmodifiableList(songs); }

    /**
     * Returns a new List with only the items that match the predicate.
     * Uses streams to keep the implementation concise and expressive.
     */
    public List<Song> filtered(Predicate<Song> p) {
        return songs.stream().filter(p).collect(Collectors.toList());
    }

    /** Returns a new List sorted by the Song's natural order (title). */
    public List<Song> sortedByTitle() {
        return songs.stream().sorted().collect(Collectors.toList());
    }

    /** Returns a new List sorted by artist, then title, both case insensitive. */
    public List<Song> sortedByArtist() {
        return songs.stream()
                .sorted(Comparator.comparing(Song::getArtist, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(Song::getTitle, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    /** Returns a new List sorted by year descending, then title. */
    public List<Song> sortedByYearDesc() {
        return songs.stream()
                .sorted(Comparator.comparingInt(Song::getYear).reversed()
                        .thenComparing(Song::getTitle, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    /** Returns a new List sorted by rating descending, then title. */
    public List<Song> sortedByRatingDesc() {
        return songs.stream()
                .sorted(Comparator.comparingInt(Song::getRating).reversed()
                        .thenComparing(Song::getTitle, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }
}
