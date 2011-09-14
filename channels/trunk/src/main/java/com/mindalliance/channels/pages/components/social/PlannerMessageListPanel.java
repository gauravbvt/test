package com.mindalliance.channels.pages.components.social;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.dao.User;
import com.mindalliance.channels.core.dao.UserInfo;
import com.mindalliance.channels.core.dao.UserService;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.SegmentObject;
import com.mindalliance.channels.pages.ModelObjectLink;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.social.PlannerMessage;
import com.mindalliance.channels.social.PlannerMessagingService;
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
public class PlannerMessageListPanel extends AbstractSocialListPanel {

    @SpringBean
    private PlannerMessagingService plannerMessagingService;

    @SpringBean
    private UserService userService;

    private static final int A_FEW = 5;
    private static final int MORE = 5;

    private static final User ALL_PLANNERS;
    private static final User ALL_USERS;
    private int numberToShow = A_FEW;
    private boolean privateOnly = false;
    private boolean showReceived = true;
    private boolean allShown;
    private WebMarkupContainer plannerMessagesContainer;
    private User newMessageRecipient;
    private ModelObject newMessageAbout;
    private String newMessageText = "";
    private Label aboutMessagesLabel;
    private AjaxFallbackLink showAFew;
    private AjaxFallbackLink showMore;
    private Updatable updatable;
    private WebMarkupContainer newMessageContainer;
    private AjaxFallbackLink showHideBroadcastsLink;
    private Label showHideBroadcastsLabel;
    private AjaxFallbackLink sentReceivedLink;
    private Label sentReceivedLabel;
    private Date whenLastRefreshed;

    static {
        ALL_PLANNERS = new User( new UserInfo( PlannerMessagingService.PLANNERS, "bla,Anonymous,bla" ) );
        ALL_USERS = new User( new UserInfo( PlannerMessagingService.USERS, "bla,Anonymous,bla" ) );
    }

    public PlannerMessageListPanel( String id, Updatable updatable, boolean collapsible ) {
        super( id, collapsible );
        this.updatable = updatable;
        newMessageRecipient = ALL_PLANNERS;
        init();
    }

