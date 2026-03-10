package com.bat.utils.helper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

import com.bat.DTO.CheckDetailDTO;
import com.bat.DTO.CustomerDTO;
import com.bat.DTO.ExportLotDTO;
import com.bat.DTO.ExportReceiptDTO;
import com.bat.DTO.ImportDTO;
import com.bat.DTO.InventoryCheckDTO;
import com.bat.DTO.LotDTO;
import com.bat.DTO.ProviderDTO;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class PDFExporter {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final NumberFormat CURRENCY_FORMATTER = NumberFormat.getCurrencyInstance(Locale.of("vi", "VN"));
    
    private Font titleFont;
    private Font headerFont;
    private Font normalFont;
    private Font boldFont;
    private Font smallFont;
    
    public PDFExporter() {
        try {
            // Use Times New Roman font from lib folder
            String fontPath = System.getProperty("user.dir") + "/lib/TimesNewRoman/";
            normalFont = new Font(BaseFont.createFont(fontPath + "SVN-Times New Roman.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED), 12, Font.NORMAL);
            titleFont = new Font(BaseFont.createFont(fontPath + "SVN-Times New Roman Bold.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED), 18, Font.BOLD);
            headerFont = new Font(BaseFont.createFont(fontPath + "SVN-Times New Roman Bold.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED), 14, Font.BOLD);
            boldFont = new Font(BaseFont.createFont(fontPath + "SVN-Times New Roman Bold.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED), 12, Font.BOLD);
            smallFont = new Font(BaseFont.createFont(fontPath + "SVN-Times New Roman.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED), 10, Font.NORMAL);
        } catch (DocumentException | IOException e) {
            throw new RuntimeException("Failed to initialize PDF fonts: " + e.getMessage(), e);
        }
    }
    
    public void exportImportReceipt(String filePath, ImportDTO importDTO, 
                                     ArrayList<LotDTO> lots,
                                     Map<Integer, String> productNames,
                                     String userName, 
                                     ProviderDTO provider) throws DocumentException, IOException {
        
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();
        
        // Add system title
        Paragraph systemTitle = new Paragraph("Hệ thống quản lý kho sách", smallFont);
        systemTitle.setAlignment(Element.ALIGN_LEFT);
        systemTitle.setSpacingAfter(5);
        document.add(systemTitle);
        
        // Add current date/time (top right)
        String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        Paragraph dateTime = new Paragraph("Thời gian in phiếu: " + currentDateTime, smallFont);
        dateTime.setAlignment(Element.ALIGN_LEFT);
        dateTime.setSpacingAfter(20);
        document.add(dateTime);
        
        // Add main title
        Paragraph title = new Paragraph("THÔNG TIN PHIẾU NHẬP", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);
        
        // Add receipt info
        Paragraph receiptInfo = new Paragraph();
        receiptInfo.add(new Phrase("Mã phiếu: " + importDTO.getReceiptId() + "\n", normalFont));
        receiptInfo.add(new Phrase("Nhà cung cấp: " + provider.getProviderName() + 
                                   "   -   " + provider.getAddress() + "\n", normalFont));
        receiptInfo.add(new Phrase("Người thực hiện: " + userName + 
                                   "   -   Mã nhân viên: " + importDTO.getUserId() + "\n", normalFont));
        
        String formattedDate = importDTO.getCreatedDate() != null 
            ? importDTO.getCreatedDate().format(DATE_FORMATTER) 
            : "";
        receiptInfo.add(new Phrase("Thời gian nhập: " + formattedDate + "\n", normalFont));
        receiptInfo.setSpacingAfter(20);
        document.add(receiptInfo);
        
        // Create lots table with 5 columns
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.setSpacingAfter(20);
        
        // Set column widths: Tên sản phẩm wider, Mã lô separate, Giá smaller
        float[] columnWidths = {3f, 1.5f, 1.2f, 1f, 1.5f};
        table.setWidths(columnWidths);
        
        // Add table headers
        addTableHeader(table, "Tên sản phẩm");
        addTableHeader(table, "Mã lô");
        addTableHeader(table, "Giá");
        addTableHeader(table, "Số lượng");
        addTableHeader(table, "Tổng tiền");
        
        // Add table data - one row per lot
        BigDecimal total = BigDecimal.ZERO;
        for (LotDTO lot : lots) {
            String productName = productNames.getOrDefault(lot.getProductId(), "N/A");
            addTableCell(table, productName);
            addTableCell(table, lot.getLotCode());
            
            String priceStr = lot.getImportPrice() != null 
                ? CURRENCY_FORMATTER.format(lot.getImportPrice()) 
                : "0 ₫";
            addTableCell(table, priceStr);
            
            addTableCell(table, String.valueOf(lot.getInitialQuantity()));
            
            BigDecimal itemTotal = lot.getImportPrice() != null 
                ? lot.getImportPrice().multiply(new BigDecimal(lot.getInitialQuantity()))
                : BigDecimal.ZERO;
            total = total.add(itemTotal);
            
            addTableCell(table, CURRENCY_FORMATTER.format(itemTotal));
        }
        
        document.add(table);
        
        // Add total
        Paragraph totalParagraph = new Paragraph(
            "Tổng thành tiền: " + CURRENCY_FORMATTER.format(total), 
            headerFont
        );
        totalParagraph.setAlignment(Element.ALIGN_RIGHT);
        totalParagraph.setSpacingAfter(30);
        document.add(totalParagraph);
        
        // Add signature section
        PdfPTable signatureTable = new PdfPTable(3);
        signatureTable.setWidthPercentage(100);
        signatureTable.setSpacingBefore(30);
        
        addSignatureCell(signatureTable, "Người lập phiếu", "(Ký và ghi rõ họ tên)");
        addSignatureCell(signatureTable, "Nhân viên nhập", "(Ký và ghi rõ họ tên)");
        addSignatureCell(signatureTable, "Nhà cung cấp", "(Ký và ghi rõ họ tên)");
        
        document.add(signatureTable);
        
        document.close();
    }
    
    public void exportExportReceipt(String filePath, ExportReceiptDTO exportDTO,
                                     ArrayList<ExportLotDTO> exportLots,
                                     Map<Integer, String> productNames,
                                     String userName,
                                     CustomerDTO customer) throws DocumentException, IOException {
        
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();
        
        // Add system title
        Paragraph systemTitle = new Paragraph("Hệ thống quản lý cửa hàng sách BestBook", smallFont);
        systemTitle.setAlignment(Element.ALIGN_LEFT);
        systemTitle.setSpacingAfter(5);
        document.add(systemTitle);
        
        // Add current date/time
        String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        Paragraph dateTime = new Paragraph("Thời gian in phiếu: " + currentDateTime, smallFont);
        dateTime.setAlignment(Element.ALIGN_LEFT);
        dateTime.setSpacingAfter(20);
        document.add(dateTime);
        
        // Add main title
        Paragraph title = new Paragraph("PHIẾU XUẤT KHO", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);
        
        // Add receipt info
        Paragraph receiptInfo = new Paragraph();
        receiptInfo.add(new Phrase("Mã phiếu: " + exportDTO.getExport_id() + "\n", normalFont));
        
        if (customer != null) {
            receiptInfo.add(new Phrase("Khách hàng: " + customer.getFullName() + 
                                       "   -   SĐT: " + customer.getPhone() + "\n", normalFont));
            if (customer.getAddress() != null && !customer.getAddress().isEmpty()) {
                receiptInfo.add(new Phrase("Địa chỉ: " + customer.getAddress() + "\n", normalFont));
            }
        }
        
        receiptInfo.add(new Phrase("Người thực hiện: " + userName + 
                                   "   -   Mã nhân viên: " + exportDTO.getUser_id() + "\n", normalFont));
        
        String formattedDate = exportDTO.getExport_date() != null 
            ? exportDTO.getExport_date().format(DATE_FORMATTER) 
            : "";
        receiptInfo.add(new Phrase("Thời gian xuất: " + formattedDate + "\n", normalFont));
        receiptInfo.setSpacingAfter(20);
        document.add(receiptInfo);
        
        // Create table with 4 columns (no lot code)
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.setSpacingAfter(20);
        
        // Set column widths
        float[] columnWidths = {4f, 1.5f, 1f, 1.5f};
        table.setWidths(columnWidths);
        
        // Add table headers
        addTableHeader(table, "Tên sản phẩm");
        addTableHeader(table, "Giá");
        addTableHeader(table, "Số lượng");
        addTableHeader(table, "Tổng tiền");
        
        // Add table data
        BigDecimal total = BigDecimal.ZERO;
        for (ExportLotDTO exportLot : exportLots) {
            String productName = productNames.getOrDefault(exportLot.getProductId(), "N/A");
            addTableCell(table, productName);
            
            String priceStr = exportLot.getExportPrice() != null 
                ? CURRENCY_FORMATTER.format(exportLot.getExportPrice()) 
                : "0 ₫";
            addTableCell(table, priceStr);
            
            addTableCell(table, String.valueOf(exportLot.getQuantity()));
            
            BigDecimal itemTotal = exportLot.getExportPrice() != null 
                ? exportLot.getExportPrice().multiply(new BigDecimal(exportLot.getQuantity()))
                : BigDecimal.ZERO;
            total = total.add(itemTotal);
            
            addTableCell(table, CURRENCY_FORMATTER.format(itemTotal));
        }
        
        document.add(table);
        
        // Add total
        Paragraph totalParagraph = new Paragraph(
            "Tổng thành tiền: " + CURRENCY_FORMATTER.format(total), 
            headerFont
        );
        totalParagraph.setAlignment(Element.ALIGN_RIGHT);
        totalParagraph.setSpacingAfter(30);
        document.add(totalParagraph);
        
        // Add signature section
        PdfPTable signatureTable = new PdfPTable(3);
        signatureTable.setWidthPercentage(100);
        signatureTable.setSpacingBefore(30);
        
        addSignatureCell(signatureTable, "Người lập phiếu", "(Ký và ghi rõ họ tên)");
        addSignatureCell(signatureTable, "Nhân viên xuất", "(Ký và ghi rõ họ tên)");
        addSignatureCell(signatureTable, "Khách hàng", "(Ký và ghi rõ họ tên)");
        
        document.add(signatureTable);
        
        document.close();
    }
    
    private void addTableHeader(PdfPTable table, String headerText) {
        PdfPCell header = new PdfPCell();
        // header.setBackgroundColor(BaseColor.LIGHT_GRAY);
        header.setBorderWidth(1);
        header.setPhrase(new Phrase(headerText, boldFont));
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        header.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.setPadding(5);
        table.addCell(header);
    }
    
    private void addTableCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell();
        cell.setPhrase(new Phrase(text, normalFont));
        cell.setPadding(5);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
    }
    
    private void addSignatureCell(PdfPTable table, String title, String subtitle) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(0);
        
        Paragraph p = new Paragraph();
        p.setAlignment(Element.ALIGN_CENTER);
        p.add(new Phrase(title + "\n", boldFont));
        p.add(new Phrase(subtitle + "\n\n\n\n", smallFont));
        
        cell.addElement(p);
        cell.setMinimumHeight(80);
        table.addCell(cell);
    }

    public void exportInventoryCheckReceipt(String filePath, InventoryCheckDTO checkDTO,
                                            ArrayList<CheckDetailDTO> detailList,
                                            java.util.Map<Integer, String> productNames,
                                            java.util.Map<Integer, String> lotCodes,
                                            String userName) throws DocumentException, IOException {
        
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();
        
        // Add system title
        Paragraph systemTitle = new Paragraph("Hệ thống quản lý cửa hàng sách BestBook", smallFont);
        systemTitle.setAlignment(Element.ALIGN_LEFT);
        systemTitle.setSpacingAfter(5);
        document.add(systemTitle);
        
        // Add current date/time (top right)
        String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        Paragraph dateTime = new Paragraph("Thời gian in phiếu: " + currentDateTime, smallFont);
        dateTime.setAlignment(Element.ALIGN_LEFT);
        dateTime.setSpacingAfter(20);
        document.add(dateTime);
        
        // Add main title
        Paragraph title = new Paragraph("PHIẾU KIỂM KÊ KHO", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);
        
        // Add receipt info
        Paragraph receiptInfo = new Paragraph();
        receiptInfo.add(new Phrase("Mã phiếu: " + checkDTO.getCheckId() + "\n", normalFont));
        receiptInfo.add(new Phrase("Người thực hiện: " + userName + 
                                   "   -   Mã nhân viên: " + checkDTO.getUserId() + "\n", normalFont));
        
        String formattedDate = checkDTO.getCheckDate() != null 
            ? checkDTO.getCheckDate().format(DATE_FORMATTER) 
            : "";
        receiptInfo.add(new Phrase("Thời gian kiểm kê: " + formattedDate + "\n", normalFont));
        receiptInfo.setSpacingAfter(20);
        document.add(receiptInfo);
        
        // Create table with 6 columns
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.setSpacingAfter(20);
        
        // Set column widths
        float[] columnWidths = {0.5f, 1f, 2.5f, 1.5f, 1f, 1f};
        table.setWidths(columnWidths);
        
        // Add table headers
        addTableHeader(table, "STT");
        addTableHeader(table, "Mã SP");
        addTableHeader(table, "Tên sản phẩm");
        addTableHeader(table, "Mã lô");
        addTableHeader(table, "SL hệ thống");
        addTableHeader(table, "SL thực tế");
        
        // Add table data
        int totalSystemQty = 0;
        int totalActualQty = 0;
        int stt = 1;
        
        for (CheckDetailDTO detail : detailList) {
            String productName = productNames.getOrDefault(detail.getLotId(), "N/A");
            String lotCode = lotCodes.getOrDefault(detail.getLotId(), "N/A");
            
            addTableCell(table, String.valueOf(stt++));
            addTableCell(table, String.valueOf(detail.getLotId()));
            addTableCell(table, productName);
            addTableCell(table, lotCode);
            addTableCell(table, String.valueOf(detail.getSystemQuantity()));
            
            // Highlight actual quantity if different from system
            PdfPCell actualCell = new PdfPCell();
            actualCell.setPhrase(new Phrase(String.valueOf(detail.getActualQuantity()), normalFont));
            actualCell.setPadding(5);
            actualCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            actualCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            
            // if (detail.getActualQuantity() != detail.getSystemQuantity()) {
            //     actualCell.setBackgroundColor(new BaseColor(255, 255, 200)); // Light yellow
            // }
            table.addCell(actualCell);
            
            totalSystemQty += detail.getSystemQuantity();
            totalActualQty += detail.getActualQuantity();
        }
        
        // Add total row
        // PdfPCell totalLabelCell = new PdfPCell(new Phrase("TỔNG CỘNG", boldFont));
        // totalLabelCell.setColspan(4);
        // totalLabelCell.setPadding(8);
        // totalLabelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        // // totalLabelCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        // table.addCell(totalLabelCell);
        
        // addTableCellBold(table, String.valueOf(totalSystemQty));
        // addTableCellBold(table, String.valueOf(totalActualQty));
        
        document.add(table);
        
        // Add difference summary
        int difference = totalActualQty - totalSystemQty;
        Paragraph diffParagraph = new Paragraph();
        diffParagraph.add(new Phrase("Chênh lệch: ", boldFont));
        
        String diffText;
        if (difference > 0) {
            diffText = "Thừa " + difference + " sản phẩm";
        } else if (difference < 0) {
            diffText = "Thiếu " + Math.abs(difference) + " sản phẩm";
        } else {
            diffText = "Không có chênh lệch";
        }
        diffParagraph.add(new Phrase(diffText, normalFont));
        diffParagraph.setAlignment(Element.ALIGN_RIGHT);
        diffParagraph.setSpacingAfter(30);
        document.add(diffParagraph);
        
        // Add signature section
        PdfPTable signatureTable = new PdfPTable(3);
        signatureTable.setWidthPercentage(100);
        signatureTable.setSpacingBefore(30);
        
        addSignatureCell(signatureTable, "Người lập phiếu", "(Ký và ghi rõ họ tên)");
        addSignatureCell(signatureTable, "Nhân viên kiểm kê", "(Ký và ghi rõ họ tên)");
        addSignatureCell(signatureTable, "Quản lý kho", "(Ký và ghi rõ họ tên)");
        
        document.add(signatureTable);
        
        document.close();
    }
}
