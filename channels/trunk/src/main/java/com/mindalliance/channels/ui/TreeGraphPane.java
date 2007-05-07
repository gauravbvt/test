// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.ui;

import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Tree;

import com.mindalliance.channels.System;
import com.mindalliance.channels.User;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.model.ScenarioElement;
import com.mindalliance.zk.mxgraph.MxFastOrganicLayout;
import com.mindalliance.zk.mxgraph.MxGraph;
import com.mindalliance.zk.mxgraph.MxPanningHandler;

/**
 * A scenario element viewer.
 *
 * @author dfeeney
 * @version $Revision$
 */
public class TreeGraphPane extends Tabbox {

    /**
     * Height of the title, in pixels.
     */
    private static final int TITLE_HEIGHT = 35;
    private static final int BORDERS = 10;

    private System system;
    private Scenario scenario;
    private User user;
    private MxGraph graph;
    private Tree tree;
    private ScenarioElement rootElement;

    /**
     * Default constructor.
     *
     * @param height the available height
     * @param system the system
     * @param scenario the current scenario
     * @param user the current user
     */
    public TreeGraphPane(
            int height, System system, Scenario scenario, User user ) {

        this.system = system;
        this.scenario = scenario;
        this.user = user;

        int contentHeight = height - TITLE_HEIGHT - BORDERS;

        Tab treeTab = new Tab( "Tree" );

        Tabs tabs = new Tabs();
        tabs.appendChild( new Tab( "Net" ) );
        tabs.appendChild( treeTab );
        tabs.setWidth( "30px" );
        tabs.setHeight( "16px" );

        this.graph = generateGraph();
        Tabpanel graphPanel = new Tabpanel();
        graphPanel.appendChild( graph );
        graphPanel.setHeight( contentHeight + "px" );

        this.tree = new Tree();
        Tabpanel treePanel = new Tabpanel();
        treePanel.setHeight( contentHeight + "px" );
        treePanel.appendChild( tree );

        Tabpanels tabPanels = new Tabpanels();
        tabPanels.appendChild( graphPanel );
        tabPanels.appendChild( treePanel );

        this.appendChild( tabs );
        this.appendChild( tabPanels );
        this.setOrient( "vertical" );
        this.setSelectedTab( treeTab );
        this.setWidth( "100%" );
    }

    private MxGraph generateGraph() {
        MxGraph graph = new MxGraph();
        graph.setLayout( new MxFastOrganicLayout() );
        graph.setWidth( "100%" );
        graph.setProperty( MxGraph.AUTO_SIZE, "true", true );
        graph.setStyle( "overflow:hidden; "
                + "background:url('images/grid.gif');" );

        graph.getPanningHandler().setProperty(
                MxPanningHandler.IS_SELECT_ON_POPUP, false, false );
        graph.getPanningHandler().setProperty(
                MxPanningHandler.IS_USE_SHIFT_KEY, true, false );
        graph.getPanningHandler().setProperty(
                MxPanningHandler.IS_PAN_ENABLED, true, false );

        return graph;
    }

    /**
     * Return the value of rootElement.
     */
    public final ScenarioElement getRootElement() {
        return this.rootElement;
    }

    /**
     * Set the root element to display in this pane.
     * @param root a scenario element
     */
    public void setRootElement( ScenarioElement root ) {
        this.rootElement = root;
    }

    /**
     * Return the value of scenario.
     */
    public final Scenario getScenario() {
        return this.scenario;
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
