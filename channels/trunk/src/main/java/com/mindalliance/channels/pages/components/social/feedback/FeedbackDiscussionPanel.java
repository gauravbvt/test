package com.mindalliance.channels.pages.components.social.feedback;

import com.mindalliance.channels.core.dao.user.ChannelsUserDao;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.social.FeedbackStatementPanel;
import com.mindalliance.channels.pages.components.social.UserMessagePanel;
import com.mindalliance.channels.social.model.Feedback;
import com.mindalliance.channels.social.model.UserMessage;
import com.mindalliance.channels.social.services.FeedbackService;
import com.mindalliance.channels.social.services.UserMessageService;
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

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/22/12
 * Time: 5:54 PM
 */
public class FeedbackDiscussionPanel extends AbstractUpdatablePanel {


    @SpringBean
    private ChannelsUserDao userDao;

    @SpringBean
    private FeedbackService feedbackService;

    @SpringBean
    private UserMessageService userMessageService;

    private Feedback feedback;

    private static final int MAX_REPLIES_ROWS = 10;
    private String reply;
    private TextArea<String> replyTextArea;
    private AjaxLink<String> replyButton;
    private AjaxLink<String> replyAndEmailButton;
    private AjaxLink<String> resetButton;
    private WebMarkupContainer repliesContainer;
    private boolean showProfile;

    public FeedbackDiscussionPanel( String id, Model<Feedback> feedbackModel, boolean showProfile ) {
        super( id, feedbackModel );
        this.showProfile = showProfile;
        feedback = feedbackModel.getObject();
        feedbackService.refresh( feedback );
        init();
    }

    private void init() {
        addFeedbackPanel();
        addRepliesList();
        addReplyField();
        addReplyButton();
        addReplyAndEmailButton();
        addResetButton();
    }

    private void addFeedbackPanel() {
        add( new FeedbackStatementPanel(
                "feedback",
                new Model<Feedback>( feedback ),
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
        ListView<UserMessage> userMessagesListView = new ListView<UserMessage>(
                "reply",
                feedback.getReplies() ) {
            @Override
            protected void populateItem( ListItem<UserMessage> item ) {
                item.add( new UserMessagePanel(
                        "message",
                        item.getModel(),
                        true,
                        item.getIndex(),
                        showProfile,
                        FeedbackDiscussionPanel.this
                ) );
            }
        };
        repliesContainer.add( userMessagesListView );
    }

    private void replyToFeedback( boolean emailIt ) {
        if ( reply != null && !reply.trim().isEmpty() ) {
            Plan plan = getPlan();
            UserMessage message = new UserMessage( plan.getUri(), plan.getVersion(), getUsername(), reply );
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

}
