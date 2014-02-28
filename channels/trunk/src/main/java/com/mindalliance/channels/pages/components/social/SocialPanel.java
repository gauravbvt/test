package com.mindalliance.channels.pages.components.social;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.db.data.messages.UserMessage;
import com.mindalliance.channels.db.services.messages.UserMessageService;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Social interactions panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 5, 2010
 * Time: 10:03:51 AM
 */
public class SocialPanel extends AbstractUpdatablePanel {

    @SpringBean
    private UserMessageService userMessageService;

    public static final String ABOUT_ME = "About me";
    public static final String PASSWORD = "My password";
    public static final String WHAT_I_DO = "What I do";
    public static final String PRESENCE = "Presence";
    public static final String ACTIVITIES = "Activities";
    public static final String MESSAGES = "Messages";
    public static final String CALENDAR = "Calendar";
    public static final String PARTICIPATION = "Participation";
    public static final String USER = "User";
    public static final String SEND_MESSAGE = "sendMessage";
    public static final String DELETE_MESSAGE = "deleteMessage";
    public static final String EMAIL_MESSAGE = "emailMessage";

    private AjaxTabbedPanel<ITab> tabbedPanel;
    private UserMessageListPanel userMessageListPanel;
    private ExecutedCommandsListPanel commandEventListPanel;
    private UserPresenceListPanel userPresenceListPanel;
    private CalendarPanel calendarPanel;
    private UserInfoPanel userProfilePanel;
    private UserParticipationPanel userParticipationPanel;
    private NewPasswordPanel newPasswordPanel;

    /**
     * When last refreshed.
     */
    private Date whenLastRefreshed = new Date();
    private boolean collapsible;
    private boolean showProfile;
    private final List<String> showTabs;


    public SocialPanel( String id, String[] showTabs ) {
        this( id, true, showTabs, true );
    }

    public SocialPanel( String id, boolean collapsible, String[] showTabs, boolean showProfile ) {
        super( id );
        this.collapsible = collapsible;
        this.showProfile = showProfile;
        this.showTabs = Arrays.asList( showTabs );
        init();
    }

    private void init() {
        addSocialTabs();
    }

    private void addSocialTabs() {
        tabbedPanel = new AjaxTabbedPanel<ITab>( "tabs", getTabs() ) {
            // Override newLink to supply an IndicatingAjaxLink (wait cursor)
            @Override
            protected WebMarkupContainer newLink( String linkId, final int index ) {
                return new AjaxLink( linkId ) {

                    public void onClick( AjaxRequestTarget target ) {
                        setSelectedTab( index );
                        if ( target != null ) {
                            target.add( tabbedPanel );
                        }
                        onAjaxUpdate( target );
                    }
                };
            }
        };
        tabbedPanel.setOutputMarkupId( true );
        add( tabbedPanel );
    }

    private List<ITab> getTabs() {
        List<ITab> tabs = new ArrayList<ITab>();
        if ( showTabs.contains( USER ) ) {
            AbstractTab tab = new AbstractTab( new Model<String>( ABOUT_ME ) ) {
                public Panel getPanel( String id ) {
                    userProfilePanel = new UserInfoPanel( id, SocialPanel.this, collapsible );
                    return userProfilePanel;
                }
            };
            tabs.add( tab );
        }
        if ( showTabs.contains( PASSWORD ) ) {
            AbstractTab tab = new AbstractTab( new Model<String>( PASSWORD ) ) {
                public Panel getPanel( String id ) {
                    newPasswordPanel = new NewPasswordPanel( id, SocialPanel.this, collapsible );
                    return newPasswordPanel;
                }
            };
            tabs.add( tab );
        }

        if ( showTabs.contains( PARTICIPATION ) ) {
            AbstractTab tab = new AbstractTab( new Model<String>( WHAT_I_DO ) ) {
                public Panel getPanel( String id ) {
                    userParticipationPanel = new UserParticipationPanel( id, SocialPanel.this, collapsible );
                    return userParticipationPanel;
                }
            };
            tabs.add( tab );
        }
        if ( showTabs.contains( PRESENCE ) )
            tabs.add( new AbstractTab( new Model<String>( PRESENCE ) ) {
                public Panel getPanel( String id ) {
                    userPresenceListPanel = new UserPresenceListPanel( id, SocialPanel.this, collapsible, showProfile );
                    return userPresenceListPanel;
                }
            } );
        if ( showTabs.contains( ACTIVITIES ) )
            tabs.add( new AbstractTab( new Model<String>( ACTIVITIES ) ) {
                public Panel getPanel( String id ) {
                    commandEventListPanel = new ExecutedCommandsListPanel( id, SocialPanel.this, collapsible, showProfile );
                    return commandEventListPanel;
                }
            } );
        if ( showTabs.contains( MESSAGES ) )
            tabs.add( new AbstractTab( new Model<String>( MESSAGES ) ) {
                public Panel getPanel( String id ) {
                    userMessageListPanel = new UserMessageListPanel( id, SocialPanel.this, collapsible, showProfile );
                    return userMessageListPanel;
                }
            } );
        if ( showTabs.contains( CALENDAR ) )
            tabs.add( new AbstractTab( new Model<String>( CALENDAR ) ) {
                public Panel getPanel( String id ) {
                    calendarPanel = new CalendarPanel( id, collapsible );
                    return calendarPanel;
                }
            } );
        return tabs;
    }

