package com.mindalliance.channels.pages.components.support;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.db.data.messages.Feedback;
import com.mindalliance.channels.db.services.messages.FeedbackService;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.AjaxIndicatorAwareContainer;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.http.request.WebClientInfo;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailSender;

import java.util.ArrayList;
import java.util.List;

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
    private Feedback.Type selectedFeedbackOption = Feedback.Type.PROBLEM;
    private boolean asap;
    private String content = "";
    private TextArea<String> contentText;
    private AjaxLink sendButton;
    private String feedbackLabel;
    private String topic;
    private final String context;
    private WebMarkupContainer feedbackOptionsContainer;
    private AjaxLink<String> newFeedback;
    private boolean opened = false;


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
        this( id, about, label, topic, null );
    }


    public UserFeedbackPanel( String id, Identifiable about, String label, String topic, String context ) {
        super( id );
        this.about = about;
        feedbackLabel = label;
        this.topic = topic;
        this.context = context;
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
        newFeedback = new AjaxLink<String>( "newFeedback" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                opened = !opened;
                newFeedback.add( new AttributeModifier(
                        "class",
                        opened ? "feedback-button feedback-button-active" : "feedback-button" ) );
                target.add( newFeedback );
                makeVisible( feedbackContainer, opened );
                target.add( feedbackContainer );
            }
        };
        newFeedback.setOutputMarkupId( true );
        addOrReplace( newFeedback );
        newFeedback.add( new Label( "label", getFeedbackLabel() ) );
    }

    private String getFeedbackLabel() {
        return feedbackLabel == null ? LABEL : feedbackLabel;
    }

    private void addFeedbackFields() {
        feedbackOptionsContainer = new WebMarkupContainer( "feedbackOptions" );
        feedbackOptionsContainer.setOutputMarkupId( true );
        addFeedbackOptions();
        addFeedbackPriority();
        addFeedbackText();
        feedbackContainer.add( feedbackOptionsContainer );
    }

    private void addFeedbackOptions() {
        addSelectedOption();
        addOtherOptions();
    }

    private void addSelectedOption() {
        AjaxLink<String> selectedOptionLink = new AjaxLink<String>( "selectedOptionLink" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                //Do nothing
            }
        };
        selectedOptionLink.add( new Label(
                "selectedOptionName",
                new PropertyModel<String>( this, "selectedFeedbackOptionName" ) ) );
        feedbackOptionsContainer.addOrReplace( selectedOptionLink );
    }

    public String getSelectedFeedbackOptionName() {
        return selectedFeedbackOption.getLabel();
    }

    private void addOtherOptions() {
        ListView<String> otherOptionsListView = new ListView<String>(
                "otherOptions",
                getOtherFeedbackOptionNames()
        ) {
            @Override
            protected void populateItem( ListItem<String> item ) {
                final String otherOption = item.getModelObject();
                AjaxLink<String> selectedOptionLink = new AjaxLink<String>( "otherOptionLink" ) {
                    @Override
                    public void onClick( AjaxRequestTarget target ) {
                        setSelectedFeedbackOption( otherOption );
                        updateFields( target );
                    }
                };
                item.add( selectedOptionLink );
                selectedOptionLink.add( new Label( "otherOptionName", otherOption ) );
            }
        };
        feedbackOptionsContainer.addOrReplace( otherOptionsListView );
    }

    private void setSelectedFeedbackOption( String otherOption ) {
        selectedFeedbackOption = Feedback.Type.fromLabel( otherOption );
    }

    private List<String> getOtherFeedbackOptionNames() {
        List<String> others = new ArrayList<String>();
        for ( Feedback.Type value : Feedback.Type.values() ) {
            if ( value != selectedFeedbackOption ) {
                others.add( value.getLabel() );
            }
        }
        return others;
    }

    private void addFeedbackText() {
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

    private void addFeedbackPriority() {
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
    }

    private void updateFields( AjaxRequestTarget target ) {
        addFeedbackOptions();
        target.add( feedbackOptionsContainer );
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
                    Change change = new Change( Change.Type.None );
                    change.setMessage( alert );
                    resetFeedback();
                    updateFields( target );
                    makeVisible( feedbackContainer, !success );
                    target.add( feedbackContainer );
                    addNewFeedbackLink();
                    target.add( newFeedback );
                    update( target, change );
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
                newFeedback.add( new AttributeModifier( "class", "feedback-button" ) );
                target.add( newFeedback );
                makeVisible( feedbackContainer, false );
                target.add( feedbackContainer );
                addNewFeedbackLink();
                target.add( newFeedback );
            }
        };
        feedbackContainer.add( cancelButton );
    }

    private boolean saveFeedback() {
        ChannelsUser currentUser = getUser();
        Feedback feedback = new Feedback( currentUser.getUsername(), feedbackType(), getDomainPlanCommunity() );
        feedback.setTopic( topic );
        feedback.setText( getContent() );
        feedback.setFromEmail( currentUser.getEmail() );
        feedback.setUrgent( isAsap() );
        if ( about != null && about instanceof ModelObject ) {
            feedback.setMoRef( (ModelObject) about );
        }
        if ( context != null ) {
            feedback.setContext( context );
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
        return selectedFeedbackOption;
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
        selectedFeedbackOption = Feedback.Type.PROBLEM;
        asap = false;
        content = "";
        opened = false;
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
