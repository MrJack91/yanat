package ch.zhaw.moba.yanat.utility;

import android.util.Log;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
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
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
        // step 3
        document.open();
        // step 4
        document.add(new Paragraph("Hello World!"));

        // add pointer
        PdfContentByte canvas = writer.getDirectContent();
        canvas.setColorStroke(BaseColor.RED);
        canvas.setLineWidth(3);
        canvas.moveTo(220, 330);
        canvas.lineTo(240, 370);
        canvas.arc(200, 350, 240, 390, 0, (float) 180);
        canvas.lineTo(220, 330);
        canvas.closePathStroke();
        canvas.setColorFill(BaseColor.RED);
        canvas.circle(220, 370, 10);
        canvas.fill();
        document.close();

        // step 5
        document.close();




        // todo: add path to builded pdf
        return "PATH/TO/PDF";
    }

    /**
     * Inspect a PDF file and write the info to a txt file
     * @param filename Path to the PDF file
     * @throws IOException
     */
    /*
    public static void inspect(String filename) throws IOException {
        PdfReader reader = new PdfReader(filename);
        Log.v("YANAT", filename);
        Log.v("YANAT", "Number of pages: ");
        Log.v("YANAT", reader.getNumberOfPages());
        Rectangle mediabox = reader.getPageSize(1);
        Log.v("YANAT", "Size of page 1: [");
        Log.v("YANAT", mediabox.getLeft());
        Log.v("YANAT", ',');
        Log.v("YANAT", mediabox.getBottom());
        Log.v("YANAT", ',');
        Log.v("YANAT", mediabox.getRight());
        Log.v("YANAT", ',');
        Log.v("YANAT", mediabox.getTop());
        Log.v("YANAT", "]");
        Log.v("YANAT", "Rotation of page 1: ");
        Log.v("YANAT", reader.getPageRotation(1));
        Log.v("YANAT", "Page size with rotation of page 1: ");
        Log.v("YANAT", reader.getPageSizeWithRotation(1));
        Log.v("YANAT", "Is rebuilt? ");
        Log.v("YANAT", reader.isRebuilt());
        Log.v("YANAT", "Is encrypted? ");
        Log.v("YANAT", reader.isEncrypted());
        // writer.flush();
        reader.close();
    }
    */

}
