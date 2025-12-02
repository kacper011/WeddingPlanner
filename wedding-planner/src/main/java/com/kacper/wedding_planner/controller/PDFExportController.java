package com.kacper.wedding_planner.controller;

import com.kacper.wedding_planner.config.CustomUserDetails;
import com.kacper.wedding_planner.model.Expense;
import com.kacper.wedding_planner.model.Guest;
import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.repository.ExpenseRepository;
import com.kacper.wedding_planner.repository.GuestRepository;
import com.kacper.wedding_planner.service.GuestService;
import com.kacper.wedding_planner.service.UserService;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Controller
public class PDFExportController {

    private static final Logger log = LoggerFactory.getLogger(PDFExportController.class);

    private final GuestService guestService;
    private final UserService userService;
    private final GuestRepository guestRepository;
    private final ExpenseRepository expenseRepository;

    public PDFExportController(GuestService guestService, UserService userService, GuestRepository guestRepository, ExpenseRepository expenseRepository) {
        this.guestService = guestService;
        this.userService = userService;
        this.guestRepository = guestRepository;
        this.expenseRepository = expenseRepository;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/guests/export/pdf")
    public ResponseEntity<byte[]> exportGuestListToPDF(@AuthenticationPrincipal CustomUserDetails principal) {
        try {
            User currentUser = userService.findByEmail(principal.getUsername());
            List<Guest> guests = guestRepository.findByUser(currentUser);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);
            document.open();

            BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA, "Cp1250", BaseFont.NOT_EMBEDDED);
            Font fontTitle = new Font(baseFont, 18, Font.BOLD, Color.PINK);
            Font fontHeader = new Font(baseFont, 12, Font.BOLD, Color.WHITE);
            Font fontCell = new Font(baseFont, 12, Font.NORMAL, Color.BLACK);

            Paragraph title = new Paragraph("Lista Gości Weselnych", fontTitle);
            title.setAlignment(Paragraph.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100f);
            table.setWidths(new float[]{2f, 2f, 2f, 3f});

            addTableHeader(table, fontHeader, new String[]{"Nazwisko", "Imię", "Obecność", "Kontakt"}, new Color(255, 192, 203));

            for (Guest g : guests) {
                table.addCell(new Phrase(safeString(g.getLastName()), fontCell));
                table.addCell(new Phrase(safeString(g.getFirstName()), fontCell));
                table.addCell(new Phrase(safeString(g.getAttendanceConfirmation()), fontCell));
                table.addCell(new Phrase(safeString(g.getContact()), fontCell));
            }

            document.add(table);
            document.close();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=lista_gosci.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(baos.toByteArray());

        } catch (Exception e) {
            log.error("Błąd generowania PDF gości", e);
            return ResponseEntity.status(500).build();
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/expenses/export/pdf")
    public ResponseEntity<byte[]> exportExpensesToPDF(@AuthenticationPrincipal CustomUserDetails principal) {
        try {
            User currentUser = userService.findByEmail(principal.getUsername());
            List<Expense> expenses = expenseRepository.findByUser(currentUser);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);
            document.open();

            BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA, "Cp1250", BaseFont.NOT_EMBEDDED);
            Font fontTitle = new Font(baseFont, 18, Font.BOLD, Color.PINK);
            Font fontHeader = new Font(baseFont, 12, Font.BOLD, Color.WHITE);
            Font fontCell = new Font(baseFont, 12, Font.NORMAL, Color.BLACK);

            Paragraph title = new Paragraph("Lista Wydatków", fontTitle);
            title.setAlignment(Paragraph.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100f);
            table.setWidths(new float[]{4f, 2f});

            addTableHeader(table, fontHeader, new String[]{"Nazwa", "Kwota (zł)"}, new Color(244, 215, 215));

            BigDecimal total = BigDecimal.ZERO;
            for (Expense expense : expenses) {
                table.addCell(new Phrase(safeString(expense.getName()), fontCell));
                table.addCell(new Phrase(expense.getAmount().toString(), fontCell));
                total = total.add(expense.getAmount());
            }

            document.add(table);
            Paragraph summary = new Paragraph("Razem: " + total + " zł", fontCell);
            summary.setSpacingBefore(20);
            summary.setAlignment(Paragraph.ALIGN_RIGHT);
            document.add(summary);
            document.close();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=wydatki.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(baos.toByteArray());

        } catch (Exception e) {
            log.error("Błąd generowania PDF wydatków", e);
            return ResponseEntity.status(500).build();
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/wedding_games/export/pdf")
    public ResponseEntity<byte[]> exportQuestionsToPDF(
            @RequestParam(name = "selectedQuestions", required = false) List<String> selectedQuestions,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        try {
            if (selectedQuestions == null || selectedQuestions.isEmpty() || selectedQuestions.size() > 50) {
                return ResponseEntity.badRequest().build();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);
            document.open();

            BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA, "Cp1250", BaseFont.NOT_EMBEDDED);
            Font fontTitle = new Font(baseFont, 18, Font.BOLD, Color.PINK);
            Font fontQuestion = new Font(baseFont, 14, Font.BOLD, Color.BLACK);

            Paragraph title = new Paragraph("Quiz - Zabawy Weselne", fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            for (int i = 0; i < selectedQuestions.size(); i++) {
                String question = safeString(selectedQuestions.get(i));
                Paragraph questionPara = new Paragraph((i + 1) + ". " + question, fontQuestion);
                questionPara.setSpacingAfter(15);
                document.add(questionPara);
            }

            document.close();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=quiz_weselny.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(baos.toByteArray());

        } catch (Exception e) {
            log.error("Błąd generowania PDF quizu", e);
            return ResponseEntity.status(500).build();
        }
    }

    private void addTableHeader(PdfPTable table, Font fontHeader, String[] headers, Color bgColor) {
        for (String h : headers) {
            PdfPCell cell = new PdfPCell();
            cell.setBackgroundColor(bgColor);
            cell.setPadding(5);
            cell.setPhrase(new Phrase(h, fontHeader));
            table.addCell(cell);
        }
    }

    private String safeString(String input) {
        return input == null ? "" : input;
    }
}
