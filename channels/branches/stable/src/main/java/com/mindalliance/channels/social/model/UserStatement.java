package com.mindalliance.channels.social.model;

import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.query.PlanService;
import com.mindalliance.channels.social.services.SurveysDAO;
import com.mindalliance.channels.social.services.notification.Messageable;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Abstract user statement.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/29/12
 * Time: 10:40 AM
 */
@Entity
abstract public class UserStatement extends AbstractModelObjectReferencingPPO implements Messageable {

    public static final String TEXT = "text";

    @Column( length = 3000 )
    private String text;

    public UserStatement() {
    }

    public UserStatement( String planUri, int planVersion, String username ) {
        super( planUri, planVersion, username );
    }


    public UserStatement( String planUri, int planVersion, String username, String text ) {
        super( planUri, planVersion, username );
        this.text = text;
    }

    public UserStatement( String planUri, int planVersion, String username, String text, ModelObject modelObject ) {
        super( planUri, planVersion, username, modelObject );
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText( String text ) {
        this.text = text;
    }


    /// Messageable

    @Override
    public String getFromUsername( String topic ) {
        return getUsername();
    }

    @Override
    public String getContent(
            String topic,
            Format format,
            PlanService planService,
            SurveysDAO surveysDAO ) {
        if ( topic.equals( TEXT ) ) return getTextContent( format, planService );
        else throw new RuntimeException( "invalid content" );
    }

    @Override
    public String getSubject(
            String topic,
            Format format,
            PlanService planService,
            SurveysDAO surveysDAO ) {
        if ( topic.equals( TEXT ) ) return getTextSubject( format, planService  );
        else throw new RuntimeException( "invalid content" );
    }

    @Override
    public String getLabel() {
        return "Message";
    }


    abstract protected String getTextContent( Format format, PlanService planService );


    abstract protected String getTextSubject( Format format, PlanService planService );


}