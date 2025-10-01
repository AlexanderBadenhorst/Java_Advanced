package Chap11.Page342;

// SongV3 still implements Comparable by title (natural order).
// But here we IGNORE compareTo() and use lambdas instead for sorting.
// This demonstrates the flexibility of Comparator lambdas in Java 8+.
class SongV3 implements Comparable<SongV3> {
    private String title;
    private String artist;
    private int bpm;

    // Defines natural ordering: by title.
    public int compareTo(SongV3 s) {
        return title.compareTo(s.getTitle());
    }

    SongV3(String title, String artist, int bpm) {
        this.title = title;
        this.artist = artist;
        this.bpm = bpm;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public int getBpm() {
        return bpm;
    }

    // For easy printing: just show the song title.
    public String toString() {
        return title;
    }
}
