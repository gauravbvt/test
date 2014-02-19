package com.mindalliance.channels.pages.components.social;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.SegmentObject;
import com.mindalliance.channels.db.data.ChannelsDocument;
import com.mindalliance.channels.db.data.PagedDataPeekableIterator;
import com.mindalliance.channels.db.data.messages.UserMessage;
import com.mindalliance.channels.db.data.messages.UserStatement;
import com.mindalliance.channels.db.data.users.UserRecord;
import com.mindalliance.channels.db.services.messages.UserMessageService;
import com.mindalliance.channels.db.services.users.UserRecordService;
import com.mindalliance.channels.pages.ModelObjectLink;
import com.mindalliance.channels.pages.Updatable;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Planner messages panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 5, 2010
 * Time: 1:32:35 PM
 */
public class UserMessageListPanel extends AbstractSocialListPanel {

    /**
     * The logger.
     */
    private final Logger LOG = LoggerFactory.getLogger( UserMessageListPanel.class );

    @SpringBean
    private UserMessageService userMessageService;

    @SpringBean
    private UserRecordService userInfoService;

    private static final int A_FEW = 5;
    private static final int MORE = 5;

    private static final ChannelsUser ALL_DEVELOPERS;
    private static final ChannelsUser ALL_USERS;
    private int numberToShow = A_FEW;
    private boolean privateOnly = false;
    private boolean showReceived = true;
    private boolean allShown;
    private WebMarkupContainer userMessagesContainer;
    private ChannelsUser newMessageRecipient;
    private ModelObject newMessageAbout;
    private String newMessageText = "";
    private Label aboutMessagesLabel;
    private AjaxLink showAFew;
    private AjaxLink showMore;
    private Updatable updatable;
    private boolean showProfile;
    private WebMarkupContainer newMessageContainer;
    private AjaxLink showHideBroadcastsLink;
    private Label showHideBroadcastsLabel;
    private AjaxLink sentReceivedLink;
    private Label sentReceivedLabel;
    private Date whenLastRefreshed;

    static {
        ALL_DEVELOPERS = new ChannelsUser( new UserRecord( "_channels_", UserRecord.PLANNERS ) );
        ALL_USERS = new ChannelsUser( new UserRecord( "_channels_", UserRecord.USERS ) );
    }

    public UserMessageListPanel( String id, Updatable updatable, boolean collapsible, boolean showProfile ) {
        super( id, collapsible );
        this.updatable = updatable;
        this.showProfile = showProfile;
        newMessageRecipient = ALL_DEVELOPERS;
        init();
    }

    @Override
    public String getHelpSectionId() {
        return null;  // Todo
    }

    @Override
    public String getHelpTopicId() {
        return null;  // Todo
    }

    protected void init() {
        super.init();
        add( makeHelpIcon( "helpMyMessages", "messages", "my-messages", "images/help_guide_gray.png" ) );
        addShowHideBroadcastsLink();
        addShowHideBroadcastsLabel();
        addShowReceivedSentLink();
        addShowReceivedSentLabel();
        userMessagesContainer = new WebMarkupContainer( "userMessagesContainer" );
        userMessagesContainer.setOutputMarkupId( true );
        add( userMessagesContainer );
        int numberListed = addUserMessages();
        addAboutMessages( numberListed );
        addShowMore();
        addShowAFew();
        addNewMessage();
        adjustComponents();
        whenLastRefreshed = new Date();
    }

    private void addShowHideBroadcastsLink() {
        showHideBroadcastsLink = new AjaxLink( "hideShowBroadcastsLink" ) {
            public void onClick( AjaxRequestTarget target ) {
                privateOnly = !privateOnly;
                addShowHideBroadcastsLabel();
                addUserMessages();
                target.add( showHideBroadcastsLabel );
                adjustComponents( target );
            }
        };
        // showHideBroadcastsLink.setVisible( isPlanner() );
        add( showHideBroadcastsLink );
    }

    private void addShowHideBroadcastsLabel() {
        showHideBroadcastsLabel = new Label(
                "hideShowBroadcasts",
                privateOnly ? "show all messages" : "hide broadcasts" );
        showHideBroadcastsLabel.setOutputMarkupId( true );
        showHideBroadcastsLink.addOrReplace( showHideBroadcastsLabel );
    }

    private void addShowReceivedSentLink() {
        sentReceivedLink = new AjaxLink( "sentReceivedLink" ) {
            public void onClick( AjaxRequestTarget target ) {
                showReceived = !showReceived;
                addShowReceivedSentLabel();
                addUserMessages();
                target.add( sentReceivedLabel );
                adjustComponents( target );
            }
        };
        // sentReceivedLink.setVisible( isPlanner() );
        add( sentReceivedLink );
    }

