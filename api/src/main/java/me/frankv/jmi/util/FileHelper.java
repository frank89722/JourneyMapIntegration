package me.frankv.jmi.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Utility class for reading and writing objects to and from JSON files.
 * <p>
 * This class provides a type-safe way to serialize and deserialize objects to and from JSON files
 * using Gson. It handles file creation, directory creation, and error handling.
 *
 * @param <T> The type of object to read from or write to the file
 */
public class FileHelper<T> {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private final Class<T> classOfT;
    /**
     * The file to read from or write to.
     */
    @Getter private final File file;
    private final File fileDir;

    /**
     * Creates a new FileHelper for the specified file path and object type.
     *
     * @param filePath The path to the file, relative to the user directory
     * @param classOfT The class of the object to read from or write to the file
     */
    public FileHelper(String filePath, Class<T> classOfT) {
        String userDir = System.getProperty("user.dir");
        this.file = new File(userDir + filePath);
        this.fileDir = new File(userDir + filePath.substring(0, filePath.lastIndexOf("/")));
        this.classOfT = classOfT;
    }

    /**
     * Reads an object from the JSON file.
     * <p>
     * This method deserializes the JSON content of the file into an object of type T.
     * If the file does not exist or cannot be read, null is returned.
     *
     * @return The deserialized object, or null if the file could not be read
     */
    public T read() {
        try (final var fileReader = new FileReader(file)) {
            return gson.fromJson(fileReader, classOfT);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Writes an object to the JSON file.
     * <p>
     * This method serializes the object to JSON and writes it to the file.
     * If the file's directory does not exist, it will be created.
     * If the file cannot be written to, an error message is printed to the console.
     *
     * @param object The object to write to the file
     */
    public void write(T object) {
        if (!fileDir.exists()) fileDir.mkdirs();
        try (final var fileWriter = new FileWriter(file)) {
            gson.toJson(object, fileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
