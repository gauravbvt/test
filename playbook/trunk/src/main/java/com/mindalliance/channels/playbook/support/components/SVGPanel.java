package com.mindalliance.channels.playbook.support.components;

import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.model.Model;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.protocol.http.WebRequestCycle;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import com.mindalliance.channels.playbook.pages.forms.panels.AbstractComponentPanel;
import com.mindalliance.channels.playbook.pages.forms.ElementPanel;
import com.mindalliance.channels.playbook.ref.impl.RefImpl;
import com.mindalliance.channels.playbook.ref.Ref;

import java.util.UUID;
import java.util.Map;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 29, 2008
 * Time: 9:49:28 PM
 */
public class SVGPanel extends AbstractComponentPanel {

    public static final String CALLBACK_VAR = "__CALLBACK__";

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

    String svgElementId;
    String svg;

    public SVGPanel(String id, ElementPanel parentPanel, String propPath, boolean readOnly, FeedbackPanel feedback) {
        super(id, parentPanel, propPath, readOnly, feedback);
    }

    protected void load() {
        super.load();
        svg = (String)getComponent();
        behave = new AbstractDefaultAjaxBehavior() {
            protected void respond(final AjaxRequestTarget target) {
                Map map = ((WebRequestCycle) RequestCycle.get()).getRequest().getParameterMap();
                String id = ((String[])map.get("refId"))[0];
                Ref ref = new RefImpl(id);
                SVGPanel.this.edit(ref, target);
            }
        };
        add(behave);
        svgContent = new Label("svg", new Model());
        svgContent.setEscapeModelStrings(false);
        add(svgContent);
        // String args = "'" + url + "','" + svgElementId + "'";
        svgElementId = UUID.randomUUID().toString();
        String args = "'" + svgElementId + "'";
        controlsDiv = new WebMarkupContainer("controls");
        zoomInButton = new Button("zoomIn");
        zoomInButton.add(new AttributeModifier("onkeypress", true, new Model("svg_zoomIn(" + args + ")")));
        zoomInButton.add(new AttributeModifier("onclick", true, new Model("svg_zoomIn(" + args + ")")));
        controlsDiv.add(zoomInButton);
        zoomOutButton = new Button("zoomOut");
        zoomOutButton.add(new AttributeModifier("onkeypress", true, new Model("svg_zoomOut(" + args + ")")));
        zoomOutButton.add(new AttributeModifier("onclick", true, new Model("svg_zoomOut(" + args + ")")));
        controlsDiv.add(zoomOutButton);
        leftButton = new Button("left");
        leftButton.add(new AttributeModifier("onkeypress", true, new Model("svg_left(" + args + ")")));
        leftButton.add(new AttributeModifier("onclick", true, new Model("svg_left(" + args + ")")));
        controlsDiv.add(leftButton);
        rightButton = new Button("right");
        rightButton.add(new AttributeModifier("onkeypress", true, new Model("svg_right(" + args + ")")));
        rightButton.add(new AttributeModifier("onclick", true, new Model("svg_right(" + args + ")")));
        controlsDiv.add(rightButton);
        upButton = new Button("up");
        upButton.add(new AttributeModifier("onkeypress", true, new Model("svg_up(" + args + ")")));
        upButton.add(new AttributeModifier("onclick", true, new Model("svg_up(" + args + ")")));
        controlsDiv.add(upButton);
        downButton = new Button("down");
        downButton.add(new AttributeModifier("onkeypress", true, new Model("svg_down(" + args + ")")));
        downButton.add(new AttributeModifier("onclick", true, new Model("svg_down(" + args + ")")));
        controlsDiv.add(downButton);
        resetButton = new Button("reset");
        resetButton.add(new AttributeModifier("onclick", true, new Model("svg_reset(" + args + ")")));
        controlsDiv.add(resetButton);
        add(controlsDiv);
        this.add(HeaderContributor.forJavaScript(SVGPanel.class, "SVGPanel.js"));
    }

    protected void onBeforeRender() { // panel must be connected to page to get callback url
        String processed = processSVG(svgElementId, behave.getCallbackUrl());
        svgContent.setModelObject(processed);
        super.onBeforeRender();
    }

    private String processSVG(String svgElementId, CharSequence url) {
        String processed;
        // insert svgElementId in svg
        processed = svg.replace("<svg", "<svg id='" + svgElementId + "'");
        // replace all callback URL
        return processed.replaceAll(CALLBACK_VAR, url.toString());
    }


}
