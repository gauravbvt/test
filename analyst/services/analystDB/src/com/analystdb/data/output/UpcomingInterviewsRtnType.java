
package com.analystdb.data.output;

import java.util.Date;
import com.analystdb.data.Interview;


/**
 * Generated for query "upcomingInterviews" on 04/04/2013 13:41:27
 * 
 */
public class UpcomingInterviewsRtnType {

    private Interview interview;
    private Date scheduled;
    private String name;

    public Interview getInterview() {
        return interview;
    }

    public void setInterview(Interview interview) {
        this.interview = interview;
    }

    public Date getScheduled() {
        return scheduled;
    }

    public void setScheduled(Date scheduled) {
        this.scheduled = scheduled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
