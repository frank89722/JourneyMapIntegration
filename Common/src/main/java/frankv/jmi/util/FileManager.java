package frankv.jmi.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileManager<T> {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private final Class<T> classOfT;
    @Getter
    private final File file;

    public FileManager(String filePath, Class<T> classOfT) {
        this.file = new File(System.getProperty("user.dir") + filePath);
        this.classOfT = classOfT;
    }

    public T read() {
        try (final var fileReader = new FileReader(file)) {
            return gson.fromJson(fileReader, classOfT);
        } catch (IOException e) {
            return null;
        }
    }

    public void write(T object) {
        try (final var fileWriter = new FileWriter(file)) {
            gson.toJson(object, fileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