    private String getMessagesTabTitle() {
        return MESSAGES;
    }

    private String getSelectedTabTitle() {
        return ( (ITab) tabbedPanel.getTabs().get( tabbedPanel.getSelectedTab() ) ).getTitle().getObject();
    }

    protected void refresh( AjaxRequestTarget target, Change change, String aspect ) {
        if ( userPresenceListPanel != null && getSelectedTabTitle().equals( PRESENCE ) ) {
            userPresenceListPanel.refresh( target, change );
        }
        if ( commandEventListPanel != null && getSelectedTabTitle().equals( ACTIVITIES ) ) {
            commandEventListPanel.refresh( target, change );
        }
        if ( userMessageListPanel != null && getSelectedTabTitle().equals( getMessagesTabTitle() ) ) {
            userMessageListPanel.refresh( target, change );
        }
        Date whenLastReceived = userMessageService.getWhenLastReceived(
                getUser().getUsername(),
                getCommunityService() );
        if ( whenLastReceived != null && whenLastReceived.after( whenLastRefreshed ) ) {
            update( target, Change.message( "New message" ) );
        }
        whenLastRefreshed = new Date();
    }

    public void update( AjaxRequestTarget target, Object object, String action ) {
        if ( action.equals( SEND_MESSAGE )
                || action.equals( DELETE_MESSAGE )
                || action.equals( EMAIL_MESSAGE ) ) {
            if ( !getSelectedTabTitle().equals( getMessagesTabTitle() ) ) {
                selectTabTitled( getMessagesTabTitle() );
                target.add( tabbedPanel );
            }

            if ( object instanceof String && action.equals( SEND_MESSAGE ) ) {
                userMessageListPanel.newMessage( (String) object, target );
            }
            if ( object instanceof UserMessage && action.equals( DELETE_MESSAGE ) ) {
                userMessageListPanel.deleteMessage( (UserMessage) object, target );
            }
            if ( object instanceof UserMessage && action.equals( EMAIL_MESSAGE ) ) {
                userMessageListPanel.emailMessage( (UserMessage) object, target );
            }
        }
    }

    private void selectTabTitled( String title ) {
        List<? extends ITab> tabs = tabbedPanel.getTabs();
        for ( int i = 0; i < tabs.size(); i++ ) {
            if ( tabs.get( i ).getTitle().getObject().equals( title ) ) {
                tabbedPanel.setSelectedTab( i );
                break;
            }
        }
    }

    public void newMessage( AjaxRequestTarget target, Change change ) {
        ModelObject about = (ModelObject) change.getSubject( getCommunityService() );
        String sendTo = change.getProperty();
        selectTabTitled( getMessagesTabTitle() );
        userMessageListPanel.newMessage( sendTo == null ? "" : sendTo, about, target );
        target.add( tabbedPanel );
    }

    public boolean isCollapsible() {
        return collapsible;
    }

}
