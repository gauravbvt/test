// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.ui;

import org.acegisecurity.context.SecurityContextHolder;
import org.zkoss.zhtml.Text;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.GenericRichlet;
import org.zkoss.zk.ui.Page;
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
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

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
     * Initialize the page.
     * @param page the page
     */
    public void service( Page page ) {
        User user = (User) SecurityContextHolder.getContext().
                        getAuthentication().getPrincipal();

        page.setTitle( "Channels" );

        Hbox split = new Hbox();
        split.setSclass( "channels_main" );
        split.setWidth( "100%" );
        split.setHeight( "250px" );
        split.setWidths( "20%,80%" );
        split.appendChild( createAccordion( user ) );
        split.appendChild( newSplitter( true ) );
        split.appendChild( createCanvas( user ) );

        Vbox main = new Vbox();
        main.setWidth( "100%" );
        main.setValign( "top" );
        main.setSpacing( "0px" );
        main.appendChild( split );
        main.appendChild( createAlertPane( user ) );
        main.appendChild( createMinimizeBar( user ) );

        Vbox window = new Vbox();
        window.setWidth( "100%" );
        window.setValign( "top" );
        window.appendChild( createToolbar( user ) );
        window.appendChild( main );
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
//        html.setWidth( "50%" );
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
     */
    private Component createAccordion( User user ) {
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
        tabpanels.appendChild( createSettingsTab( user ) );

        Tabbox accordion = new Tabbox();
        accordion.setWidth( "100%" );
        accordion.setMold( "accordion" );
        accordion.appendChild( tabs );
        accordion.appendChild( tabpanels );
        accordion.setSelectedIndex( 0 );
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

    /**
     * Create the scenario tab.
     * @param user the user
     */
    private Tabpanel createScenarioTab( User user ) {
        Tree tree = new Tree();

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
     */
    private Tabpanel createSettingsTab( User user ) {
        Vbox vbox = new Vbox();
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
    private Component createAlertPane( User user ) {
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
//        splitter.setCollapse( before? "before" : "after" );
        splitter.setCollapse( "none" );
        splitter.setWidth( "1px" );
        return splitter;
    }
}
