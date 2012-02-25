package com.mindalliance.channels.pages.components.support;

import com.mindalliance.channels.core.command.ModelObjectRef;
import com.mindalliance.channels.core.community.feedback.Feedback;
import com.mindalliance.channels.core.community.feedback.FeedbackService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.AjaxIndicatorAwareContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.http.request.WebClientInfo;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailSender;

/**
 * Feedback panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/2/11
 * Time: 1:07 PM
 */
public class UserFeedbackPanel extends AbstractUpdatablePanel {

    /**
     * The logger.
     */
    private final Logger LOG = LoggerFactory.getLogger( UserFeedbackPanel.class );
    private final static String LABEL = "Send feedback";

    @SpringBean
    private MailSender mailSender;

    @SpringBean
    private FeedbackService feedbackService;

    private Identifiable about;
    private WebMarkupContainer feedbackContainer;
    private boolean question = true;
    private boolean problem;
    private boolean suggestion;
    private boolean asap;
    private String content = "";
    private TextArea<String> contentText;
    private AjaxLink sendButton;
    private AjaxCheckBox suggestionCheckBox;
    private AjaxCheckBox problemCheckBox;
    private AjaxCheckBox questionCheckBox;
    private String feedbackLabel;
    private String topic;


    public UserFeedbackPanel( String id ) {
        this( id, null, null, null );
    }

    public UserFeedbackPanel( String id, String topic ) {
        this( id, null, null, topic );
    }


    public UserFeedbackPanel( String id, Identifiable about, String label ) {
        this( id, about, label, null );
    }

    public UserFeedbackPanel( String id, Identifiable about, String label, String topic ) {
        super( id );
        this.about = about;
        feedbackLabel = label;
        this.topic = topic;
        init();
    }


    private void init() {
        addNewFeedbackLink();
        AjaxIndicatorAwareContainer indicatorAware = new AjaxIndicatorAwareContainer( "aware", "spinner" );
        add( indicatorAware );
        feedbackContainer = new WebMarkupContainer( "feedback" );
        feedbackContainer.setOutputMarkupId( true );
        makeVisible( feedbackContainer, false );
        indicatorAware.add( feedbackContainer );
        addFeedbackFields();
        addFeedbackButtons();
    }

    private void addNewFeedbackLink() {
        AjaxLink<String> newFeedback = new AjaxLink<String>( "newFeedback" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                makeVisible( feedbackContainer, true );
                target.add( feedbackContainer );
            }
        };
        add( newFeedback );
        newFeedback.add( new Label( "label", getFeedbackLabel() ) );
    }

    private String getFeedbackLabel() {
        return feedbackLabel == null ? LABEL : feedbackLabel;
    }

    private void addFeedbackFields() {
        questionCheckBox = new AjaxCheckBox(
                "question",
                new PropertyModel<Boolean>( this, "question" )
        ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                updateFields( target );
            }
        };
        feedbackContainer.add( questionCheckBox );
        problemCheckBox = new AjaxCheckBox(
                "problem",
                new PropertyModel<Boolean>( this, "problem" )
        ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                updateFields( target );
            }
        };
        feedbackContainer.add( problemCheckBox );
        suggestionCheckBox = new AjaxCheckBox(
                "suggestion",
                new PropertyModel<Boolean>( this, "suggestion" )
        ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                updateFields( target );
            }
        };
        feedbackContainer.add( suggestionCheckBox );
        AjaxCheckBox priorityCheckBox = new AjaxCheckBox(
                "priority",
                new PropertyModel<Boolean>( this, "asap" )
        ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                //Nothing
            }
        };
        feedbackContainer.add( priorityCheckBox );
        contentText = new TextArea<String>(
                "content",
                new PropertyModel<String>( this, "content" ) );
        contentText.setOutputMarkupId( true );
        contentText.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                // do nothing
            }
        } );
        feedbackContainer.add( contentText );
    }

    private void updateFields( AjaxRequestTarget target ) {
        target.add( questionCheckBox );
        target.add( problemCheckBox );
        target.add( suggestionCheckBox );
    }

    private void addFeedbackButtons() {
        sendButton = new AjaxLink( "send" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                if ( !getContent().isEmpty() ) {
                    boolean success = saveFeedback();
                    String alert = success
                            ? "Feedback sent. Thank you!"
                            : "Oops! Your feedback could not be sent. Sorry.";
                    target.appendJavaScript( "alert('" + alert + "');" );
                    resetFeedback();
                    updateFields( target );
                    makeVisible( feedbackContainer, !success );
                    target.add( feedbackContainer );
                } else {
                    target.appendJavaScript( "alert('Please enter a short text.');" );
                    target.add( feedbackContainer );
                }
            }
        };
        sendButton.setOutputMarkupId( true );
        add( sendButton );
        feedbackContainer.add( sendButton );
        AjaxLink cancelButton = new AjaxLink( "cancel" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                resetFeedback();
                updateFields( target );
                makeVisible( feedbackContainer, false );
                target.add( feedbackContainer );
            }
        };
        feedbackContainer.add( cancelButton );
    }

    private boolean saveFeedback() {
        ChannelsUser currentUser = getUser();
        Plan plan = getPlan();
        Feedback feedback = new Feedback( currentUser.getUsername(), plan.getUri(), feedbackType() );
        feedback.setTopic( topic );
        feedback.setContent( getContent() );
        feedback.setFromEmail( currentUser.getEmail() );
        feedback.setUrgent( isAsap() );
        if ( about != null ) {
            feedback.setAbout( new ModelObjectRef( about ).asString() );
        }
        try {
            feedbackService.save( feedback );
            return true;
        } catch ( Exception e ) {
            LOG.warn( currentUser.getUsername()
                    + " failed to record feedback ", e );
            return false;
        }

    }

    private Feedback.Type feedbackType() {
        return isProblem()
                ? Feedback.Type.PROBLEM
                : isQuestion()
                    ? Feedback.Type.QUESTION
                    : Feedback.Type.SUGGESTION;
    }



    private String getClientProperties() {
        WebClientInfo clientInfo = getUser().getClientInfo();
        if ( clientInfo != null ) {
            return clientInfo.getProperties().toString();
        } else {
            return "No client info";
        }
    }

    private void resetFeedback() {
        question = true;
        problem = false;
        suggestion = false;
        asap = false;
        content = "";
    }

    public boolean isQuestion() {
        return question;
    }

    public void setQuestion( boolean val ) {
        if ( !question ) {
            this.question = val;
            if ( question ) {
                problem = false;
                suggestion = false;
            }
        }
    }

    public boolean isProblem() {
        return problem;
    }

    public void setProblem( boolean val ) {
        if ( !problem ) {
            this.problem = val;
            if ( problem ) {
                question = false;
                suggestion = false;
            }
        }
    }

    public boolean isSuggestion() {
        return suggestion;
    }

    public void setSuggestion( boolean val ) {
        if ( !suggestion ) {
            this.suggestion = val;
            if ( suggestion ) {
                problem = false;
                question = false;
            }
        }
    }

    public boolean isAsap() {
        return asap;
    }

    public void setAsap( boolean asap ) {
        this.asap = asap;
    }

    public String getContent() {
        return content == null ? "" : content;
    }

    public void setContent( String content ) {
        this.content = content;
    }

}
