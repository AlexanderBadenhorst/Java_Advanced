package JavaSongLibraryApp.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Immutable value object that represents a Song.
 * Implements Comparable for natural ordering by title, and Serializable so it can be saved to disk.
 */
public class Song implements Comparable<Song>, Serializable {
    private static final long serialVersionUID = 1L; // Serialization version for compatibility

    // Immutable fields define the state of a Song
    private final String title;
    private final String artist;
    private final int year;
    private final int rating; // Allowed range 1..5

    /**
     * Validates inputs and creates an immutable Song.
     * Throws IllegalArgumentException if any input is invalid.
     */
    public Song(String title, String artist, int year, int rating) {
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Title required");
        if (artist == null || artist.isBlank()) throw new IllegalArgumentException("Artist required");
        if (year < 1900 || year > 2100) throw new IllegalArgumentException("Year out of range");
        if (rating < 1 || rating > 5) throw new IllegalArgumentException("Rating must be 1..5");

        // Trim to keep data clean and deterministic for equality and display
        this.title = title.trim();
        this.artist = artist.trim();
        this.year = year;
        this.rating = rating;
    }

    // Simple getters. No setters to keep the object immutable.
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public int getYear() { return year; }
    public int getRating() { return rating; }

    /**
     * Natural ordering. Sorts by title, case insensitive.
     * This allows Collections.sort(listOfSongs) to work without an explicit Comparator.
     */
    @Override
    public int compareTo(Song other) {
        return this.title.compareToIgnoreCase(other.title);
    }

    /**
     * Equality is based on title and artist, case insensitive.
     * This makes sense for de-duplicating songs in sets or when removing by object reference.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;                // Same object in memory
        if (!(o instanceof Song)) return false;    // Not even a Song
        Song song = (Song) o;
        return title.equalsIgnoreCase(song.title)
                && artist.equalsIgnoreCase(song.artist);
    }

    /**
     * Hash code must agree with equals.
     * Use lower-cased values to match equals' case-insensitive logic.
     */
    @Override
    public int hashCode() {
        return Objects.hash(title.toLowerCase(), artist.toLowerCase());
    }

    /**
     * Human-readable representation used by the JList to render items.
     */
    @Override
    public String toString() {
        // Feel free to change the separator to "-" if you prefer.
        return String.format("%s — %s, %d, %d★", title, artist, year, rating);
    }
}
