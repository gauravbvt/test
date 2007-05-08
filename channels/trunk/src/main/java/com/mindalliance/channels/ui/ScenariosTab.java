// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.ui;

import java.beans.PropertyVetoException;
import java.text.MessageFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.OpenEvent;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zul.Box;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Menuseparator;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;

import com.mindalliance.channels.Model;
import com.mindalliance.channels.Project;
import com.mindalliance.channels.System;
import com.mindalliance.channels.User;
import com.mindalliance.channels.model.ModelImpl;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.project.ProjectImpl;

/**
 * The scenarios, models and projects tab.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 */
public class ScenariosTab extends Tabpanel {

    private static Log logger = LogFactory.getLog( ScenariosTab.class );

    private static final String CHART_ICON = "images/16x16/dot-chart.png";
    private static final String SCENARIO_ICON = "images/16x16/branch.png";
    private static final String DASHBOARD_ICON = "images/16x16/chart.png";
    private static final String REPORT_ICON = "images/16x16/document_chart.png";
    private static final String MODEL_ICON = "images/16x16/cube_molecule.png";
    private static final String PROJECT_ICON = "images/16x16/environment.png";

    private System system ;
    private User user;
    private Session session;

    /**
     * Convenience constructor.
     *
     * @param maxHeight the maximum height of the canvas
     * @param height the height of the tab's content
     * @param canvas the canvas
     * @param user the current user
     * @param system the system
     */
    public ScenariosTab(
            int maxHeight, int height, Box canvas, User user, System system ) {

        super();
        this.user = user;
        this.system = system;
        setPage( canvas.getPage() );
        this.session = getDesktop().getSession();

        Treechildren treeChildren = new Treechildren();
        for ( Project p : system.getProjects() )
            treeChildren.appendChild(
                createProjectNode( maxHeight, canvas, p, user, system ) );

        Tree tree = new Tree();
        tree.appendChild( treeChildren );
        tree.addEventListener( "onSelect", new EventListener() {
            public boolean isAsap() {
                return true;
            }

            public void onEvent( Event event ) {
                SelectEvent e = (SelectEvent) event;
                Treeitem i = (Treeitem) e.getSelectedItems().iterator().next();
                AccordionSelection s = (AccordionSelection) i.getValue();
                if ( s != null )
                    s.select();
            }
        } );

        Menupopup menu = createPopup();
        appendChild( tree );
        appendChild( menu );
        setContext( menu.getId() );
        menu.setParent( this );
        setHeight( height + "px" );
    }

    /**
     * Create a project node in the tree.
     *
     * @param maxHeight the available height for the canvas
     * @param canvas the canvas to associate with
     * @param project the project
     * @param user the current user
     * @param system the system
     * @return a tree node, collapsed
     */
    private Treeitem createProjectNode(
            int maxHeight,
            Box canvas, Project project, User user, System system ) {

        Treechildren models = new Treechildren();
        for ( Model m : project.getModels() )
            models.appendChild(
                    createModelNode( maxHeight, canvas, m, user, system ) );

        Treeitem result = newTreeitem(
                    new AccordionSelection(
                        PROJECT_ICON,
                        project.getName(),
                        "Project properties",
                        "ROLE_USER",
                        null,
                        canvas,
                        project ),
                    models );
        return result;
    }

    /**
     * Create a model node in the tree.
     *
     * @param maxHeight the available height for the canvas
     * @param canvas the canvas to associate with
     * @param model the model
     * @param user the current user
     * @param system the system
     * @return a tree node, collapsed
     */
    private Treeitem createModelNode(
            int maxHeight,
            Box canvas, Model model, User user, System system ) {

        Treechildren scenarios = new Treechildren();
        for ( Scenario s : ( (ModelImpl) model ).getScenarios() )
            scenarios.appendChild(
                    createScenarioNode( maxHeight, canvas, s, user, system ) );

        return newTreeitem(
            new AccordionSelection(
                MODEL_ICON,
                model.getName(),
                "Model properties",
                "ROLE_USER",
                null,
                canvas,
                model ),
            scenarios );
    }

