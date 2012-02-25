package com.mindalliance.channels.core.community.rfi;

import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.orm.model.AbstractPersistentPlanObject;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
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
        DRAFT,
        ACTIVE,
        RETIRED
    }
    
    private String modelObjectClassName = Plan.class.getSimpleName();

    private String name ="unnamed";

    @OneToMany( mappedBy = "questionnaire", cascade = CascadeType.ALL )
    private List<RFI> rfis = new ArrayList<RFI>();

    @OneToMany( mappedBy="questionnaire", cascade = CascadeType.ALL)
    private List<Question> questions = new ArrayList<Question>(  );

    private Status status = Status.DRAFT;

    public String getModelObjectClassName() {
        return modelObjectClassName;
    }

    public void setModelObjectClassName( String modelObjectClassName ) {
        this.modelObjectClassName = modelObjectClassName;
    }

    public String getName() {
        return name;
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

    public List<RFI> getRfis() {
        return rfis;
    }

    public void setRfis( List<RFI> rfis ) {
        this.rfis = rfis;
    }
}
