package com.ga.airticketmanagement.service;




import com.ga.airticketmanagement.exception.InformationNotFoundException;
import com.ga.airticketmanagement.model.Booking;
import com.ga.airticketmanagement.model.ImageEntity;
import com.ga.airticketmanagement.repository.ImageRepository;
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


    @Autowired
    private ImageRepository imageRepository;


    @Value("${file.upload-dir}")
    private String uploadDir;


    public ImageEntity saveImage(MultipartFile file, Long userId) throws IOException {


        String originalFileName = file.getOriginalFilename();


        String contentType = file.getContentType();


        long fileSize = file.getSize();


        String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFileName;


        File uploadDirectory = new File(uploadDir);

        if (!uploadDirectory.exists()) {
            uploadDirectory.mkdirs();
        }


        Path filePath = Paths.get(uploadDir, uniqueFileName);


        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);


        ImageEntity imageEntity = new ImageEntity();

        imageEntity.setFileName(uniqueFileName);

        imageEntity.setOriginalFileName(originalFileName);

        imageEntity.setFileType(contentType);

        imageEntity.setFileSize(fileSize);

        imageEntity.setFilePath(filePath.toString());

        imageEntity.setUserId(userId);

        return imageRepository.save(imageEntity);
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