package app.persistence;

import app.model.Song;
import app.model.SongLibrary;
import app.util.ValidationException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class LibraryStorage {
    private final Path file;

    public LibraryStorage(Path file) {
        this.file = file;
    }

    public void save(List<Song> songs) throws ValidationException {
        try {
            Files.createDirectories(file.getParent());
        } catch (IOException e) {
            throw new ValidationException("Could not create directory", e);
        }

        try (ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(Files.newOutputStream(file)))) {
            out.writeObject(songs);
        } catch (IOException e) {
            throw new ValidationException("Failed to save library", e);
        }
    }

    @SuppressWarnings("unchecked")
    public SongLibrary load() throws ValidationException {
        if (!Files.exists(file)) return new SongLibrary();

        try (ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(Files.newInputStream(file)))) {
            Object obj = in.readObject();
            SongLibrary lib = new SongLibrary();
            for (Song s : (List<Song>) obj) {
                lib.add(s);
            }
            return lib;
        } catch (IOException | ClassNotFoundException e) {
            throw new ValidationException("Failed to load library", e);
        }
    }
}