    protected void init() {
        super.init();
        addShowHideBroadcastsLink();
        addShowHideBroadcastsLabel();
        addShowReceivedSentLink();
        addShowReceivedSentLabel();
        plannerMessagesContainer = new WebMarkupContainer( "plannerMessagesContainer" );
        plannerMessagesContainer.setOutputMarkupId( true );
        add( plannerMessagesContainer );
        int numberListed = addPlannerMessages();
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
                addPlannerMessages();
                target.addComponent( showHideBroadcastsLabel );
                adjustComponents( target );
            }
        };
        showHideBroadcastsLink.setVisible( isPlanner() );
        add( showHideBroadcastsLink );
    }

    private boolean isPlanner() {
        return  User.current().isPlanner();
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
                addPlannerMessages();
                target.addComponent( sentReceivedLabel );
                adjustComponents( target );
            }
        };
        sentReceivedLink.setVisible( isPlanner() );
        add( sentReceivedLink );
    }

    private void addShowReceivedSentLabel() {
        sentReceivedLabel = new Label(
                "sentReceived",
                showReceived ? "show sent" : "show received" );
        sentReceivedLabel.setOutputMarkupId( true );
        sentReceivedLink.addOrReplace( sentReceivedLabel );
    }

    private  int addPlannerMessages() {
        List<PlannerMessage> plannerMessages = getPlannerMessages();
        ListView<PlannerMessage> plannerMessageListView = new ListView<PlannerMessage>(
                "plannerMessages",
                plannerMessages ) {
            protected void populateItem( ListItem<PlannerMessage> item ) {
                PlannerMessage plannerMessage = item.getModelObject();
                PlannerMessagePanel plannerMessagePanel = new PlannerMessagePanel(
                        "plannerMessage",
                        new Model<PlannerMessage>( plannerMessage ),
                        isShowReceived(),
                        item.getIndex(),
                        updatable );
                item.add( plannerMessagePanel );
            }
        };
        plannerMessagesContainer.addOrReplace( plannerMessageListView );
        return plannerMessages.size();
    }

    private void addAboutMessages( int numberListed ) {
        aboutMessagesLabel = new Label( "aboutMessages", getAboutMessage( numberListed ) );
        aboutMessagesLabel.setOutputMarkupId( true );
        aboutMessagesLabel.setVisible( isPlanner() );
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
                addPlannerMessages();
                adjustComponents( target );
            }
        };
        showMore.setOutputMarkupId( true );
        plannerMessagesContainer.add( showMore );
    }

    private void addShowAFew() {
        showAFew = new AjaxFallbackLink( "showFew" ) {
            public void onClick( AjaxRequestTarget target ) {
                numberToShow = A_FEW;
                showAFew.setEnabled( false );
                addPlannerMessages();
                adjustComponents( target );
            }
        };
        showAFew.setOutputMarkupId( true );
        plannerMessagesContainer.add( showAFew );
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
        DropDownChoice<User> recipientChoice = new DropDownChoice<User>(
                "recipient",
                new PropertyModel<User>( this, "newMessageRecipient" ),
                getCandidateRecipients(),
                new ChoiceRenderer<User>() {
                    public Object getDisplayValue( User user ) {
                        return user == ALL_PLANNERS
                                ? "All planners"
                                : user == ALL_USERS
                                ? "Everyone"
                                : user.getFullName() + " (" + user.getUsername() + ")";
                    }

                    public String getIdValue( User object, int index ) {
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


    private List<User> getCandidateRecipients() {
        List<User> recipients = new ArrayList<User>();
        for ( User user : userService.getPlanners( getPlan().getUri() ) ) {
            if ( !user.getUsername().equals( User.current().getUsername() ) ) {
                recipients.add( user );
            }
        }
        final Collator collator = Collator.getInstance();
        Collections.sort( recipients, new Comparator<User>() {
            public int compare( User user1, User user2 ) {
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
                    sendNewMessage( false );
                    resetNewMessage( target );
                    addPlannerMessages();
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
                    boolean success = sendNewMessage( true );
                    resetNewMessage( target );
                    addPlannerMessages();
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
        target.addComponent( newMessageContainer );
    }

    private boolean sendNewMessage( boolean emailIt ) {
        String text = getNewMessageText();
        if ( !text.isEmpty() ) {
            PlannerMessage plannerMessage = new PlannerMessage( text, planUrn() );
            plannerMessage.setToUsername( getNewMessageRecipient().getUsername() );
            if ( getNewMessageAbout() != null )
                plannerMessage.setAbout( getNewMessageAbout() );
            return plannerMessagingService.sendMessage( plannerMessage, emailIt, planUrn() );
        } else {
            return false;
        }
    }

    private void adjustComponents( AjaxRequestTarget target ) {
        adjustComponents();
        target.addComponent( plannerMessagesContainer );
        target.addComponent( showAFew );
        target.addComponent( showMore );
        target.addComponent( aboutMessagesLabel );
    }

    private void adjustComponents() {
        List<PlannerMessage> plannerMessages = getPlannerMessages();
        addAboutMessages( plannerMessages.size() );
        //makeVisible( plannerMessagesContainer, !plannerMessages.isEmpty() );
        makeVisible( showMore, !allShown );
        makeVisible( showAFew, plannerMessages.size() > A_FEW );
    }

    public void newMessage( String username, AjaxRequestTarget target ) {
        newMessage( username, null, target );
    }

    public void newMessage( String username, ModelObject about, AjaxRequestTarget target ) {
        setNewMessageRecipient( userService.getUserNamed( username ) );
        setNewMessageAbout( about );
        addNewMessage();
        refresh( target, new Change( Change.Type.Communicated ) );
        target.addComponent( newMessageContainer );
    }

    public void deleteMessage( PlannerMessage message, AjaxRequestTarget target ) {
        plannerMessagingService.deleteMessage( message, planUrn() );
        refresh( target, new Change( Change.Type.Communicated ) );
    }

    public void emailMessage( PlannerMessage message, AjaxRequestTarget target ) {
        boolean success = plannerMessagingService.email( message, planUrn() );
        addPlannerMessages();
        adjustComponents( target );
        update( target, Change.message( success
                ? ( "Message emailed to " + ( message.isBroadcast() ? "all planners" : message.getToUsername() ) )
                : "Message NOT emailed" ) );
    }

    public List<PlannerMessage> getPlannerMessages() {
        List<PlannerMessage> plannerMessages = new ArrayList<PlannerMessage>();
        Iterator<PlannerMessage> iterator;
        if ( isShowReceived() ) {
            iterator = plannerMessagingService.getReceivedMessages( planUrn() );
        } else {
            iterator = plannerMessagingService.getSentMessages( planUrn() );
        }
        while ( iterator.hasNext() && plannerMessages.size() < numberToShow ) {
            PlannerMessage plannerMessage = iterator.next();
            if ( plannerMessage != null ) {
                if ( !( privateOnly && plannerMessage.isBroadcast() ) ) {
                    plannerMessages.add( plannerMessage );
                }
            }
        }
        allShown = !iterator.hasNext();
        return plannerMessages;
    }

    public void refresh( AjaxRequestTarget target, Change change ) {
        Date whenLastChanged = plannerMessagingService.getWhenLastChanged( planUrn() );
        if ( whenLastChanged != null && whenLastChanged.after( whenLastRefreshed ) ) {
            addPlannerMessages();
            adjustComponents( target );
            whenLastRefreshed = new Date();
        }
    }

    public User getNewMessageRecipient() {
        return newMessageRecipient == null ? ALL_PLANNERS : newMessageRecipient;
    }

    public void setNewMessageRecipient( User newMessageRecipient ) {
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
