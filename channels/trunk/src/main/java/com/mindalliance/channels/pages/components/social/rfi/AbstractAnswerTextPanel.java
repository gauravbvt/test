package com.mindalliance.channels.pages.components.social.rfi;

import com.mindalliance.channels.db.data.surveys.Answer;
import com.mindalliance.channels.db.data.surveys.Question;
import com.mindalliance.channels.db.data.surveys.RFI;
import org.apache.wicket.model.IModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/1/12
 * Time: 10:36 PM
 */
public abstract class AbstractAnswerTextPanel extends AbstractAnswerPanel {

    public AbstractAnswerTextPanel( String id, IModel<Question> questionModel, IModel<RFI> rfiModel ) {
        super( id, questionModel, rfiModel );
    }

    protected List<AnswerWrapper> getAnswerWrappers() {
        List<AnswerWrapper> wrappers = new ArrayList<AnswerWrapper>();
        if ( getQuestion().isMultipleAnswers() ) {
            for ( Answer answer : getAnswerSet().getValidAnswers() ) {
                wrappers.add( new AnswerWrapper( answer ) );
            }
            wrappers.add( new AnswerWrapper() );
        } else {
            Answer answer = getAnswerSet().getAnswer();
            if ( answer == null ) {
                wrappers.add( new AnswerWrapper() );
            } else {
                wrappers.add( new AnswerWrapper( answer ) );
            }
        }
        return wrappers;
    }

    public class AnswerWrapper implements Serializable {
        private Answer answer;

        protected AnswerWrapper() {
        }

        protected AnswerWrapper( Answer answer ) {
            this.answer = answer;
        }

        public String getText() {
            return answer == null ? "" : answer.getText();
        }

        public void setText( String text ) {
            if ( text == null || text.isEmpty() ) {
                if ( answer != null ) {
                    answer.remove();
                }
            } else {
                if ( answer == null ) {
                    answer = new Answer( getAnswerSet() );
                    getAnswerSet().addAnswer( answer );
                }
                answer.setText( text );
            }
        }

        public String getUrl() {
            return answer == null ? "" : answer.getText();
        }

        public void setUrl( String text ) {
            if ( text == null || text.isEmpty() ) {
                if ( answer != null ) {
                    answer.remove();
                }
            } else {
                if ( answer == null ) {
                    answer = new Answer( getAnswerSet() );
                    getAnswerSet().addAnswer( answer );
                }
                answer.setText( asUrl( text ) );
            }
        }

        private String asUrl( String text ) {
            String url = text.toLowerCase();
            return ( url.startsWith( "http://" ) || url.startsWith( "https://" ) )
                    ? text
                    : "http://" + text;
        }

    }


}
