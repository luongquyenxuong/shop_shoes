package com.example.shoes_shop.controller.admin;

import com.example.shoes_shop.constant.Constant;
import com.example.shoes_shop.dto.*;
import com.example.shoes_shop.entity.User;
import com.example.shoes_shop.mapper.OrderMapper;
import com.example.shoes_shop.repository.*;
import com.example.shoes_shop.security.CustomUserDetails;
import com.example.shoes_shop.service.*;
import com.example.shoes_shop.util.ExcelUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
public class DashboardController {


    private final ProductService productService;

    private final ProductRepository productRepository;

    private final CategoryService categoryService;
    private final ServletContext context;
    private final BrandService brandService;

    private final OrderService orderService;
    private final OrderRepository orderRepository;

    private final UserRepository userRepository;
    private final UserService userService;

    private final StatisticRepository statisticRepository;

    private final SuppliersProductRepository suppliersProductRepository;

    private final CategoryRepository categoryRepository;

    private final PostService postService;


    @GetMapping("/admin")
    public String dashboard(Model model) {




        model.addAttribute("ordersWait", orderService.getCountOrderWait());
        model.addAttribute("ordersDelivery", orderService.getCountOrderDelivery());
        model.addAttribute("ordersReturn", orderService.getCountOrderReturn());
        model.addAttribute("ordersConfirmation", orderService.getCountOrderConfirmation());


        return "admin/index";
    }

    @GetMapping("/api/wait/count")
    public ResponseEntity<Integer> getOrdersWaitCount() {
        return ResponseEntity.ok(orderService.getCountOrderWait());
    }

    @GetMapping("/api/delivery/count")
    public ResponseEntity<Integer> getOrdersDeliveryCount() {
        return ResponseEntity.ok(orderService.getCountOrderDelivery());
    }

    @GetMapping("/api/return/count")
    public ResponseEntity<Integer> getOrdersReturnCount() {
        return ResponseEntity.ok(orderService.getCountOrderReturn());
    }

    @GetMapping("/api/confirmation/count")
    public ResponseEntity<Integer> getOrdersConfirmationCount() {
        return ResponseEntity.ok(orderService.getCountOrderConfirmation());
    }

    @GetMapping("/api/admin/count/products")
    public ResponseEntity<Integer> getCountProduct() {
        Integer countProducts = productService.getCountProduct();
        return ResponseEntity.ok(countProducts);
    }

    @GetMapping("/api/admin/count/posts")
    public ResponseEntity<Object> getCountPost() {
        Long countPosts = postService.getCountPost();
        return ResponseEntity.ok(countPosts);
    }

    @GetMapping("/api/admin/count/categories")
    public ResponseEntity<Object> getCountCategories() {
        Long countCategories = categoryService.getCountCategories();
        return ResponseEntity.ok(countCategories);
    }

    @GetMapping("/api/admin/count/brands")
    public ResponseEntity<Object> getCountBrands() {
        Long countBrands = brandService.getCountBrands();
        return ResponseEntity.ok(countBrands);
    }

    @GetMapping("/api/admin/count/users")
    public ResponseEntity<Object> getCountUsers() {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Long countUsers = userRepository.getUserCount(user.getId());

        return ResponseEntity.ok(countUsers);
    }

    @GetMapping("/api/admin/count/orders")
    public ResponseEntity<Object> getCountOrders() {
        Integer countOrders = orderService.getCountOrder();
        return ResponseEntity.ok(countOrders);
    }

    @GetMapping("/api/admin/statistics")
    public ResponseEntity<Object> getStatistic30Day() {
        List<StatisticDTO> statistics = statisticRepository.getStatistic30Day();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/api/admin/get-product-view")
    public ResponseEntity<Object> getProductView() {
        List<ProductInfoDTO> productList = productRepository.getListViewProducts(Constant.LIMIT_PRODUCT);

        if (productList == null || productList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<ChartDTO> chartList = productList.stream()
                .map(productInfoDTO -> new ChartDTO(productInfoDTO.getName(), productInfoDTO.getViews()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(chartList);
    }

    @GetMapping("/api/admin/statistic-order-cancel")
    public ResponseEntity<Object> getStatisticOrderCancel() {
        List<ChartDTO> chartDTOS = statisticRepository.getStatisticOrderCancel();
        List<ChartDTO> a =statisticRepository.getViewPurchaseRatio();
        return ResponseEntity.ok(chartDTOS);
    }
    @GetMapping("/api/admin/statistic-view-purchase-ratio")
    public ResponseEntity<Object> getViewPurchaseRatio() {

        List<ChartDTO> chartDTOS =statisticRepository.getViewPurchaseRatio();
        return ResponseEntity.ok(chartDTOS);
    }

    @GetMapping("/api/admin/statistic-order-refund")
    public ResponseEntity<Object> getStatisticOrderRefund() {
        List<ChartDTO> chartDTOS = statisticRepository.getStatisticOrderRefund();
        return ResponseEntity.ok(chartDTOS);
    }

    @PostMapping("/api/admin/statistics")
    public ResponseEntity<Object> getStatisticDayByDay(@RequestBody FilterDayByDay filterDayByDay) {
        List<StatisticDTO> statisticDTOS = statisticRepository.getStatisticDayByDay(filterDayByDay.getToDate(), filterDayByDay.getFromDate());
        return ResponseEntity.ok(statisticDTOS);
    }

    @GetMapping("/api/admin/product-order-categories")
    public ResponseEntity<Object> getListProductOrderCategories() {
        List<ChartDTO> chartDTOS = categoryRepository.getListProductOrderCategories();
        return ResponseEntity.ok(chartDTOS);
    }

    @GetMapping("/api/admin/product-order")
    public ResponseEntity<Object> getProductOrder() {
        Pageable pageable = PageRequest.of(0, 10);
        List<ChartDTO> chartDTOS = productRepository.getProductOrders(pageable, LocalDate.now().getMonthValue(), LocalDate.now().getYear());
        return ResponseEntity.ok(chartDTOS);
    }

    @GetMapping("/api/statistic/export/excel")
    public void exportAllData(HttpServletResponse response) {
        List<ProductSizeDTO> products = productService.getAllProduct();
        List<PurchaseOrderDTO> purchaseOrders = suppliersProductRepository.getSuppliersProduct();
        List<OrderDTO> ordersComplete = orderRepository.getOrdersComplete().stream().map(OrderMapper::orderDTO).collect(Collectors.toList());
        List<OrderDTO> ordersAll = orderRepository.getOrdersAll().stream().map(OrderMapper::orderDTO).collect(Collectors.toList());
        List<ProductReport> productReports = productRepository.getProductReport();
        List<UserOrderSummaryDTO> userOrderSummaryDTOs= userRepository.getUserOrderSummary();
        String fullPath = ExcelUtil.generateExcel(products,
                purchaseOrders,ordersComplete,ordersAll,productReports,userOrderSummaryDTOs, context);
        if (fullPath != null) {
            ExcelUtil.fileDownload(fullPath, response, context);
        }
    }

}

