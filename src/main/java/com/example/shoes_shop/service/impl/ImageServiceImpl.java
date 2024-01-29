package com.example.shoes_shop.service.impl;

import com.example.shoes_shop.entity.Image;
import com.example.shoes_shop.exception.BadRequestException;
import com.example.shoes_shop.exception.InternalServerException;
import com.example.shoes_shop.repository.ImageRepository;
import com.example.shoes_shop.service.ImageService;
import com.example.shoes_shop.util.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ImageServiceImpl implements ImageService {
    private final ImageRepository imageRepository;

    @Override
    public void saveImage(Image image) {
        imageRepository.save(image);
    }

    @Override
    public void deleteImage(String[] fileNames) {

        for (String fileName : fileNames) {
            //Lấy đường dẫn file
            String link = "/media/static/" + fileName;
            //Kiểm tra link
            Image image = imageRepository.findByLink(link);
            if (image == null) {
                throw new BadRequestException("File không tồn tại");
            }
            //Kiểm tra ảnh đã được dùng
            Integer inUse = imageRepository.checkImageInUse(link);
            if (inUse != null) {
                throw new BadRequestException("Ảnh đã được sử dụng không thể xóa!");
            }
            //Xóa file trong databases
            imageRepository.delete(image);
            FileUtil.checkImageInFolder(fileName);
        }


    }

    @Override
    public List<String> getListImageOfUser(UUID userId) {
        return imageRepository.getListImageOfUser(userId);
    }

    ;
}
