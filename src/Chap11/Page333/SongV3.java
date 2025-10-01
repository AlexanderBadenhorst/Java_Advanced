package Chap11.Page333;

// Making SongV3 Comparable defines its *natural order*.
// Per the book page: if you call Collections.sort(list) with NO Comparator,
// Java uses this compareTo() to order the elements.
class SongV3 implements Comparable<SongV3> {
    private String title;
    private String artist;
    private int bpm;

    // Natural ordering for SongV3: by TITLE.
    // We just reuse String's compareTo(), which already knows how to alphabetize.
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

    // Printing a song prints its title; that’s why the console shows only titles
    public String toString() {
        return title;
    }
}
