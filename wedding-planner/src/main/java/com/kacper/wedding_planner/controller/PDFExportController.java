package com.kacper.wedding_planner.controller;

import com.kacper.wedding_planner.model.Guest;
import com.kacper.wedding_planner.service.GuestService;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.awt.*;
import java.io.IOException;
import java.util.List;

@Controller
public class PDFExportController {

    private final GuestService guestService;

    public PDFExportController(GuestService guestService) {
        this.guestService = guestService;
    }

    @GetMapping("/guests/export/pdf")
    public void exportGuestListToPDF(HttpServletResponse response) throws IOException {

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=lista_gosci.pdf");

        List<Guest> guests = guestService.getAllGuests();

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
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

        PdfPCell header = new PdfPCell();
        header.setBackgroundColor(Color.PINK);
        header.setPadding(5);

        header.setPhrase(new Phrase("Nazwisko", fontHeader));
        table.addCell(header);
        header.setPhrase(new Phrase("Imię", fontHeader));
        table.addCell(header);
        header.setPhrase(new Phrase("Obecność", fontHeader));
        table.addCell(header);
        header.setPhrase(new Phrase("Kontakt", fontHeader));
        table.addCell(header);

        for (Guest g : guests) {
            table.addCell(new Phrase(g.getNazwisko(), fontCell));
            table.addCell(new Phrase(g.getImie(), fontCell));
            table.addCell(new Phrase(g.getPotwierdzenieObecnosci(), fontCell));
            table.addCell(new Phrase(g.getKontakt(), fontCell));
        }

        document.add(table);
        document.close();
    }

}
