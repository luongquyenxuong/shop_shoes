package com.example.shoes_shop.service.impl;

import com.example.shoes_shop.constant.AttributeType;
import com.example.shoes_shop.constant.Constant;
import com.example.shoes_shop.constant.StatusProductSupplier;
import com.example.shoes_shop.dto.*;
import com.example.shoes_shop.entity.*;
import com.example.shoes_shop.entity.keys.SizeId;
import com.example.shoes_shop.exception.BadRequestException;
import com.example.shoes_shop.exception.InternalServerException;
import com.example.shoes_shop.exception.NotFoundException;
import com.example.shoes_shop.mapper.ProductMapper;
import com.example.shoes_shop.repository.*;
import com.example.shoes_shop.service.ProductService;
import com.example.shoes_shop.util.PageUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.slugify.Slugify;
import com.google.gson.Gson;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;


    private final ProductSizeRepository productSizeRepository;

    private final BrandRepository brandRepository;

    private final CategoryRepository categoryRepository;

    private final SupplierRepository supplierRepository;

    private final SuppliersProductRepository suppliersProductRepository;

    @Override
    public Page<Product> adminGetListProduct(String id, String name, String category, String brand, Integer page) {
        page--;
        if (page < 0) {
            page = 0;
        }
        Pageable pageable = PageRequest.of(page, Constant.LIMIT_PRODUCT);
        return productRepository.adminGetListProducts(id, name, category, brand, pageable);
    }

    @Override
    public Product createProduct(CreateProductRequest createProductRequest) {


        Product checkNameProduct=productRepository.findByName(createProductRequest.getName().trim());
        if(checkNameProduct !=null){
            throw new BadRequestException("Tên giày bị trùng");
        }

        //Kiểm tra có danh muc
        if (createProductRequest.getCategoryIds().isEmpty()) {
            throw new BadRequestException("Danh mục trống!");
        }
        //Kiểm tra có ảnh sản phẩm
        if (createProductRequest.getImages().isEmpty()) {
            throw new BadRequestException("Ảnh sản phẩm trống!");
        }
        //Kiểm tra có mã lô hàng
        if (createProductRequest.getCodeShipment().isEmpty()) {
            throw new BadRequestException("Mã lô hàng sản phẩm trống!");
        }



        Product product = ProductMapper.toProduct(createProductRequest);

        //Sinh id
        String id = RandomStringUtils.randomAlphanumeric(6);
        product.setId(id);
        product.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        product.setView(0);

        //Supplier
        SuppliersProduct sp = SuppliersProduct.builder()
                .codeShipment(createProductRequest.getCodeShipment())
                .entryPrice(product.getPrice())
                .supplier(Supplier.builder()
                        .id(createProductRequest.getSupplierId())
                        .build())
                .product(Product.builder()
                        .id(product.getId())
                        .build())
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .status(StatusProductSupplier.NOT_YET_IMPORTED.name())
                .build();


//        product.setProductSuppliers(suppliersProduct);

        try {
            productRepository.save(product);
            suppliersProductRepository.save(sp);
        } catch (Exception ex) {
            throw new InternalServerException("Lỗi khi thêm sản phẩm");
        }
        return product;
    }

    @Override
    public List<ProductSize> getListSizeOfProduct(String id) {
        return productSizeRepository.findByProductId(id);
    }

    @Override
    public Integer createSizeCount(CreateSizeCountRequest createSizeCountRequest) {
        boolean isValid = false;
        for (int size : Constant.SIZE_VN) {
            if (size == createSizeCountRequest.getSize()) {
                isValid = true;
                break;
            }
        }
        if (!isValid) {
            throw new BadRequestException("Size không hợp lệ");
        }
        //Kiểm trả sản phẩm có tồn tại
        Optional<Product> product = productRepository.findById(createSizeCountRequest.getProductId());
        if (product.isEmpty()) {
            throw new NotFoundException("Không tìm thấy sản phẩm trong hệ thống!");
        }
        Optional<ProductSize> optionalProductSize = productSizeRepository.findById(new SizeId(product.get().getId(), createSizeCountRequest.getSize()));
        ProductSize productSize = new ProductSize();
        if (optionalProductSize.isEmpty()) {
            //Lưu vào bảng product_size nêu chưa có size

            productSize.setProductId(createSizeCountRequest.getProductId());
            productSize.setSize(createSizeCountRequest.getSize());
            productSize.setQuantity(createSizeCountRequest.getCount());
            productSize.setTotalSold(0L);

            productSizeRepository.save(productSize);

        } else {
            //Cập nhật số lượng
            Integer quantity = optionalProductSize.get().getQuantity() + createSizeCountRequest.getCount();
            optionalProductSize.get().setQuantity(quantity);
            productSize = productSizeRepository.save(optionalProductSize.get());
        }


        //Cập nhật bảng suppliers_product
        SuppliersProduct suppliersProduct = product.get().getProductSuppliers().iterator().next();
        if (suppliersProduct.getSize() == null) {
            suppliersProduct.setSize(createSizeCountRequest.getSize());
            suppliersProduct.setQuantity(createSizeCountRequest.getCount());
            suppliersProduct.setModifiedAt(new Timestamp(System.currentTimeMillis()));
            suppliersProduct.setStatus(StatusProductSupplier.IMPORTED.name());
            suppliersProductRepository.save(suppliersProduct);
            return productSize.getQuantity();
        }

        suppliersProductRepository.save(SuppliersProduct.builder()
                .codeShipment(product.get().getProductSuppliers().iterator().next().getCodeShipment())
                .size(createSizeCountRequest.getSize())
                .quantity(createSizeCountRequest.getCount())
                .modifiedAt(new Timestamp(System.currentTimeMillis()))
                .entryPrice(product.get().getPrice())
                .product(product.get())
                .supplier(product.get().getProductSuppliers().iterator().next().getSupplier())
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .status(StatusProductSupplier.IMPORTED.name())
                .build());
        return productSize.getQuantity();

    }

    @Override
    public void updateProduct(CreateProductRequest createProductRequest, String id) {
        //Kiểm tra sản phẩm có tồn tại
        Optional<Product> product = productRepository.findById(id);
        if (product.isEmpty()) {
            throw new NotFoundException("Không tìm thấy sản phẩm!");
        }

        //Kiểm tra có danh muc
        if (createProductRequest.getCategoryIds().isEmpty()) {
            throw new BadRequestException("Danh mục trống!");
        }

        //Kiểm tra có ảnh sản phẩm
        if (createProductRequest.getImages().isEmpty()) {
            throw new BadRequestException("Ảnh sản phẩm trống!");
        }

        Product result = ProductMapper.toProduct(createProductRequest);

        result.setId(id);
        result.setCreatedAt(product.get().getCreatedAt());
        result.setView(product.get().getView());
        result.setProductSuppliers(product.get().getProductSuppliers());
        result.setProductSizes(product.get().getProductSizes());
        result.setComments(product.get().getComments());
        result.setModifiedAt(new Timestamp(System.currentTimeMillis()));
        try {
            productRepository.save(result);
        } catch (Exception e) {
            throw new InternalServerException("Có lỗi khi sửa sản phẩm!");
        }
    }

    @Override
    public void importProducts(MultipartFile file) {

        ArrayList<String[]> products = new ArrayList<>();

        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVReader csvReader = new CSVReader(reader)) {
            csvReader.skip(1);
            String[] nextRecord;
            while ((nextRecord = csvReader.readNext()) != null) {
                products.add(nextRecord);
            }
        } catch (IOException | CsvValidationException e) {
            throw new InternalServerException(e.getMessage());// Xử lý lỗi khi đọc tệp CSV
        }

        for (String[] strings : products) {
            String id = strings[0].trim();
            String name;
            if (strings.length >= 2) {
                 name = strings[1].trim();
                // Các bước xử lý khác cho biến "name"
            } else {
                // Xử lý khi mảng không có đủ phần tử
                throw new InternalServerException("Lỗi: Mảng không có đủ phần tử");
            }
            String supplier = strings[2].replaceAll("^\"|\"$", "").trim();
            String codeShipment = strings[3].trim();
            String brand = strings[4].trim();
            String categories = strings[5];
            String attributes = strings[6];
            String price = strings[7].trim();
            String priceSale = strings[8].trim();
            String size = strings[9].trim();
            String quantity = strings[10].trim();

            Optional<Product> optionalProduct = productRepository.findById(id);
            Optional<Supplier> supplierOptional = supplierRepository.findSupplierByNameAndCodeShipment(supplier, codeShipment);
            List<Brand> brandOptional = brandRepository.findByName(brand.toUpperCase().trim());
            Product checkNameProduct=productRepository.findByName(name.trim());

            if(checkNameProduct != null && !id.equals(checkNameProduct.getId())){
                throw new BadRequestException("Tên giày bị trùng");
            }


            //Kiểm tra nhà cung cấp hoặc mã đợt hàng tồn tại không
            if (supplierOptional.isEmpty()) {
                throw new NotFoundException("Nhà cung cấp hoặc mã đợt hàng không tồn tại");
            }

            //Kiểm tra nhãn hiệu tồn tại không
            if (brandOptional.isEmpty()) {
                throw new NotFoundException("Thương hiệu không tồn tại");
            }

            //Kiểm tra xem danh mục tồn tại không
            String cleanedString = categories.substring(1, categories.length() - 1);

            // Phân tách chuỗi thành mảng các phần tử
            String[] stringArray = cleanedString.split(",");

            // Chuyển mảng thành danh sách chuỗi
            List<String> categoryList = Arrays.asList(stringArray);
            List<Category> lstCategory = new ArrayList<>();
            for (int j = 0; j < categoryList.size(); j++) {
                List<Category> categoryOptional = categoryRepository.findByName(categoryList.get(j).toUpperCase().trim());
                if (categoryOptional.isEmpty()) {
                    throw new NotFoundException("Danh mục không tồn tại");
                }
                lstCategory.add(categoryOptional.get(0));
            }


            //Kiểm tra attribute
            // Sử dụng Gson để chuyển đổi chuỗi JSON thành danh sách Map<String, String>
            Gson gson = new Gson();
            Type listType = List.class.getTypeParameters()[0];
            ArrayList<Map<String, String>> attributeList = gson.fromJson(attributes, listType);
            ArrayList<Map<String, String>> lstAttribute = new ArrayList<>();
            for (int j = 0; j < attributeList.size(); j++) {
                String attributeName = attributeList.get(j).keySet().iterator().next().toUpperCase().trim();
                String attributeValue = attributeList.get(j).values().iterator().next().toUpperCase().trim();
                if (!AttributeType.contains(attributeName)) {
                    throw new NotFoundException("Thuộc tính không tồn tại");
                }
                if (!Arrays.asList(AttributeType.valueOf(attributeName).getValues()).contains(attributeValue)) {
                    throw new NotFoundException("Giá trị thuộc tính không tồn tại");
                }
                Map<String, String> value = new HashMap<>();
                value.put("name", attributeName.toUpperCase());
                value.put("value", attributeValue.toUpperCase());
                lstAttribute.add(value);
            }



            //Kiểm tra product đã tạo chưa
            if (optionalProduct.isPresent()) {
                Optional<ProductSize> optionalProductSize = productSizeRepository.findById(new SizeId(id, Integer.valueOf(size)));
                ProductSize productSize = new ProductSize();
                if (optionalProductSize.isEmpty()) {
                    //Lưu vào bảng product_size nêu chưa có size

                    productSize.setProductId(optionalProduct.get().getId());
                    productSize.setSize(Integer.valueOf(size));
                    productSize.setQuantity(Integer.valueOf(quantity));
                    productSize.setTotalSold(0L);
                    productSizeRepository.save(productSize);
                } else {
                    //Cập nhật số lượng
                    Integer q = optionalProductSize.get().getQuantity() + Integer.parseInt(quantity);
                    optionalProductSize.get().setQuantity(q);
                    productSizeRepository.save(optionalProductSize.get());
                }
                //Supplier
                SuppliersProduct sp = SuppliersProduct.builder()
                        .codeShipment(codeShipment)
                        .quantity(Integer.parseInt(quantity))
                        .size(Integer.parseInt(size))
                        .entryPrice(optionalProduct.get().getPrice())
                        .supplier(supplierOptional.get())
                        .product(Product.builder()
                                .id(id)
                                .build())
                        .createdAt(new Timestamp(System.currentTimeMillis()))
                        .status(StatusProductSupplier.IMPORTED.name())
                        .build();
                suppliersProductRepository.save(sp);
                continue;
            }




            //Tạo product,product_size,product_supplier,product_category
            try {
                Product product = new Product();
                product.setId(id);
                product.setName(name);
                product.setPrice(Double.parseDouble(price));
                product.setSalePrice(Double.parseDouble(priceSale));
                product.setAttributeValues(lstAttribute);

                Slugify slug = new Slugify();
                product.setSlug(slug.slugify(name));

                product.setCategories(lstCategory);
                product.setBrand(brandOptional.get(0));
                product.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                product.setStatus(1);
                product.setView(0);
                //Supplier
                SuppliersProduct sp = SuppliersProduct.builder()
                        .codeShipment(codeShipment)
                        .quantity(Integer.valueOf(quantity))
                        .size(Integer.valueOf(size))
                        .entryPrice(product.getPrice())
                        .supplier(supplierOptional.get())
                        .product(Product.builder()
                                .id(product.getId())
                                .build())
                        .createdAt(new Timestamp(System.currentTimeMillis()))
                        .status(StatusProductSupplier.IMPORTED.name())
                        .build();


                ProductSize productSize = new ProductSize();
                productSize.setProductId(id);
                productSize.setSize(Integer.valueOf(size));
                productSize.setQuantity(Integer.valueOf(quantity));
                productSize.setTotalSold(0L);


                productRepository.save(product);
                suppliersProductRepository.save(sp);
                productSizeRepository.save(productSize);

            } catch (NumberFormatException e) {
                throw new InternalServerException("Lỗi khi lưu sản phẩm");
            }

        }

    }

    @Override
    public List<ProductInfoDTO> getRelatedProducts(String id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isEmpty()) {
            throw new NotFoundException("Sản phẩm không tồn tại");
        }
        return productRepository.getRelatedProducts(id,Constant.LIMIT_PRODUCT_RELATED);
    }

    @Override
    public List<ShortProductInfoDTO> getAvailableProducts() {
        return productRepository.getAvailableProducts();
    }

    @Override
    public List<ProductInfoDTO> getListNewProducts() {
        return productRepository.getListNewProducts(Constant.LIMIT_PRODUCT_NEW);
    }



    @Override
    public List<ProductInfoDTO> getListBestSellProducts() {
        return productRepository.getListBestSellProducts(Constant.LIMIT_PRODUCT_SELL);
    }

    @Override
    public List<ProductInfoDTO> getListViewProducts() {
        return productRepository.getListViewProducts(Constant.LIMIT_PRODUCT_VIEW);
    }

    @Override
    public List<ProductSizeDTO> getListAvailableSize(String id) {
        return productSizeRepository.findAllSizeOfProduct(id);
    }

    @Override
    public PageableDTO filterProduct(FilterProductRequest req) {
        PageUtil pageUtil = new PageUtil(Constant.LIMIT_PRODUCT_SHOP, req.getPage());

        //Lấy danh sách sản phẩm và tổng số sản phẩm
        int totalItems;
        List<ProductInfoDTO> products;

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonValues;
        try {
            jsonValues = objectMapper.writeValueAsString(req.getAttributes());
        } catch (JsonProcessingException e) {
            // Xử lý exception nếu có
            e.printStackTrace();
            jsonValues = "[]"; // Gán một giá trị mặc định nếu có lỗi
        }
        if (req.getSizes().isEmpty()) {
            //Nếu không có size

            products = productRepository.getAllProducts(req.getBrands(), req.getCategories(),req.getAttributes(), req.getMinPrice(), req.getMaxPrice(), Constant.LIMIT_PRODUCT_SHOP, pageUtil.calculateOffset());
            totalItems = productRepository.countProductAllSize(req.getBrands(), req.getCategories(), jsonValues,req.getMinPrice(), req.getMaxPrice());
        } else {
            //Nếu có size
            products = productRepository.searchProductBySize(req.getBrands(), req.getCategories(),jsonValues, req.getMinPrice(), req.getMaxPrice(), req.getSizes(), Constant.LIMIT_PRODUCT_SHOP, pageUtil.calculateOffset());
            totalItems = productRepository.countProductBySize(req.getBrands(), req.getCategories(), jsonValues,req.getMinPrice(), req.getMaxPrice(), req.getSizes());
        }

        //Tính tổng số trang
        int totalPages = pageUtil.calculateTotalPage(totalItems);

        return new PageableDTO(products, totalPages, req.getPage());
    }

    @Override
    public Integer getCountProduct() {
        return Math.toIntExact(productRepository.count());
    }

    @Override
    public List<ProductSizeDTO> getAllProduct() {
        return productRepository.getAll();
    }

    @Override
    public Boolean checkProductSizeAvailable(String id, int size) {
        ProductSize productSize = productSizeRepository.checkProductAndSizeAvailable(id, size);
        return productSize != null;
    }

    @Override
    public PageableDTO searchProductByKeyword(String keyword, Integer page) {
        // Validate
        if (keyword == null) {
            keyword = "";
        }
        if (page == null) {
            page = 1;
        }

        PageUtil pageInfo = new PageUtil(Constant.LIMIT_PRODUCT_SEARCH, page);

        //Lấy danh sách sản phẩm theo key
        List<ProductInfoDTO> products = productRepository.searchProductByKeyword(keyword, Constant.LIMIT_PRODUCT_SEARCH, pageInfo.calculateOffset());

        //Lấy số sản phẩm theo key
        int totalItems = productRepository.countProductByKeyword(keyword);

        //Tính số trang
        int totalPages = pageInfo.calculateTotalPage(totalItems);

        return new PageableDTO(products, totalPages, page);
    }

    @Override
    public Product getProductById(String id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isEmpty()) {
            throw new NotFoundException("Không tìm thấy sản phẩm trong hệ thống!");
        }
        return product.get();
    }

    @Override
    public List<ShortProductInfoDTO> getListProduct() {
        return productRepository.getListProduct();
    }

    @Override
    public DetailProductInfoDTO getDetailProductById(String id) {
        Optional<Product> rs = productRepository.findById(id);
        if (rs.isEmpty()) {
            throw new NotFoundException("Sản phẩm không tồn tại");
        }
        Product product = rs.get();

        if (product.getStatus() != 1) {
            throw new NotFoundException("Sản phâm không tồn tại");
        }
        DetailProductInfoDTO dto = new DetailProductInfoDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setPrice(product.getSalePrice());
        dto.setViews(product.getView());
        dto.setSlug(product.getSlug());
        dto.setDescription(product.getDescription());
        dto.setBrand(product.getBrand());
        dto.setProductImages(product.getImages());
        dto.setComments(product.getComments());

        //Cộng sản phẩm xem
        product.setView(product.getView() + 1);
        productRepository.save(product);

        return dto;
    }
}
