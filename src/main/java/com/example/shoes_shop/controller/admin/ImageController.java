package com.example.shoes_shop.controller.admin;

import com.example.shoes_shop.dto.ResponseDTO;
import com.example.shoes_shop.entity.Image;
import com.example.shoes_shop.service.ImageService;
import com.example.shoes_shop.util.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ImageController {


    private final ImageService imageService;

    @PostMapping("/api/upload/files")
    public ResponseEntity<Object> uploadFile(@RequestParam("file") MultipartFile file) {
        Image image = FileUtil.uploadFile(file);
        imageService.saveImage(image);
        return ResponseEntity.ok(image.getLink());
    }

    @GetMapping("/media/static/{filename:.+}")
    public ResponseEntity<Object> downloadFile(@PathVariable String filename) {
        Map<String, Object> result = FileUtil.downloadFile(filename);

        File file = (File) result.get("File");


        UrlResource resource = (UrlResource) result.get("Resource");


        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .body(resource);
    }

    @DeleteMapping("/api/delete-image")
    public ResponseEntity<Object> deleteImage(@RequestBody String[] fileNames) {
        imageService.deleteImage(fileNames);

        return ResponseEntity.ok("Xóa file thành công!");
    }
}
