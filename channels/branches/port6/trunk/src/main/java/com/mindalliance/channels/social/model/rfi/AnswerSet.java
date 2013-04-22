package com.mindalliance.channels.social.model.rfi;

import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.orm.model.AbstractPersistentChannelsObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import java.util.ArrayList;
import java.util.List;

/**
 * An ansn answer set.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/16/12
 * Time: 1:49 PM
 */

@Entity
public class AnswerSet extends AbstractPersistentChannelsObject {

    @ManyToOne
    private Question question;

    @ManyToOne
    private RFI rfi;

    private boolean shared = true; // answers are shared by default

    private boolean anonymous;

    @Column( length = 10000 )
    private String comment;

    @OneToMany( mappedBy = "answerSet", cascade = CascadeType.ALL )
    @OrderBy( "sequence" )
    private List<Answer> answers = new ArrayList<Answer>();

    public AnswerSet() {
    }

    public AnswerSet( PlanCommunity planCommunity, ChannelsUser user, RFI rfi, Question question ) {
        super( planCommunity, user );
        this.rfi = rfi;
        this.question = question;
    }

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
        List<Answer> allAnswers = getValidAnswers();
        return allAnswers.isEmpty() ? null : allAnswers.get( 0 );
    }

    @SuppressWarnings( "unchecked" )
    public List<Answer> getValidAnswers() {
        return (List<Answer>) CollectionUtils.select(
                getAnswers(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return !( (Answer) object ).wasRemoved();
                    }
                }
        );
    }

    public String getComment() {
        return comment;
    }

    public void setComment( String comment ) {
        this.comment = StringUtils.abbreviate( comment, 10000 );
    }

    public Answer getOrCreateAnswer() {
        Answer answer = getAnswer();
        if ( answer == null ) {
            answer = new Answer( this );
            addAnswer( answer );
        }
        return answer;
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

    public String getSingleChoice() {
        Answer answer = getAnswer();
        return answer == null ? null : answer.getText();
    }


    public void selectSingleChoice( String choice ) {
        Answer answer = getAnswer();
        if ( answer == null ) {
            answer = new Answer( this );
            answers.add( answer );
        }
        if ( answer.getText().equals( choice ) ) {
            answer.remove();
        } else {
            answer.setText( choice );
        }
    }

    public boolean isChoiceSelected( final String choice ) {
        return CollectionUtils.exists(
                getValidAnswers(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (Answer) object ).getText().equals( choice );
                    }
                }
        );
    }

    public void addChoice( String choice ) {
        if ( !isChoiceSelected( choice ) ) {
            Answer answer = new Answer( this );
            answer.setText( choice );
            answers.add( answer );
        }
    }

    public void removeChoice( final String choice ) {
        Answer answer = (Answer) CollectionUtils.find(
                getValidAnswers(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (Answer) object ).getText().equals( choice );
                    }
                }
        );
        if ( answer != null ) answer.remove();
    }

    public void deleteAnswer( Answer answer ) {
        answers.remove( answer );
    }

    @SuppressWarnings( "unchecked" )
    public List<Answer> deleteRemovedOrEmptyAnswers() {
        List<Answer> toBeRemoved = (List<Answer>)CollectionUtils.select(
                getAnswers(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        Answer answer = (Answer)object;
                        return answer.wasRemoved() || !answer.isGiven();
                    }
                }
        );
        for ( Answer answer : toBeRemoved ) {
            answers.remove( answer );
        }
        return toBeRemoved;
    }
}
