
package com.shareshelf.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@Service
public class BookImageStorageService {

    private final Path uploadDirectory;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    private static final Map<String, String> ALLOWED_TYPES = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png",
            "image/webp", ".webp"
    );

    public BookImageStorageService(
            @Value("${app.upload.directory:uploads}") String uploadDirectory) {
        this.uploadDirectory = Paths.get(uploadDirectory)
                .toAbsolutePath()
                .normalize()
                .resolve("books");
    }

    @PostConstruct
    public void initialize() {
        try {
            Files.createDirectories(uploadDirectory);
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Kitap görselleri klasörü oluşturulamadı.",
                    exception
            );
        }
    }

    public String store(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            return null;
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Kitap görseli en fazla 5 MB olabilir."
            );
        }

        String extension = ALLOWED_TYPES.get(file.getContentType());

        if (extension == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Yalnızca JPG, PNG ve WEBP görselleri yükleyebilirsiniz."
            );
        }

        String fileName = UUID.randomUUID() + extension;

        try {
            Path targetPath = uploadDirectory.resolve(fileName).normalize();

            if (!targetPath.startsWith(uploadDirectory)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Geçersiz dosya yolu."
                );
            }

            Files.copy(file.getInputStream(), targetPath);

            return "/uploads/books/" + fileName;

        } catch (IOException exception) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Kitap görseli kaydedilemedi."
            );
        }
    }
}
