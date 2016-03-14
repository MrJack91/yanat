package ch.zhaw.moba.yanat.domain.model;


import android.content.Context;

import java.util.List;

import ch.zhaw.moba.yanat.domain.repository.PointRepository;

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

    public PointRepository getPointRepository(Context context) {
        if (points == null) {
            pointRepository = new PointRepository(context, this.id);
        }
        return pointRepository;
    }
}
