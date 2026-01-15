package com.expensetracker.service;

import com.expensetracker.model.Expense;
import com.expensetracker.model.User;
import com.expensetracker.repository.ExpenseRepository;
import com.expensetracker.repository.UserRepository;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.opencsv.CSVWriter;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExportService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));
    }

    public byte[] exportToCSV(String username, Long categoryId, LocalDate startDate, LocalDate endDate) throws Exception {
        User user = getUserByUsername(username);
        List<Expense> expenses = expenseRepository.findWithFiltersByUser(user, categoryId, startDate, endDate);
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
        CSVWriter csvWriter = new CSVWriter(writer);

        // Write header
        String[] header = {"ID", "Description", "Amount", "Date", "Category"};
        csvWriter.writeNext(header);

        // Write data
        for (Expense expense : expenses) {
            String[] row = {
                    expense.getId().toString(),
                    expense.getDescription(),
                    expense.getAmount().toString(),
                    expense.getDate().format(dateFormatter),
                    expense.getCategory().getName()
            };
            csvWriter.writeNext(row);
        }

        csvWriter.close();
        return outputStream.toByteArray();
    }

    public byte[] exportToExcel(String username, Long categoryId, LocalDate startDate, LocalDate endDate) throws Exception {
        User user = getUserByUsername(username);
        List<Expense> expenses = expenseRepository.findWithFiltersByUser(user, categoryId, startDate, endDate);
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Expenses");

            // Create header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Description", "Amount", "Date", "Category"};
            for (int i = 0; i < headers.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Create data rows
            int rowNum = 1;
            for (Expense expense : expenses) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(expense.getId());
                row.createCell(1).setCellValue(expense.getDescription());
                row.createCell(2).setCellValue(expense.getAmount().doubleValue());
                row.createCell(3).setCellValue(expense.getDate().format(dateFormatter));
                row.createCell(4).setCellValue(expense.getCategory().getName());
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    public byte[] exportToPDF(String username, Long categoryId, LocalDate startDate, LocalDate endDate) throws Exception {
        User user = getUserByUsername(username);
        List<Expense> expenses = expenseRepository.findWithFiltersByUser(user, categoryId, startDate, endDate);
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);

        // Title
        Paragraph title = new Paragraph("Expense Report")
                .setFontSize(20)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(title);

        // Date range info
        String dateRange = "All Expenses";
        if (startDate != null && endDate != null) {
            dateRange = "From " + startDate.format(dateFormatter) + " to " + endDate.format(dateFormatter);
        } else if (startDate != null) {
            dateRange = "From " + startDate.format(dateFormatter);
        } else if (endDate != null) {
            dateRange = "Until " + endDate.format(dateFormatter);
        }
        document.add(new Paragraph(dateRange)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20));

        // Create table
        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 3, 2, 2, 2}))
                .setWidth(UnitValue.createPercentValue(100));

        // Header cells
        String[] headers = {"ID", "Description", "Amount", "Date", "Category"};
        for (String header : headers) {
            Cell headerCell = new Cell()
                    .add(new Paragraph(header).setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER);
            table.addHeaderCell(headerCell);
        }

        // Data cells
        for (Expense expense : expenses) {
            table.addCell(new Cell().add(new Paragraph(expense.getId().toString())));
            table.addCell(new Cell().add(new Paragraph(expense.getDescription())));
            table.addCell(new Cell().add(new Paragraph("₹" + expense.getAmount().toString())));
            table.addCell(new Cell().add(new Paragraph(expense.getDate().format(dateFormatter))));
            table.addCell(new Cell().add(new Paragraph(expense.getCategory().getName())));
        }

        document.add(table);

        // Total
        double total = expenses.stream()
                .mapToDouble(e -> e.getAmount().doubleValue())
                .sum();
        document.add(new Paragraph("Total: ₹" + String.format("%.2f", total))
                .setBold()
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginTop(20));

        document.close();
        return outputStream.toByteArray();
    }
}
