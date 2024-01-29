package com.example.shoes_shop.util;

import com.example.shoes_shop.constant.Constant;
import com.example.shoes_shop.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class ExcelUtil {
    private static final int BUFFER_SIZE = 4096;

    private static final String TEMP_EXPORT_DATA_DIRECTORY = File.separator + "resources" + File.separator + "reports";
    private static final String EXPORT_DATA_REPORT_FILE_NAME = "statistic";

    public static String generateExcel(List<ProductSizeDTO> products,
                                       List<PurchaseOrderDTO> purchaseOrders,
                                       List<OrderDTO> ordersComplete,
                                       List<OrderDTO>orders,
                                       List<ProductReport> productReports,
                                       List<UserOrderSummaryDTO> userOrderSummaryDTOS,
                                       ServletContext context) {
        String filePath = context.getRealPath(TEMP_EXPORT_DATA_DIRECTORY);
        Path directoryPath = Paths.get(filePath);
        File file = new File(filePath);

        try {
            Files.createDirectories(directoryPath);
        } catch (IOException e) {
            log.error("Error creating directories: {}", e.getMessage());
            return null;
        }

        String xlsx = ".xlsx";

        String fullPath = directoryPath.resolve(EXPORT_DATA_REPORT_FILE_NAME + xlsx).toString();

        try (FileOutputStream fos = new FileOutputStream(file + "\\" + EXPORT_DATA_REPORT_FILE_NAME + xlsx);
             BufferedOutputStream bos = new BufferedOutputStream(fos);
             XSSFWorkbook workbook = new XSSFWorkbook()) {

            createProductSheet(workbook, products);
            createImportProductSheet(workbook, purchaseOrders);
            createOrderCompleteSheet(workbook, ordersComplete);
            createOrderAllSheet(workbook, orders);
            createInventorySheet(workbook, productReports);
            createUserSheet(workbook, userOrderSummaryDTOS);

            workbook.write(bos);
            return fullPath;
        } catch (Exception e) {
            log.error("Error generating Excel: {}", e.getMessage());
            return null;
        }
    }

    public static void fileDownload(String fullPath, HttpServletResponse response, ServletContext context) {
        File file = new File(fullPath);
        if (file.exists()) {

            try (FileInputStream fis = new FileInputStream(file);
                 OutputStream os = response.getOutputStream()) {

                String mimeType = context.getMimeType(fullPath);
                response.setContentType(mimeType);
                response.setHeader("content-disposition", "attachment; filename=" + EXPORT_DATA_REPORT_FILE_NAME + "." + "xlsx");

                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                Files.delete(file.toPath());
            } catch (IOException e) {
                log.error("Error deleting file: {}", e.getMessage());
            } catch (Exception e) {
                log.error("Can't download file, detail: {}", e.getMessage());
            }
        }
    }

    private static void createProductSheet(XSSFWorkbook workbook, List<ProductSizeDTO> products) {
        XSSFSheet worksheet = workbook.createSheet("Sản phẩm");
        worksheet.setDefaultColumnWidth(20);

        createIntroRow(workbook, worksheet, "Shop : Shoes shop ", 0, true, 9);
        createIntroRow(workbook, worksheet, "Địa chỉ: 601 Đ. Cách Mạng Tháng 8, Phường 15, Quận 10, Thành phố Hồ Chí Minh", 1, false, 9);
        createIntroRow(workbook, worksheet, "Chủ shop: Lương Quyền Xương", 2, false, 9);
        createIntroRow(workbook, worksheet, "Email: quyenxuong.shoes@gmail.com", 3, false, 9);
        createIntroRow(workbook, worksheet, "Thống kê sản phẩm", 4, true, 9);

        createHeaderRow(workbook, worksheet, "STT",
                "Mã sản phẩm",
                "Tên sản phẩm",
                "Thương hiệu",
                "Giá nhập",
                "Giá bán",
                "Kích cỡ",
                "Tình trạng",
                "Lượt xem",
                "Ngày thêm");

        int count = 0;
        if (!products.isEmpty()) {
            for (int i = 0; i < products.size(); i++) {
                ProductSizeDTO product = products.get(i);
                XSSFRow bodyRow = worksheet.createRow(i + 8);
                XSSFCellStyle bodyCellStyle = workbook.createCellStyle();
                bodyCellStyle.setFillForegroundColor(new XSSFColor(Color.WHITE, null));


                // Đóng khung và thêm màu nền cho toàn bộ bảng
                bodyCellStyle.setBorderTop(BorderStyle.THIN);
                bodyCellStyle.setBorderBottom(BorderStyle.THIN);
                bodyCellStyle.setBorderLeft(BorderStyle.THIN);
                bodyCellStyle.setBorderRight(BorderStyle.THIN);
                bodyCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                createCell(bodyRow, 0, ++count, bodyCellStyle);
                createCell(bodyRow, 1, product.getProductId(), bodyCellStyle);
                createCell(bodyRow, 2, product.getProductName(), bodyCellStyle);
                createCell(bodyRow, 3, product.getBrand(), bodyCellStyle);
                createCell(bodyRow, 4, CurrencyFormatter.formatCurrency(product.getPrice()), bodyCellStyle);
                createCell(bodyRow, 5, CurrencyFormatter.formatCurrency(product.getPriceSale()), bodyCellStyle);
                createCell(bodyRow, 6, product.getSize(), bodyCellStyle);
                createCell(bodyRow, 7, product.getStatus() == 1 ? "Đang kinh doanh" : "Ngừng kinh doanh", bodyCellStyle);
                createCell(bodyRow, 8, product.getView(), bodyCellStyle);
                createCell(bodyRow, 9, product.getCreatedAt(), bodyCellStyle);
            }
        }
    }

    private static void createOrderCompleteSheet(XSSFWorkbook workbook, List<OrderDTO> orderDTOS) {
        XSSFSheet worksheet = workbook.createSheet("Doanh thu");
        worksheet.setDefaultColumnWidth(20);

        createIntroRow(workbook, worksheet, "Shop : Shoes shop ", 0, true, 8);
        createIntroRow(workbook, worksheet, "Địa chỉ: 601 Đ. Cách Mạng Tháng 8, Phường 15, Quận 10, Thành phố Hồ Chí Minh", 1, false, 8);
        createIntroRow(workbook, worksheet, "Chủ shop: Lương Quyền Xương", 2, false, 8);
        createIntroRow(workbook, worksheet, "Email: quyenxuong.shoes@gmail.com", 3, false, 8);
        createIntroRow(workbook, worksheet, "Thống kê doanh thu", 4, true, 8);
        createHeaderRow(workbook, worksheet, "STT",
                "Mã đơn hàng",
                "Số lượng sản phẩm",
                "Thành tiền",
                "Lời nhuận",
                "Khách hàng",
                "SĐT",
                "Ngày đặt",
                "Ngày giao");

        int count = 0;
        if (!orderDTOS.isEmpty()) {
            for (int i = 0; i < orderDTOS.size(); i++) {
                OrderDTO orderDTO = setStatusText(orderDTOS.get(i));


                XSSFRow bodyRow = worksheet.createRow(i + 8);
                XSSFCellStyle bodyCellStyle = workbook.createCellStyle();
                bodyCellStyle.setFillForegroundColor(new XSSFColor(Color.WHITE, null));


                // Đóng khung và thêm màu nền cho toàn bộ bảng
                bodyCellStyle.setBorderTop(BorderStyle.THIN);
                bodyCellStyle.setBorderBottom(BorderStyle.THIN);
                bodyCellStyle.setBorderLeft(BorderStyle.THIN);
                bodyCellStyle.setBorderRight(BorderStyle.THIN);
                bodyCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                createCell(bodyRow, 0, ++count, bodyCellStyle);
                createCell(bodyRow, 1, orderDTO.getId(), bodyCellStyle);
                createCell(bodyRow, 2, orderDTO.getDetails().size(), bodyCellStyle);
                createCell(bodyRow, 3, CurrencyFormatter.formatCurrency(orderDTO.getTotalPrice()), bodyCellStyle);
                createCell(bodyRow, 4, CurrencyFormatter.formatCurrency(orderDTO.getProfit()), bodyCellStyle);
                createCell(bodyRow, 5, orderDTO.getCreatedEmail(), bodyCellStyle);
                createCell(bodyRow, 6, orderDTO.getCreatedPhone(), bodyCellStyle);
                createCellWithDateFormat(bodyRow, 7, orderDTO.getCreatedAt(), bodyCellStyle, workbook);
                createCellWithDateFormat(bodyRow, 8, orderDTO.getCompleteAt(), bodyCellStyle, workbook);

            }
        }
    }

    private static void createUserSheet(XSSFWorkbook workbook, List<UserOrderSummaryDTO> userOrderSummaryDTOS) {
        XSSFSheet worksheet = workbook.createSheet("Tài khoản");
        worksheet.setDefaultColumnWidth(20);

        createIntroRow(workbook, worksheet, "Shop : Shoes shop ", 0, true, 8);
        createIntroRow(workbook, worksheet, "Địa chỉ: 601 Đ. Cách Mạng Tháng 8, Phường 15, Quận 10, Thành phố Hồ Chí Minh", 1, false, 8);
        createIntroRow(workbook, worksheet, "Chủ shop: Lương Quyền Xương", 2, false, 8);
        createIntroRow(workbook, worksheet, "Email: quyenxuong.shoes@gmail.com", 3, false, 8);
        createIntroRow(workbook, worksheet, "Tài khoản hệ thống", 4, true, 8);
        createHeaderRow(workbook, worksheet, "STT",
                "Tên khách hàng",
                "Tổng đơn hàng",
                "Đơn chờ xác nhận",
                "Đơn chờ lấy hàng",
                "Đơn đang giao",
                "Đơn hoàn thành",
                "Đơn trả lại",
                "Đơn hủy"
                );

        int count = 0;
        if (!userOrderSummaryDTOS.isEmpty()) {
            for (int i = 0; i < userOrderSummaryDTOS.size(); i++) {
                UserOrderSummaryDTO userOrderSummaryDTO = userOrderSummaryDTOS.get(i);


                XSSFRow bodyRow = worksheet.createRow(i + 8);
                XSSFCellStyle bodyCellStyle = workbook.createCellStyle();
                bodyCellStyle.setFillForegroundColor(new XSSFColor(Color.WHITE, null));


                // Đóng khung và thêm màu nền cho toàn bộ bảng
                bodyCellStyle.setBorderTop(BorderStyle.THIN);
                bodyCellStyle.setBorderBottom(BorderStyle.THIN);
                bodyCellStyle.setBorderLeft(BorderStyle.THIN);
                bodyCellStyle.setBorderRight(BorderStyle.THIN);
                bodyCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                createCell(bodyRow, 0, ++count, bodyCellStyle);
                createCell(bodyRow, 1, userOrderSummaryDTO.getUsername(), bodyCellStyle);
                createCell(bodyRow, 2, userOrderSummaryDTO.getTotalOrders(), bodyCellStyle);
                createCell(bodyRow, 3, userOrderSummaryDTO.getPendingConfirmation(), bodyCellStyle);
                createCell(bodyRow, 4, userOrderSummaryDTO.getPendingPickup(), bodyCellStyle);
                createCell(bodyRow, 5, userOrderSummaryDTO.getDelivering(), bodyCellStyle);
                createCell(bodyRow, 6, userOrderSummaryDTO.getCompleted(), bodyCellStyle);
                createCell(bodyRow, 7, userOrderSummaryDTO.getReturned(), bodyCellStyle);
                createCell(bodyRow, 8, userOrderSummaryDTO.getCancelled(), bodyCellStyle);
            }
        }
    }
    private static void createInventorySheet(XSSFWorkbook workbook, List<ProductReport> productReports) {
        XSSFSheet worksheet = workbook.createSheet("Tồn kho");
        worksheet.setDefaultColumnWidth(20);

        createIntroRow(workbook, worksheet, "Shop : Shoes shop ", 0, true, 7);
        createIntroRow(workbook, worksheet, "Địa chỉ: 601 Đ. Cách Mạng Tháng 8, Phường 15, Quận 10, Thành phố Hồ Chí Minh", 1, false, 7);
        createIntroRow(workbook, worksheet, "Chủ shop: Lương Quyền Xương", 2, false, 7);
        createIntroRow(workbook, worksheet, "Email: quyenxuong.shoes@gmail.com", 3, false, 7);
        createIntroRow(workbook, worksheet, "Thống kê tồn kho", 4, true, 7);
        createHeaderRow(workbook, worksheet, "STT",
                "Mã sản phẩm",
                "Tên sản phẩm",
                "Size",
                "Số lượng đã nhập",
                "Số lượng đã bán",
                "Số lượng đang bán",
                "Còn lại");

        int count = 0;
        if (!productReports.isEmpty()) {
            for (int i = 0; i < productReports.size(); i++) {
                ProductReport productReport = productReports.get(i);


                XSSFRow bodyRow = worksheet.createRow(i + 8);
                XSSFCellStyle bodyCellStyle = workbook.createCellStyle();
                bodyCellStyle.setFillForegroundColor(new XSSFColor(Color.WHITE, null));


                // Đóng khung và thêm màu nền cho toàn bộ bảng
                bodyCellStyle.setBorderTop(BorderStyle.THIN);
                bodyCellStyle.setBorderBottom(BorderStyle.THIN);
                bodyCellStyle.setBorderLeft(BorderStyle.THIN);
                bodyCellStyle.setBorderRight(BorderStyle.THIN);
                bodyCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                createCell(bodyRow, 0, ++count, bodyCellStyle);
                createCell(bodyRow, 1, productReport.getId(), bodyCellStyle);
                createCell(bodyRow, 2, productReport.getName(), bodyCellStyle);
                createCell(bodyRow, 3, productReport.getSize(), bodyCellStyle);
                createCell(bodyRow, 4, productReport.getTotalSupplierQuantity(), bodyCellStyle);
                createCell(bodyRow, 5, productReport.getTotalSold(), bodyCellStyle);
                createCell(bodyRow, 6, productReport.getTotalShipping(), bodyCellStyle);
                createCell(bodyRow, 7, productReport.getTotalQuantity(), bodyCellStyle);
            }
        }
    }


    private static void createOrderAllSheet(XSSFWorkbook workbook, List<OrderDTO> orderDTOS) {
        XSSFSheet worksheet = workbook.createSheet("Đơn hàng");
        worksheet.setDefaultColumnWidth(20);

        createIntroRow(workbook, worksheet, "Shop : Shoes shop ", 0, true, 8);
        createIntroRow(workbook, worksheet, "Địa chỉ: 601 Đ. Cách Mạng Tháng 8, Phường 15, Quận 10, Thành phố Hồ Chí Minh", 1, false, 8);
        createIntroRow(workbook, worksheet, "Chủ shop: Lương Quyền Xương", 2, false, 8);
        createIntroRow(workbook, worksheet, "Email: quyenxuong.shoes@gmail.com", 3, false, 8);
        createIntroRow(workbook, worksheet, "Thống kê đơn hàng", 4, true, 8);

        createHeaderRow(workbook, worksheet, "STT",
                "Mã đơn hàng",
                "Số lượng sản phẩm",
                "Thành tiền",
                "Khách hàng",
                "SĐT",
                "Trạng thái",
                "Ngày đặt",
                "Thời gian thay đổi cuối"
        );


        int count = 0;
        if (!orderDTOS.isEmpty()) {
            for (int i = 0; i < orderDTOS.size(); i++) {
                OrderDTO orderDTO = setStatusText(orderDTOS.get(i));


                XSSFRow bodyRow = worksheet.createRow(i + 8);
                XSSFCellStyle bodyCellStyle = workbook.createCellStyle();
                bodyCellStyle.setFillForegroundColor(new XSSFColor(Color.WHITE, null));


                // Đóng khung và thêm màu nền cho toàn bộ bảng
                bodyCellStyle.setBorderTop(BorderStyle.THIN);
                bodyCellStyle.setBorderBottom(BorderStyle.THIN);
                bodyCellStyle.setBorderLeft(BorderStyle.THIN);
                bodyCellStyle.setBorderRight(BorderStyle.THIN);
                bodyCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                createCell(bodyRow, 0, ++count, bodyCellStyle);
                createCell(bodyRow, 1, orderDTO.getId(), bodyCellStyle);
                createCell(bodyRow, 2, orderDTO.getDetails().size(), bodyCellStyle);
                createCell(bodyRow, 3, CurrencyFormatter.formatCurrency(orderDTO.getTotalPrice()), bodyCellStyle);
                createCell(bodyRow, 4, orderDTO.getCreatedEmail(), bodyCellStyle);
                createCell(bodyRow, 5, orderDTO.getCreatedPhone(), bodyCellStyle);
                createCell(bodyRow, 6, orderDTO.getStatusText(), bodyCellStyle);
                createCellWithDateFormat(bodyRow, 7, orderDTO.getCreatedAt(), bodyCellStyle, workbook);
                createCellWithDateFormat(bodyRow, 8, orderDTO.getLastChangeTime(), bodyCellStyle, workbook);

            }
        }
    }


    private static OrderDTO setStatusText(OrderDTO orderDTO){
        int orderStatus = orderDTO.getStatus();
        Integer refundStatus = orderDTO.getStatusRefund();
        boolean shouldAddPaymentInfo = true;

        switch (orderStatus) {
            case Constant.CONFIRMATION_STATUS:
                orderDTO.setStatusText("Chờ xác nhận");
                break;
            case Constant.ORDER_STATUS:
                orderDTO.setStatusText("Chờ lấy hàng");
                break;
            case Constant.DELIVERY_STATUS:
                orderDTO.setStatusText("Đang giao hàng");
                break;
            case Constant.COMPLETED_STATUS:
                orderDTO.setStatusText("Đã giao hàng");
                break;
            case Constant.RETURNED_STATUS:
                shouldAddPaymentInfo = false; // Không thêm thông báo "đã thanh toán"
                orderDTO.setStatusText(orderDTO.getFullyReturned() != null && orderDTO.getFullyReturned()
                        ? "Đơn hàng đã trả lại toàn bộ"
                        : "Đơn hàng trả lại " + orderDTO.getReturnDetails().size() + " sản phẩm");
                break;
            case Constant.CANCELED_STATUS:
                shouldAddPaymentInfo = false; // Không thêm thông báo "đã thanh toán"
                orderDTO.setStatusText("Đơn hàng đã hủy");
                break;
        }

        if (shouldAddPaymentInfo && orderDTO.getTransaction() != null) {
            orderDTO.setStatusText(orderDTO.getStatusText() + " và đã thanh toán");
        }


        if (refundStatus != null) {
            switch (refundStatus) {
                case Constant.REQUEST_REFUND:
                    orderDTO.setStatusText("Đã gửi yêu cầu trả hàng");
                    break;
                case Constant.PROCESSING_REFUND:
                    orderDTO.setStatusText("Đang xử lý trả hàng");
                    break;
                case Constant.REFUNDED:
                    orderDTO.setStatusText(orderDTO.getFullyReturned() != null && orderDTO.getFullyReturned()
                            ? "Đơn hàng đã trả lại toàn bộ"
                            : "Đơn hàng trả lại " + orderDTO.getReturnDetails().size() + " sản phẩm");
                    break;
                // Thêm các trạng thái khác nếu cần
                default:
                    // Xử lý một trạng thái khác nếu cần
                    orderDTO.setStatusText("Đã từ chối yêu cầu trả hàng");
                    break;
            }
        }
        return orderDTO;
    }

    private static void createImportProductSheet(XSSFWorkbook workbook, List<PurchaseOrderDTO> purchaseOrders) {
        XSSFSheet worksheet = workbook.createSheet("Nhập hàng");
        worksheet.setDefaultColumnWidth(20);

        // Phần giới thiệu
        createIntroRow(workbook, worksheet, "Shoes shop", 0, true, 10);
        createIntroRow(workbook, worksheet, "Địa chỉ: 601 Đ. Cách Mạng Tháng 8, Phường 15, Quận 10, Thành phố Hồ Chí Minh", 1, false, 10);
        createIntroRow(workbook, worksheet, "Chủ shop: Lương Quyền Xương", 2, false, 10);
        createIntroRow(workbook, worksheet, "Email: quyenxuong.shoes@gmail.com", 3, false, 10);
        createIntroRow(workbook, worksheet, "Thống kê nhập hàng", 4, true, 10);

        createHeaderRow(workbook, worksheet, "STT",
                "Mã đợt hàng",
                "Mã sản phẩm",
                "Tên sản phẩm",
                "Thương hiệu",
                "Nhà cung cấp",
                "Thời gian",
                "Size",
                "Số lượng",
                "Đơn giá",
                "Thành tiền"
        );
        int count = 0;
        if (!purchaseOrders.isEmpty()) {
            for (int i = 0; i < purchaseOrders.size(); i++) {
                PurchaseOrderDTO purchaseOrder = purchaseOrders.get(i);
                XSSFRow bodyRow = worksheet.createRow(i + 8);
                XSSFCellStyle bodyCellStyle = workbook.createCellStyle();
                bodyCellStyle.setFillForegroundColor(new XSSFColor(Color.WHITE, null));

                // Đóng khung và thêm màu nền cho toàn bộ bảng
                bodyCellStyle.setBorderTop(BorderStyle.THIN);
                bodyCellStyle.setBorderBottom(BorderStyle.THIN);
                bodyCellStyle.setBorderLeft(BorderStyle.THIN);
                bodyCellStyle.setBorderRight(BorderStyle.THIN);
                bodyCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                createCell(bodyRow, 0, ++count, bodyCellStyle);
                createCell(bodyRow, 1, purchaseOrder.getPurchaseOrderId(), bodyCellStyle);
                createCell(bodyRow, 2, purchaseOrder.getProductId(), bodyCellStyle);
                createCell(bodyRow, 3, purchaseOrder.getProductName(), bodyCellStyle);
                createCell(bodyRow, 4, purchaseOrder.getBrand(), bodyCellStyle);
                createCell(bodyRow, 5, purchaseOrder.getSupplier(), bodyCellStyle);
                createCell(bodyRow, 6, purchaseOrder.getPurchaseDate(), bodyCellStyle);
                createCell(bodyRow, 7, purchaseOrder.getSize(), bodyCellStyle);
                createCell(bodyRow, 8, purchaseOrder.getQuantity(), bodyCellStyle);
                createCell(bodyRow, 9, CurrencyFormatter.formatCurrency(purchaseOrder.getPurchasePrice()), bodyCellStyle);
                createCell(bodyRow, 10, CurrencyFormatter.formatCurrency(purchaseOrder.getPurchasePrice() * purchaseOrder.getQuantity()), bodyCellStyle);
            }
        }
    }

    private static void createHeaderRow(XSSFWorkbook workbook, XSSFSheet worksheet, String... headers) {
        XSSFRow headerRow = worksheet.createRow(7);

        XSSFCellStyle headerCellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontName(XSSFFont.DEFAULT_FONT_NAME);
        font.setColor(new XSSFColor(Color.WHITE, null));
        headerCellStyle.setFont(font);
        headerCellStyle.setFillForegroundColor(new XSSFColor(Color.BLACK, null));
        headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerCellStyle.setBorderLeft(BorderStyle.THIN);
        headerCellStyle.setBorderBottom(BorderStyle.THIN);
        headerCellStyle.setBorderRight(BorderStyle.THIN);
        headerCellStyle.setBorderTop(BorderStyle.THIN);

        // Tạo các ô trong header với tên cột cố định

        for (int i = 0; i < headers.length; i++) {
            createCell(headerRow, i, headers[i], headerCellStyle);
        }
    }

    private static void createCell(XSSFRow row, int columnIndex, Object value, XSSFCellStyle cellStyle) {
        XSSFCell cell = row.createCell(columnIndex);
        cell.setCellValue(value != null ? value.toString() : "");
        cell.setCellStyle(cellStyle);
    }

    private static void createCellWithDateFormat(XSSFRow row, int columnIndex, Date value, XSSFCellStyle cellStyle, XSSFWorkbook workbook) {
        XSSFCell cell = row.createCell(columnIndex);
        cellStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("dd/MM/yyyy HH:mm:ss"));
        cell.setCellValue(value);
        cell.setCellStyle(cellStyle);
    }

    private static void createIntroRow(XSSFWorkbook workbook, XSSFSheet worksheet, String info, int rowIndex, boolean isTitle, int lastCol) {
        XSSFRow introRow = worksheet.createRow(rowIndex);
        XSSFCellStyle introCellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();

        if (isTitle) {
            introCellStyle.setFillForegroundColor(new XSSFColor(Color.YELLOW, null));
            font.setBold(true);
            font.setFontHeightInPoints((short) 14); // Tăng kích thước chữ
        } else {
            introCellStyle.setFillForegroundColor(new XSSFColor(Color.YELLOW, null)); // Thêm màu nền
        }

        introCellStyle.setAlignment(HorizontalAlignment.CENTER); // Căn giữa nội dung
        introCellStyle.setFont(font);

        // Đóng khung
        introCellStyle.setBorderTop(BorderStyle.THIN);
        introCellStyle.setBorderBottom(BorderStyle.THIN);
        introCellStyle.setBorderLeft(BorderStyle.THIN);
        introCellStyle.setBorderRight(BorderStyle.THIN);

        // Thiết lập mẫu nền
        introCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellRangeAddress mergedCell = new CellRangeAddress(rowIndex, rowIndex, 0, lastCol); // Hợp nhất các ô từ cột 0 đến cột 8
        worksheet.addMergedRegion(mergedCell);
        // Đóng khung bên ngoài
        RegionUtil.setBorderTop(BorderStyle.THIN, mergedCell, worksheet);
        RegionUtil.setBorderBottom(BorderStyle.THIN, mergedCell, worksheet);
        RegionUtil.setBorderLeft(BorderStyle.THIN, mergedCell, worksheet);
        RegionUtil.setBorderRight(BorderStyle.THIN, mergedCell, worksheet);


        createCell(introRow, 0, info, introCellStyle);
    }


}
