package com.example.shoes_shop.service;

import com.example.shoes_shop.dto.*;
import com.example.shoes_shop.entity.Product;
import com.example.shoes_shop.entity.ProductSize;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    //Lấy sản phẩm
    Page<Product> adminGetListProduct(String id, String name, String category, String brand, Integer page);

    //Tạo sản phẩm
    Product createProduct(CreateProductRequest createProductRequest);

    //Lấy chi tiết sản phẩm
    Product getProductById(String id);

    List<ShortProductInfoDTO> getListProduct();

    //Lấy chi tiết sản phẩm theo id
    DetailProductInfoDTO getDetailProductById(String id);

    //Lấy size của sản phẩm
    List<ProductSize> getListSizeOfProduct(String id);

    //Nhập số lượng theo size
    Integer createSizeCount(CreateSizeCountRequest createSizeCountRequest);

    //Sửa sản phẩm
    void updateProduct(CreateProductRequest createProductRequest,String id);

    //Thêm sản phẩm bằng file
    void importProducts(MultipartFile file);

    //Lấy sản phẩm liên quan
    List<ProductInfoDTO> getRelatedProducts(String id);


    //Kiểm tra sản phẩm có khuyến mại
//    List<ProductInfoDTO> checkPublicPromotion(List<ProductInfoDTO> products);

    //Lấy sản phẩm có sẵn size
    List<ShortProductInfoDTO> getAvailableProducts();
    //Lấy sản phẩm mới nhất
    List<ProductInfoDTO> getListNewProducts();

    //Lấy sản phẩm bán nhiều nhất
    List<ProductInfoDTO> getListBestSellProducts();

    //Lấy sản phẩm xem nhiều
    List<ProductInfoDTO> getListViewProducts();
    //Lấy size có sẵn
    List<ProductSizeDTO> getListAvailableSize(String id);

    //Tìm kiếm sản phẩm theo danh mục, nhãn hiệu, giá
    PageableDTO filterProduct(FilterProductRequest req);

    //Đếm số lượng sản phẩm
    Integer getCountProduct();

    List<ProductSizeDTO> getAllProduct();
    //Check size sản phẩm
    Boolean checkProductSizeAvailable(String id, int size);

    //Tìm kiếm sản phẩm theo tên sản phẩm
    PageableDTO searchProductByKeyword(String keyword, Integer page);
}
