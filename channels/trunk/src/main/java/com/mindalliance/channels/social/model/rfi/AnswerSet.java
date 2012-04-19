package com.mindalliance.channels.social.model.rfi;

import com.mindalliance.channels.core.orm.model.AbstractPersistentPlanObject;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/16/12
 * Time: 1:49 PM
 */

@Entity
public class AnswerSet extends AbstractPersistentPlanObject {

    @ManyToOne
    private Question question;

    @ManyToOne
    private RFI rfi;

    private boolean shared;

    private boolean anonymous;

    @OneToMany( mappedBy = "answerSet", cascade = CascadeType.ALL )
    @OrderBy( "sequence" )
    private List<Answer> answers = new ArrayList<Answer>();

    public Question getQuestion() {
        return question;
    }

    public void setQuestion( Question question ) {
        this.question = question;
    }

    public RFI getRfi() {
        return rfi;
    }

    public void setRfi( RFI rfi ) {
        this.rfi = rfi;
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared( boolean shared ) {
        this.shared = shared;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public void setAnonymous( boolean anonymous ) {
        this.anonymous = anonymous;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers( List<Answer> answers ) {
        this.answers = answers;
    }

    public void addAnswer( Answer answer ) {
        answers.add( answer );
    }

    public Answer getAnswer() {
        return answers.isEmpty() ? null : answers.get( 0 );
    }

    public void setAnswer( Answer answer ) {
        if ( answers.isEmpty() ) {
            answers.add( answer );
        } else {
            answers.set( 0, answer );
        }
    }

    public boolean isEmpty() {
        return getAnswers().isEmpty();
    }
}
