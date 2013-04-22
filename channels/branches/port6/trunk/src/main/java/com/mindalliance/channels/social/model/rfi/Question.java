package com.mindalliance.channels.social.model.rfi;

import com.mindalliance.channels.core.orm.model.AbstractPersistentChannelsObject;
import org.apache.commons.lang.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/16/12
 * Time: 1:49 PM
 */
@Entity
public class Question extends AbstractPersistentChannelsObject {




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
    
    public static final String MULTIPLE_CHOICE_SEPARATOR = ":::" ;

    @ManyToOne
    private Questionnaire questionnaire;
    
    private int index = 0;

    private Type type = Type.STATEMENT;

    // Multiple choice question follows pattern: "Question:::choice 1:::choice 2:::..."
    private String options = "";

    @Column( length = 5000 )
    private String text = "";

    private boolean answerRequired = true;

    private boolean openEnded = true;

    private boolean multipleAnswers = false;

    private boolean activated = false;

    public Question() {
    }

    public Question( String username, Questionnaire questionnaire ) {
        super( questionnaire.getCommunityUri(), questionnaire.getPlanUri(), questionnaire.getPlanVersion(), username);
        this.questionnaire = questionnaire;
    }

    public Questionnaire getQuestionnaire() {
        return questionnaire;
    }

    public void setQuestionnaire( Questionnaire questionnaire ) {
        this.questionnaire = questionnaire;
    }

    public String getText() {
        return text == null ? "" : text;
    }

    public void setText( String text ) {
        this.text = StringUtils.abbreviate( text, 5000 );
    }

    public String getOptions() {
        return options == null ? "" : options.trim();
    }

    public void setOptions( String options ) {
        this.options = options;
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
        assert getType() == Type.CHOICE;
        return getOptions().isEmpty()
                ? new ArrayList<String>()
                : new ArrayList<String>( Arrays.asList( getOptions().split( Question.MULTIPLE_CHOICE_SEPARATOR ) ) );
    }

    public void setAnswerChoices( List<String> choices ) {
        String s = choices.isEmpty()
            ? ""
            : StringUtils.join( choices, MULTIPLE_CHOICE_SEPARATOR );
        setOptions( s );
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
