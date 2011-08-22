package com.mindalliance.channels.surveys;

import java.io.Serializable;
import java.util.Date;

/**
 * A survey response.
 */
public class SurveyResponse implements Serializable {

    public enum Status {
        TBD,
        Partial,
        Complete,
        Abandoned
    }

    private Status status;
    private final Survey survey;
    private final String userEmail;
    private final Date date;

    public SurveyResponse( Survey survey, Status status, String userEmail ) {
        this.survey = survey;
        this.status = status;
        this.userEmail = userEmail;
        date = new Date();
    }

    public void setStatus( Status status ) {
        this.status = status;
    }

    public boolean isComplete() {
        return status == Status.Complete;
    }

    public boolean isTBD() {
        return status == Status.TBD;
    }

    public boolean isPartial() {
        return status == Status.Partial;
    }

    public boolean isAbandoned() {
        return status == Status.Abandoned;
    }

    public Survey getSurvey() {
        return survey;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public Date getDate() {
        return date;
    }

    public String getStatusLabel() {
        return isComplete()
                ? "completed"
                : status.name().toLowerCase();
    }


}
