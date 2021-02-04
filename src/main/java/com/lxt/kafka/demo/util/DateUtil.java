package com.lxt.kafka.demo.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.time.FastDateFormat;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

/**
 * @author lixt90
 */
@Slf4j
public class DateUtil {

    private DateUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static final String PATTERN_ON_WEEK = "EEE MMM dd HH:mm:ss zzz yyyy";
    public static final String PATTERN_ON_DATE = "yyyy-MM-dd";
    public static final String PATTERN_ON_SECOND = "yyyy-MM-dd HH:mm:ss";

    public static final FastDateFormat FORMATTER_ON_DATE = FastDateFormat.getInstance(PATTERN_ON_DATE);
    public static final FastDateFormat FORMATTER_ON_SECOND = FastDateFormat.getInstance(PATTERN_ON_SECOND);
    public static final FastDateFormat FORMATTER_ON_WEEK = FastDateFormat.getInstance(PATTERN_ON_WEEK, Locale.US);


    public static Date weekDateParser(Date date) {
        try {
            String weekDate = FORMATTER_ON_SECOND.format(date.getTime());
            return DateUtils.addHours(FORMATTER_ON_SECOND.parse(weekDate), -8);
        } catch (ParseException e) {
            log.error("日期转换异常:{}", e.getMessage(), e);
            return null;
        }
    }
}
