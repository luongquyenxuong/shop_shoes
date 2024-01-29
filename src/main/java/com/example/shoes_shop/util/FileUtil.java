package com.example.shoes_shop.util;

import com.example.shoes_shop.entity.Image;
import com.example.shoes_shop.exception.BadRequestException;
import com.example.shoes_shop.exception.InternalServerException;
import com.example.shoes_shop.exception.NotFoundException;
import com.example.shoes_shop.security.CustomUserDetails;
import com.example.shoes_shop.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class FileUtil {
//    private static String UPLOAD_DIR ="/media/upload";
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "\\src\\main\\resources\\media\\upload\\";

    public static String getResourceBasePath() {
        File path = null;
        try {
            path = new File(ResourceUtils.getURL("classpath:").getPath());
        } catch (FileNotFoundException e) {

        }
        if (path == null || !path.exists()) {
            path = new File("");
        }
        String pathStr = path.getAbsolutePath();
        pathStr = pathStr.replace("\\target\\classes", "");
        return pathStr;
    }
    public static Image uploadFile(MultipartFile multipartFile){

        //Tạo thư mục chứa ảnh nếu không tồn tại
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        System.out.println(UPLOAD_DIR);
        //Lấy tên file và đuôi mở rộng của file
        String originalFilename = multipartFile.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        if (originalFilename.length() > 0) {

            //Kiểm tra xem file có đúng định dạng không
            if (!extension.equals("png") && !extension.equals("jpg") && !extension.equals("gif") && !extension.equals("svg") && !extension.equals("jpeg")) {
                throw new BadRequestException("Không hỗ trợ định dạng file này!");
            }
            try {
                Image image = new Image();
                image.setId(UUID.randomUUID().toString());
                image.setName(multipartFile.getName());
                image.setSize(multipartFile.getSize());
                image.setType(extension);
                String link = "/media/static/" + image.getId() + "." + extension;
                image.setLink(link);
                image.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                image.setCreatedBy(((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser());

                //Tạo file
                File serveFile = new File(UPLOAD_DIR + "/" + image.getId() + "." + extension);
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(serveFile));
                bos.write(multipartFile.getBytes());
                bos.close();


                return image;

            } catch (Exception e) {
                throw new InternalServerException("Có lỗi trong quá trình upload file!");
            }
        }
        throw new BadRequestException("File không hợp lệ!");
    }

    public static Map<String, Object> downloadFile(String filename){
        File file = new File(UPLOAD_DIR + "/" + filename);
        if (!file.exists()) {
            throw new NotFoundException("File không tồn tại!");
        }

        UrlResource resource;
        try {
            Map<String, Object> result = new HashMap<>();
            resource = new UrlResource(file.toURI());
            result.put("File", file);
            result.put("Resource", resource);
            return result;
        } catch (MalformedURLException ex) {
            throw new NotFoundException("File không tồn tại!");
        }
    }

    public static void checkImageInFolder(String filename){
        //Kiểm tra file có tồn tại trong thư mục
        File file = new File(UPLOAD_DIR + "/" + filename);
        if (file.exists()) {
            //Xóa file ở thư mục
            if (!file.delete()) {
                throw new InternalServerException("Lỗi khi xóa ảnh!");
            }
        }
    }

}
