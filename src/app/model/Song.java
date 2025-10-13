package app.model;

import java.io.Serializable;
import java.util.Objects;

public class Song implements Comparable<Song>, Serializable {
    private static final long serialVersionUID = 1L;

    private final String title;
    private final String artist;
    private final int year;
    private final int rating; // 1..5

    public Song(String title, String artist, int year, int rating) {
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Title required");
        if (artist == null || artist.isBlank()) throw new IllegalArgumentException("Artist required");
        if (year < 1900 || year > 2100) throw new IllegalArgumentException("Year out of range");
        if (rating < 1 || rating > 5) throw new IllegalArgumentException("Rating must be 1..5");

        this.title = title.trim();
        this.artist = artist.trim();
        this.year = year;
        this.rating = rating;
    }

    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public int getYear() { return year; }
    public int getRating() { return rating; }

    // Natural order by title, case insensitive
    @Override
    public int compareTo(Song other) {
        return this.title.compareToIgnoreCase(other.title);
    }

    // Equality by title and artist, good for Set, distinct lists
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Song)) return false;
        Song song = (Song) o;
        return title.equalsIgnoreCase(song.title) && artist.equalsIgnoreCase(song.artist);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title.toLowerCase(), artist.toLowerCase());
    }

    @Override
    public String toString() {
        return String.format("%s — %s, %d, %d★", title, artist, year, rating);
    }
}
