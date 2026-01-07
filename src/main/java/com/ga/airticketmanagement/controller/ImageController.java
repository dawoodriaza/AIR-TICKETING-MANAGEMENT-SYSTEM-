package com.ga.airticketmanagement.controller;



import com.ga.airticketmanagement.model.ImageEntity;
import com.ga.airticketmanagement.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/images")
public class ImageController {


    @Autowired
    private ImageService imageService;


    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file,
    @RequestParam(value = "userId", required = false) Long userId) {


        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Please select a file to upload");
        }

        // Get content type to validate file is an image
        String contentType = file.getContentType();


        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Only image files are allowed");
        }

        try {

            ImageEntity savedImage = imageService.saveImage(file, userId);

            Map<String, Object> response = new HashMap<>();
            response.put("id", savedImage.getId());
            response.put("userId", savedImage.getUserId());
            response.put("fileName", savedImage.getFileName());
            response.put("originalFileName", savedImage.getOriginalFileName());
            response.put("fileType", savedImage.getFileType());
            response.put("fileSize", savedImage.getFileSize());
            response.put("uploadedAt", savedImage.getUploadedAt());
            response.put("message", "Image uploaded successfully");


            return ResponseEntity.ok(response);

        } catch (IOException e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload image: " + e.getMessage());
        }
    }


    @GetMapping("/{fileName}")

    public ResponseEntity<?> getImageInfo(@PathVariable String fileName) {


        ImageEntity image = imageService.getImageByFileName(fileName);


        if (image == null) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Image not found with fileName: " + fileName);
        }


        return ResponseEntity.ok(image);
    }


    @GetMapping("/all")
    public ResponseEntity<List<ImageEntity>> getAllImages() {

        List<ImageEntity> images = imageService.getAllImages();


        return ResponseEntity.ok(images);
    }


    @GetMapping("/download/{fileName}")
    public ResponseEntity<?> downloadImage(@PathVariable String fileName) {

        try {

            byte[] imageBytes = imageService.getImageFile(fileName);


            if (imageBytes == null) {

                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Image not found");
            }


            ImageEntity image = imageService.getImageByFileName(fileName);


            HttpHeaders headers = new HttpHeaders();

            headers.setContentType(MediaType.parseMediaType(image.getFileType()));


            headers.setContentDispositionFormData("inline", image.getOriginalFileName());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(imageBytes);

        } catch (IOException e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve image: " + e.getMessage());
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteImage(@PathVariable Long id) {

        boolean deleted = imageService.deleteImage(id);


        if (deleted) {

            return ResponseEntity.ok("Image deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Image not found with id: " + id);
        }
    }
}