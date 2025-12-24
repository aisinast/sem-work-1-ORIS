package ru.itis.spotty.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public class FileService {
    private final String uploadPath;

    public FileService(String uploadPath) {
        this.uploadPath = uploadPath;

        try {
            Files.createDirectories(Paths.get(uploadPath));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    public String saveFile(InputStream inputStream, String originFileName) throws IOException {
        String fileExtension = getFileExtension(originFileName);
        String fileName = UUID.randomUUID() + fileExtension;
        Path filePath = Paths.get(uploadPath, fileName);

        Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);

        return "/uploads/" + fileName;
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex);
    }
}
