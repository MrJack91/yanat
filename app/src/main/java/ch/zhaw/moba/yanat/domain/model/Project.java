package ch.zhaw.moba.yanat.domain.model;


import android.content.Context;
import android.util.Log;

import com.itextpdf.text.DocumentException;

import java.io.IOException;
import java.util.List;

import ch.zhaw.moba.yanat.domain.repository.PointRepository;
import ch.zhaw.moba.yanat.utility.PdfGenerator;

/**
 * Created by michael on 04.03.16.
 */
public class Project extends AbstractModel {

    protected PointRepository pointRepository = null;

    protected String title = "";
    protected String pdf = null;
    protected int pdfWidth = 0;
    protected int pdfHeight = 0;

    protected List<Point> points = null;

    public Project() {
        super();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPdf() {
        return pdf;
    }

    public void setPdf(String pdf) {
        this.pdf = pdf;
    }

    public int getPdfWidth() {
        return pdfWidth;
    }

    public void setPdfWidth(int pdfWidth) {
        this.pdfWidth = pdfWidth;
    }

    public int getPdfHeight() {
        return pdfHeight;
    }

    public void setPdfHeight(int pdfHeight) {
        this.pdfHeight = pdfHeight;
    }

    /**
     * Can only be called, after project is persist (has an ID)
     * @param context
     * @return
     */
    public PointRepository getPointRepository(Context context) {
        if (this.id == 0) {
            Log.v("YANAT-Error", "Can not call point repository without persist object. You have to persist object first");
            return null;
        }
        if (pointRepository == null) {
            pointRepository = new PointRepository(context, this.id);
        }
        return pointRepository;
    }

    public String buildPdf() {
        PdfGenerator pdfGenerator = new PdfGenerator();

        String pdfName = "/data/data/ch.zhaw.moba.yanat/files/" + this.getId() + "/test.pdf";

        String path = null;
        try {
            path = pdfGenerator.buildPdf(pdfName);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }
}
