package com.ga.airticketmanagement.service;




import com.ga.airticketmanagement.exception.InformationNotFoundException;
import com.ga.airticketmanagement.model.ImageEntity;
import com.ga.airticketmanagement.repository.ImageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;


@Service
public class ImageService {

    private static final Logger logger = LoggerFactory.getLogger(ImageService.class);

    @Autowired
    private ImageRepository imageRepository;


    @Value("${file.upload-dir}")
    private String uploadDir;


    public ImageEntity saveImage(MultipartFile file, Long userId) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty or null");
        }

        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.isBlank()) {
            throw new IllegalArgumentException("File name is null or empty");
        }

        String contentType = file.getContentType();
        long fileSize = file.getSize();

        String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFileName;

        // Normalize the upload directory path
        // Handle absolute paths starting with / on Windows
        Path uploadDirectoryPath;
        if (uploadDir.startsWith("/") && !uploadDir.startsWith("//")) {
            // Unix-style absolute path
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                // On Windows, convert /assets/uploads to a relative path from project root
                // or use it as-is if it's meant to be in the project directory
                // For now, we'll make it relative to the current working directory
                String projectRoot = System.getProperty("user.dir");
                uploadDirectoryPath = Paths.get(projectRoot, uploadDir.substring(1)).toAbsolutePath().normalize();
                logger.info("Windows detected. Converting path from {} to {}", uploadDir, uploadDirectoryPath);
            } else {
                // On Unix/Linux/Mac, use as absolute path
                uploadDirectoryPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            }
        } else {
            // Relative path or Windows absolute path (C:\...)
            uploadDirectoryPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        }
        
        File uploadDirectory = uploadDirectoryPath.toFile();

        // Create directory if it doesn't exist
        if (!uploadDirectory.exists()) {
            boolean created = uploadDirectory.mkdirs();
            if (!created) {
                throw new IOException("Failed to create upload directory: " + uploadDirectoryPath);
            }
            logger.info("Created upload directory: {}", uploadDirectoryPath);
        }

        // Check if directory is actually a directory and writable
        if (!uploadDirectory.isDirectory()) {
            throw new IOException("Upload path exists but is not a directory: " + uploadDirectoryPath);
        }
        if (!uploadDirectory.canWrite()) {
            throw new IOException("Upload directory is not writable: " + uploadDirectoryPath);
        }

        // Create the full file path
        Path filePath = uploadDirectoryPath.resolve(uniqueFileName);
        logger.info("Saving file to: {}", filePath);

        // Copy the file
        try {
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            logger.info("File saved successfully: {}", filePath);
        } catch (IOException e) {
            logger.error("Failed to save file to: {}", filePath, e);
            throw new IOException("Failed to save file: " + e.getMessage(), e);
        }

        // Verify file was actually saved
        if (!Files.exists(filePath)) {
            throw new IOException("File was not saved. Path: " + filePath);
        }


        ImageEntity imageEntity = new ImageEntity();

        imageEntity.setFileName(uniqueFileName);
        imageEntity.setOriginalFileName(originalFileName);
        imageEntity.setFileType(contentType);
        imageEntity.setFileSize(fileSize);
        imageEntity.setFilePath(filePath.toString());
        imageEntity.setUserId(userId);

        ImageEntity savedEntity = imageRepository.save(imageEntity);
        logger.info("ImageEntity saved to database with ID: {}", savedEntity.getId());
        
        return savedEntity;
    }


    public ImageEntity getImageByFileName(String fileName) {
        if (fileName == null) {
            throw new IllegalArgumentException("Request body is missing");
        }
        return imageRepository.findByFileName(fileName).orElse(null);
    }


    public List<ImageEntity> getAllImages() {

        return imageRepository.findAll();
    }


    public boolean deleteImage(Long id) {

        ImageEntity imageEntity = imageRepository.findById(id).orElseThrow(()-> new InformationNotFoundException ("imageEntity with Id " + id + " not found"));

        imageRepository.findById(id).ifPresent(image -> {
            try {

                Path path = Paths.get(image.getFilePath());

                Files.deleteIfExists(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageRepository.deleteById(id);
        });

        return true;
    }


    public byte[] getImageFile(String fileName) throws IOException {

        ImageEntity image = getImageByFileName(fileName);

        if (image == null) {
            return null;
        }

        Path path = Paths.get(image.getFilePath());

        return Files.readAllBytes(path);
    }
}