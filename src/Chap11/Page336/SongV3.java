package Chap11.Page336;

// SongV3 still implements Comparable by TITLE (natural order).
// But in Jukebox5, we IGNORE this and use explicit Comparators instead.
// This is to avoid the confusion of having two different sorting mechanisms.
class SongV3 implements Comparable<SongV3> {
    private String title;
    private String artist;
    private int bpm;

    // Natural order (Comparable): by title.
    // NOTE: Not actually used in Jukebox5, but still here for completeness.
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

    // Print the title only when printing the object.
    public String toString() {
        return title;
    }
}
