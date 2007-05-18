// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.ui;

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

import com.mindalliance.channels.User;
import com.mindalliance.channels.data.elements.project.Model;
import com.mindalliance.channels.data.elements.project.Project;
import com.mindalliance.channels.data.elements.project.Scenario;
import com.mindalliance.channels.services.PortfolioService;
import com.mindalliance.channels.services.RegistryService;
import com.mindalliance.channels.services.SystemService;

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

    private SystemService system ;
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
            int maxHeight, int height, Box canvas,
            User user, SystemService system ) {

        super();
        this.user = user;
        this.system = system;
        setPage( canvas.getPage() );
        this.session = getDesktop().getSession();

        Treechildren treeChildren = new Treechildren();
        for ( Project p : system.getPortfolioService().getProjects( user ) )
            treeChildren.appendChild(
                createProjectNode( maxHeight, canvas, p ) );

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
     * @return a tree node, collapsed
     */
    private Treeitem createProjectNode(
            int maxHeight, Box canvas, Project project ) {

        Treechildren models = new Treechildren();
        for ( Model m : project.getModels() )
            models.appendChild(
                    createModelNode( maxHeight, canvas, m ) );

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
     * @return a tree node, collapsed
     */
    private Treeitem createModelNode(
            int maxHeight, Box canvas, Model model ) {

        Treechildren scenarios = new Treechildren();
        for ( Scenario s : model.getScenarios() )
            scenarios.appendChild(
                    createScenarioNode( maxHeight, canvas, s ) );

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
     */
    private Treeitem createScenarioNode(
            int maxHeight, Box canvas, Scenario scenario ) {

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
                        maxHeight, canvas.getPage(), scenario,
                        getSystem(), getUser() ),
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

                    resetMenu( menu, selection );
                }
            }
        } );

        return result;
    }

    /**
     * Adjust the popup menu for the given selection and user.
     * @param menu the popup menu
     * @param selection the current selection
     */
    protected void resetMenu(
            Menupopup menu, AccordionSelection selection ) {

        final RegistryService registry = getSystem().getRegistryService();
        final PortfolioService portfolio = getSystem().getPortfolioService();

        menu.getChildren().clear();
        if ( selection == null ) {
            if ( registry.isAdministrator( getUser() ) )
                menu.appendChild( newAddProjectItem() );

        } else if ( selection.getIcon().equals( PROJECT_ICON ) ) {
            final Project project = (Project) selection.getObject();

            if ( registry.isAdministrator( getUser() ) ) {
                menu.appendChild( newAddProjectItem() );
                menu.appendChild( new Menuseparator() );
                menu.appendChild( newDeleteProjectItem( project ) );
            }
            if ( portfolio.isManager( getUser(), project )
                    || registry.isAdministrator( getUser() ) ) {
                menu.appendChild( newRenameProjectItem( project ) );
                menu.appendChild( newAddModelItem( project ) );
            }

        } else if ( selection.getIcon().equals( MODEL_ICON ) ) {
            Model model = (Model) selection.getObject();
            Project project = model.getProject();

            if ( portfolio.isManager( getUser(), project )
                    || registry.isAdministrator( getUser() ) ) {
                menu.appendChild( newAddModelItem( project ) );
                menu.appendChild( new Menuseparator() );
                menu.appendChild( newDeleteModelItem( model ) );
                menu.appendChild( newRenameModelItem( model ) );
                menu.appendChild( newAddScenarioItem( model ) );
            }

        } else if ( selection.getIcon().equals( SCENARIO_ICON ) ) {
            Scenario scenario = (Scenario) selection.getObject();
            Model model = scenario.getModel();
            if ( portfolio.isManager( getUser(), model.getProject() )
                    || registry.isAdministrator( getUser() ) ) {
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
                            getSystem().getPortfolioService()
                                .removeProject( project );
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

    private Menuitem newDeleteModelItem( final Model model ) {
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
                            scenario.getModel().removeScenario( scenario );
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
                        if ( projectName != null ) {
                            Project project = new Project();
                            project.setName( projectName );
                            getSystem().getPortfolioService()
                                .addProject( project );
                            Executions.sendRedirect( null );
                        }
                    } catch ( InterruptedException e1 ) {
                        logger.warn( "Dialog was interrupted", e1 );
                    }
                }
            } );
        return result;
    }

    private Menuitem newAddModelItem( final Project project ) {
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
                            Model model = new Model();
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

    private Menuitem newAddScenarioItem( final Model model ) {
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

    private Menuitem newRenameProjectItem( final Project project ) {
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
                        if ( projectName != null ) {
                            project.setName( projectName );
                            Executions.sendRedirect( null );
                        }
                    } catch ( InterruptedException e1 ) {
                        logger.warn( "Dialog was interrupted", e1 );
                    }
                }
            } );
        return result;
    }

    private Menuitem newRenameModelItem( final Model model ) {
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
    public final SystemService getSystem() {
        return this.system;
    }

    /**
     * Return the value of user.
     */
    public final User getUser() {
        return this.user;
    }
}
