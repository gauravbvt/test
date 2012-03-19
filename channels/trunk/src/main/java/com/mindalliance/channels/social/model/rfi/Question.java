package com.mindalliance.channels.social.model.rfi;

import com.mindalliance.channels.core.orm.model.AbstractPersistentPlanObject;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/16/12
 * Time: 1:49 PM
 */
@Entity
public class Question extends AbstractPersistentPlanObject {

    public enum Type {
        STATEMENT,
        YES_NO,
        MULTIPLE_CHOICE,
        SHORT_FORM,
        LONG_FORM,
        LIST,
        DOCUMENTS
    }

    @ManyToOne( cascade = CascadeType.ALL)
    private Questionnaire questionnaire;
    
    private int index;

    private Type type = Type.STATEMENT;

    // Multiple choice question follows pattern: "Question||choice 1||choice 2||..."
    private String text = "";

    private boolean retired = false;

    public Questionnaire getQuestionnaire() {
        return questionnaire;
    }

    public void setQuestionnaire( Questionnaire questionnaire ) {
        this.questionnaire = questionnaire;
    }

    public String getText() {
        return text;
    }

    public void setText( String text ) {
        this.text = text;
    }

    public boolean isRetired() {
        return retired;
    }

    public void setRetired( boolean retired ) {
        this.retired = retired;
    }

    public Type getType() {
        return type;
    }

    public void setType( Type type ) {
        this.type = type;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex( int index ) {
        this.index = index;
    }
}
