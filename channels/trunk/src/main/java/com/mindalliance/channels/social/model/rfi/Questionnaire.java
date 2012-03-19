package com.mindalliance.channels.social.model.rfi;

import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.orm.model.AbstractPersistentPlanObject;
import com.mindalliance.channels.pages.Channels;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/16/12
 * Time: 1:40 PM
 */
@Entity
public class Questionnaire extends AbstractPersistentPlanObject {

    public enum Status {
        /**
         * RFI surveys can be launched with the questionnaire.
         */
        ACTIVE,
        /**
         * RFI surveys can no longer be launched with the questionnaire.
         */
        RETIRED,
        /**
         * RFI surveys can not yet be launched with the questionnaire.
         */
        DRAFT
    }

    public static final Questionnaire UNKNOWN = new Questionnaire( Channels.UNKNOWN_QUESTIONNAIRE_ID );

    
    private String about = ModelObject.typeNames().get( 0 );

    private String name ="unnamed";

    @OneToMany( mappedBy="questionnaire", cascade = CascadeType.ALL)
    @OrderBy( "index")
    private List<Question> questions = new ArrayList<Question>(  );

    private Status status = Status.DRAFT;
    
    public Questionnaire() {}

    public Questionnaire( long id ) {
        this.id = id;
    }
    
    public boolean isUnknown() {
        return this.equals( UNKNOWN );
    }

    public String getAbout() {
        return about;
    }

    public void setAbout( String typeName ) {
        about = typeName;
    }

    public String getName() {
        return ( name == null || name.isEmpty() )? "unnamed" : name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions( List<Question> questions ) {
        this.questions = questions;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus( Status status ) {
        this.status = status;
    }


}