    /**
     * Create a scenario node.
     *
     * @param maxHeight the available height for the canvas
     * @param canvas the associated canvas
     * @param scenario the scenario
     * @param user the current user
     * @param system the system
     */
    private Treeitem createScenarioNode(
            int maxHeight,
            Box canvas, Scenario scenario, User user, System system ) {

        Treechildren reports = new Treechildren();
        reports.appendChild( newTreeitem(
            new AccordionSelection(
                    REPORT_ICON,
                    "Playbook",
                    "View playbook",
                    "ROLE_USER",
                    null,
                    canvas ),
            null ) );

        reports.appendChild( newTreeitem(
            new AccordionSelection(
                    REPORT_ICON,
                    "Issues Analysis",
                    "View issues",
                    "ROLE_USER",
                    null,
                    canvas ),
            null ) );

        reports.appendChild( newTreeitem(
            new AccordionSelection(
                    DASHBOARD_ICON,
                    "Dashboard",
                    "View dashboard",
                    "ROLE_USER",
                    null,
                    canvas ),
            null ) );

        reports.appendChild( newTreeitem(
            new AccordionSelection(
                    CHART_ICON,
                    "Info Flow",
                    "View information flows",
                    "ROLE_USER",
                    null,
                    canvas ),
            null ) );

        return newTreeitem(
                new AccordionSelection(
                    SCENARIO_ICON,
                    scenario.getName(),
                    "Scenario viewer",
                    "ROLE_USER",
                    new ScenarioViewer(
                        maxHeight, canvas.getPage(), system, scenario, user ),
                    canvas,
                    scenario ),
                reports );
    }

    /**
     * Create the popup menu. The menu is recreated depending on the user
     * and the selection on every right-click.
     */
    private Menupopup createPopup() {
        Menupopup result = new Menupopup();
        result.setId( "tree-popup" );
        result.addEventListener( "onOpen", new EventListener() {
            public boolean isAsap() {
                return true;
            }

            public void onEvent( Event event ) {
                OpenEvent oe = (OpenEvent) event;
                if ( oe.isOpen() ) {
                    Menupopup menu = (Menupopup) oe.getTarget();

                    AccordionSelection selection =
                        (AccordionSelection) session.getAttribute(
                                DesktopRichlet.CURRENT_SELECTION );

                    resetMenu( menu, selection, getUser() );
                }
            }
        } );

        return result;
    }

    /**
     * Adjust the popup menu for the given selection and user.
     * @param menu the popup menu
     * @param selection the current selection
     * @param user the user
     */
    protected void resetMenu(
        Menupopup menu, AccordionSelection selection, User user ) {

        menu.getChildren().clear();
        if ( selection == null ) {
            if ( getSystem().isAdministrator( user ) )
                menu.appendChild( newAddProjectItem() );

        } else if ( selection.getIcon().equals( PROJECT_ICON ) ) {
            final ProjectImpl project = (ProjectImpl) selection.getObject();

            if ( getSystem().isAdministrator( user ) ) {
                menu.appendChild( newAddProjectItem() );
                menu.appendChild( new Menuseparator() );
                menu.appendChild( newDeleteProjectItem( project ) );
            }
            if ( project.isManager( user )
                    || system.isAdministrator( user ) ) {
                menu.appendChild( newRenameProjectItem( project ) );
                menu.appendChild( newAddModelItem( project ) );
            }

        } else if ( selection.getIcon().equals( MODEL_ICON ) ) {
            ModelImpl model = (ModelImpl) selection.getObject();
            ProjectImpl project = (ProjectImpl) model.getProject();

            if ( project.isManager( user )
                    || system.isAdministrator( user ) ) {
                menu.appendChild( newAddModelItem( project ) );
                menu.appendChild( new Menuseparator() );
                menu.appendChild( newDeleteModelItem( model ) );
                menu.appendChild( newRenameModelItem( model ) );
                menu.appendChild( newAddScenarioItem( model ) );
            }

        } else if ( selection.getIcon().equals( SCENARIO_ICON ) ) {
            Scenario scenario = (Scenario) selection.getObject();
            ModelImpl model = (ModelImpl) scenario.getModel();
            Project project = model.getProject();

            if ( project.isManager( user )
                    || system.isAdministrator( user ) ) {
                menu.appendChild( newAddScenarioItem( model ) );
                menu.appendChild( new Menuseparator() );
                menu.appendChild( newDeleteScenarioItem( scenario ) );
                menu.appendChild( newRenameScenarioItem( scenario ) );
            }
        }
    }

