package com.mindalliance.channels.pages.components.social;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.ChannelsUserDao;
import com.mindalliance.channels.core.dao.user.ChannelsUserInfo;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.model.SegmentObject;
import com.mindalliance.channels.pages.ModelObjectLink;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.social.model.UserMessage;
import com.mindalliance.channels.social.services.UserMessageService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxFallbackLink;
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

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Planner messages panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 5, 2010
 * Time: 1:32:35 PM
 */
public class UserMessageListPanel extends AbstractSocialListPanel {

    @SpringBean
    private UserMessageService userMessageService;

    @SpringBean
    private ChannelsUserDao userDao;

    private static final int A_FEW = 5;
    private static final int MORE = 5;

    private static final ChannelsUser ALL_PLANNERS;
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
    private AjaxFallbackLink showAFew;
    private AjaxFallbackLink showMore;
    private Updatable updatable;
    private boolean showProfile;
    private WebMarkupContainer newMessageContainer;
    private AjaxFallbackLink showHideBroadcastsLink;
    private Label showHideBroadcastsLabel;
    private AjaxFallbackLink sentReceivedLink;
    private Label sentReceivedLabel;
    private Date whenLastRefreshed;

    static {
        ALL_PLANNERS = new ChannelsUser( new ChannelsUserInfo(  ChannelsUserInfo.PLANNERS, "bla,Anonymous,bla" ) );
        ALL_USERS = new ChannelsUser( new ChannelsUserInfo( ChannelsUserInfo.USERS, "bla,Anonymous,bla" ) );
    }

    public UserMessageListPanel( String id, Updatable updatable, boolean collapsible, boolean showProfile ) {
        super( id, collapsible );
        this.updatable = updatable;
        this.showProfile = showProfile;
        newMessageRecipient = ALL_PLANNERS;
        init();
    }

    protected void init() {
        super.init();
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
        showHideBroadcastsLink = new AjaxFallbackLink( "hideShowBroadcastsLink" ) {
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

    private boolean isPlanner() {
        return  getUser().isPlanner();
    }

    private void addShowHideBroadcastsLabel() {
        showHideBroadcastsLabel = new Label(
                "hideShowBroadcasts",
                privateOnly ? "show all messages" : "hide broadcasts" );
        showHideBroadcastsLabel.setOutputMarkupId( true );
        showHideBroadcastsLink.addOrReplace( showHideBroadcastsLabel );
    }

    private void addShowReceivedSentLink() {
        sentReceivedLink = new AjaxFallbackLink( "sentReceivedLink" ) {
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

    private String getAboutMessage(int numberListed ) {
       String message = (numberListed == 0 ? "No" : "Showing") + " messages";
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
        showMore = new AjaxFallbackLink( "showMore" ) {
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
        showAFew = new AjaxFallbackLink( "showFew" ) {
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
        newMessageContainer.setVisible( isPlanner() );
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
                        return user == ALL_PLANNERS
                                ? "All planners"
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
        for ( ChannelsUser user : userDao.getPlanners( getPlan().getUri() ) ) {
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
        recipients.add( ALL_PLANNERS );
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
        AjaxFallbackLink cancelLink = new AjaxFallbackLink( "reset" ) {
            public void onClick( AjaxRequestTarget target ) {
                resetNewMessage( target );
            }
        };
        newMessageContainer.add( cancelLink );
    }

    private void addSend( final WebMarkupContainer newMessageContainer ) {
        AjaxFallbackLink sendLink = new AjaxFallbackLink( "send" ) {
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
        AjaxFallbackLink sendAndEmailLink = new IndicatingAjaxFallbackLink( "sendAndEmail" ) {
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
        newMessageRecipient = ALL_PLANNERS;
        addNewMessage();
        target.add( newMessageContainer );
    }

    private boolean sendNewMessage( boolean emailIt, ChannelsUser sender ) {
        String text = getNewMessageText();
        if ( !text.isEmpty() ) {
            Plan plan = getPlan();
            UserMessage userMessage = new UserMessage( 
                    plan.getUri(), 
                    plan.getVersion(), 
                    sender.getUsername(), 
                    text );
            userMessage.setToUsername( getNewMessageRecipient().getUsername() );
            if ( getNewMessageAbout() != null )
                userMessage.setMoRef( getNewMessageAbout() );
            userMessageService.sendMessage( userMessage, emailIt );
            return true;
        } else {
            return false;
        }
    }

    private void adjustComponents( AjaxRequestTarget target) {
        adjustComponents( );
        target.add( userMessagesContainer );
        target.add( showAFew );
        target.add( showMore );
        target.add( aboutMessagesLabel );
    }

    private void adjustComponents(  ) {
        List<UserMessage> userMessages = getUserMessages( getUser() );
        addAboutMessages( userMessages.size() );
        makeVisible( showMore, !allShown );
        makeVisible( showAFew, userMessages.size() > A_FEW );
    }

    public void newMessage( String username, AjaxRequestTarget target ) {
        newMessage( username, null, target );
    }

    public void newMessage( String username, ModelObject about, AjaxRequestTarget target ) {
        setNewMessageRecipient( userDao.getUserNamed( username ) );
        setNewMessageAbout( about );
        addNewMessage();
        refresh( target, new Change( Change.Type.Communicated ) );
        target.add( newMessageContainer );
    }

    public void deleteMessage( UserMessage message, AjaxRequestTarget target ) {
        userMessageService.deleteMessage( message );
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
                                + ( message.isBroadcast( getUser() )
                                ? message.isToAllPlanners()
                                   ? "all planners"
                                   : "all users"
                                : getUserFullName( message.getToUsername() ) ) ) );
    }

    public List<UserMessage> getUserMessages( ChannelsUser user ) {
        Plan plan = getPlan();
        String username = getUser().getUsername();
        List<UserMessage> userMessages = new ArrayList<UserMessage>();
        Iterator<UserMessage> iterator;
        if ( isShowReceived() ) {
            iterator = userMessageService.getReceivedMessages( username, plan.getUri() );
        } else {
            iterator = userMessageService.getSentMessages( username, plan.getUri() );
        }
        while ( iterator.hasNext() && userMessages.size() < numberToShow ) {
            UserMessage userMessage = iterator.next();
            if ( userMessage != null ) {
                if ( !( privateOnly && userMessage.isBroadcast( user ) ) ) {
                    userMessages.add( userMessage );
                }
            }
        }
        allShown = !iterator.hasNext();
        return userMessages;
    }

    public void refresh( AjaxRequestTarget target, Change change ) {
        Date whenLastChanged = userMessageService.getWhenLastChanged( planVersionUri() );
        if ( whenLastChanged != null && whenLastChanged.after( whenLastRefreshed ) ) {
            addUserMessages();
            adjustComponents( target );
            whenLastRefreshed = new Date();
        }
    }

    public ChannelsUser getNewMessageRecipient() {
        return newMessageRecipient == null ? ALL_PLANNERS : newMessageRecipient;
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
