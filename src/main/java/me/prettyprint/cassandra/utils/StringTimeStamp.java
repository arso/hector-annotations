package me.prettyprint.cassandra.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Date: 2010-06-16
 * Time: 06:37:19
 */
public class StringTimeStamp {
    private static final SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    private String dateString;
    private Date date;

    public StringTimeStamp(Date date){
        if (date == null) throw new IllegalArgumentException("Date param cannot be null");

        this.date  = new Date (date.getTime());
        dateString = formater.format(date);
    }

    public StringTimeStamp(String date){
        if(!StringUtils.hasLength(date)) throw new IllegalArgumentException("String date param cannot be null or empty");
        
        try {
            this.date = formater.parse(date) ;
        } catch (ParseException e) {
          throw new IllegalArgumentException(e);
        }
    }

    public String getDateString(){
        return dateString;
    }

    public Date getDate(){
        return new Date(date.getTime());
    }
}
