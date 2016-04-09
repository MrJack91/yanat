package ch.zhaw.moba.yanat.utility;

import android.content.Context;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import ch.zhaw.moba.yanat.domain.model.Point;
import ch.zhaw.moba.yanat.domain.model.Project;
import ch.zhaw.moba.yanat.domain.repository.PointRepository;

/**
 * Created by michael on 21.03.16.
 */
public class PdfGenerator {

    protected Project project;

    public static final float POINT_TO_MM = (float) 0.352778;

    public PdfGenerator() {

    }

    public String buildPdf(String filename, Project project, Context context) throws DocumentException, IOException {

        PointRepository pointRepository = project.getPointRepository(context);
        List<Point> points = pointRepository.findAll();

        PdfReader template = new PdfReader(project.getPdf());

        // step 1
        Rectangle rectangle = new Rectangle(project.getPdfWidth()/PdfGenerator.POINT_TO_MM, project.getPdfHeight()/PdfGenerator.POINT_TO_MM);
        Document document = new Document(rectangle, 0, 0, 0, 0);

        // step 2
        PdfWriter newPdf = PdfWriter.getInstance(document, new FileOutputStream(filename));
        // PdfCopy newPdf = new PdfCopy(document, new FileOutputStream(filename));

        // step 3
        document.open();

        // step 4
        // import first fpage
        PdfImportedPage page = newPdf.getImportedPage(template, 1);

        PdfContentByte cb = newPdf.getDirectContent();
        cb.addTemplate(page, 0, 0);

        for (Point point : points) {
            this.addMarker(newPdf, point);
        }

        // step 5
        document.close();

        template.close();
        newPdf.close();

        return filename;
    }

    /**
     *
     * @param newPdf
     * @param point
     */
    protected void addMarker(PdfWriter newPdf, Point point) {
        // cast to mm from bottom left corner
        double x = point.getPosX() / POINT_TO_MM;
        double y = point.getPosY() / POINT_TO_MM;

        // build text
        int textLines = 2;
        String text =
                "Abs.: " + point.getHeightAbsolute() + "\n" +
                "Rel.: " + point.getHeightRelative();
        // todo: fix: if groundfloor is zero -> means not null
        if (point.getHeightToGroundFloor() > 0 || point.isGroundFloor()) {
            textLines++;
            text += "\nGF: " + point.getHeightToGroundFloor();
        }
        if (point.getComment().length() > 0) {
            textLines++;
            text += "\n" + point.getComment();
        }

        // add pointer
        PdfContentByte canvas = newPdf.getDirectContent();

        PdfGState gState = new PdfGState();
        gState.setFillOpacity(0.4f);
        gState.setStrokeOpacity(0.8f);
        canvas.setGState(gState);
        canvas.setColorStroke(BaseColor.RED);
        canvas.setLineWidth(3);

        // pin
        canvas.arc(x-20, y+20, x+20, y+60, 0, (float) 180);
        canvas.lineTo(x, y);
        canvas.closePathStroke();
        canvas.closePathFillStroke();

        canvas.setColorFill(BaseColor.RED);
        canvas.circle(x, y+40, 10);
        canvas.fill();

        // add text box
        float llx = 35;  // low left x
        float lly = 0;
        float width = 90;
        float height = 80;
        canvas.roundRectangle(x+llx, y+lly, width, height, 2);
        canvas.stroke();
        // canvas.setColorFill(BaseColor.WHITE);
        canvas.fill();

        // add text
        gState = new PdfGState();
        gState.setFillOpacity(1f);
        gState.setStrokeOpacity(1);
        canvas.setGState(gState);
        Rectangle rect = new Rectangle((float) (x+llx+8), (float) (y+lly+3), (float) (x+llx+width-3), (float) (y+lly+height));
        // (4-textLines)*10
        ColumnText ct = new ColumnText(canvas);
        ct.setSimpleColumn(rect);
        ct.addElement(new Paragraph(text));
        try {
            ct.go();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }


}
