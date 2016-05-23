package ch.zhaw.moba.yanat.utility;

import android.content.Context;
import android.util.Log;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
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
public class PdfGeneratorUtility {

    protected Project project;

    public static final float POINT_TO_MM = (float) 0.352778;
    private int textsize;

    public PdfGeneratorUtility() {

    }

    public String buildPdf(String filename, Project project, Context context, int textsize) throws DocumentException, IOException {
        this.project = project;
        this.textsize = textsize;

        PointRepository pointRepository = project.getPointRepository(context);
        List<Point> points = pointRepository.findAll();

        // group points by coordinates
        List<List> groupedPoints = pointRepository.groupPointsByCoordinates(points);

        PdfReader template = new PdfReader(project.getPdf());

        // step 1
        Rectangle rectangle = new Rectangle(project.getPdfWidth() / PdfGeneratorUtility.POINT_TO_MM, project.getPdfHeight() / PdfGeneratorUtility.POINT_TO_MM);
        Document document = new Document(rectangle, 0, 0, 0, 0);

        // step 2
        PdfWriter newPdf = PdfWriter.getInstance(document, new FileOutputStream(filename));

        // step 3
        document.open();

        // step 4
        // import first fpage
        PdfImportedPage page = newPdf.getImportedPage(template, 1);

        PdfContentByte cb = newPdf.getDirectContent();
        cb.addTemplate(page, 0, 0);

        for (List<Point> groupOfPoints : groupedPoints) {
            this.addMarker(newPdf, groupOfPoints);
        }

        // step 5
        document.close();

        template.close();
        newPdf.close();

        return filename;
    }

    /**
     * @param newPdf
     * @param points
     */
    protected void addMarker(PdfWriter newPdf, List<Point> points) {
        Point pointCommon = points.get(0);
        // cast to mm from bottom left corner
        double x = pointCommon.getPosX() / POINT_TO_MM;
        double y = (project.getPdfHeight() - pointCommon.getPosY()) / POINT_TO_MM;

        Log.v("YANAT pdf marker", "(x,y)pt => (x,y)mm" + pointCommon.getPosX() + ", " + pointCommon.getPosY() + " -> " + x + ", " + y);

        PdfContentByte canvas = newPdf.getDirectContent();
        PdfGState gState = new PdfGState();
        gState.setFillOpacity(0.4f);
        gState.setStrokeOpacity(0.7f);
        canvas.setGState(gState);


        // add pointer
        canvas.setColorStroke(BaseColor.RED);
        canvas.setLineWidth(3);

        // pin
        canvas.arc(x - 20, y + 20, x + 20, y + 60, 0, (float) 180);
        canvas.lineTo(x, y);
        canvas.closePathStroke();
        canvas.closePathFillStroke();

        //// pin point inside
        canvas.setColorFill(BaseColor.RED);
        canvas.circle(x, y + 40, 10);
        canvas.fill();


        // build text
        int textLines = 0;
        String text = "";
        for (Point point : points) {
            textLines += 2;
            text +=
                    point.getTitle() + ": " +
                            GeneralUtility.formatHeight(point.getHeightAbsolute()) + "m (abs.)\n" +
                            GeneralUtility.formatHeight(point.getHeightRelative()) + "m (rel.)\n";
            if (point.getHeightToGroundFloor() != null && !point.isGroundFloor()) {
                textLines++;
                text += GeneralUtility.formatHeight(point.getHeightToGroundFloor()) + "m (EG)\n";
            } else if (point.isGroundFloor()) {
                textLines++;
                text += "ist EG\n";
            }
            if (point.getComment().length() > 0) {
                textLines++;
                text += point.getComment() + "\n";
            }
        }

        // add text box
        float llx = 35;  // low left x
        float lly = 0;
        float width = 115;
        float height = 82;
        float fontSizeFactor = (float)(textsize / (float)12);
        float lineCorrection = (4 - textLines) * 20 * fontSizeFactor;

        // add background of text box
        gState = new PdfGState();
        gState.setFillOpacity(0.8f);
        gState.setStrokeOpacity(0.8f);
        canvas.setGState(gState);
        canvas.roundRectangle(x + llx, y + lly + lineCorrection, width * fontSizeFactor, ((height * fontSizeFactor) - lineCorrection), 2);
        canvas.setColorFill(BaseColor.WHITE);
        canvas.fillStroke();

        // add text
        canvas.setColorFill(BaseColor.RED);
        gState = new PdfGState();
        gState.setFillOpacity(1f);
        gState.setStrokeOpacity(1);
        canvas.setGState(gState);

        // build text rectangle
        Rectangle rect = new Rectangle(
                (float) (x + llx + 8),                                  // lower left x
                (float) (y + lly + 3 + lineCorrection),                 // lower left y
                (float) (x + llx + (width*fontSizeFactor) - 3),         // upper right x
                (float) (y + lly + (height*fontSizeFactor))             // upper right y
        );
        rect.setBackgroundColor(BaseColor.LIGHT_GRAY);

        ColumnText ct = new ColumnText(canvas);
        ct.setSimpleColumn(rect);
        Font font = new Font();
        font.setSize(textsize);
        Paragraph p = new Paragraph(text, font);
        ct.addElement(p);

        try {
            ct.go();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }


}
