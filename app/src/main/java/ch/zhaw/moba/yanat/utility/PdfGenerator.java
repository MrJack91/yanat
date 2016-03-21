package ch.zhaw.moba.yanat.utility;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.IOException;

import ch.zhaw.moba.yanat.domain.model.Project;

/**
 * Created by michael on 21.03.16.
 */
public class PdfGenerator {

    protected Project project;

    public PdfGenerator() {

    }

    /** Path to the resulting PDF file. */
    public static final String RESULT = "results/part1/chapter01/hello.pdf";

    public String buildPdf(String filename) throws DocumentException, IOException {

        // step 1
        Document document = new Document();
        // step 2
        PdfWriter.getInstance(document, new FileOutputStream(filename));
        // step 3
        document.open();
        // step 4
        document.add(new Paragraph("Hello World!"));
        // step 5
        document.close();


        // todo: add path to builded pdf
        return "PATH/TO/PDF";
    }

}
