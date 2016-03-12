package ch.zhaw.moba.yanat.domain.model;


import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by michael on 04.03.16.
 */
abstract class Model {

    protected int id = 0;

    protected Long createDate = null;
    protected Long tstamp = null;

    final String DATE_FORMAT_DAY = "dd.MM.yyyy";

    protected Model() {
        // crdate equals current tstamp
        // tstamp is in miliseconds convert to seconds
        Long tstamp = this.setCurrentTstamp();
        this.createDate = tstamp;
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

    public String getCreateDateString() {
        return this.formatDate(this.getCreateDate(), DATE_FORMAT_DAY);
    }


    public void setCreateDate(Long createDate) {
        this.createDate = createDate;
    }

    public Long getTstamp() {
        return tstamp;
    }

    public String getTstampString() {
        String dateOnDay = this.formatDate(this.getTstamp(), DATE_FORMAT_DAY);
        // if last edit on same day, show only time
        if (dateOnDay.equals(this.formatDate(this.getCurrentTstamp(), DATE_FORMAT_DAY))) {
            dateOnDay = this.formatDate(this.getTstamp(), "HH:mm:ss");
        }
        return dateOnDay;
    }

    public void setTstamp(Long tstamp) {
        this.tstamp = tstamp;
    }

    public Long setCurrentTstamp() {
        this.setTstamp(this.getCurrentTstamp());
        return tstamp;
    }

    protected String formatDate(long timestamp, String format) {
        Date currenTimeZone = this.castDate(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(currenTimeZone);
    }

    protected Date castDate(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        TimeZone tz = TimeZone.getDefault();
        calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
        Date currenTimeZone = new Date(timestamp*1000);
        return currenTimeZone;
    }

    protected long getCurrentTstamp() {
        return new Date().getTime()/1000;
    }
}
