package com.mindalliance.channels.pages;

import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.model.Plan;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.http.WebRequestCycle;
import org.apache.wicket.protocol.http.request.WebClientInfo;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Help page.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 27, 2010
 * Time: 1:21:18 PM
 */
public class HelpPage extends WebPage {

    /**
     * The logger.
     */
    private final Logger LOG = LoggerFactory.getLogger( HelpPage.class );
    /**
     * Plan manager.
     */
    @SpringBean
    private PlanManager planManager;

    @SpringBean
    private MailSender mailSender;


    private WebMarkupContainer feedbackContainer;
    private boolean question;
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

    public HelpPage() {
        setStatelessHint( true );
        dateFormat = new SimpleDateFormat( "yyyy/MM/dd HH:mm:ss" );
        init();
    }

    private void init() {
        AjaxLink<String> newFeedback = new AjaxLink<String>( "newFeedback" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                makeVisible( feedbackContainer, true );
                target.addComponent( feedbackContainer );
            }
        };
        add( newFeedback );
        feedbackContainer = new WebMarkupContainer( "feedback" );
        feedbackContainer.setOutputMarkupId( true );
        makeVisible( feedbackContainer, false );
        add( feedbackContainer );
        addFeedbackFields();
        addFeedbackButtons();
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
                // nothing
            }
        } );
        makeVisible( contentText, false );
        feedbackContainer.add( contentText );
    }

    private void updateFields( AjaxRequestTarget target ) {
        boolean visible = isQuestion() || isProblem() || isSuggestion();
        makeVisible( contentText, visible );
        target.addComponent( contentText );
        makeVisible( sendButton, visible );
        target.addComponent( sendButton );
        target.addComponent( questionCheckBox );
        target.addComponent( problemCheckBox );
        target.addComponent( suggestionCheckBox );
    }

    private void addFeedbackButtons() {
        sendButton = new AjaxLink( "send" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
/*
                if ( getContent().isEmpty() ) {
                    target.appendJavascript( "alert('Please state your " + contentType() + ".');" );
                    target.addComponent( feedbackContainer );
                } else {
*/              if ( !getContent().isEmpty() ) {
                    boolean success = sendFeedback();
                    String alert = success
                            ? "Feedback sent. Thank you!"
                            : "There was a problem. Your feedback could not be sent. Please manually send an email to channels@mind-allaince.com";
                    target.appendJavascript( "alert('" + alert + "');" );
                    resetFeedback();
                    updateFields( target );
                    makeVisible( feedbackContainer, !success );
                    target.addComponent( feedbackContainer );
                }
            }
        };
        sendButton.setOutputMarkupId( true );
        makeVisible( sendButton, false );
        add( sendButton );
        feedbackContainer.add( sendButton );
        AjaxLink cancelButton = new AjaxLink( "cancel" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                resetFeedback();
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
        WebClientInfo clientInfo = (WebClientInfo) WebRequestCycle.get().getClientInfo();
        return "Plan: " + plan.getUri()
                + "\nUser: " + user.getFullName()
                + "\n"
                + dateFormat.format( new Date() )
                + "\n----------------------------------------------------------------------------\n\n"
                + getContent()
                + "\n\n----------------------------------------------------------------------------\n"
                + clientInfo.getProperties().toString();

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
        question = false;
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

    public void setQuestion( boolean question ) {
        this.question = question;
        if ( question ) {
            problem = false;
            suggestion = false;
        }
    }

    public boolean isProblem() {
        return problem;
    }

    public void setProblem( boolean problem ) {
        this.problem = problem;
        if ( problem ) {
            question = false;
            suggestion = false;
        }
    }

    public boolean isSuggestion() {
        return suggestion;
    }

    public void setSuggestion( boolean suggestion ) {
        this.suggestion = suggestion;
        if ( suggestion ) {
            problem = false;
            question = false;
        }
    }

    public boolean isAsap() {
        return asap;
    }

    public void setAsap( boolean asap ) {
        this.asap = asap;
    }

    public String getContent() {
        return content;
    }

    public void setContent( String content ) {
        this.content = content;
    }
}
