package com.mindalliance.channels.pages.components.support;

import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.components.AjaxIndicatorAwareContainer;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.http.request.WebClientInfo;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Feedback panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/2/11
 * Time: 1:07 PM
 */
public class UserFeedbackPanel extends Panel {

    /**
     * The logger.
     */
    private final Logger LOG = LoggerFactory.getLogger( UserFeedbackPanel.class );
    /**
     * Plan manager.
     */
    @SpringBean
    private PlanManager planManager;

    @SpringBean
    private MailSender mailSender;


    private WebMarkupContainer feedbackContainer;
    private boolean question = true;
    private boolean problem;
    private boolean suggestion;
    private boolean asap;
    private String content = "";
    private TextArea<String> contentText;
    private AjaxLink sendButton;
    private SimpleDateFormat dateFormat;
    private AjaxCheckBox suggestionCheckBox;
    private AjaxCheckBox problemCheckBox;
    private AjaxCheckBox questionCheckBox;
    private static final int MAX_SUBJECT_LENGTH = 60;
    private String clientInfo = "";


    public UserFeedbackPanel( String id ) {
        super( id );
        init();
    }

    private void init() {
        dateFormat = new SimpleDateFormat( "yyyy/MM/dd HH:mm:ss" );
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
                clientInfo = getClientProperties();
                makeVisible( feedbackContainer, true );
                target.addComponent( feedbackContainer );
            }
        };
        add( newFeedback );
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
        target.addComponent( questionCheckBox );
        target.addComponent( problemCheckBox );
        target.addComponent( suggestionCheckBox );
    }

    private void addFeedbackButtons() {
        sendButton = new AjaxLink( "send" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                if ( !getContent().isEmpty() ) {
                    boolean success = sendFeedback();
                    String alert = success
                            ? "Feedback sent. Thank you!"
                            : "Oops! Your feedback could not be sent. Sorry.";
                    target.appendJavascript( "alert('" + alert + "');" );
                    resetFeedback();
                    updateFields( target );
                    makeVisible( feedbackContainer, !success );
                    target.addComponent( feedbackContainer );
                } else {
                    target.appendJavascript( "alert('Please enter a short text.');" );
                    target.addComponent( feedbackContainer );
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
                target.addComponent( feedbackContainer );
            }
        };
        feedbackContainer.add( cancelButton );
    }

    private String contentType() {
        return isQuestion()
                ? "question"
                : isProblem()
                ? "problem"
                : "suggestion";
    }

    private boolean sendFeedback() {
        User currentUser = User.current();
        Plan plan = currentUser.getPlan();
        String toAddress = plan.getPlannerSupportCommunityUri( getApp().getSupportCommunityUri() );
        try {
            SimpleMailMessage email = new SimpleMailMessage();
            email.setTo( toAddress );
            email.setFrom( currentUser.getEmail() );
            email.setReplyTo( currentUser.getEmail() );
            String subject = makeEmailSubject();
            email.setSubject( subject );
            email.setText( makeContent( plan, currentUser ) );
            LOG.info( currentUser.getUsername()
                    + " emailing \"" + subject + "\" to "
                    + toAddress );
            mailSender.send( email );
            return true;
        } catch ( Exception e ) {
            LOG.warn( currentUser.getUsername()
                    + " failed to email feedback ", e );
            return false;

        }

    }

    private String makeContent( Plan plan, User user ) {
        return "Plan: " + plan.getUri()
                + "\nUser: " + user.getFullName()
                + "\n"
                + dateFormat.format( new Date() )
                + "\n----------------------------------------------------------------------------\n\n"
                + getContent()
                + "\n\n----------------------------------------------------------------------------\n"
                + clientInfo;

    }

    private String getClientProperties() {
        WebClientInfo clientInfo = User.current().getClientInfo();
        if ( clientInfo != null ) {
            return clientInfo.getProperties().toString();
        } else {
            return "No client info";
        }
    }

    private String makeEmailSubject() {
        StringBuilder sb = new StringBuilder();
        sb.append( "Feedback" );
        if ( isAsap() ) sb.append( " [ASAP]" );
        sb.append( " - " );
        sb.append( WordUtils.capitalize( contentType() ) );
        sb.append( " - " );
        sb.append( contentAbbreviated() );
        return sb.toString();
    }

    private String contentAbbreviated() {
        String summary = getContent().replaceAll( "\\s", " " );
        return StringUtils.abbreviate( summary, MAX_SUBJECT_LENGTH );
    }

    private void resetFeedback() {
        question = true;
        problem = false;
        suggestion = false;
        asap = false;
        content = "";
    }

    /**
     * Set a component's visibility.
     *
     * @param component a component
     * @param visible   a boolean
     */
    private static void makeVisible( Component component, boolean visible ) {
        component.add( new AttributeModifier( "style", true, new Model<String>(
                visible ? "" : "display:none" ) ) );
    }

    private Channels getApp() {
        return (Channels) getApplication();
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
