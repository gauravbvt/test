
package com.analystdb.data.output;

import java.util.Date;
import com.analystdb.data.Interview;


/**
 * Generated for query "upcomingInterviews" on 02/11/2013 20:47:46
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
