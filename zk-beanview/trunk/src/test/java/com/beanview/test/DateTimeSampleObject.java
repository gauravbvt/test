package com.beanview.test;


import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * 
 */
public class DateTimeSampleObject
{
    Date dateTest = new Date(new java.util.Date().getTime());

    Time timeTest = new Time(new java.util.Date().getTime());

    Timestamp timeStampTest = new Timestamp(new java.util.Date().getTime());

    public Date getDateTest()
    {
        return this.dateTest;
    }

    public void setDateTest(Date dateTest)
    {
        this.dateTest = dateTest;
    }

    public Timestamp getTimeStampTest()
    {
        return this.timeStampTest;
    }

    public void setTimeStampTest(Timestamp timeStampTest)
    {
        this.timeStampTest = timeStampTest;
    }

    public Time getTimeTest()
    {
        return this.timeTest;
    }

    public void setTimeTest(Time timeTest)
    {
        this.timeTest = timeTest;
    }
}
