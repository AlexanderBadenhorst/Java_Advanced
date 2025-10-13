package app.model;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SongLibrary {
    private final List<Song> songs = new ArrayList<>();

    public void add(Song s) { songs.add(s); }

    public List<Song> all() { return Collections.unmodifiableList(songs); }

    public List<Song> filtered(Predicate<Song> p) {
        return songs.stream().filter(p).collect(Collectors.toList());
    }

    public List<Song> sortedByTitle() {
        return songs.stream().sorted().collect(Collectors.toList());
    }

    public List<Song> sortedByArtist() {
        return songs.stream()
                .sorted(Comparator.comparing(Song::getArtist, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(Song::getTitle, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    public List<Song> sortedByYearDesc() {
        return songs.stream()
                .sorted(Comparator.comparingInt(Song::getYear).reversed()
                        .thenComparing(Song::getTitle, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    public List<Song> sortedByRatingDesc() {
        return songs.stream()
                .sorted(Comparator.comparingInt(Song::getRating).reversed()
                        .thenComparing(Song::getTitle, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }
}