    private void addShowReceivedSentLabel() {
        sentReceivedLabel = new Label(
                "sentReceived",
                showReceived ? "show sent" : "show received" );
        sentReceivedLabel.setOutputMarkupId( true );
        sentReceivedLink.addOrReplace( sentReceivedLabel );
    }

    private int addUserMessages() {
        List<UserMessage> userMessages = getUserMessages( getUser() );
        ListView<UserMessage> userMessageListView = new ListView<UserMessage>(
                "userMessages",
                userMessages ) {
            protected void populateItem( ListItem<UserMessage> item ) {
                UserMessage userMessage = item.getModelObject();
                UserMessagePanel userMessagePanel = new UserMessagePanel(
                        "userMessage",
                        new Model<UserMessage>( userMessage ),
                        isShowReceived(),
                        item.getIndex(),
                        showProfile,
                        updatable );
                item.add( userMessagePanel );
            }
        };
        userMessagesContainer.addOrReplace( userMessageListView );
        return userMessages.size();
    }

    private void addAboutMessages( int numberListed ) {
        aboutMessagesLabel = new Label( "aboutMessages", getAboutMessage( numberListed ) );
        aboutMessagesLabel.setOutputMarkupId( true );
        // aboutMessagesLabel.setVisible( isPlanner() );
        addOrReplace( aboutMessagesLabel );
    }

    private String getAboutMessage( int numberListed ) {
        String message = ( numberListed == 0 ? "No" : "Showing" ) + " messages";
        message += showReceived ? " received" : " sent";
        if ( privateOnly ) message += " (excluding broadcasts)";
        return message;
    }

    public boolean isPrivateOnly() {
        return privateOnly;
    }

    public void setPrivateOnly( boolean privateOnly ) {
        this.privateOnly = privateOnly;
    }

    private void addShowMore() {
        showMore = new AjaxLink( "showMore" ) {
            public void onClick( AjaxRequestTarget target ) {
                numberToShow += MORE;
                addUserMessages();
                adjustComponents( target );
            }
        };
        showMore.setOutputMarkupId( true );
        userMessagesContainer.add( showMore );
    }

    private void addShowAFew() {
        showAFew = new AjaxLink( "showFew" ) {
            public void onClick( AjaxRequestTarget target ) {
                numberToShow = A_FEW;
                showAFew.setEnabled( false );
                addUserMessages();
                adjustComponents( target );
            }
        };
        showAFew.setOutputMarkupId( true );
        userMessagesContainer.add( showAFew );
    }

    private void addNewMessage() {
        newMessageContainer = new WebMarkupContainer( "newMessage" );
        newMessageContainer.setOutputMarkupId( true );
        // newMessageContainer.setVisible( isPlanner() );
        newMessageContainer.add( makeHelpIcon( "helpNewMessage", "messages", "new-message", "images/help_guide_gray.png" ) );
        addOrReplace( newMessageContainer );
        addRecipientChoice( newMessageContainer );
        addAbout( newMessageContainer );
        addMessageText( newMessageContainer );
        addReset( newMessageContainer );
        addSend( newMessageContainer );
        addSendAndEmail( newMessageContainer );
    }

