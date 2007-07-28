// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.ui;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zkoss.zk.ui.Component;
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
import com.mindalliance.channels.data.frames.PortfolioService;
import com.mindalliance.channels.data.frames.Project;
import com.mindalliance.channels.data.models.Scenario;
import com.mindalliance.channels.data.models.Storyline;
import com.mindalliance.channels.data.system.RegistryService;
import com.mindalliance.channels.data.system.SystemService;
import com.mindalliance.channels.ui.editor.EditorFactory;

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

    private EditorFactory editorFactory;
    private Box canvas;
    private int maxHeight;
    private Tree tree;

    private Set<Object> expandedNodes = new HashSet<Object>();

    /**
     * Convenience constructor.
     *
     * @param maxHeight the maximum height of the canvas
     * @param height the height of the tab's content
     * @param canvas the canvas
     * @param factory the editor creator
     */
    public ScenariosTab(
            int maxHeight, int height, Box canvas, EditorFactory factory ) {

        super();
        this.editorFactory = factory;
        this.maxHeight = maxHeight;
        this.canvas = canvas;

        setPage( canvas.getPage() );

        rebuildTree();
        setHeight( height + "px" );
    }

    /**
     * Rebuild the tree and popup menu.
     */
    private void rebuildTree() {
        getChildren().clear();

        Treechildren treeChildren = new Treechildren();
        PortfolioService portfolio =
            getEditorFactory().getSystem().getPortfolioService();
        User user = getEditorFactory().getUser();
        for ( Project p : portfolio.getProjects( user ) )
            treeChildren.appendChild(
                createProjectNode( p ) );

        tree = new Tree();
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
        tree.addEventListener( "onOpen", new EventListener() {
            public boolean isAsap() {
                return true;
            }

            public void onEvent( Event event ) {
                SelectEvent e = (SelectEvent) event;
                Treeitem i = (Treeitem) e.getSelectedItems().iterator().next();
                if ( i.isOpen() )
                    expandedNodes.add( i.getValue() );
                else
                    expandedNodes.remove( i.getValue() );
            }
        } );

        Menupopup menu = createPopup();
        menu.setParent( this );
        appendChild( tree );
        appendChild( menu );
        setContext( menu.getId() );
    }

    /**
     * Refresh the tree.
     * @todo keep current selection
     */
    private void refresh() {
        // Executions.sendRedirect( null );
        expandedNodes = new HashSet<Object>();
        rebuildTree();
        tree.invalidate();
    }

    /**
     * Create a project node in the tree.
     * @param project the project
     *
     * @return a tree node, collapsed
     */
    private Treeitem createProjectNode( Project project ) {

        Treechildren models = new Treechildren();
        for ( Scenario m : project.getScenarios() )
            models.appendChild(
                    createScenarioNode( project, m ) );

        Treeitem result = newTreeitem(
                    new AccordionSelection(
                        PROJECT_ICON,
                        project.getName(),
                        "Project properties",
                        "ROLE_USER",
                        getCanvas(),
                        getEditorFactory().createEditor( project ) ),
                    models );
        return result;
    }

    /**
     * Create a model node in the tree.
     * @param project the projet
     * @param scenario the model
     * @return a tree node, collapsed
     */
    private Treeitem createScenarioNode( Project project, Scenario scenario ) {

        Treechildren scenarios = new Treechildren();
        for ( Storyline s : scenario.getStorylines() )
            scenarios.appendChild(
                    createStorylineNode( project, scenario, s ) );

        return newTreeitem(
            new AccordionSelection(
                MODEL_ICON,
                scenario.getName(),
                "Scenario properties",
                "ROLE_USER",
                getCanvas(),
                getEditorFactory().createEditor( scenario ) ),
            scenarios );
    }

    /**
     * Create a scenario node.
     * @param project the project
     * @param scenario the scenario
     * @param storyline the storyline
     */
    private Treeitem createStorylineNode(
            Project project,
            Scenario scenario, Storyline storyline ) {

        Treechildren reports = new Treechildren();
        reports.appendChild( newTreeitem(
            new AccordionSelection(
                    REPORT_ICON,
                    "Playbook",
                    "View playbook",
                    "ROLE_USER",
                    getCanvas(),
                    getEditorFactory().createEditor( null ) ),
            null ) );

        reports.appendChild( newTreeitem(
            new AccordionSelection(
                    REPORT_ICON,
                    "Issues Analysis",
                    "View issues",
                    "ROLE_USER",
                    getCanvas(),
                    getEditorFactory().createEditor( null ) ),
            null ) );

        reports.appendChild( newTreeitem(
            new AccordionSelection(
                    DASHBOARD_ICON,
                    "Dashboard",
                    "View dashboard",
                    "ROLE_USER",
                    getCanvas(),
                    getEditorFactory().createEditor( null ) ),
            null ) );

        reports.appendChild( newTreeitem(
            new AccordionSelection(
                    CHART_ICON,
                    "Info Flow",
                    "View information flows",
                    "ROLE_USER",
                    getCanvas(),
                    getEditorFactory().createEditor( null ) ),
            null ) );

        return newTreeitem(
            new AccordionSelection(
                SCENARIO_ICON,
                storyline.getName(),
                "Storyline viewer",
                "ROLE_USER",
                getCanvas(),
                new ScenarioViewer(
                    getMaxHeight(),
                    project, scenario, storyline, getEditorFactory()
                )
            ),
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
                        (AccordionSelection) getSession().getAttribute(
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
    private void resetMenu(
            Menupopup menu, AccordionSelection selection ) {

        SystemService system = getEditorFactory().getSystem();
        final RegistryService registry = system.getRegistryService();
        final PortfolioService portfolio = system.getPortfolioService();

        menu.getChildren().clear();
        User user = getEditorFactory().getUser();
        if ( selection == null ) {
            if ( registry.isAdministrator( user ) )
                menu.appendChild( newAddProjectItem() );

        } else if ( selection.getIcon().equals( PROJECT_ICON ) ) {
            final Project project =
                (Project) selection.getEditor().getObject();

            if ( registry.isAdministrator( user ) ) {
                menu.appendChild( newAddProjectItem() );
                menu.appendChild( new Menuseparator() );
                menu.appendChild( newDeleteProjectItem( project ) );
            }
            if ( portfolio.isManager( user, project )
                    || registry.isAdministrator( user ) ) {
                menu.appendChild( newRenameProjectItem( project ) );
                menu.appendChild( newAddModelItem( project ) );
            }

        } else if ( selection.getIcon().equals( MODEL_ICON ) ) {
            Scenario scenario = (Scenario) selection.getEditor().getObject();

//            Project project = scenario.getProject();
//            if ( portfolio.isManager( user, project )
//                    || registry.isAdministrator( user ) ) {
//                menu.appendChild( newAddModelItem( project ) );
//                menu.appendChild( new Menuseparator() );
            menu.appendChild( newDeleteModelItem( scenario ) );
            menu.appendChild( newRenameModelItem( scenario ) );
            menu.appendChild( newAddScenarioItem( scenario ) );
//            }

        } else if ( selection.getIcon().equals( SCENARIO_ICON ) ) {
            ScenarioViewer viewer = (ScenarioViewer) selection.getEditor();
            Project project = viewer.getProject();
            Storyline storyline = viewer.getStoryline();
            Scenario scenario = viewer.getScenario();
            if ( portfolio.isManager( user, project )
                    || registry.isAdministrator( user ) ) {
                menu.appendChild( newAddScenarioItem( scenario ) );
                menu.appendChild( new Menuseparator() );
                menu.appendChild( newEditScenarioItem( storyline ) );
                menu.appendChild(
                    newDeleteStorylineItem( scenario, storyline ) );
                menu.appendChild( newRenameScenarioItem( storyline ) );
            }
        }
    }

    /**
     * Open an editor on the selected scenario.
     * @param storyline the scenario
     */
    private Component newEditScenarioItem( final Storyline storyline ) {
        // TODO make this a command
        Menuitem result = newMenuitem(
            "Edit scenario...",
            new Runnable() {
                public void run() {
                    if ( getEditorFactory().popupEditor( storyline ) != null )
                        refresh();
                }
            } );
        return result;
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
                            getEditorFactory().getSystem()
                                .getPortfolioService()
                                    .removeProject( project );
                            getSession().setAttribute(
                                DesktopRichlet.CURRENT_SELECTION, null );
                            refresh();
                        }
                    } catch ( InterruptedException e1 ) {
                        logger.warn( "Dialog was interrupted", e1 );
                    }
                }
            } );
        return deleteProjectItem;
    }

    private Menuitem newDeleteModelItem( final Scenario scenario ) {
        // TODO make this a command
        Menuitem result = newMenuitem(
            "Delete this model...",
            new Runnable() {
                public void run() {
                    try {
                        int button = Messagebox.show(
                            MessageFormat.format(
                                "Delete model {0}?",
                                scenario.getName() ),
                            null,
                            Messagebox.OK | Messagebox.CANCEL,
                            Messagebox.QUESTION );
                        if ( button == Messagebox.OK ) {
                            // FIXME
                            //scenario.getProject().removeScenario( scenario );
                            getSession().setAttribute(
                                    DesktopRichlet.CURRENT_SELECTION, null );
                            refresh();
                        }
                    } catch ( InterruptedException e1 ) {
                        logger.warn( "Dialog was interrupted", e1 );
                    }
                }
            } );
        return result;
    }

    private Menuitem newDeleteStorylineItem(
            final Scenario scenario,
            final Storyline storyline ) {

        // TODO make this a command
        Menuitem result = newMenuitem(
            "Delete this scenario...",
            new Runnable() {
                public void run() {
                    try {
                        int button = Messagebox.show(
                            MessageFormat.format(
                                "Delete scenario {0}?",
                                storyline.getName() ),
                            null,
                            Messagebox.OK | Messagebox.CANCEL,
                            Messagebox.QUESTION );
                        if ( button == Messagebox.OK ) {
                            scenario.removeStoryline( storyline );
                            getSession().setAttribute(
                                    DesktopRichlet.CURRENT_SELECTION, null );
                            refresh();
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
                            getEditorFactory().getSystem()
                                .getPortfolioService().addProject( project );
                            refresh();
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
                            "Scenario name?",
                            "Enter the name of the new model",
                            "" );
                        if ( modelName != null ) {
                            Scenario scenario = new Scenario();
                            scenario.setName( modelName );
                            project.addScenario( scenario );
                            refresh();
                        }
                    } catch ( InterruptedException e1 ) {
                        logger.warn( "Dialog was interrupted", e1 );
                    }
                }
            } );
        return result;
    }

    private Menuitem newAddScenarioItem( final Scenario scenario ) {
        // TODO make this a command
        Menuitem result = newMenuitem(
            "Add a scenario...",
            new Runnable() {
                public void run() {
                    try {
                        String scenarioName = Prompter.prompt(
                            "Storyline name?",
                            "Enter the name of the new scenario",
                            "" );
                        if ( scenarioName != null ) {
                            Storyline storyline = new Storyline();
                            storyline.setName( scenarioName );
                            scenario.addStoryline( storyline );
                            refresh();
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
                            refresh();
                        }
                    } catch ( InterruptedException e1 ) {
                        logger.warn( "Dialog was interrupted", e1 );
                    }
                }
            } );
        return result;
    }

    private Menuitem newRenameModelItem( final Scenario scenario ) {
        // TODO make this a command
        Menuitem result = newMenuitem(
            "Rename this model...",
            new Runnable() {
                public void run() {
                    try {
                        String modelName = Prompter.prompt(
                            "New model name?",
                            "Enter the new name of the model",
                            scenario.getName() );
                        if ( modelName != null ) {
                            scenario.setName( modelName );
                            refresh();
                        }
                    } catch ( InterruptedException e1 ) {
                        logger.warn( "Dialog was interrupted", e1 );
                    }
                }
            } );
        return result;
    }

    private Menuitem newRenameScenarioItem( final Storyline storyline ) {
        // TODO make this a command
        Menuitem result = newMenuitem(
            "Rename this scenario...",
            new Runnable() {
                public void run() {
                    try {
                        String scenarioName = Prompter.prompt(
                            "New scenario name?",
                            "Enter the new name of the scenario",
                            storyline.getName() );
                        if ( scenarioName != null ) {
                            storyline.setName( scenarioName );
                            refresh();
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
     * Return the value of editorFactory.
     */
    public final EditorFactory getEditorFactory() {
        return this.editorFactory;
    }

    /**
     * Return the canvas.
     */
    public final Box getCanvas() {
        return this.canvas;
    }

    /**
     * Return the maxHeight.
     */
    public final int getMaxHeight() {
        return this.maxHeight;
    }

    /**
     * Return the session.
     */
    public final Session getSession() {
        return getDesktop().getSession();
    }
}
