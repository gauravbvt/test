// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.ui;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.acegisecurity.context.SecurityContextHolder;
import org.springframework.context.ApplicationContext;
import org.zkoss.zhtml.Text;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.GenericRichlet;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.event.ClientInfoEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Box;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Splitter;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Vbox;

import com.mindalliance.channels.System;
import com.mindalliance.channels.User;

/**
 * The user desktop.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 */
public class DesktopRichlet extends GenericRichlet {

    /**
     * Session attribute name of the current tab selection.
     */
    public static final String TAB_SELECTION = "TabSelection" ;

    /**
     * Session attribute name of the current accordion selection.
     */
    public static final String CURRENT_SELECTION = "AccordionSelection" ;

    /**
     * The fixed real estate height of the accordion pane.
     * 4*28 + 11...
     */
    private static final int FIXED_ACCORDION_HEIGHT = 123;

    /**
     * The total height of the fixed parts of the desktop.
     * 46 + 87 + 25...
     */
    private static final int FIXED_HEIGHT = 158;
    private static final int DEFAULT_CANVAS_HEIGHT = 100;
    private static final String DESKTOP_HEIGHT = "DesktopHeight";

    /**
     * Default constructor.
     */
    public DesktopRichlet() {
        super();
    }

    /**
     * Get the current logged in user.
     */
    private User getUser() {
        return (User) SecurityContextHolder.getContext().
                            getAuthentication().getPrincipal();
    }

    /**
     * Get the system object given a page.
     * @param page the page
     * @return the current system object
     */
    private System getSystem( Page page ) {
        Session zkSession = page.getDesktop().getSession();
        HttpSession httpSession = (HttpSession) zkSession.getNativeSession();
        ServletContext servletContext = httpSession.getServletContext();
        ApplicationContext appContext =
            (ApplicationContext) servletContext.getAttribute(
                "org.springframework.web.context.WebApplicationContext.ROOT" );

        return (System) appContext.getBean( "system" );
    }

    /**
     * Initialize the page.
     * @param page the page
     */
    public void service( final Page page ) {
        final User user = getUser();
        final System system = getSystem( page );

        page.setTitle( "Channels" );

        // Display the screen in 2 phases to minimize resizing flicker...
        Component window = getDesktopHeight( page ) == -1 ?
                new Text( "Loading..." )
              : createDesktop( page, user, system );

        window.addEventListener( "onClientInfo", new EventListener() {

            public boolean isAsap() {
                return true;
            }

            public void onEvent( Event event ) {
                ClientInfoEvent cie = (ClientInfoEvent) event;
                int oldH = getDesktopHeight( page );
                int newH = cie.getDesktopHeight();
                if ( oldH != newH ) {
                    page.getDesktop().getSession().setAttribute(
                            DESKTOP_HEIGHT, newH );
                    Executions.sendRedirect( null );
                }
            }
        } );
        window.setPage( page );
    }

    /**
     * Create the desktop.
     * @param page the page
     * @param user the current user
     * @param system the system
     */
    private Vbox createDesktop( Page page, User user, System system ) {
        Box canvas = createCanvas( user );
        canvas.setPage( page );
        int canvasHeight = getCanvasHeight( page );
        canvas.setHeight( canvasHeight + "px" );
        canvas.setWidth( "100%" );

        Hbox split = new Hbox();
        split.setSclass( "channels_main" );
        split.setWidth( "100%" );
        split.setHeight( canvasHeight + "px" );
        split.appendChild(
                createAccordion( canvasHeight, user, system, canvas ) );
        split.appendChild( newSplitter( true ) );
        split.appendChild( canvas );
        split.setWidths( "30%,70%" );

        final Vbox window = new Vbox();
        window.setWidth( "100%" );
        window.setValign( "top" );
        window.appendChild( new Toolbar( user, system ) );
        window.setSpacing( "0px" );
        window.appendChild( split );
        window.appendChild( new AlertPane( user, system ) );
        window.appendChild( createMinimizeBar( user ) );

        return window;
    }

