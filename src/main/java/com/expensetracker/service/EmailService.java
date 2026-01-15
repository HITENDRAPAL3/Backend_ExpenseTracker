package com.expensetracker.service;

import com.expensetracker.dto.CategorySummaryDTO;
import com.expensetracker.dto.ExpenseDTO;
import com.expensetracker.model.User;
import com.expensetracker.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    private final TemplateEngine templateEngine;
    private final ExpenseService expenseService;
    private final UserRepository userRepository;

    public void sendExpenseReport(String username, LocalDate startDate, LocalDate endDate) throws MessagingException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));

        String htmlContent = generateReportHtml(username, startDate, endDate);
        String subject = buildSubject(startDate, endDate);

        // Send email
        sendHtmlEmail(user.getEmail(), subject, htmlContent);

        log.info("Expense report sent to {} for user {}", user.getEmail(), username);
    }

    /**
     * Generate HTML report without sending email - useful when SMTP is blocked
     */
    public byte[] generateReportAsHtml(String username, LocalDate startDate, LocalDate endDate) {
        String htmlContent = generateReportHtml(username, startDate, endDate);
        return htmlContent.getBytes();
    }

    private String generateReportHtml(String username, LocalDate startDate, LocalDate endDate) {
        // Fetch expense data
        List<ExpenseDTO> expenses = expenseService.getAllExpenses(username, null, startDate, endDate);
        List<CategorySummaryDTO> categorySummary = expenseService.getCategorySummary(username, startDate, endDate);
        BigDecimal totalAmount = expenseService.getTotalExpenses(username, startDate, endDate);

        // Prepare template context
        Context context = new Context();
        context.setVariable("username", username);
        context.setVariable("startDate", startDate);
        context.setVariable("endDate", endDate);
        context.setVariable("expenses", expenses);
        context.setVariable("categorySummary", categorySummary);
        context.setVariable("totalAmount", totalAmount);
        context.setVariable("expenseCount", expenses.size());
        context.setVariable("generatedAt", LocalDateTime.now());

        // Render HTML template
        return templateEngine.process("email/expense-report", context);
    }

    private String buildSubject(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null) {
            return String.format("Expense Report: %s to %s", startDate, endDate);
        } else if (startDate != null) {
            return String.format("Expense Report: From %s", startDate);
        } else if (endDate != null) {
            return String.format("Expense Report: Until %s", endDate);
        }
        return "Expense Report: All Time Summary";
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        if (mailSender == null) {
            throw new MessagingException("Email service not configured. Use 'Download Report' instead.");
        }

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom("no-reply@expensetracker.com");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }
}
