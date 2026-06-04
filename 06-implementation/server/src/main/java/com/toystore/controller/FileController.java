package com.toystore.controller;

import com.toystore.dto.FileUploadResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "*")
public class FileController {

    private final String uploadDir = "src/main/resources/static/images/";

    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload image file", description = "Uploads an image file and returns its URL")
    public ResponseEntity<FileUploadResponse> uploadImage(
                                                            @Parameter(
                                                                    description = "Image file to upload",
                                                                    required = true,
                                                                    content = @Content(
                                                                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                                                                            schema = @Schema(type = "string", format = "binary")
                                                                    )
                                                            )
                                                            @RequestParam("image") MultipartFile file) throws IOException {

        System.out.println("📥 Upload request received: " + file.getOriginalFilename());
        System.out.println("📥 File size: " + file.getSize() + " bytes");
        System.out.println("📥 Content-Type: " + file.getContentType());

        Path uploadPath = Paths.get(uploadDir);
        System.out.println("📁 Upload directory: " + uploadPath.toAbsolutePath());

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            System.out.println("📁 Created directory: " + uploadPath);
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            System.err.println("❌ Filename is null or empty!");
            return ResponseEntity.badRequest().body(null);
        }

        String fileName = UUID.randomUUID().toString() + "_" +
                originalFilename.replaceAll("\\s+", "_");

        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        System.out.println("✅ File saved: " + filePath);

        String imageUrl = "images/" + fileName;


        return ResponseEntity.ok(new FileUploadResponse(imageUrl));
    }
}