    /**
     * Create the accordion pane.
     * @param maxHeight height at which scollbar will appear
     * @param user the current user
     * @param system the system
     * @param canvas the canvas to control
     */
    private Component createAccordion(
            int maxHeight,
            User user, System system, Box canvas ) {

        final int tabContentHeight = maxHeight - FIXED_ACCORDION_HEIGHT;
        AccordionTab[] tabDefs = new AccordionTab[] {
            new AccordionTab(
                "images/16x16/user_building.png",
                "People & Places",
                "Phonebook and locators",
                new SelectionTab( tabContentHeight, user, canvas,
                    new AccordionSelection(
                        "images/24x24/id_card.png",
                        "My profile",
                        "Your profile",
                        "ROLE_USER",
                        null, null ),
                    new AccordionSelection(
                        "images/24x24/users-phone.png",
                        "My contacts",
                        "Your social network",
                        "ROLE_USER",
                        null, null ),
                    new AccordionSelection(
                        "images/24x24/orgs.png",
                        "Organizations",
                        "All organizational profiles",
                        "ROLE_USER",
                        null, null ),
                    new AccordionSelection(
                        "images/24x24/systems.png",
                        "Systems & resources",
                        "Profiles of systems and information resources",
                        "ROLE_USER",
                        null, null )
                ) ),

            new AccordionTab(
                "images/16x16/branch_element.png",
                "Scenarios",
                "Projects, Models and Scenarios",
                new ScenariosTab(
                    maxHeight, tabContentHeight, canvas, user, system ) ),

            new AccordionTab(
                "images/16x16/books.png",
                "Library",
                "Reference section",
                new SelectionTab( tabContentHeight, user, canvas,
                    new AccordionSelection(
                        "images/24x24/book_open2.png",
                        "Dictionary",
                        "Typologies",
                        "ROLE_USER",
                        null, null ),
                    new AccordionSelection(
                        "images/24x24/branch_element.png",
                        "Common scenarios",
                        "Parameterized scenarios",
                        "ROLE_USER",
                        null, null ),
                    new AccordionSelection(
                        "images/24x24/scroll.png",
                        "Policies",
                        "Policies that impact information sharing",
                        "ROLE_USER",
                        null, null ),
                    new AccordionSelection(
                        "images/24x24/earth_find.png",
                        "Gazetteer",
                        "Location, location, location...",
                        "ROLE_USER",
                        null, null )
                ) ),

            new AccordionTab(
                "images/16x16/preferences.png",
                "Settings",
                "Preferences, Logs, etc.",
                new SelectionTab( tabContentHeight, user, canvas,
                    new AccordionSelection(
                        "images/24x24/user1_preferences.png",
                        "My preferences",
                        "Your personal settings",
                        "ROLE_USER",
                        null, null ),
                    new AccordionSelection(
                        "images/24x24/step.png",
                        "Activity log",
                        "What's going on on this server",
                        "ROLE_USER",
                        null, null ),
                    new AccordionSelection(
                        "images/24x24/users3_preferences.png",
                        "Users management",
                        "Keep track of users",
                        "ROLE_ADMIN",
                        null, null  ),
                    new AccordionSelection(
                        "images/24x24/server_preferences.png",
                        "System configuration",
                        "Administer channels",
                        "ROLE_ADMIN",
                        null, null  ),
                    new AccordionSelection(
                        "images/24x24/oszillograph.png",
                        "System monitoring",
                        "Keep an eye on things",
                        "ROLE_ADMIN",
                        null, null  )
                ) ),
        };

        Tabs tabs = new Tabs();
        Tabpanels tabpanels = new Tabpanels();
        for ( AccordionTab def : tabDefs ) {
            Tab tab = new Tab( def.getLabel(), def.getIcon() );
            tab.setTooltip( def.getTooltip() );
            tabs.appendChild( tab );
            Tabpanel content = def.getContent();
            content.setHeight( tabContentHeight + "px" );
            tabpanels.appendChild( content );
        }

        final Tabbox accordion = new Tabbox();
        accordion.setMold( "accordion" );
        accordion.setWidth( "100%" );
        accordion.appendChild( tabs );
        accordion.appendChild( tabpanels );
        accordion.addEventListener( "onSelect", new EventListener() {
            public boolean isAsap() {
                return true;
            }

            public void onEvent( Event event ) {
                Integer sel = Integer.valueOf( accordion.getSelectedIndex() );
                accordion.getDesktop().getSession().setAttribute(
                        TAB_SELECTION,
                        sel );
            }
        } );
        accordion.setSelectedIndex( getTabSelection( canvas ) );
        return accordion;
    }

