package com.lumiere.app.service.impl;

import com.lumiere.app.domain.Orders;
import com.lumiere.app.repository.OrdersRepository;
import com.lumiere.app.service.CustomerService;
import com.lumiere.app.service.OrderItemService;
import com.lumiere.app.service.OrdersQueryService;
import com.lumiere.app.service.OrdersService;
import com.lumiere.app.service.dto.OrderItemDTO;
import com.lumiere.app.service.dto.OrdersDTO;
import com.lumiere.app.service.mapper.OrdersMapper;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumiere.app.domain.Orders}.
 */
@Service
@Transactional
public class OrdersServiceImpl implements OrdersService {

    private static final Logger LOG = LoggerFactory.getLogger(OrdersServiceImpl.class);

    private final OrdersRepository ordersRepository;

    private final OrdersMapper ordersMapper;
    private final CustomerService customerService;
    private final OrdersQueryService ordersQueryService;
    private final OrderItemService orderItemService;

    public OrdersServiceImpl(OrdersRepository ordersRepository, OrdersMapper ordersMapper, CustomerService customerService, OrdersQueryService ordersQueryService, OrderItemService orderItemService) {
        this.ordersRepository = ordersRepository;
        this.ordersMapper = ordersMapper;
        this.customerService = customerService;
        this.ordersQueryService = ordersQueryService;
        this.orderItemService = orderItemService;
    }

    @Override
    public OrdersDTO save(OrdersDTO ordersDTO) {
        LOG.debug("Request to save Orders : {}", ordersDTO);
        Orders orders = ordersMapper.toEntity(ordersDTO);
        orders = ordersRepository.save(orders);
        return ordersMapper.toDto(orders);
    }

    @Override
    public OrdersDTO update(OrdersDTO ordersDTO) {
        LOG.debug("Request to update Orders : {}", ordersDTO);
        Orders orders = ordersMapper.toEntity(ordersDTO);
        orders = ordersRepository.save(orders);
        return ordersMapper.toDto(orders);
    }

    @Override
    public Optional<OrdersDTO> partialUpdate(OrdersDTO ordersDTO) {
        LOG.debug("Request to partially update Orders : {}", ordersDTO);

        return ordersRepository
            .findById(ordersDTO.getId())
            .map(existingOrders -> {
                ordersMapper.partialUpdate(existingOrders, ordersDTO);

                return existingOrders;
            })
            .map(ordersRepository::save)
            .map(ordersMapper::toDto);
    }

