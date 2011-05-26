package com.androidrecord;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import static java.util.Calendar.*;

public class DateTime implements Comparable<DateTime> {

    private static final String FORMAT = "yyyy-MM-dd HH:mm:ss";
    private GregorianCalendar calendar;

    public DateTime() {
        this.calendar = new GregorianCalendar();
    }

    public DateTime(GregorianCalendar calendar) {
        this.calendar = calendar;
    }

    public int getDay() {
        return calendar.get(DATE);
    }

    public int getMonth() {
        return calendar.get(MONTH);
    }

    public int getYear() {
        return calendar.get(YEAR);
    }

    public int getHour() {
        return calendar.get(HOUR_OF_DAY);
    }

    public int getMinutes() {
        return calendar.get(MINUTE);
    }

    public int getSeconds() {
        return calendar.get(SECOND);
    }

    public void setDay(int day) {
        calendar.set(DATE, day);
    }

    public void setMonth(int month) {
        calendar.set(MONTH, month);
    }

    public void setYear(int year) {
        calendar.set(YEAR, year);
    }

    public void setHour(int hour) {
        calendar.set(HOUR_OF_DAY, hour);
    }

    public void setMinutes(int minutes) {
        calendar.set(MINUTE, minutes);
    }

    public void setSeconds(int seconds) {
        calendar.set(SECOND, seconds);
    }

    public static DateTime from(String dateString) {
        try {
            Date parsedDate = format().parse(dateString);
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(parsedDate);

            return new DateTime(calendar);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private static SimpleDateFormat format() {
        return new SimpleDateFormat(FORMAT);
    }

    public static DateTime now() {
        return new DateTime();
    }

    public String display(String format) {
        return new SimpleDateFormat(format).format(calendar.getTime());
    }

    public static DateTime copy(DateTime dateTime) {
        DateTime copy = new DateTime();
        copy.calendar = (GregorianCalendar) dateTime.calendar.clone();
        return copy;
    }

    public boolean isBefore(DateTime dateTime) {
        return differenceTo(dateTime) < 0;
    }

    public boolean isAfter(DateTime dateTime) {
        return differenceTo(dateTime) > 0;
    }

    private long differenceTo(DateTime dateTime) {
        return calendar.getTimeInMillis() - dateTime.calendar.getTimeInMillis();
    }

    public int compareTo(DateTime dateTime) {
        long difference = differenceTo(dateTime);
        if (difference < 0) return -1;
        if (difference > 0) return 1;
        return 0;
    }

    @Override
    public String toString() {
        return format().format(calendar.getTime());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DateTime)) return false;

        DateTime dateTime = (DateTime) o;

        if (calendar != null ? !calendar.equals(dateTime.calendar) : dateTime.calendar != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return calendar != null ? calendar.hashCode() : 0;
    }
}
