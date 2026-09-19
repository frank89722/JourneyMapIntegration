package me.frankv.jmi.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileHelper<T> {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private final Class<T> classOfT;
    @Getter private final File file;
    private final File fileDir;

    public FileHelper(String filePath, Class<T> classOfT) {
        String userDir = System.getProperty("user.dir");
        this.file = new File(userDir + filePath);
        this.fileDir = new File(userDir + filePath.substring(0, filePath.lastIndexOf("/")));
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
        if (!fileDir.exists()) fileDir.mkdirs();
        try (final var fileWriter = new FileWriter(file)) {
            gson.toJson(object, fileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
