package com.mindalliance.channels.playbook.pages.graphs;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.protocol.http.WebRequestCycle;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.log4j.Logger;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ref.impl.RefImpl;
import com.mindalliance.channels.playbook.support.models.Container;
import com.mindalliance.channels.playbook.graph.DirectedGraph;
import com.mindalliance.channels.playbook.graph.svg.SVGTransformation;
import com.mindalliance.channels.playbook.graph.svg.SVGTransform;

import java.util.Map;
import java.util.UUID;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 5, 2008
 * Time: 12:46:28 PM
 */
abstract public class GraphPanel extends Panel {

    protected Label svgContent;
    protected WebMarkupContainer controlsDiv;
    protected Button zoomInButton;
    protected Button zoomOutButton;
    protected Button upButton;
    protected Button downButton;
    protected Button leftButton;
    protected Button rightButton;
    protected Button resetButton;
    AbstractDefaultAjaxBehavior behave;
    SVGTransformation transformation = new SVGTransformation(); // post-creation, client-caused transformations
    protected Container priorContainer;
    protected Ref priorSelection;
    protected DirectedGraph directedGraph;
    protected String svg;
    String svgElementId;
    protected Ref currentSelection; // TODO remove when extends ContentView

    public GraphPanel(String id, IModel model) {
        super(id, model);
        load();
    }

    protected void load() {
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
        add(svgContent);
        // String args = "'" + url + "','" + svgElementId + "'";
        svgElementId = UUID.randomUUID().toString();
        controlsDiv = new WebMarkupContainer("controls");
        zoomInButton = new Button("zoomIn");
        controlsDiv.add(zoomInButton);
        zoomOutButton = new Button("zoomOut");
        controlsDiv.add(zoomOutButton);
        leftButton = new Button("left");
        controlsDiv.add(leftButton);
        rightButton = new Button("right");
        controlsDiv.add(rightButton);
        upButton = new Button("up");
        controlsDiv.add(upButton);
        downButton = new Button("down");
        controlsDiv.add(downButton);
        resetButton = new Button("reset");
        controlsDiv.add(resetButton);
        add(controlsDiv);
        this.add(HeaderContributor.forJavaScript(GraphPanel.class, "GraphPanel.js"));
    }

    private void processCallback(Map map, AjaxRequestTarget target) {
        if (map.containsKey("selected")) {
            String id = ((String[]) map.get("selected"))[0];
            Ref ref = new RefImpl(id);
            doAjaxSelection(ref, target);
        }
        if (map.containsKey("transform")) {
            String transformString = ((String[]) map.get("transform"))[0];
            transformation.appendTransform(SVGTransform.fromString(transformString));
        }
    }

    abstract protected DirectedGraph makeDirectedGraph(Container container);

    protected void onBeforeRender() { // panel must be connected to page to get callback url
        setupButtons();
        if (svgContent.getModelObject() == null) {
            addGraphSvg();  // only if not already done
        }
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

    protected void addGraphSvg() {
        Container container = (Container) getModelObject();

        if (directedGraph == null || !container.equals(priorContainer)) { // make new directed graph only if needed
            directedGraph = makeDirectedGraph(container);
            priorContainer = container;
            svg = null; // force regeneration
        }
        if (priorSelection != getSelected()) {
            svg = null; // force regeneration
        }
        if (svg == null) { // regenerate svg only if needed
            svg = directedGraph.makeSvg(svgElementId, behave.getCallbackUrl().toString(), getSelected(), transformation);
            // Logger.getLogger(this.getClass()).info(svg);
        }
        svgContent.setModelObject(svg);
        priorSelection = getSelected();
    }

    protected void setSelected(Ref ref) {  // overrides ContentView
        currentSelection = ref;
        // update svgContent if needed
        addGraphSvg();
    }

    protected Ref getSelected() {  // TODO - remove when extends ContentView
        return currentSelection;
    }

    protected void doAjaxSelection(Ref ref, AjaxRequestTarget target) {   // TODO - remove when extends ContentView
        System.out.println("Selected " + ref);
        currentSelection = ref;
        setSelected(ref);
        target.addComponent(svgContent);
    }

}
