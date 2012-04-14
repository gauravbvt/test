package com.mindalliance.channels.social.model.rfi;

import com.mindalliance.channels.core.dao.user.PlanParticipation;
import com.mindalliance.channels.core.orm.model.AbstractPersistentPlanObject;
import com.mindalliance.channels.core.query.QueryService;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/21/12
 * Time: 8:25 PM
 */
@Entity
public class RFIForward extends AbstractPersistentPlanObject {

    /**
     * username of user being questioned.
     */
    private String surveyedUsername;

    /**
     * Name of actor user participates as in survey.
     */
    private long actorId;

    @Column(length=3000)
    private String message = "";

    @ManyToOne
    private RFI rfi;

    public RFIForward() {}

    public RFIForward( String planUri, int planVersion, String fromUsername, PlanParticipation participation, QueryService queryService ) {
        super( planUri, planVersion, fromUsername  );
        surveyedUsername = participation.getUsername();
        actorId = participation.getActor( queryService ).getId();
    }

    public String getSurveyedUsername() {
        return surveyedUsername;
    }

    public void setSurveyedUsername( String surveyedUsername ) {
        this.surveyedUsername = surveyedUsername;
    }

    public long getActorId() {
        return actorId;
    }

    public void setActorId( long actorId ) {
        this.actorId = actorId;
    }

    public RFI getRfi() {
        return rfi;
    }

    public void setRfi( RFI rfi ) {
        this.rfi = rfi;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage( String message ) {
        this.message = message;
    }
}