    private void addRecipientChoice( WebMarkupContainer newMessageContainer ) {
        DropDownChoice<ChannelsUser> recipientChoice = new DropDownChoice<ChannelsUser>(
                "recipient",
                new PropertyModel<ChannelsUser>( this, "newMessageRecipient" ),
                getCandidateRecipients(),
                new ChoiceRenderer<ChannelsUser>() {
                    public Object getDisplayValue( ChannelsUser user ) {
                        return user == ALL_DEVELOPERS
                                ? "All developers"
                                : user == ALL_USERS
                                ? "Everyone"
                                : user.getFullName() + " (" + user.getUsername() + ")";
                    }

                    public String getIdValue( ChannelsUser object, int index ) {
                        return "" + index;
                    }
                }
        );
        recipientChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                // do nothing
            }
        } );
        newMessageContainer.add( recipientChoice );
    }


    private List<ChannelsUser> getCandidateRecipients() {
        List<ChannelsUser> recipients = new ArrayList<ChannelsUser>();
        for ( ChannelsUser user : userInfoService.getUsers( getPlan().getUri() ) ) {
            if ( !user.getUsername().equals( getUser().getUsername() ) ) {
                recipients.add( user );
            }
        }
        final Collator collator = Collator.getInstance();
        Collections.sort( recipients, new Comparator<ChannelsUser>() {
            public int compare( ChannelsUser user1, ChannelsUser user2 ) {
                return collator.compare( user2.getNormalizedFullName(), user1.getNormalizedFullName() );
            }
        } );
        recipients.add( ALL_USERS );
        recipients.add( ALL_DEVELOPERS );
        Collections.reverse( recipients );
        return recipients;
    }

    private void addAbout( WebMarkupContainer newMessageContainer ) {
        WebMarkupContainer newMessageAboutContainer = new WebMarkupContainer( "aboutContainer" );
        newMessageAboutContainer.setOutputMarkupId( true );
        newMessageContainer.add( newMessageAboutContainer );
        if ( newMessageAbout != null ) {
            ModelObjectLink moLink = new ModelObjectLink(
                    "about",
                    new Model<ModelObject>( newMessageAbout ),
                    new Model<String>( getTargetDescription( newMessageAbout ) )
            );
            newMessageAboutContainer.add( moLink );
        } else {
            Label label = new Label( "about", "" );
            newMessageAboutContainer.add( label );
        }
        newMessageAboutContainer.setVisible( newMessageAbout != null );
    }

    private String getTargetDescription( ModelObject mo ) {
        String description = "\"" + mo.getLabel() + "\"";
        if ( mo instanceof SegmentObject ) {
            description += " in segment \"" + ( (SegmentObject) mo ).getSegment().getLabel() + "\"";
        }
        return description;
    }


    private void addMessageText( WebMarkupContainer newMessageContainer ) {
        TextArea<String> textArea = new TextArea<String>(
                "text",
                new PropertyModel<String>( this, "newMessageText" ) );
        textArea.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                // do nothing
            }
        } );
        newMessageContainer.add( textArea );
    }

    private void addReset( final WebMarkupContainer newMessageContainer ) {
        AjaxLink cancelLink = new AjaxLink( "reset" ) {
            public void onClick( AjaxRequestTarget target ) {
                resetNewMessage( target );
            }
        };
        newMessageContainer.add( cancelLink );
    }

    private void addSend( final WebMarkupContainer newMessageContainer ) {
        AjaxLink sendLink = new AjaxLink( "send" ) {
            public void onClick( AjaxRequestTarget target ) {
                if ( !getNewMessageText().isEmpty() ) {
                    sendNewMessage( false, getUser() );
                    resetNewMessage( target );
                    addUserMessages();
                    adjustComponents( target );
                    update(
                            target,
                            Change.message( "Message sent" ) );
                } else {
                    update(
                            target,
                            Change.message( "Message is empty. Not sent" ) );
                }
            }
        };
        newMessageContainer.add( sendLink );
    }

    private void addSendAndEmail( final WebMarkupContainer newMessageContainer ) {
        AjaxLink sendAndEmailLink = new IndicatingAjaxLink( "sendAndEmail" ) {
            public void onClick( AjaxRequestTarget target ) {
                if ( !getNewMessageText().isEmpty() ) {
                    boolean success = sendNewMessage( true, getUser() );
                    resetNewMessage( target );
                    addUserMessages();
                    adjustComponents( target );
                    update(
                            target,
                            Change.message( success ? "Message sent and emailed" : "Message sent but NOT emailed" ) );
                } else {
                    update(
                            target,
                            Change.message( "Message is empty. Not sent" ) );
                }
            }
        };
        newMessageContainer.add( sendAndEmailLink );
    }


    private void resetNewMessage( AjaxRequestTarget target ) {
        newMessageText = "";
        newMessageAbout = null;
        newMessageRecipient = ALL_DEVELOPERS;
        addNewMessage();
        target.add( newMessageContainer );
    }

    private boolean sendNewMessage( boolean emailIt, ChannelsUser sender ) {
        String text = getNewMessageText();
        if ( !text.isEmpty() ) {
            UserMessage userMessage = new UserMessage(
                    sender.getUsername(),
                    text,
                    getPlanCommunity() );
            userMessage.setToUsername( getNewMessageRecipient().getUsername() );
            if ( getNewMessageAbout() != null )
                userMessage.setMoRef( getNewMessageAbout() );
            userMessageService.sendMessage( userMessage, emailIt );
            return true;
        } else {
            return false;
        }
    }

    private void adjustComponents( AjaxRequestTarget target ) {
        adjustComponents();
        target.add( userMessagesContainer );
        target.add( showAFew );
        target.add( showMore );
        target.add( aboutMessagesLabel );
    }

    private void adjustComponents() {
        List<UserMessage> userMessages = getUserMessages( getUser() );
        addAboutMessages( userMessages.size() );
        makeVisible( showMore, !allShown );
        makeVisible( showAFew, userMessages.size() > A_FEW );
    }

    public void newMessage( String username, AjaxRequestTarget target ) {
        newMessage( username, null, target );
    }

    public void newMessage( String username, ModelObject about, AjaxRequestTarget target ) {
        setNewMessageRecipient( userInfoService.getUserWithIdentity( username ) );
        setNewMessageAbout( about );
        addNewMessage();
        refresh( target, new Change( Change.Type.Communicated ) );
        target.add( newMessageContainer );
    }

    public void deleteMessage( UserMessage message, AjaxRequestTarget target ) {
        try {
            userMessageService.deleteMessage( message );
        } catch ( Exception e ) {
            LOG.warn( "Error while deleting message (double delete?): " + e );
        }
        refresh( target, new Change( Change.Type.Communicated ) );
    }

    public void emailMessage( UserMessage message, AjaxRequestTarget target ) {
        userMessageService.markToNotify( message );
        addUserMessages();
        adjustComponents( target );
        update(
                target,
                Change.message(
                        "Message will be emailed to "
                                + ( message.isBroadcast( getUser(), getPlanCommunity() )
                                ? message.isToAllPlanners()
                                ? "all planners"
                                : "all users"
                                : getUserFullName( message.getToUsername( UserStatement.STATEMENT ) ) ) ) );
    }

    @SuppressWarnings( "unchecked" )
    public List<UserMessage> getUserMessages( ChannelsUser user ) {
        final CommunityService communityService = getCommunityService();
        final String username = getUser().getUsername();
        List<UserMessage> userMessages = new ArrayList<UserMessage>();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put( UserMessageService.USERNAME_PARAM, username );
        params.put(
                UserMessageService.TYPE_PARAM,
                isShowReceived() ? UserMessageService.RECEIVED : UserMessageService.SENT );
        Iterator<UserMessage> iterator;
        Iterator<UserMessage> messageIterator = new PagedDataPeekableIterator<UserMessage>(
                userMessageService,
                params,
                ChannelsDocument.SORT_CREATED_DESC,
                getPlanCommunity()
        );
        if ( isShowReceived() ) {
            // filter on privileges
            iterator = (Iterator<UserMessage>) IteratorUtils.filteredIterator(
                    messageIterator,
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            UserMessage userMessage = (UserMessage) object;
                            return ( !userMessage.isToAllPlanners()
                                    || userInfoService.isPlanner( username, communityService.getPlan().getUri() ) )
                                    && ( !userMessage.isToAllUsers()
                                    || userInfoService.isParticipant( username, communityService.getPlan().getUri() ) );
                        }
                    } );
        } else {
            iterator = messageIterator;
        }
        while ( iterator.hasNext() && userMessages.size() < numberToShow ) {
            UserMessage userMessage = iterator.next();
            if ( userMessage != null ) {
                if ( !( privateOnly && userMessage.isBroadcast( user, getPlanCommunity() ) ) ) {
                    userMessages.add( userMessage );
                }
            }
        }
        allShown = !iterator.hasNext();
        return userMessages;
    }

    public void refresh( AjaxRequestTarget target, Change change ) {
        Date whenLastChanged = userMessageService.getWhenLastChanged( getPlanCommunity().getUri() );
        if ( whenLastChanged == null || whenLastChanged.after( whenLastRefreshed ) ) {
            addUserMessages();
            adjustComponents( target );
            whenLastRefreshed = new Date();
        }
    }

    public ChannelsUser getNewMessageRecipient() {
        return newMessageRecipient == null ? ALL_DEVELOPERS : newMessageRecipient;
    }

    public void setNewMessageRecipient( ChannelsUser newMessageRecipient ) {
        this.newMessageRecipient = newMessageRecipient;
    }

    public ModelObject getNewMessageAbout() {
        return newMessageAbout;
    }

    public void setNewMessageAbout( ModelObject newMessageAbout ) {
        this.newMessageAbout = newMessageAbout;
    }

    public String getNewMessageText() {
        return newMessageText == null ? "" : newMessageText;
    }

    public void setNewMessageText( String newMessageText ) {
        this.newMessageText = newMessageText;
    }

    public boolean isShowReceived() {
        return showReceived;
    }

}
