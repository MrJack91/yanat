package ch.zhaw.moba.yanat;

import java.util.Date;

/**
 * Created by michael on 04.03.16.
 */
public class Project {

    protected String title = "";

    protected Long crdate = null;
    protected Long tstamp = null;

    public Project(String title) {
        this.title = title;

        // crdate equals tstamp
        Long tstamp = new Date().getTime();
        this.crdate = tstamp;
        this.tstamp = tstamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getCrdate() {
        return crdate;
    }

    public void setCrdate(Long crdate) {
        this.crdate = crdate;
    }

    public Long getTstamp() {
        return tstamp;
    }

    public void setTstamp(Long tstamp) {
        this.tstamp = tstamp;
    }

}
