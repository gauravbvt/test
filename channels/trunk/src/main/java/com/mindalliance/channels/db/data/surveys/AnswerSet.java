package com.mindalliance.channels.db.data.surveys;

import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.db.data.AbstractChannelsDocument;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Answers given to a survey question by an individual.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/30/13
 * Time: 12:34 PM
 */
@Document( collection = "surveys" )
public class AnswerSet extends AbstractChannelsDocument {

    private String questionUid;

    private boolean shared = true; // answers are shared by default

    private boolean anonymous;

    private String comment;

    private List<Answer> answers = new ArrayList<Answer>();

    public AnswerSet() {
    }

    public AnswerSet( PlanCommunity planCommunity, ChannelsUser user ) {
        super( planCommunity, user );
    }

    public String getQuestionUid() {
        return questionUid;
    }

    public void setQuestionUid( String questionUid ) {
        this.questionUid = questionUid;
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

    public void removeAnswer( Answer answer ) {
        answers.remove( answer );
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
        this.comment = comment;
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
        List<Answer> toBeRemoved = (List<Answer>) CollectionUtils.select(
                getAnswers(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        Answer answer = (Answer) object;
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
