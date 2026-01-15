package com.expensetracker.controller;

import com.expensetracker.dto.EmailReportRequest;
import com.expensetracker.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
@Slf4j
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/send-report")
    public ResponseEntity<Map<String, Object>> sendExpenseReport(
            Authentication authentication,
            @RequestBody(required = false) EmailReportRequest request) {
        
        String username = authentication.getName();
        Map<String, Object> response = new HashMap<>();

        try {
            emailService.sendExpenseReport(
                    username,
                    request != null ? request.getStartDate() : null,
                    request != null ? request.getEndDate() : null
            );

            response.put("success", true);
            response.put("message", "Expense report sent successfully to your registered email!");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Failed to send expense report for user {}: {}", username, e.getMessage());
            response.put("success", false);
            response.put("message", "Failed to send email: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Download the expense report as HTML file (works when SMTP is blocked)
     */
    @GetMapping("/download-report")
    public ResponseEntity<byte[]> downloadExpenseReport(
            Authentication authentication,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        String username = authentication.getName();

        try {
            byte[] htmlContent = emailService.generateReportAsHtml(username, startDate, endDate);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_HTML);
            headers.setContentDispositionFormData("attachment", "expense-report.html");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(htmlContent);

        } catch (Exception e) {
            log.error("Failed to generate expense report for user {}: {}", username, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
