package Chap11.Page315;

import java.util.*;

public class Jukebox1 {
    public static void main(String[] args) {
        new Jukebox1().go();
    }

    public void go() {
        List<String> songList = MockSongs.getSongStrings();
        System.out.println(songList);
        Collections.sort(songList);//sorting songs using the 'natural order' (alphabetical(special characters, numbers, capital letters, lower case letters))
        System.out.println(songList);
    }
}

