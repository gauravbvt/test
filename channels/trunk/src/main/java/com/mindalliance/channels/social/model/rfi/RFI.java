package com.mindalliance.channels.social.model.rfi;

import com.mindalliance.channels.core.orm.model.AbstractPersistentPlanObject;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/16/12
 * Time: 1:50 PM
 */
@Entity
public class RFI extends AbstractPersistentPlanObject {

    @ManyToOne( cascade = CascadeType.ALL )
    private Questionnaire questionnaire;

    private boolean declined = false;

    private String reasonDeclined = "";

    @OneToMany( mappedBy="rfi", cascade = CascadeType.ALL )
    private List<Forwarding> forwardedTo = new ArrayList<Forwarding>(  );

    @OneToMany (mappedBy="rfi", cascade = CascadeType.ALL)
    private List<AnswerSet> answerSets = new ArrayList<AnswerSet>(  );

    public Questionnaire getQuestionnaire() {
        return questionnaire;
    }

    public void setQuestionnaire( Questionnaire questionnaire ) {
        this.questionnaire = questionnaire;
    }

    public boolean isDeclined() {
        return declined;
    }

    public void setDeclined( boolean declined ) {
        this.declined = declined;
    }

    public String getReasonDeclined() {
        return reasonDeclined;
    }

    public void setReasonDeclined( String reasonDeclined ) {
        this.reasonDeclined = reasonDeclined;
    }

    public List<Forwarding> getForwardedTo() {
        return forwardedTo;
    }

    public void setForwardedTo( List<Forwarding> forwardedTo ) {
        this.forwardedTo = forwardedTo == null ? new ArrayList<Forwarding>(  ) : forwardedTo;
    }
    
    public void addForwarding( Forwarding forwarding ) {
        getForwardedTo().add( forwarding );
    }

    public List<AnswerSet> getAnswerSets() {
        return answerSets;
    }

    public void setAnswerSets( List<AnswerSet> answerSets ) {
        this.answerSets = answerSets;
    }
}
