// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.context.SecurityContextHolder;
import org.springframework.context.ApplicationContext;
import org.zkoss.zhtml.Text;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.GenericRichlet;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zul.Box;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hbox;
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

import com.mindalliance.channels.Model;
import com.mindalliance.channels.Project;
import com.mindalliance.channels.System;
import com.mindalliance.channels.User;
import com.mindalliance.channels.model.ModelImpl;
import com.mindalliance.channels.model.Scenario;

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

        Component canvas = createCanvas( user );

        Hbox split = new Hbox();
        split.setSclass( "channels_main" );
        split.setWidth( "100%" );
        split.setHeight( "400px" );
        split.setWidths( "20%,80%" );
        split.appendChild( createAccordion( user, system, canvas ) );
        split.appendChild( newSplitter( true ) );
        split.appendChild( canvas );

        Vbox window = new Vbox();
        window.setWidth( "100%" );
        window.setValign( "top" );
        window.appendChild( new Toolbar( user, system ) );
        window.setSpacing( "0px" );
        window.appendChild( split );
        window.appendChild( new AlertPane( user, system ) );
        window.appendChild( createMinimizeBar( user ) );

        window.setPage( page );
    }

    /**
     * Create the accordion pane.
     * @param user the current user
     * @param system the system
     * @param canvas the canvas to control
     */
    private Component createAccordion(
            User user, System system, Component canvas ) {

        AccordionTab[] tabDefs = new AccordionTab[] {
            new AccordionTab(
                "images/16x16/user_building.png",
                "People & Places",
                "Phonebook and locators",
                new SelectionTab( user, canvas,
                    new Selection(
                        "images/24x24/id_card.png",
                        "My profile",
                        "Your profile",
                        "ROLE_USER",
                        null, null ),
                    new Selection(
                        "images/24x24/users-phone.png",
                        "My contacts",
                        "Your social network",
                        "ROLE_USER",
                        null, null ),
                    new Selection(
                        "images/24x24/orgs.png",
                        "Organizations",
                        "All organizational profiles",
                        "ROLE_USER",
                        null, null ),
                    new Selection(
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
                createScenarioTab( canvas, user, system ) ),

            new AccordionTab(
                "images/16x16/books.png",
                "Library",
                "Reference section",
                new SelectionTab( user, canvas,
                    new Selection(
                        "images/24x24/book_open2.png",
                        "Dictionary",
                        "Typologies",
                        "ROLE_USER",
                        null, null ),
                    new Selection(
                        "images/24x24/branch_element.png",
                        "Common scenarios",
                        "Parameterized scenarios",
                        "ROLE_USER",
                        null, null ),
                    new Selection(
                        "images/24x24/scroll.png",
                        "Policies",
                        "Policies that impact information sharing",
                        "ROLE_USER",
                        null, null ),
                    new Selection(
                        "images/24x24/earth_find.png",
                        "Gazetteer",
                        "Location, location, location...",
                        "ROLE_USER",
                        null, null )
                ) ),

            new AccordionTab(
                "images/16x16/preferences.png",
                "Settings",
                "Projects, Models and Scenarios",
                new SelectionTab( user, canvas,
                    new Selection(
                        "images/24x24/user1_preferences.png",
                        "My preferences",
                        "Your personal settings",
                        "ROLE_USER",
                        null, null ),
                    new Selection(
                        "images/24x24/step.png",
                        "Activity log",
                        "What's going on on this server",
                        "ROLE_USER",
                        null, null ),
                    new Selection(
                        "images/24x24/users3_preferences.png",
                        "Users management",
                        "Keep track of users",
                        "ROLE_ADMIN",
                        null, null  ),
                    new Selection(
                        "images/24x24/server_preferences.png",
                        "System configuration",
                        "Administer channels",
                        "ROLE_ADMIN",
                        null, null  ),
                    new Selection(
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
            tabpanels.appendChild( def.getContent() );
        }

        Tabbox accordion = new Tabbox();
        accordion.setWidth( "100%" );
        accordion.setMold( "accordion" );
        accordion.appendChild( tabs );
        accordion.appendChild( tabpanels );
        accordion.setSelectedIndex( 1 );
        return accordion;
    }

    private Treeitem newTreeitem( Selection selection, Treechildren kids ) {

        Treeitem result = new Treeitem( selection.getLabel() );
        result.setImage( selection.getIcon() );
        result.setTooltip( selection.getTooltip() );
        result.setOpen( false );
        result.setValue( selection );

        if ( kids != null )
            result.appendChild( kids );

        return result;
    }

    /**
     * Create the scenario tab.
     * @param canvas the canvas to tie to selections
     * @param user the user
     * @param system the system
     */
    private Tabpanel createScenarioTab(
            Component canvas, User user, System system ) {

        Treechildren treeChildren = new Treechildren();
        for ( Project p : system.getProjects() )
            treeChildren.appendChild(
                    createProjectNode( canvas, p, user, system ) );

        Tree tree = new Tree();
        tree.appendChild( treeChildren );
        tree.addEventListener( "onSelect", new EventListener() {
            public boolean isAsap() {
                return true;
            }

            public void onEvent( Event event ) {
                SelectEvent e = (SelectEvent) event;
                Treeitem i = (Treeitem) e.getSelectedItems().iterator().next();
                Selection s = (Selection) i.getValue();
                if ( s != null )
                    s.select();
            }
        } );

        Tabpanel tabpanel = new Tabpanel();
        tabpanel.appendChild( tree );
        return tabpanel;
    }

    /**
     * Create a project node in the tree.
     *
     * @param canvas the canvas to associate with
     * @param project the project
     * @param user the current user
     * @param system the system
     * @return a tree node, collapsed
     */
    private Treeitem createProjectNode(
            Component canvas, Project project, User user, System system ) {

        Treechildren models = new Treechildren();
        for ( Model m : project.getModels() )
            models.appendChild( createModelNode( canvas, m, user, system ) );

        return newTreeitem(
            new Selection(
                "images/16x16/environment.png",
                project.getName(),
                "Project properties",
                "ROLE_USER",
                null,
                canvas ),
            models );
    }

    /**
     * Create a model node in the tree.
     *
     * @param canvas the canvas to associate with
     * @param model the model
     * @param user the current user
     * @param system the system
     * @return a tree node, collapsed
     */
    private Treeitem createModelNode(
            Component canvas, Model model, User user, System system ) {

        Treechildren scenarios = new Treechildren();
        for ( Scenario s : ( (ModelImpl) model ).getScenarios() )
            scenarios.appendChild(
                    createScenarioNode( canvas, s, user, system ) );

        return newTreeitem(
            new Selection(
                "images/16x16/cube_molecule.png",
                model.getName(),
                "Model properties",
                "ROLE_USER",
                null,
                canvas ),
            scenarios );
    }

    /**
     * Create a scenario node.
     *
     * @param canvas the associated canvas
     * @param scenario the scenario
     * @param user the current user
     * @param system the system
     */
    private Treeitem createScenarioNode(
            Component canvas, Scenario scenario, User user, System system ) {

        Treechildren reports = new Treechildren();
        reports.appendChild( newTreeitem(
            new Selection(
                    "images/16x16/document_chart.png",
                    "Playbook",
                    "View playbook",
                    "ROLE_USER",
                    null,
                    canvas ),
            null ) );

        reports.appendChild( newTreeitem(
            new Selection(
                    "images/16x16/document_chart.png",
                    "Issues Analysis",
                    "View issues",
                    "ROLE_USER",
                    null,
                    canvas ),
            null ) );

        reports.appendChild( newTreeitem(
            new Selection(
                    "images/16x16/chart.png",
                    "Dashboard",
                    "View dashboard",
                    "ROLE_USER",
                    null,
                    canvas ),
            null ) );

        reports.appendChild( newTreeitem(
            new Selection(
                    "images/16x16/dot-chart.png",
                    "Info Flow",
                    "View information flows",
                    "ROLE_USER",
                    null,
                    canvas ),
            null ) );

        return newTreeitem(
                new Selection(
                    "images/16x16/branch.png",
                    scenario.getName(),
                    "Scenario viewer",
                    "ROLE_USER",
                    new ScenarioViewer( system, scenario, user ),
                    canvas ),
                reports );
    }

    /**
     * Create the main canvas for a given user.
     * This component gets filled by the current selection from
     * the accordion pane.
     * @param user the user
     */
    private Component createCanvas( User user ) {
        Box box = new Box();
        box.setWidth( "100%" );
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

    //===================================================
    /**
     * Summary of what happens when the user selects an item in a tab
     * in the accordion pane.
     */
    private static class Selection {

        private String icon;
        private String label;
        private Component pane;
        private String tooltip;
        private String roles;
        private Component canvas;

        /**
         * Default constructor.
         *
         * @param icon path to the icon to display in lists
         * @param label the label of the selection
         * @param tooltip the tooltip of the selection
         * @param roles authorized roles for this selection
         * @param pane the pane to display in the canvas when selected
         * @param canvas the associated canvas
         */
        public Selection(
                String icon, String label, String tooltip, String roles,
                Component pane, Component canvas ) {

            this.icon = icon;
            this.label = label;
            this.pane = pane;
            this.tooltip = tooltip;
            this.roles = roles;
            this.canvas = canvas;
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
         * Return the value of pane.
         */
        public Component getPane() {
            return this.pane;
        }

        /**
         * Return the value of tooltip.
         */
        public String getTooltip() {
            return this.tooltip;
        }

        /**
         * Perform the action associated with this selection.
         */
        @SuppressWarnings( "unchecked" )
        public void select() {
            List children = new ArrayList( canvas.getChildren() );
            for ( Object child : children )
                canvas.removeChild( (Component) child );

            canvas.appendChild( pane != null ? pane
                                             : new Text( "TBD: " + label ) );
            canvas.invalidate();
        }

        public String getRoles() {
            return this.roles;
        }

        /**
         * Test if a user is authorized to perform this selection.
         * @param user the user
         */
        public boolean isAuthorized( User user ) {
            StringTokenizer tokenizer = new StringTokenizer( getRoles(), ", " );
            while ( tokenizer.hasMoreTokens() ) {
                String role = tokenizer.nextToken();
                for ( GrantedAuthority a : user.getAuthorities() ) {
                    if ( a.getAuthority().equals( role ) )
                        return true;
                }
            }
            return false;
        }

        /**
         * Get the canvas associated with this selection.
         */
        public Component getCanvas() {
            return this.canvas;
        }

        /**
         * Set the value of canvas.
         * @param canvas The new value of canvas
         */
        public void setCanvas( Component canvas ) {
            this.canvas = canvas;
        }
    }

    //===================================================
    /**
     * Definition of an accordion tab.
     */
    private static class AccordionTab {

        private String icon;
        private String label;
        private String tooltip;
        private Component content;

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
                Component content ) {

            super();
            this.icon = icon;
            this.label = label;
            this.tooltip = tooltip;
            this.content = content;
        }

        /**
         * Return the value of content.
         */
        public Component getContent() {
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
         * @param user the current user
         * @param canvas the canvas to map the selections to
         * @param selections the selectionable items in the pane
         */
        public SelectionTab(
                User user, Component canvas, Selection... selections ) {
            super();

            Vbox vbox = new Vbox();
            for ( final Selection sel : selections ) {
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