    private Menuitem newDeleteProjectItem( final Project project ) {
        // TODO make this a command
        Menuitem deleteProjectItem = newMenuitem(
            "Delete this project...",
            new Runnable() {
                public void run() {
                    try {
                        int button = Messagebox.show(
                            MessageFormat.format(
                                "Delete project {0}?",
                                project.getName() ),
                            null,
                            Messagebox.OK | Messagebox.CANCEL,
                            Messagebox.QUESTION );
                        if ( button == Messagebox.OK ) {
                            getSystem().removeProject( project );
                            session.setAttribute(
                                DesktopRichlet.CURRENT_SELECTION, null );
                            Executions.sendRedirect( null );
                        }
                    } catch ( InterruptedException e1 ) {
                        logger.warn( "Dialog was interrupted", e1 );
                    }
                }
            } );
        return deleteProjectItem;
    }

    private Menuitem newDeleteModelItem( final ModelImpl model ) {
        // TODO make this a command
        Menuitem result = newMenuitem(
            "Delete this model...",
            new Runnable() {
                public void run() {
                    try {
                        int button = Messagebox.show(
                            MessageFormat.format(
                                "Delete model {0}?",
                                model.getName() ),
                            null,
                            Messagebox.OK | Messagebox.CANCEL,
                            Messagebox.QUESTION );
                        if ( button == Messagebox.OK ) {
                            model.getProject().removeModel( model );
                            session.setAttribute(
                                    DesktopRichlet.CURRENT_SELECTION, null );
                            Executions.sendRedirect( null );
                        }
                    } catch ( InterruptedException e1 ) {
                        logger.warn( "Dialog was interrupted", e1 );
                    }
                }
            } );
        return result;
    }

    private Menuitem newDeleteScenarioItem( final Scenario scenario ) {
        // TODO make this a command
        Menuitem result = newMenuitem(
            "Delete this scenario...",
            new Runnable() {
                public void run() {
                    try {
                        int button = Messagebox.show(
                            MessageFormat.format(
                                "Delete scenario {0}?",
                                scenario.getName() ),
                            null,
                            Messagebox.OK | Messagebox.CANCEL,
                            Messagebox.QUESTION );
                        if ( button == Messagebox.OK ) {
                            ModelImpl model = (ModelImpl) scenario.getModel();
                            model.removeScenario( scenario );
                            session.setAttribute(
                                    DesktopRichlet.CURRENT_SELECTION, null );
                            Executions.sendRedirect( null );
                        }
                    } catch ( InterruptedException e1 ) {
                        logger.warn( "Dialog was interrupted", e1 );
                    }
                }
            } );
        return result;
    }

    private Menuitem newAddProjectItem() {
        // TODO make this a command
        Menuitem result = newMenuitem(
            "Add a project...",
            new Runnable() {
                public void run() {
                    try {
                        String projectName = Prompter.prompt(
                            "Project name?",
                            "Enter the name of the new project",
                            "" );
                        if ( projectName != null )
                            try {
                                ProjectImpl project = new ProjectImpl();
                                project.setName( projectName );
                                getSystem().addProject( project );
                                Executions.sendRedirect( null );
                            } catch ( PropertyVetoException e ) {
                                Messagebox.show(
                                    e.getMessage(), null,
                                    Messagebox.OK, Messagebox.ERROR );
                            }
                    } catch ( InterruptedException e1 ) {
                        logger.warn( "Dialog was interrupted", e1 );
                    }
                }
            } );
        return result;
    }

