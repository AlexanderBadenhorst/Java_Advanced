package JavaSongLibraryApp.persistence;

import JavaSongLibraryApp.model.Song;
import JavaSongLibraryApp.model.SongLibrary;
import JavaSongLibraryApp.util.ValidationException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Handles persistence of the library to a local file using Java serialization.
 * The file is a binary .ser file stored under the user's home directory.
 */
public class LibraryStorage {
    private final Path file; // Where we read and write the serialized data

    public LibraryStorage(Path file) {
        this.file = file;
    }

    /**
     * Saves the given list of songs to disk.
     * Wraps IOExceptions into a checked ValidationException so callers must handle it.
     */
    public void save(List<Song> songs) throws ValidationException {
        try {
            // Ensure the directory exists, so file output does not fail due to missing parent folders.
            Files.createDirectories(file.getParent());
        } catch (IOException e) {
            throw new ValidationException("Could not create directory", e);
        }

        // Try-with-resources guarantees the stream is closed even if an exception is thrown.
        try (ObjectOutputStream out = new ObjectOutputStream(
                new BufferedOutputStream(Files.newOutputStream(file)))) {
            out.writeObject(songs); // Serialize the entire list
        } catch (IOException e) {
            throw new ValidationException("Failed to save library", e);
        }
    }

    /**
     * Loads songs from disk into a new SongLibrary.
     * If the file does not exist, returns an empty library.
     */
    @SuppressWarnings("unchecked") // We know we wrote a List<Song>, so this cast is safe here.
    public SongLibrary load() throws ValidationException {
        // First-run scenario. Nothing saved yet, so return an empty library.
        if (!Files.exists(file)) return new SongLibrary();

        // Read the serialized list and reconstruct the library.
        try (ObjectInputStream in = new ObjectInputStream(
                new BufferedInputStream(Files.newInputStream(file)))) {
            Object obj = in.readObject();     // Expecting a List<Song>
            SongLibrary lib = new SongLibrary();
            for (Song s : (List<Song>) obj) {
                lib.add(s);
            }
            return lib;
        } catch (IOException | ClassNotFoundException e) {
            // Convert low-level exceptions to a domain-specific checked exception.
            throw new ValidationException("Failed to load library", e);
        }
    }
}
