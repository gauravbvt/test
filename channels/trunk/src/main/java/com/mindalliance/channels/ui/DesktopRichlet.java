// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

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

import com.mindalliance.channels.User;
import com.mindalliance.channels.data.elements.resources.Organization;
import com.mindalliance.channels.services.DirectoryService;
import com.mindalliance.channels.services.SystemService;
import com.mindalliance.channels.ui.editor.EditorFactory;

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
     * 46 + 75 + 25...
     */
    private static final int FIXED_HEIGHT = 141;
    private static final int DEFAULT_CANVAS_HEIGHT = 100;
    private static final String DESKTOP_HEIGHT = "DesktopHeight";
    private static final int MIN_DESKTOP_HEIGHT = 435;

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
     * Get the SystemService instance associated with the page.
     * @return the SystemService instance
     */
    private SystemService getSystemService() {
        Session zkSession = Executions.getCurrent().getDesktop().getSession();
        HttpSession httpSession = (HttpSession) zkSession.getNativeSession();
        ServletContext servletContext = httpSession.getServletContext();
        ApplicationContext appContext =
            (ApplicationContext) servletContext.getAttribute(
                "org.springframework.web.context.WebApplicationContext.ROOT" );
        return (SystemService) appContext.getBean( "systemservice" );
    }

    /**
     * Initialize the page.
     * @param page the page
     */
    public void service( final Page page ) {
        final User user = getUser();
        final SystemService system = getSystemService();

        page.setTitle( "Channels" );

        // Display the screen in 2 phases to minimize resizing flicker...
        Component window = getDesktopHeight( page ) == -1 ?
                new Text( "Loading..." )
              : createDesktop( page, user, system );

        window.setPage( page );
        window.addEventListener( "onClientInfo", new EventListener() {

            public boolean isAsap() {
                return true;
            }

            public void onEvent( Event event ) {
                ClientInfoEvent cie = (ClientInfoEvent) event;
                int oldH = getDesktopHeight( page );
                int newH = Math.max(
                        MIN_DESKTOP_HEIGHT, cie.getDesktopHeight() );
                if ( oldH != newH ) {
                    page.getDesktop().getSession().setAttribute(
                            DESKTOP_HEIGHT, newH );
                    Executions.sendRedirect( null );
                }
            }
        } );
    }

    /**
     * Create the desktop.
     * @param page the page
     * @param user the current user
     * @param system the system
     */
    private Vbox createDesktop( Page page, User user, SystemService system ) {
        final Vbox window = new Vbox();
        window.setPage( page );
        window.setWidth( "100%" );
        window.setValign( "top" );

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
        split.setWidths( "236px,auto" );

        window.appendChild( new Toolbar( user, system ) );
        window.setSpacing( "0px" );
        window.appendChild( split );
        window.appendChild( new AlertPane( user, system ) );
        window.appendChild( createMinimizeBar( user ) );

        return window;
    }

    /**
     * Create the accordion pane.
     * @todo Hook to actual objects in the model
     * @param maxHeight height at which scollbar will appear
     * @param user the current user
     * @param system the system
     * @param canvas the canvas to control
     */
    private Component createAccordion(
            int maxHeight, User user, SystemService system, Box canvas ) {

        EditorFactory ef = new EditorFactory();
        ef.setPage( canvas.getPage() );
        ef.setSystem( system );
        ef.setUser( user );

        final int tabContentHeight = maxHeight - FIXED_ACCORDION_HEIGHT;
        AccordionTab[] tabDefs = new AccordionTab[] {
            createPeoplePlaceTab( canvas, ef, tabContentHeight ),

            new AccordionTab(
                "images/16x16/branch_element.png",
                "Scenarios",
                "Projects, Models and Scenarios",
                new ScenariosTab(
                    maxHeight, tabContentHeight, canvas, ef ) ),

            new AccordionTab(
                "images/16x16/books.png",
                "Library",
                "Reference section",
                new SelectionTab( tabContentHeight, user,
                    new AccordionSelection(
                        "images/24x24/book_open2.png",
                        "Dictionary",
                        "Typologies",
                        "ROLE_USER",
                        canvas, ef.createEditor( null ) ),
                    new AccordionSelection(
                        "images/24x24/branch_element.png",
                        "Common scenarios",
                        "Parameterized scenarios",
                        "ROLE_USER",
                        canvas, ef.createEditor( null ) ),
                    new AccordionSelection(
                        "images/24x24/scroll.png",
                        "Policies",
                        "Policies that impact information sharing",
                        "ROLE_USER",
                        canvas, ef.createEditor( null ) ),
                    new AccordionSelection(
                        "images/24x24/earth_find.png",
                        "Gazetteer",
                        "Location, location, location...",
                        "ROLE_USER",
                        canvas, ef.createEditor( null ) )
                ) ),

            new AccordionTab(
                "images/16x16/preferences.png",
                "Settings",
                "Preferences, Logs, etc.",
                new SelectionTab( tabContentHeight, user,
                    new AccordionSelection(
                        "images/24x24/user1_preferences.png",
                        "My preferences",
                        "Your personal settings",
                        "ROLE_USER",
                        canvas, ef.createEditor( null ) ),
                    new AccordionSelection(
                        "images/24x24/step.png",
                        "Activity log",
                        "What's going on on this server",
                        "ROLE_USER",
                        canvas, ef.createEditor( null ) ),
                    new AccordionSelection(
                        "images/24x24/users3_preferences.png",
                        "Users management",
                        "Keep track of users",
                        "ROLE_ADMIN",
                        canvas, ef.createEditor( null )  ),
                    new AccordionSelection(
                        "images/24x24/server_preferences.png",
                        "System configuration",
                        "Administer channels",
                        "ROLE_ADMIN",
                        canvas, ef.createEditor( null )  ),
                    new AccordionSelection(
                        "images/24x24/oszillograph.png",
                        "System monitoring",
                        "Keep an eye on things",
                        "ROLE_ADMIN",
                        canvas, ef.createEditor( null )  )
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
     * Create the people and places tab.
     * @param canvas the canvas
     * @param ef the editor factory
     * @param tabContentHeight the content height
     */
    private AccordionTab createPeoplePlaceTab(
            Box canvas, EditorFactory ef, final int tabContentHeight ) {

        User user = ef.getUser();
        DirectoryService directory = ef.getSystem().getDirectoryService();

        return new AccordionTab(
            "images/16x16/user_building.png",
            "People & Places",
            "Phonebook and locators",
            new SelectionTab( tabContentHeight, user,
                new AccordionSelection(
                    "images/24x24/id_card.png",
                    "My profile",
                    "Your profile",
                    "ROLE_USER",
                    canvas, ef.createEditor( user ) ),
                new AccordionSelection(
                    "images/24x24/users-phone.png",
                    "My contacts",
                    "Your social network",
                    "ROLE_USER",
                    canvas, ef.createEditor( null ) ),
                new AccordionSelection(
                    "images/24x24/orgs.png",
                    "Organizations",
                    "All organizational profiles",
                    "ROLE_USER",
                    canvas,
                    createOrgBrowser( ef, directory ) ),
                new AccordionSelection(
                    "images/24x24/systems.png",
                    "Systems & resources",
                    "Profiles of systems and information resources",
                    "ROLE_USER",
                    canvas, ef.createEditor( null ) )
            ) );
    }

    /**
     * Hook up the organizations browser.
     * @param ef the editor factory
     * @param directory the directory
     */
    private ObjectBrowser<Organization> createOrgBrowser(
            EditorFactory ef, final DirectoryService directory ) {

        final ObjectBrowser<Organization> browser =
            ef.createBrowser(
                directory.getOrganizations(),
                Organization.class,
                null
            );

        // React to backgound modifications to list
        directory.addPropertyChangeListener( "organizations",
            new PropertyChangeListener() {
                public void propertyChange( PropertyChangeEvent evt ) {
                    browser.setObjects( directory.getOrganizations() );
                }
            } );

        return browser;
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
         * @param selections the selectionable items in the pane
         */
        public SelectionTab(
                int height, User user,
                AccordionSelection... selections ) {
            super();
            setHeight( height + "px" );

            Vbox vbox = new Vbox();
            vbox.setWidth( "100%" );

            for ( final AccordionSelection sel : selections ) {
                if ( sel.isAuthorized( user ) ) {
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
