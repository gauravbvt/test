/**
 * 
 */
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
 * @author dfeeney
 * 
 */
public class TreeGraphPane extends Tabbox {

    private System system;

    private Scenario scenario;
    
    private User user;
    
    private MxGraph graph;

    private Tree tree;

    private ScenarioElement root;

    public TreeGraphPane(System system, Scenario scenario, User user) {
        this.system = system;
        this.scenario = scenario;
        this.user = user;
        Tabs tabs = new Tabs();
        Tabpanels tabPanels = new Tabpanels();
        Tab treeTab = new Tab("Tree");

        tabs.appendChild(new Tab("Net"));
        tabs.appendChild(treeTab);
        generateGraph();
        Tabpanel panel = new Tabpanel();
        panel.appendChild(graph);
        tabPanels.appendChild(panel);
        tree = new Tree();
        tree.setWidth("200px");
        // tree.setHeight("200px");
        panel = new Tabpanel();
        panel.appendChild(tree);
        tabPanels.appendChild(panel);
        this.appendChild(tabs);
        this.appendChild(tabPanels);
        this.setSelectedTab(treeTab);
    }

    private void generateGraph() {
        graph = new MxGraph();
        graph.setLayout(new MxFastOrganicLayout());
        graph.setWidth("200px");
        // graph.setHeight("200px");
        graph.setProperty(MxGraph.AUTO_SIZE, "true", true);
        graph.setStyle("overflow:hidden; " +
                "background:url('/channels/images/grid.gif');");

        graph.getPanningHandler().setProperty(
                MxPanningHandler.IS_SELECT_ON_POPUP, false, false);
        graph.getPanningHandler().setProperty(
                MxPanningHandler.IS_USE_SHIFT_KEY, true, false);
        graph.getPanningHandler().setProperty(MxPanningHandler.IS_PAN_ENABLED,
                true, false);
    }

    public void setRoot(ScenarioElement root) {
        this.root = root;
    }

    public void setWidth(String width) {
        super.setWidth(width);
        graph.setWidth(width);
        tree.setWidth(width);
    }

    public void setHeight(String height) {
        super.setHeight(height);
        graph.setHeight(height);
        tree.setHeight(height);
    }

}
