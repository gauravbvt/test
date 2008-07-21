package com.mindalliance.channels.playbook.pages.graphs;

import com.mindalliance.channels.playbook.graph.DirectedGraph;
import com.mindalliance.channels.playbook.graph.svg.SVGTransform;
import com.mindalliance.channels.playbook.graph.svg.SVGTransformation;
import com.mindalliance.channels.playbook.pages.ContentView;
import com.mindalliance.channels.playbook.pages.SelectionManager;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.models.Container;
import com.mindalliance.channels.playbook.support.components.NoFormButton;
import com.mindalliance.channels.playbook.support.RefUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.WebRequestCycle;

import java.util.Map;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 5, 2008
 * Time: 12:46:28 PM
 */
abstract public class GraphPanel extends ContentView {

    protected Label svgContent;
    protected WebMarkupContainer controlsDiv;
    protected NoFormButton zoomInButton;
    protected NoFormButton zoomOutButton;
    protected NoFormButton upButton;
    protected NoFormButton downButton;
    protected NoFormButton leftButton;
    protected NoFormButton rightButton;
    protected NoFormButton resetButton;
    AbstractDefaultAjaxBehavior behave;
    SVGTransformation transformation = new SVGTransformation(); // post-creation, client-caused transformations
    protected Container priorContainer;
    protected DirectedGraph directedGraph;
    protected String svg;
    String svgElementId;
    Ref priorSelection;

    public GraphPanel(String id, IModel container, SelectionManager masterSelection) {
        super(id, container, masterSelection);
        initialize();
    }

    protected void initialize() {
        behave = new AbstractDefaultAjaxBehavior() {
            protected void respond(final AjaxRequestTarget target) {
                Map map = ((WebRequestCycle) RequestCycle.get()).getRequest().getParameterMap();
                processCallback(map, target);
            }
        };
        add(behave);
        svgContent = new Label("graph", new Model());
        svgContent.setEscapeModelStrings(false);
        svgContent.setOutputMarkupId(true);
        addOrReplace(svgContent);
        // String args = "'" + url + "','" + svgElementId + "'";
        svgElementId = RefUtils.makeUUID();
        controlsDiv = new WebMarkupContainer("controls");
        zoomInButton = new NoFormButton("zoomIn");
        controlsDiv.add(zoomInButton);
        zoomOutButton = new NoFormButton("zoomOut");
        controlsDiv.add(zoomOutButton);
        leftButton = new NoFormButton("left");
        controlsDiv.add(leftButton);
        rightButton = new NoFormButton("right");
        controlsDiv.add(rightButton);
        upButton = new NoFormButton("up");
        controlsDiv.add(upButton);
        downButton = new NoFormButton("down");
        controlsDiv.add(downButton);
        resetButton = new NoFormButton("reset");
        controlsDiv.add(resetButton);
        addOrReplace(controlsDiv);
        this.add(HeaderContributor.forJavaScript(GraphPanel.class, "GraphPanel.js"));
    }

    private void processCallback(Map map, AjaxRequestTarget target) {
        if (map.containsKey("selected")) {
            try {
                String identifier = ((String[]) map.get("selected"))[0];
                String[] pair = identifier.split(":");
                Ref ref = (Ref)Class.forName(pair[0]).newInstance();
                ref.setId(pair[1]);
                doAjaxSelection(ref, target);
            } catch (Exception e) {
                Logger.getLogger(this.getClass()).error("Failed to process selection callback: " + e );
                e.printStackTrace();
            }
        }
        if (map.containsKey("transform")) {
            String transformString = ((String[]) map.get("transform"))[0];
            transformation.appendTransform(SVGTransform.fromString(transformString));
        }
    }

    abstract protected DirectedGraph makeDirectedGraph(Container container);

    protected void onBeforeRender() { // panel must be connected to page to get callback url
        setupButtons();
        svgContent.setModelObject(makeGraphSvg()); // no caching
        /*if (svgContent.getModelObject() == null) {
            addGraphSvg();  // only if not already done
        }*/
        super.onBeforeRender();
    }

    protected void setupButtons() {
        String args = "'" + svgElementId + "','" + behave.getCallbackUrl() + "'";
        zoomInButton.add(new AttributeModifier("onkeypress", true, new Model("svg_zoomIn(" + args + ")")));
        zoomInButton.add(new AttributeModifier("onclick", true, new Model("svg_zoomIn(" + args + ")")));
        zoomOutButton.add(new AttributeModifier("onkeypress", true, new Model("svg_zoomOut(" + args + ")")));
        zoomOutButton.add(new AttributeModifier("onclick", true, new Model("svg_zoomOut(" + args + ")")));
        leftButton.add(new AttributeModifier("onkeypress", true, new Model("svg_left(" + args + ")")));
        leftButton.add(new AttributeModifier("onclick", true, new Model("svg_left(" + args + ")")));
        rightButton.add(new AttributeModifier("onkeypress", true, new Model("svg_right(" + args + ")")));
        rightButton.add(new AttributeModifier("onclick", true, new Model("svg_right(" + args + ")")));
        upButton.add(new AttributeModifier("onkeypress", true, new Model("svg_up(" + args + ")")));
        upButton.add(new AttributeModifier("onclick", true, new Model("svg_up(" + args + ")")));
        downButton.add(new AttributeModifier("onkeypress", true, new Model("svg_down(" + args + ")")));
        downButton.add(new AttributeModifier("onclick", true, new Model("svg_down(" + args + ")")));
        resetButton.add(new AttributeModifier("onclick", true, new Model("svg_reset(" + args + ")")));
    }

    protected String makeGraphSvg() {
        Container container = getContainer();
        directedGraph = makeDirectedGraph(container);
        String callback = behave.getCallbackUrl().toString();
        svg = directedGraph.makeSvg(svgElementId, callback, getSelected(), transformation);
        return svg;
    }

   /* protected void addGraphSvg() {
        Container container = getContainer();

        if (directedGraph == null || !container.equals(priorContainer)) { // make new directed graph only if needed
            directedGraph = makeDirectedGraph(container);
            priorContainer = container;
            svg = null; // force regeneration
        }
        if (getSelected() != priorSelection) svg = null;
        if (svg == null)
            try { // regenerate svg only if needed
                String callback = behave.getCallbackUrl().toString();
                svg = directedGraph.makeSvg(svgElementId, callback, getSelected(), transformation);
            }
            catch (IllegalStateException e) {
                Logger.getLogger(this.getClass()).warn("Graph update without a page");
            }
        svgContent.setModelObject(svg);
        priorSelection = getSelected();
    }*/

    public void setSelected(Ref ref) {  // overrides ContentView
        super.setSelected(ref);
        // force update of svgContent
        // svgContent.setModelObject(null);
    }

}