    private Menuitem newAddModelItem( final ProjectImpl project ) {
        // TODO make this a command
        Menuitem result = newMenuitem(
            "Add a model...",
            new Runnable() {
                public void run() {
                    try {
                        String modelName = Prompter.prompt(
                            "Model name?",
                            "Enter the name of the new model",
                            "" );
                        if ( modelName != null ) {
                            ModelImpl model = new ModelImpl();
                            model.setName( modelName );
                            project.addModel( model );
                            Executions.sendRedirect( null );
                        }
                    } catch ( InterruptedException e1 ) {
                        logger.warn( "Dialog was interrupted", e1 );
                    }
                }
            } );
        return result;
    }

    private Menuitem newAddScenarioItem( final ModelImpl model ) {
        // TODO make this a command
        Menuitem result = newMenuitem(
            "Add a scenario...",
            new Runnable() {
                public void run() {
                    try {
                        String scenarioName = Prompter.prompt(
                            "Scenario name?",
                            "Enter the name of the new scenario",
                            "" );
                        if ( scenarioName != null ) {
                            Scenario scenario = new Scenario();
                            scenario.setName( scenarioName );
                            model.addScenario( scenario );
                            Executions.sendRedirect( null );
                        }
                    } catch ( InterruptedException e1 ) {
                        logger.warn( "Dialog was interrupted", e1 );
                    }
                }
            } );
        return result;
    }

    private Menuitem newRenameProjectItem( final ProjectImpl project ) {
        // TODO make this a command
        Menuitem result = newMenuitem(
            "Rename this project...",
            new Runnable() {
                public void run() {
                    try {
                        String projectName = Prompter.prompt(
                            "New project name?",
                            "Enter the new name of the project",
                            project.getName() );
                        if ( projectName != null )
                            try {
                                project.setName( projectName );
                                getSystem().addProject( project );
                                Executions.sendRedirect( null );
                            } catch ( PropertyVetoException e ) {
                                Messagebox.show(
                                    e.getMessage(), null,
                                    Messagebox.OK, Messagebox.ERROR );
                            }
                    } catch ( InterruptedException e1 ) {
                        logger.warn( "Dialog was interrupted", e1 );
                    }
                }
            } );
        return result;
    }

    private Menuitem newRenameModelItem( final ModelImpl model ) {
        // TODO make this a command
        Menuitem result = newMenuitem(
            "Rename this model...",
            new Runnable() {
                public void run() {
                    try {
                        String modelName = Prompter.prompt(
                            "New model name?",
                            "Enter the new name of the model",
                            model.getName() );
                        if ( modelName != null ) {
                            model.setName( modelName );
                            Executions.sendRedirect( null );
                        }
                    } catch ( InterruptedException e1 ) {
                        logger.warn( "Dialog was interrupted", e1 );
                    }
                }
            } );
        return result;
    }

    private Menuitem newRenameScenarioItem( final Scenario scenario ) {
        // TODO make this a command
        Menuitem result = newMenuitem(
            "Rename this scenario...",
            new Runnable() {
                public void run() {
                    try {
                        String scenarioName = Prompter.prompt(
                            "New scenario name?",
                            "Enter the new name of the scenario",
                            scenario.getName() );
                        if ( scenarioName != null ) {
                            scenario.setName( scenarioName );
                            Executions.sendRedirect( null );
                        }
                    } catch ( InterruptedException e1 ) {
                        logger.warn( "Dialog was interrupted", e1 );
                    }
                }
            } );
        return result;
    }

    /**
     * Create a new menu item that runs the given command when selected.
     * @param label the menu label
     * @param command the command to execute
     */
    private Menuitem newMenuitem( String label, final Runnable command ) {
        Menuitem item = new Menuitem( label );
        item.addEventListener( "onClick", new EventListener() {
            public boolean isAsap() {
                return true;
            }

            public void onEvent( Event event ) {
                if ( command != null )
                    command.run();
            }
        } );
        return item;
    }

    /**
     * Create an item in the tree.
     * @param selection the selection description
     * @param kids the subitems.
     */
    private Treeitem newTreeitem(
            AccordionSelection selection, Treechildren kids ) {

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
     * Return the value of system.
     */
    public final System getSystem() {
        return this.system;
    }

    /**
     * Return the value of user.
     */
    public final User getUser() {
        return this.user;
    }
}
