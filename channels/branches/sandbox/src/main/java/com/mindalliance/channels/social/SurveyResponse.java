package com.mindalliance.channels.social;

import com.mindalliance.channels.surveys.Survey;

import java.io.Serializable;
import java.util.Date;

/**
 * A survey response.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/14/11
 * Time: 11:17 AM
 */
public class SurveyResponse implements Serializable {

    public enum Status {
        TBD,
        Partial,
        Complete,
        Abandoned
    }

    private Status status;
    private Survey survey;
    private String userEmail;
    private Date date;

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
