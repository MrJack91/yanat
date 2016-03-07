package ch.zhaw.moba.yanat.domain.model;

import java.util.Date;

/**
 * Created by michael on 04.03.16.
 */
public class Project {

    protected int id = 0;

    protected Long createDate = null;
    protected Long tstamp = null;

    protected String title = "";
    protected String pdf = null;
    protected int pdfWidth = 0;
    protected int pdfHeight = 0;



    public Project() {
        // crdate equals tstamp
        Long tstamp = new Date().getTime();
        this.createDate = tstamp;
        this.tstamp = tstamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Long createDate) {
        this.createDate = createDate;
    }

    public Long getTstamp() {
        return tstamp;
    }

    public void setTstamp(Long tstamp) {
        this.tstamp = tstamp;
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
}