    public Page<OrdersDTO> findAllWithEagerRelationships(Pageable pageable) {
        return ordersRepository.findAllWithEagerRelationships(pageable).map(ordersMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrdersDTO> findOne(Long id) {
        LOG.debug("Request to get Orders : {}", id);
        return ordersRepository.findOneWithEagerRelationships(id).map(ordersMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Orders : {}", id);
        ordersRepository.deleteById(id);
    }

    @Override
    public void writeOrderInvoiceExcel(Long orderId, HttpServletResponse response) {
        OrdersDTO order = this.findOne(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        List<OrderItemDTO> items = orderItemService.findAllByOrderId(orderId);

        try (Workbook wb = new XSSFWorkbook()) {
            // ====== Styles ======
            Font fTitle = wb.createFont(); fTitle.setBold(true); fTitle.setFontHeightInPoints((short)16);
            CellStyle sTitle = wb.createCellStyle(); sTitle.setFont(fTitle);

            Font fHdr = wb.createFont(); fHdr.setBold(true);
            CellStyle sHdr = wb.createCellStyle();
            sHdr.setFont(fHdr);
            sHdr.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            sHdr.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            setAllBorders(sHdr, BorderStyle.THIN);

            CellStyle sText = wb.createCellStyle(); setAllBorders(sText, BorderStyle.THIN);

            CellStyle sInt  = wb.createCellStyle(); setAllBorders(sInt, BorderStyle.THIN);
            sInt.setDataFormat(wb.createDataFormat().getFormat("#,##0"));

            CellStyle sMoney = wb.createCellStyle(); setAllBorders(sMoney, BorderStyle.THIN);
            sMoney.setDataFormat(wb.createDataFormat().getFormat("#,##0 \"₫\""));

            Font fBold = wb.createFont(); fBold.setBold(true);
            CellStyle sMoneyBold = wb.createCellStyle(); setAllBorders(sMoneyBold, BorderStyle.THIN);
            sMoneyBold.setFont(fBold);
            sMoneyBold.setDataFormat(wb.createDataFormat().getFormat("#,##0 \"₫\""));

            Sheet sheet = wb.createSheet("Invoice");
            int r = 0;

            // ===== Header: tiêu đề + thời điểm tạo =====
            Row row0 = sheet.createRow(r++);
            Cell c0 = row0.createCell(0);
            c0.setCellValue("Đơn hàng #" + safe(order.getCode()));
            c0.setCellStyle(sTitle);
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 5));

            Row row1 = sheet.createRow(r++);
            String createdAt = order.getPlacedAt() != null
                ? DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy")
                .format(order.getPlacedAt().atZone(ZoneId.systemDefault()))
                : "";
            row1.createCell(0).setCellValue(createdAt);

            r++; // dòng trống

            // ===== Box tiêu đề "Chi tiết đơn hàng" =====
            Row boxTitle = sheet.createRow(r++);
            Cell cBoxTitle = boxTitle.createCell(0);
            cBoxTitle.setCellValue("Chi tiết đơn hàng (" + (items != null ? items.size() : 0) + ")");
            cBoxTitle.setCellStyle(fHdrTitle(wb));
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(r - 1, r - 1, 0, 5));

            // ===== Header bảng items =====
            Row hdr = sheet.createRow(r++);
            String[] cols = {"Sản phẩm", "SKU", "Số lượng", "Đơn giá", "Tổng"};
            for (int i = 0; i < cols.length; i++) {
                Cell hc = hdr.createCell(i);
                hc.setCellValue(cols[i]);
                hc.setCellStyle(sHdr);
            }

            long subtotal = 0L;
            if (items != null) {
                for (OrderItemDTO it : items) {
                    Row rr = sheet.createRow(r++);

                    String pvName = it.getProductVariant() != null ? safe(it.getProductVariant().getName()) : "";
                    String pvSku  = it.getProductVariant() != null ? safe(it.getProductVariant().getSku())  : "";

                    // Tên
                    Cell a = rr.createCell(0); a.setCellValue(pvName); a.setCellStyle(sText);
                    // SKU
                    Cell b = rr.createCell(1); b.setCellValue(pvSku);  b.setCellStyle(sText);
                    // Số lượng
                    Cell c = rr.createCell(2); c.setCellValue(nz(it.getQuantity())); c.setCellStyle(sInt);
                    // Đơn giá
                    Cell d = rr.createCell(3); d.setCellValue(nz(it.getUnitPrice())); d.setCellStyle(sMoney);
                    // Thành tiền
                    long lineTotal = nz(it.getTotalPrice());
                    subtotal += lineTotal;
                    Cell e = rr.createCell(4); e.setCellValue(lineTotal); e.setCellStyle(sMoney);
                }
            }

            r++; // trống

            // ===== Cột phải: Khách hàng & Trạng thái =====
            int rightCol = 7;
            int r2 = 3;

            Row custTitle = getOrCreate(sheet, r2++);
            Cell cCustTitle = custTitle.createCell(rightCol);
            cCustTitle.setCellValue("Khách hàng");
            cCustTitle.setCellStyle(fHdrTitle(wb));
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(r2 - 1, r2 - 1, rightCol, rightCol + 3));

            String fullName = "";
            String phone = "";
            if (order.getCustomer() != null) {
                // Tùy CustomerDTO của bạn, thay đổi cho đúng field
                // ví dụ: getFullName(), getFirstName()/getLastName(), getPhone()...
                try {
                    // demo gộp first/last nếu có
                    String fn = safe((String) order.getCustomer().getClass().getMethod("getFirstName").invoke(order.getCustomer()));
                    String ln = safe((String) order.getCustomer().getClass().getMethod("getLastName").invoke(order.getCustomer()));
                    fullName = (fn + " " + ln).trim();
                } catch (Exception ignore) {}
                try {
                    phone = safe((String) order.getCustomer().getClass().getMethod("getPhone").invoke(order.getCustomer()));
                } catch (Exception ignore) {}
            }
            Row rName = getOrCreate(sheet, r2++); rName.createCell(rightCol).setCellValue(fullName);
            Row rPhone = getOrCreate(sheet, r2++); rPhone.createCell(rightCol).setCellValue(phone);
            Row rPayM = getOrCreate(sheet, r2++); rPayM.createCell(rightCol).setCellValue(safe(order.getPaymentMethod()));

            r2++;
            Row sttTitle = getOrCreate(sheet, r2++);
            Cell cStt = sttTitle.createCell(rightCol);
            cStt.setCellValue("Cập nhật trạng thái");
            cStt.setCellStyle(fHdrTitle(wb));
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(r2 - 1, r2 - 1, rightCol, rightCol + 3));

            Row sttRow1 = getOrCreate(sheet, r2++);
            sttRow1.createCell(rightCol).setCellValue("Trạng thái đơn hàng: " + safe(String.valueOf(order.getStatus())));
            Row sttRow2 = getOrCreate(sheet, r2++);
            sttRow2.createCell(rightCol).setCellValue("Trạng thái thanh toán: " + safe(String.valueOf(order.getPaymentStatus())));

            // ===== Box "Thanh toán" dưới bảng items =====
            r++;
            Row payTitle = sheet.createRow(r++);
            Cell cPayTitle = payTitle.createCell(0);
            cPayTitle.setCellValue("Thanh toán");
            cPayTitle.setCellStyle(fHdrTitle(wb));
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(r - 1, r - 1, 0, 5));

