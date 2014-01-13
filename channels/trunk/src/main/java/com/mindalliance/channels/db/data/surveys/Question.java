package com.mindalliance.channels.db.data.surveys;

import com.mindalliance.channels.db.data.AbstractChannelsDocument;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * A question in a survey questionnaire.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/30/13
 * Time: 11:17 AM
 */
@Document( collection = "surveys" )
public class Question extends AbstractChannelsDocument {

    public enum Type {
        SHORT_FORM,
        YES_NO,
        CHOICE,
        LONG_FORM,
        DOCUMENT,
        STATEMENT;

        public String getLabel() {
            switch( this ) {
                case STATEMENT: return "statement";
                case YES_NO: return "yes or no";
                case CHOICE: return "choice";
                case SHORT_FORM: return "short text";
                case LONG_FORM: return "essay";
                case DOCUMENT: return "document";
                default: return "";
            }
        }
    }

    private int index = 0;

    private Type type = Type.STATEMENT;

    // Multiple choice question
    private List<String> answerChoices = new ArrayList<String>();

    private String text = "";

    private boolean answerRequired = true;

    private boolean openEnded = true;

    private boolean multipleAnswers = false;

    private boolean activated = false;

    private String questionnaireUid;

    public Question() {
    }

    public Question( String username, Questionnaire questionnaire ) {
        super( questionnaire.getCommunityUri(), questionnaire.getPlanUri(), questionnaire.getPlanVersion(), username);
        setActivated( !questionnaire.isActive() ); // activate the question on creation if the questionnaire is deactivated
        questionnaireUid = questionnaire.getUid();
    }

    public String getQuestionnaireUid() {
        return questionnaireUid;
    }

    public void setQuestionnaireUid( String questionnaireUid ) {
        questionnaireUid = questionnaireUid;
    }

    public String getText() {
        return text == null ? "" : text;
    }

    public void setText( String text ) {
        this.text = text;
    }

    public boolean isAnswerRequired() {
        return isRequirable() && answerRequired;
    }

    public void setAnswerRequired( boolean answerRequired ) {
        this.answerRequired = answerRequired;
    }

    public boolean isOpenEnded() {
        return isOpenable() && openEnded;
    }

    public void setOpenEnded( boolean openEnded ) {
        this.openEnded = openEnded;
    }

    public boolean isMultipleAnswers() {
        return isMultipleable() && multipleAnswers;
    }

    public void setMultipleAnswers( boolean multipleAnswers ) {
        this.multipleAnswers = multipleAnswers;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated( boolean activated ) {
        this.activated = activated;
    }

    public Type getType() {
        return type == null ? Type.STATEMENT : type;
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

    public List<String> getAnswerChoices() {
        return answerChoices == null ? new ArrayList<String>(  ) : answerChoices;
    }

    public void setAnswerChoices( List<String> answerChoices ) {
        this.answerChoices = answerChoices;
    }

    public boolean isRequirable() {
        return isAnswerable();
    }

    public boolean isOpenable() {
        return isAnswerable();
    }

    public boolean isMultipleable() {
        return getType() != Type.STATEMENT && getType() != Type.YES_NO;
    }

    public boolean isAnswerable() {
        return getType() != Type.STATEMENT;
    }


}

