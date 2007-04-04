// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.ui;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.acegisecurity.context.SecurityContextHolder;
import org.springframework.context.ApplicationContext;
import org.zkoss.zhtml.Text;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.GenericRichlet;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Session;
import org.zkoss.zul.Box;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Html;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Splitter;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

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
     * Default constructor.
     */
    public DesktopRichlet() {
        super();
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
        final User user = (User) SecurityContextHolder.getContext().
                            getAuthentication().getPrincipal();
        final System system = getSystem( page );

        page.setTitle( "Channels" );

        Hbox split = new Hbox();
        split.setSclass( "channels_main" );
        split.setWidth( "100%" );
        split.setHeight( "400px" );
        split.setWidths( "20%,80%" );
        split.appendChild( createAccordion( user, system ) );
        split.appendChild( newSplitter( true ) );
        split.appendChild( createCanvas( user ) );

        Vbox window = new Vbox();
        window.setWidth( "100%" );
        window.setValign( "top" );
        window.appendChild( createToolbar( user ) );
        window.setSpacing( "0px" );
        window.appendChild( split );
        window.appendChild( createAlertPane( user, page ) );
        window.appendChild( createMinimizeBar( user ) );

        window.setPage( page );
    }

    /**
     * Create a new toolbar for a given user.
     * @param the user
     */
    private Component createToolbar( User user ) {
        Hbox toolbar = new Hbox();
        toolbar.setSclass( "channels_toolbar" );
        toolbar.setHeight( "60px" );
        toolbar.setWidth( "100%" );
        toolbar.setValign( "middle" );

        toolbar.appendChild(
            new Html( "<a class=\"logo\" href=\"profile.jsp\">"
                + user.getName() + "</a>" ) );

        toolbar.appendChild(
            new Text( " Acting as " ) );

        Listbox roles = new Listbox();
        roles.setWidth( null );
        for ( String role : new String[]{
            "Employee", "Fire Warden", "Group Manager" } )
                roles.appendItem( role, role );
        toolbar.appendChild( roles );

        Html html = new Html( "<a href=\"logout.jsp\">Logout</a>" );
        toolbar.appendChild( html );

        Hbox iconbar = new Hbox();
        iconbar.setSpacing( "0px" );

        String[][] icons = new String[][]{
            { "Cut", "/images/16x16/cut.png" },
            { "Copy", "/images/16x16/copy.png" },
            { "Paste", "/images/16x16/paste.png" },
            { "Undo", "/images/16x16/undo.png" },
            { "Redo", "/images/16x16/redo.png" },
            { "Agree", "/images/16x16/nav_up_green.png" },
            { "Disagree", "/images/16x16/nav_down_red.png" },
            { "Chat", "/images/16x16/messages.png" },
            { "Search", "/images/16x16/find.png" },
            { "Todos", "/images/16x16/note_pinned.png" },
            { "Help", "/images/16x16/help.png" },
        };
        for ( String[] spec : icons ) {
            Button button = new Button( spec[0], spec[1] );
            button.setOrient( "vertical" );
            iconbar.appendChild( button );
        }

        toolbar.appendChild( iconbar );
        return toolbar;
    }

    /**
     * Create the accordion pane.
     * @param user the current user
     * @param system the system
     */
    private Component createAccordion( User user, System system ) {
        Tabs tabs = new Tabs();
        tabs.appendChild(
            new Tab( "People & Places", "images/16x16/user_building.png" ) );
        tabs.appendChild(
            new Tab( "Scenarios", "images/16x16/branch_element.png" ) );
        tabs.appendChild(
            new Tab( "Library", "images/16x16/books.png" ) );
        tabs.appendChild(
            new Tab( "Settings", "images/16x16/preferences.png" ) );

        Tabpanels tabpanels = new Tabpanels();
        tabpanels.appendChild( createPeopleTab( user ) );
        tabpanels.appendChild( createScenarioTab( user ) );
        tabpanels.appendChild( createLibraryTab( user ) );
        tabpanels.appendChild( createSettingsTab( user, system ) );

        Tabbox accordion = new Tabbox();
        accordion.setWidth( "100%" );
        accordion.setMold( "accordion" );
        accordion.appendChild( tabs );
        accordion.appendChild( tabpanels );
        accordion.setSelectedIndex( 1 );
        return accordion;
    }

    /**
     * Create the people tab.
     * @param user the user
     */
    private Tabpanel createPeopleTab( User user ) {
        String[][] buttons = new String[][] {
            { "My profile",  "images/24x24/id_card.png",
                "Your profile" },
            { "My contacts", "images/24x24/users-phone.png",
                "Your social network" },
            { "People", "images/24x24/village-people.png",
                "All people profiles" },
            { "Organizations", "images/24x24/orgs.png",
                "All organizational profiles" },
            { "Systems & resources", "images/24x24/systems.png",
                "Profiles of systems and information resources" },
        };

        Vbox vbox = new Vbox();
        for ( String[] def : buttons ) {
            Button button = new Button( def[0], def[1] );
            button.setTooltiptext( def[2] );
            vbox.appendChild( button );
        }

        Tabpanel tabpanel = new Tabpanel();
        tabpanel.appendChild( vbox );
        return tabpanel;
    }

    private Treeitem newTreeitem(
            boolean collapsed, String icon, String label,
            Treeitem... subs ) {

        Treeitem result = new Treeitem( label );
        result.setImage( icon );

        if ( subs.length > 0 ) {
            result.setOpen( !collapsed );
            Treechildren kids = new Treechildren();
            for ( Treeitem sub : subs )
                kids.appendChild( sub );
            result.appendChild( kids );
        }

        return result;
    }

    /**
     * Create the scenario tab.
     * @param user the user
     */
    private Tabpanel createScenarioTab( User user ) {

        final String project  = "images/16x16/environment.png";
        final String model    = "images/16x16/cube_molecule.png";
        final String scenario = "images/16x16/branch.png";
        final String report1  = "images/16x16/document_chart.png";
        final String report2  = "images/16x16/chart.png";
        final String report3  = "images/16x16/dot-chart.png";

        Treechildren treeChildren = new Treechildren();
        treeChildren.appendChild(
            newTreeitem( false, project, "ACME Business Continuity",
                newTreeitem( false, model, "Headquarters",
                    newTreeitem( true, scenario, "Blackout",
                        newTreeitem( false, report1, "Playbook" ),
                        newTreeitem( false, report1, "Issues Analysis" ),
                        newTreeitem( false, report2, "Dashboard" ),
                        newTreeitem( false, report3, "Info Flow" ) ),
                    newTreeitem( false, scenario, "Building Fire",
                        newTreeitem( false, report1, "Playbook" ),
                        newTreeitem( false, report1, "Issues Analysis" ),
                        newTreeitem( false, report2, "Dashboard" ),
                        newTreeitem( false, report3, "Info Flow" ) ),
                    newTreeitem( true, scenario, "Firewall Breach",
                        newTreeitem( false, report1, "Playbook" ),
                        newTreeitem( false, report1, "Issues Analysis" ),
                        newTreeitem( false, report2, "Dashboard" ),
                        newTreeitem( false, report3, "Info Flow" ) ) ),
                newTreeitem( true, model, "Supply Chain",
                    newTreeitem( false, scenario, "Some scenario" ) )
            ) );
        treeChildren.appendChild(
            newTreeitem( true, project, "CDC Avian Flu Preparedness",
                newTreeitem( false, model, "Some model" ) ) );
        treeChildren.appendChild(
            newTreeitem( true, project, "International Markets",
                newTreeitem( false, model, "Some other model" ) ) );

        Tree tree = new Tree();
        tree.appendChild( treeChildren );

        Tabpanel tabpanel = new Tabpanel();
        tabpanel.appendChild( tree );
        return tabpanel;
    }

    /**
     * Create the library tab.
     * @param user the user
     */
    private Tabpanel createLibraryTab( User user ) {
        String[][] buttons = new String[][] {
            { "Dictionary",  "images/24x24/book_open2.png",
                "Typologies" },
            { "Common scenarios", "images/24x24/branch_element.png",
                "Parameterized scenarios" },
            { "Policies", "images/24x24/scroll.png",
                "Policies that impact information sharing" },
            { "Gazetteer", "images/24x24/earth_find.png",
                "Location" },
        };

        Vbox vbox = new Vbox();
        for ( String[] def : buttons ) {
            Button button = new Button( def[0], def[1] );
            button.setTooltiptext( def[2] );
            vbox.appendChild( button );
        }

        Tabpanel tabpanel = new Tabpanel();
        tabpanel.appendChild( vbox );
        return tabpanel;
    }

    /**
     * Create the settings tab.
     * @param user the user
     * @param system the system
     */
    private Tabpanel createSettingsTab( User user, System system ) {
        String[][] userButtons = new String[][] {
            { "My preferences",  "images/24x24/user1_preferences.png",
                "Your personal settings" },
            { "Activity log", "images/24x24/step.png",
                "What's going on on this server" },
            { "Users management", "images/24x24/users3_preferences.png",
                "Keep track of users" },
        };

        String[][] adminButtons = new String[][] {
            { "Users management", "images/24x24/users3_preferences.png",
                "Keep track of users" },
            { "System configuration", "images/24x24/server_preferences.png",
                "Administer channels" },
            { "System monitoring", "images/24x24/oszillograph.png",
                "Keep an eye on things" },
        };

        Vbox vbox = new Vbox();
        for ( String[] def : userButtons ) {
            Button button = new Button( def[0], def[1] );
            button.setTooltiptext( def[2] );
            vbox.appendChild( button );
        }

        if ( system.isAdministrator( user ) )
            for ( String[] def : adminButtons ) {
                Button button = new Button( def[0], def[1] );
                button.setTooltiptext( def[2] );
                vbox.appendChild( button );
            }

        Tabpanel tabpanel = new Tabpanel();
        tabpanel.appendChild( vbox );
        return tabpanel;
    }

    /**
     * Create the main canvas for a given user.
     * @param user the user
     */
    private Component createCanvas( User user ) {
        Box box = new Box();
        box.setWidth( "100%" );
        box.appendChild( new Window( "when", "normal", false ) );
        box.appendChild( new Window( "what", "normal", false ) );
        box.appendChild( new Window( "who", "normal", false ) );

        return box;
    }

    /**
     * Create the alert pane.
     * @param user the user
     */
    private Component createAlertPane( User user, Page page ) {
        Box box = new Box();
        box.setSclass( "channels_alerts" );
        box.setHeight( "90px" );
        box.setWidth( "100%" );
        box.appendChild( new Text( "Alerts" ) );

        return box;
    }

    /**
     * Create the minimized window bar.
     * @param user the current user
     */
    private Component createMinimizeBar( User user ) {
        Box box = new Box();
        box.setSclass( "channels_min" );
        box.setWidth( "100%" );
        box.appendChild( new Text( "Minimized windows" ) );

        return box;
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
}
