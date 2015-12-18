package com.ahoostudio.stalk.droid.stalk.helper;

import com.ahoostudio.stalk.droid.util.StringUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by nattapon on 7/19/15 AD.
 */
public class CommunicationHelper {
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String DEFAULT_DATE_FORMAT = "MM-dd-yyyy HH:mm:ss";

    public static Date dateConvertUtil(String time) throws ParseException {
        java.text.DateFormat dateFormat = new SimpleDateFormat(CommunicationHelper.DATE_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = new Date();
        if (!StringUtil.IsNullOrEmpty(time)) {
            date = dateFormat.parse(time);
        }

        return date;
    }
}
