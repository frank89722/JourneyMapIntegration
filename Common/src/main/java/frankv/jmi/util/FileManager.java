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

    @Getter
    private File file;

    public FileManager(String filePath) {
        this.file = new File(System.getProperty("user.dir") + filePath);
    }

    public T read(Class<T> clazz) {
        try (var fileReader = new FileReader(file)) {
            return gson.fromJson(fileReader, clazz);
        } catch (IOException e) {
            return null;
        }
    }

    public void write(T object) {
        try (var fileWriter = new FileWriter(file)) {
            gson.toJson(object, fileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
