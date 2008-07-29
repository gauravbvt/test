package com.mindalliance.channels.playbook.pages.forms.tabs.networking;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.graph.support.Networking;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.support.models.RefModel;
import com.mindalliance.channels.playbook.ref.Ref;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.util.ModelIteratorAdapter;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.IModel;

import java.util.List;
import java.util.Iterator;
import java.io.Serializable;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 29, 2008
 * Time: 10:58:30 AM
 */
public class NetworkingBasicTab extends AbstractFormTab {

    protected Networking networking;
    protected AjaxLink fromResourceLink;
    protected AjaxLink toResourceLink;
    protected Label fromResourceLabel;
    protected Label toResourceLabel;
    protected WebMarkupContainer accessAndJobDiv;
    protected WebMarkupContainer hasJobDiv;
    protected Label fromResourceJobLabel;
    protected Label toResourceJobLabel;
    protected WebMarkupContainer hasAccessDiv;
    protected Label fromResourceAccessLabel;
    protected Label toResourceAccessLabel;
    protected WebMarkupContainer agreementsDiv;
    protected RefreshingView agreementsView;
    protected WebMarkupContainer relationshipsDiv;
    protected RefreshingView relationshipsView;
    protected WebMarkupContainer flowsDiv;
    protected RefreshingView flowsView;

    public NetworkingBasicTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        networking = (Networking) getElement().deref();
        fromResourceLink = new AjaxLink("fromResourceLink") {
            public void onClick(AjaxRequestTarget target) {
                edit(networking.getFromResource(), target);
            }
        };
        add(fromResourceLink);
        fromResourceLabel = new Label("fromResource", new Model((String) RefUtils.get(networking.getFromResource(), "name")));
        fromResourceLink.add(fromResourceLabel);
        toResourceLink = new AjaxLink("toResourceLink") {
            public void onClick(AjaxRequestTarget target) {
                edit(networking.getToResource(), target);
            }
        };
        add(toResourceLink);
        toResourceLabel = new Label("toResource", new Model((String) RefUtils.get(networking.getToResource(), "name")));
        toResourceLink.add(toResourceLabel);

        accessAndJobDiv = new WebMarkupContainer("accessAndJobDiv");
        setVisibility(accessAndJobDiv, networking.hasAccess() || networking.hasJobWith());
        add(accessAndJobDiv);

        hasJobDiv = new WebMarkupContainer("hasJobDiv");
        setVisibility(hasJobDiv, networking.hasJobWith());
        accessAndJobDiv.add(hasJobDiv);
        fromResourceJobLabel = new Label("fromResourceJob", new Model((String) RefUtils.get(networking.getFromResource(), "name")));
        hasJobDiv.add(fromResourceJobLabel);
        toResourceJobLabel = new Label("toResourceJob", new Model((String) RefUtils.get(networking.getToResource(), "name")));
        hasJobDiv.add(toResourceJobLabel);

        hasAccessDiv = new WebMarkupContainer("hasAccessDiv");
        setVisibility(hasAccessDiv, networking.hasAccess());
        accessAndJobDiv.add(hasAccessDiv);
        fromResourceAccessLabel = new Label("fromResourceAccess", new Model((String) RefUtils.get(networking.getFromResource(), "name")));
        hasAccessDiv.add(fromResourceAccessLabel);
        toResourceAccessLabel = new Label("toResourceAccess", new Model((String) RefUtils.get(networking.getToResource(), "name")));
        hasAccessDiv.add(toResourceAccessLabel);

        agreementsDiv = new WebMarkupContainer("agreementsDiv");
        setVisibility(agreementsDiv, networking.getAgreements().size() > 0);
        add(agreementsDiv);
        agreementsView = new RefreshingView("agreements", new Model((Serializable) networking.getAgreements())) {
            protected Iterator getItemModels() {
                List<Ref> agreements = (List<Ref>) agreementsView.getModelObject();
                return new ModelIteratorAdapter(agreements.iterator()) {
                    protected IModel model(Object agreement) {
                        return new RefModel(agreement);
                    }
                };
            }
            protected void populateItem(Item item) {
                final Ref agreement = (Ref) item.getModelObject();
                AjaxLink agreementLink = new AjaxLink("agreementLink") {
                    public void onClick(AjaxRequestTarget target) {
                        edit(agreement, target);
                    }
                };
                Label agreementString = new Label("agreement", agreement.deref().about());
                agreementLink.add(agreementString);
                item.add(agreementLink);
            }
        };
        agreementsDiv.add(agreementsView);

        relationshipsDiv = new WebMarkupContainer("relationshipsDiv");
        setVisibility(relationshipsDiv, networking.getRelationships().size() > 0);
        add(relationshipsDiv);
        relationshipsView = new RefreshingView("relationships", new Model((Serializable) networking.getRelationships())) {
            protected Iterator getItemModels() {
                List<Ref> relationships = (List<Ref>) relationshipsView.getModelObject();
                return new ModelIteratorAdapter(relationships.iterator()) {
                    protected IModel model(Object relationship) {
                        return new RefModel(relationship);
                    }
                };
            }
            protected void populateItem(Item item) {
                final Ref relationship = (Ref) item.getModelObject();
                AjaxLink relationshipLink = new AjaxLink("relationshipLink") {
                    public void onClick(AjaxRequestTarget target) {
                        edit(relationship, target);
                    }
                };
                Label relationshipString = new Label("relationship", relationship.deref().about());
                relationshipLink.add(relationshipString);
                item.add(relationshipLink);
            }
        };
        relationshipsDiv.add(relationshipsView);

        flowsDiv = new WebMarkupContainer("flowsDiv");
        setVisibility(flowsDiv, networking.getFlowActs().size() > 0);
        add(flowsDiv);
        flowsView = new RefreshingView("flowActs", new Model((Serializable) networking.getFlowActs())) {
            protected Iterator getItemModels() {
                List<Ref> flowActs = (List<Ref>) flowsView.getModelObject();
                return new ModelIteratorAdapter(flowActs.iterator()) {
                    protected IModel model(Object flowAct) {
                        return new RefModel(flowAct);
                    }
                };
            }
            protected void populateItem(Item item) {
                final Ref flowAct = (Ref) item.getModelObject();
                AjaxLink flowActLink = new AjaxLink("flowActLink") {
                    public void onClick(AjaxRequestTarget target) {
                        edit(flowAct, target);
                    }
                };
                Label flowActString = new Label("flowAct", flowAct.deref().about());
                flowActLink.add(flowActString);
                item.add(flowActLink);
            }
        };
        flowsDiv.add(flowsView);
    }


}