            // Không có các field subtotal/discount/shipping ở DTO => ghi hiển thị cơ bản
            r = writeAmountRow(sheet, r, "Tạm tính:", subtotal, sText, sMoney);
            r = writeAmountRow(sheet, r, "Phí vận chuyển:", 0L, sText, sMoney);
            r = writeAmountRow(sheet, r, "Giảm giá:", 0L, sText, sMoney);

            long grand = order.getTotalAmount() != null ? order.getTotalAmount().longValue() : subtotal;
            Row grandRow = sheet.createRow(r++);
            Cell gt = grandRow.createCell(0); gt.setCellValue("Tổng cộng:"); gt.setCellStyle(headerRight(wb));
            Cell gv = grandRow.createCell(4); gv.setCellValue(grand);       gv.setCellStyle(sMoneyBold);

            // ===== Auto-fit =====
            for (int i = 0; i <= 5; i++) sheet.autoSizeColumn(i);
            for (int i = rightCol; i <= rightCol + 3; i++) sheet.autoSizeColumn(i);

            // ===== Stream về client =====
            String filename = URLEncoder.encode("invoice_" + safe(order.getCode()) + ".xlsx", StandardCharsets.UTF_8);
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + filename);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            try (ServletOutputStream os = response.getOutputStream()) {
                wb.write(os);
                os.flush();
            }
        } catch (Exception e) {
            throw new RuntimeException("Export invoice error", e);
        }
    }

    /* ================= helpers ================= */

    private static long nz(Integer v) { return v == null ? 0L : v.longValue(); }
    private static long nz(java.math.BigDecimal v) { return v == null ? 0L : v.longValue(); }
    private static String safe(String s){ return s == null ? "" : s; }

    private static void setAllBorders(CellStyle style, BorderStyle b) {
        style.setBorderTop(b); style.setBorderBottom(b); style.setBorderLeft(b); style.setBorderRight(b);
    }
    private static CellStyle fHdrTitle(Workbook wb){
        Font f = wb.createFont(); f.setBold(true);
        CellStyle cs = wb.createCellStyle(); cs.setFont(f);
        cs.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return cs;
    }
    private static CellStyle headerRight(Workbook wb){
        Font f = wb.createFont(); f.setBold(true);
        CellStyle cs = wb.createCellStyle(); cs.setFont(f);
        cs.setAlignment(HorizontalAlignment.RIGHT);
        return cs;
    }
    private static Row getOrCreate(Sheet sheet, int rowIdx){
        Row r = sheet.getRow(rowIdx);
        return r != null ? r : sheet.createRow(rowIdx);
    }
    private static int writeAmountRow(Sheet sh, int r, String label, long value, CellStyle sText, CellStyle sMoney){
        Row row = sh.createRow(r++);
        Cell l = row.createCell(0); l.setCellValue(label); l.setCellStyle(sText);
        Cell v = row.createCell(4); v.setCellValue(value); v.setCellStyle(sMoney);
        return r;
    }
}
