package fr.s4e2.ouatelse.utils;

import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfName;

/**
 * Multitude of reusable and useful functions specific to PDF creation
 */
public class PDFUtils {

    /**
     * Empty Constructor
     */
    private PDFUtils() {
    }

    /**
     * Creates an h1 type title
     *
     * @param title the text to insert in the h1
     * @return an h1 type title
     */
    public static Paragraph buildH1Title(String title) {
        Paragraph titleParagraph = new Paragraph(title);
        titleParagraph.setRole(PdfName.H1);
        titleParagraph.setSpacingAfter(10);
        titleParagraph.getFont().setSize(15f);
        titleParagraph.setAlignment(Element.ALIGN_CENTER);

        return titleParagraph;
    }

    /**
     * Creates an h2 type title
     *
     * @param title the text to insert in the h2
     * @return an h2 type title
     */
    public static Paragraph buildH2Title(String title) {
        Paragraph titleParagraph = new Paragraph(title);
        titleParagraph.setRole(PdfName.H2);
        titleParagraph.setSpacingAfter(5);
        titleParagraph.getFont().setSize(12f);
        titleParagraph.setAlignment(Element.ALIGN_CENTER);

        return titleParagraph;
    }
}
