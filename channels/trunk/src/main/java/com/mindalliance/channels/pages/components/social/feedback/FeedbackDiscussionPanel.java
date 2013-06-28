package com.mindalliance.channels.pages.components.social.feedback;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.db.data.messages.Feedback;
import com.mindalliance.channels.db.data.messages.UserMessage;
import com.mindalliance.channels.db.services.messages.FeedbackService;
import com.mindalliance.channels.db.services.messages.UserMessageService;
import com.mindalliance.channels.db.services.users.UserRecordService;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.social.FeedbackStatementPanel;
import com.mindalliance.channels.pages.components.social.UserMessagePanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/22/12
 * Time: 5:54 PM
 */
public class FeedbackDiscussionPanel extends AbstractUpdatablePanel {


    @SpringBean
    private UserRecordService userInfoService;

    @SpringBean
    private FeedbackService feedbackService;

    @SpringBean
    private UserMessageService userMessageService;

    private static final int MAX_REPLIES_ROWS = 10;
    private String reply;
    private TextArea<String> replyTextArea;
    private AjaxLink<String> replyButton;
    private AjaxLink<String> replyAndEmailButton;
    private AjaxLink<String> resetButton;
    private WebMarkupContainer repliesContainer;
    private boolean showProfile;
    private boolean canResolve;
    private boolean personalOnly;
    private WebMarkupContainer resolvedContainer;

    public FeedbackDiscussionPanel(
            String id,
            Model<Feedback> feedbackModel,
            boolean showProfile ) {
        this( id, feedbackModel, showProfile, false, false );
    }

    public FeedbackDiscussionPanel(
            String id,
            Model<Feedback> feedbackModel,
            boolean showProfile,
            boolean canResolve,
            boolean personalOnly ) {
        super( id, feedbackModel );
        this.showProfile = showProfile;
        this.canResolve = canResolve;
        this.personalOnly = personalOnly;
        init();
    }

    private void init() {
        addFeedbackPanel();
        addRepliesList();
        addReplyField();
        addReplyButton();
        addReplyAndEmailButton();
        addResetButton();
        addResolve();
    }

    private void addResolve() {
        String resolvedValue = getFeedback().isResolved() ? "Mark as not resolved" : "Mark as resolved";
        resolvedContainer = new WebMarkupContainer( "resolvedContainer" );
        resolvedContainer.setOutputMarkupId( true );
        AjaxLink<String> resolvedButton = new AjaxLink<String>( "resolvedButton") {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                feedbackService.toggleResolved( getFeedback() );
                addResolve();
                target.add( resolvedContainer );
                update( target, new Change( Change.Type.Updated, getFeedback() ) );
            }
        };
        resolvedButton.add( new AttributeModifier( "value", new Model<String>( resolvedValue) ) );
        resolvedButton.setOutputMarkupId( true );
        resolvedContainer.add( resolvedButton );
        resolvedContainer.setVisible( canResolve );
        addOrReplace( resolvedContainer );
    }

    private void addFeedbackPanel() {
        add( new FeedbackStatementPanel(
                "feedback",
                new PropertyModel<Feedback>( this, "feedback" ),
                1,
                showProfile,
                this
        ) );
    }


    private void addReplyField() {
        replyTextArea = new TextArea<String>(
                "reply",
                new PropertyModel<String>( this, "reply" ) );
        replyTextArea.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                // do nothing
            }
        } );
        add( replyTextArea );
    }

    private void addReplyButton() {
        replyButton = new AjaxLink<String>( "replyButton" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                if ( !getReply().isEmpty() ) {
                    replyToFeedback( false );
                    updateAfterReplyOrReset( target );
                }
            }
        };
        add( replyButton );
    }

    private void addReplyAndEmailButton() {
        replyAndEmailButton = new AjaxLink<String>( "replyAndEmailButton" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                if ( !getReply().isEmpty() ) {
                    replyToFeedback( true );
                    updateAfterReplyOrReset( target );
                }
            }
        };
        add( replyAndEmailButton );
    }

    private void addResetButton() {
        resetButton = new AjaxLink<String>( "resetButton" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                updateAfterReplyOrReset( target );
            }
        };
        add( resetButton );
    }


    private void updateAfterReplyOrReset( AjaxRequestTarget target ) {
        addRepliesList();
        reply = null;
        target.add( repliesContainer );
        target.add( replyTextArea );
    }


    private void addRepliesList() {
        repliesContainer = new WebMarkupContainer( "replies" );
        repliesContainer.setOutputMarkupId( true );
        addOrReplace( repliesContainer );
        List<UserMessage> replies = getFeedback().getReplies();
        Collections.sort( replies , new Comparator<UserMessage>() {
            @Override
            public int compare( UserMessage m1, UserMessage m2 ) {
                return m2.getCreated().compareTo( m1.getCreated() ) ;
            }
        });
        ListView<UserMessage> userMessagesListView = new ListView<UserMessage>(
                "reply",
                replies ) {
            @Override
            protected void populateItem( ListItem<UserMessage> item ) {
                item.add( new UserMessagePanel(
                        "message",
                        item.getModel(),
                        true,
                        item.getIndex(),
                        showProfile,
                        false, // don't allow message deletion
                        FeedbackDiscussionPanel.this
                ) );
            }
        };
        if ( personalOnly ) feedbackService.markFeedbackRepliesRead( getFeedback() );
        repliesContainer.add( userMessagesListView );
    }

    private void replyToFeedback( boolean emailIt ) {
        if ( reply != null && !reply.trim().isEmpty() ) {
            Feedback feedback = getFeedback();
            UserMessage message = new UserMessage( getUsername(), reply, getPlanCommunity() );
            message.setToUsername( feedback.getUsername() );
            message.setText( reply );
            message.setSendNotification( emailIt );
            feedbackService.addReplyTo( feedback, message, userMessageService );
        }
    }

    public String getReply() {
        return reply == null ? "" : reply;
    }

    public void setReply( String content ) {
        reply = content;
    }

    public Feedback getFeedback() {
        // Always get an up-to-date copy from the db
        Feedback feedback = (Feedback) getModel().getObject();
        return feedbackService.load( feedback.getUid() );
    }

}
