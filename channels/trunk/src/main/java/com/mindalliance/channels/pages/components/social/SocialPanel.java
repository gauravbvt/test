package com.mindalliance.channels.pages.components.social;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.social.model.UserMessage;
import com.mindalliance.channels.social.services.UserMessageService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
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

    public static final String PRESENCE = "Presence";
    public static final String ACTIVITIES = "Activities";
    public static final String MESSAGES = "Messages";
    public static final String CALENDAR = "Calendar";
    // public static final String SURVEYS = "Surveys";
    public static final String USER = "User";
    public static final String SEND_MESSAGE = "sendMessage";
    public static final String DELETE_MESSAGE = "deleteMessage";
    public static final String EMAIL_MESSAGE = "emailMessage";

    private AjaxTabbedPanel tabbedPanel;
    private UserMessageListPanel plannerMessageListPanel;
    private ExecutedCommandsListPanel commandEventListPanel;
    private UserPresenceListPanel plannerPresenceListPanel;
    private SurveyListPanel surveyListPanel;
    private CalendarPanel calendarPanel;
    private UserInfoPanel userProfilePanel;
    /**
     * When last refreshed.
     */
    private Date whenLastRefreshed = new Date();
    private boolean collapsible;
    private final List<String> showTabs;


    public SocialPanel( String id, String[] showTabs ) {
        this( id, true, showTabs );
    }

    public SocialPanel( String id, boolean collapsible, String[] showTabs ) {
        super( id );
        this.collapsible = collapsible;
        this.showTabs = Arrays.asList( showTabs );
        init();
    }

    private void init() {
        addSocialTabs();
    }

    private void addSocialTabs() {
        tabbedPanel = new AjaxTabbedPanel( "tabs", getTabs() ) {
            // Override newLink to supply an IndicatingAjaxLink (wait cursor)
            @Override
            protected WebMarkupContainer newLink( String linkId, final int index ) {
                return new IndicatingAjaxLink( linkId ) {

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
        if ( showTabs.contains( PRESENCE ) )
            tabs.add( new AbstractTab( new Model<String>( "Presence" ) ) {
                public Panel getPanel( String id ) {
                    plannerPresenceListPanel = new UserPresenceListPanel( id, SocialPanel.this, collapsible );
                    return plannerPresenceListPanel;
                }
            } );
        if ( showTabs.contains( ACTIVITIES ) )
            tabs.add( new AbstractTab( new Model<String>( "Activities" ) ) {
                public Panel getPanel( String id ) {
                    commandEventListPanel = new ExecutedCommandsListPanel( id, SocialPanel.this, collapsible );
                    return commandEventListPanel;
                }
            } );
        if ( showTabs.contains( MESSAGES ) )
            tabs.add( new AbstractTab( new Model<String>( getMessagesTabTitle() ) ) {
                public Panel getPanel( String id ) {
                    plannerMessageListPanel = new UserMessageListPanel( id, SocialPanel.this, collapsible );
                    return plannerMessageListPanel;
                }
            } );
        if ( showTabs.contains( CALENDAR ) )
            tabs.add( new AbstractTab( new Model<String>( "Calendar" ) ) {
                public Panel getPanel( String id ) {
                    calendarPanel = new CalendarPanel( id, collapsible );
                    return calendarPanel;
                }
            } );
      /*  if ( showTabs.contains( SURVEYS ) ) {
            AbstractTab tab = new AbstractTab( new Model<String>( "Surveys" ) ) {
                public Panel getPanel( String id ) {
                    surveyListPanel = new SurveyListPanel( id, SocialPanel.this, collapsible );
                    return surveyListPanel;
                }
            };
            tabs.add( tab );
        }*/
        if ( showTabs.contains( USER ) ) {
            AbstractTab tab = new AbstractTab( new Model<String>( "About Me") ) {
                public Panel getPanel( String id ) {
                    userProfilePanel = new UserInfoPanel( id, SocialPanel.this, collapsible );
                    return userProfilePanel;
                }
            };
            tabs.add( tab );
        }
        return tabs;
    }

    private String getMessagesTabTitle() {
       return getUser().isPlanner() ? "Messages" : "News";
    }

    private String getSelectedTabTitle() {
        return tabbedPanel.getTabs().get( tabbedPanel.getSelectedTab() ).getTitle().getObject();
    }

    protected void refresh( AjaxRequestTarget target, Change change, String aspect ) {
        if ( plannerPresenceListPanel != null && getSelectedTabTitle().equals( "Presence" ) ) {
            plannerPresenceListPanel.refresh( target, change );
        }
        if ( commandEventListPanel != null && getSelectedTabTitle().equals( "Activities" )  ) {
            commandEventListPanel.refresh( target, change );
        }
        if ( plannerMessageListPanel != null && getSelectedTabTitle().equals( getMessagesTabTitle() )  ) {
            plannerMessageListPanel.refresh( target, change );
        }
        Plan plan = getPlan();
        Date whenLastReceived = userMessageService.getWhenLastReceived(
                getUser().getUsername(),
                plan.getUri(),
                plan.getVersion() );
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
                plannerMessageListPanel.newMessage( (String) object, target );
            }
            if ( object instanceof UserMessage && action.equals( DELETE_MESSAGE ) ) {
                plannerMessageListPanel.deleteMessage( (UserMessage) object, target );
            }
            if ( object instanceof UserMessage && action.equals( EMAIL_MESSAGE ) ) {
                plannerMessageListPanel.emailMessage( (UserMessage) object, target );
            }
        }
    }

    private void selectTabTitled( String title ) {
        List<? extends ITab> tabs = tabbedPanel.getTabs();
        for ( int i = 0; i < tabs.size(); i++) {
            if ( tabs.get(i).getTitle().getObject().equals( title )) {
                tabbedPanel.setSelectedTab( i );
                break;
            }
        }
     }

    public void newMessage( AjaxRequestTarget target, Change change ) {
        ModelObject about = (ModelObject) change.getSubject( getQueryService() );
        String sendTo = change.getProperty();
        selectTabTitled( getMessagesTabTitle() );
        plannerMessageListPanel.newMessage( sendTo == null ? "" : sendTo, about, target );
        target.add( tabbedPanel );
    }

    public boolean isCollapsible() {
        return collapsible;
    }

}
