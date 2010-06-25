package com.mindalliance.channels.surveys;

import java.io.Serializable;

/**
 * Summary data about a survey.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Aug 21, 2009
 * Time: 12:12:35 PM
 */
public class SurveyData implements Serializable {

    /**
     * How many completed the survey.
     */
    private int countCompleted = 0;
    /**
     * How many are working on the survey.
     */
    private int countInProgress = 0;
    /**
     * How many abandoned the survey.
     */
    private int countAbandoned = 0;
    /**
     * How many partially completed the survey.
     */
    private int countPartial = 0;

    /**
     * preview link.
     */
    private String previewLink = "";
    /**
     * Reporting link.
     */
    private String reportingLink = "";
    /**
     * Publish link
     */
    private String publishLink = "";
    /**
     * Survey status.
     */
    private Survey.Status status;

    public int getCountCompleted() {
        return countCompleted;
    }

    public void setCountCompleted( int countCompleted ) {
        this.countCompleted = countCompleted;
    }

    public int getCountInProgress() {
        return countInProgress;
    }

    public void setCountInProgress( int countInProgress ) {
        this.countInProgress = countInProgress;
    }

    public int getCountAbandoned() {
        return countAbandoned;
    }

    public void setCountAbandoned( int countAbandoned ) {
        this.countAbandoned = countAbandoned;
    }

    public int getCountPartial() {
        return countPartial;
    }

    public void setCountPartial( int countPartial ) {
        this.countPartial = countPartial;
    }

    public String getPreviewLink() {
        return previewLink;
    }

    public void setPreviewLink( String previewLink ) {
        this.previewLink = previewLink;
    }

    public String getReportingLink() {
        return reportingLink;
    }

    public void setReportingLink( String reportingLink ) {
        this.reportingLink = reportingLink;
    }

    public String getPublishLink() {
        return publishLink;
    }

    public void setPublishLink( String publishLink ) {
        this.publishLink = publishLink;
    }

    public Survey.Status getStatus() {
        return status;
    }

    public void setStatus( Survey.Status status ) {
        this.status = status;
    }
}