    /**
     * Get the current tab selection.
     * @param component a component to get to the session
     * @return the current selection, or 1 if unspecified.
     */
    public int getTabSelection( Component component ) {
        Integer sel = (Integer) component.getDesktop().getSession()
                        .getAttribute( TAB_SELECTION );
        return sel == null ? 1 : sel.intValue();
    }

    /**
     * Create the main canvas for a given user.
     * This component gets filled by the current selection from
     * the accordion pane.
     * @param user the user
     */
    private Box createCanvas( User user ) {
        return new Box();
    }

    /**
     * Create the minimized window bar.
     * @param user the current user
     */
    private Component createMinimizeBar( User user ) {
        Box box = new Box();
        box.setSclass( "channels_min" );
        box.setWidth( "100%" );
        box.setHeight( "14px" );
        box.appendChild( new Text( "Minimized windows" ) );

        return box;
    }

    /**
     * Return the available height of the desktop.
     * @param page the page
     * @return the height, or -1 if unknown
     */
    private int getDesktopHeight( Page page ) {
        Session session = page.getDesktop().getSession();
        Integer oldH = (Integer) session.getAttribute( DESKTOP_HEIGHT );
        return oldH == null ? -1 : oldH.intValue();
    }

    /**
     * Figure out the height of the canvas component given what we know of the
     * desktop's size.
     * @return the size in pixels
     */
    private int getCanvasHeight( Page page ) {
        int height = getDesktopHeight( page );
        return height == -1 ? DEFAULT_CANVAS_HEIGHT
                            : ( height - FIXED_HEIGHT );
    }

    /**
     * Create a splitter.
     * @param before if true collapse arrow point to previous pane.
     */
    private Splitter newSplitter( boolean before ) {
        Splitter splitter = new Splitter();
        splitter.setCollapse( "none" );
        return splitter;
    }

    //===================================================
    /**
     * Definition of an accordion tab.
     */
    private static class AccordionTab {

        private String icon;
        private String label;
        private String tooltip;
        private Tabpanel content;

        /**
         * Default constructor.
         *
         * @param icon the tab's icon
         * @param label the tab's label
         * @param tooltip the tooltip
         * @param content the content when selected
         */
        public AccordionTab(
                String icon, String label, String tooltip,
                Tabpanel content ) {

            super();
            this.icon = icon;
            this.label = label;
            this.tooltip = tooltip;
            this.content = content;
        }

        /**
         * Return the value of content.
         */
        public Tabpanel getContent() {
            return this.content;
        }

        /**
         * Return the value of icon.
         */
        public String getIcon() {
            return this.icon;
        }

        /**
         * Return the value of label.
         */
        public String getLabel() {
            return this.label;
        }

        /**
         * Return the value of tooltip.
         */
        public String getTooltip() {
            return this.tooltip;
        }
    }

    //===================================================
    /**
     * A simple tab containing a flat list of selections.
     */
    private static class SelectionTab extends Tabpanel {

        /**
         * Default constructor.
         * @param height the available height for the content
         * @param user the current user
         * @param canvas the canvas to map the selections to
         * @param selections the selectionable items in the pane
         */
        public SelectionTab(
                int height,
                User user, Component canvas,
                AccordionSelection... selections ) {
            super();
            setHeight( height + "px" );

            Vbox vbox = new Vbox();
            vbox.setWidth( "100%" );

            for ( final AccordionSelection sel : selections ) {
                if ( sel.isAuthorized( user ) ) {
                    sel.setCanvas( canvas );

                    Button button = new Button( sel.getLabel(), sel.getIcon() );
                    button.setTooltiptext( sel.getTooltip() );
                    button.addEventListener( "onClick", new EventListener() {
                        public boolean isAsap() {
                            return true;
                        }

                        public void onEvent( Event event ) {
                            sel.select();
                        }
                    } );
                    vbox.appendChild( button );
                }
            }

            appendChild( vbox );
        }
    }
}
