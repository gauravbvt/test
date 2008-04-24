package com.mindalliance.channels.playbook.pages.forms.tabs.resource;

import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.util.ModelIteratorAdapter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.IModel;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.AjaxRequestTarget;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;

import java.util.Iterator;
import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 24, 2008
 * Time: 9:45:02 AM
 */
public class ResourceNetworkTab extends AbstractFormTab {

    RefreshingView relationshipsView;
    Label relationshipLabel;
    RefreshingView agreementsView;
    Label agreementLabel;

    public ResourceNetworkTab(String id, Ref element) {
        super(id, element);
    }

    protected void load() {
        super.load();
        // Relationships
        relationshipLabel = new Label("relationshipDescription", new Model("Please select a relationship"));
        relationshipsView = new RefreshingView("relationships", new RefPropertyModel(element, "relationships")) {
            protected Iterator getItemModels() {
                List<Ref> relationships = (List<Ref>) getModel().getObject();
                return new ModelIteratorAdapter(relationships.iterator()) {
                    protected IModel model(Object relationship) {
                        return new RefModel(relationship);
                    }
                };
            }

            protected void populateItem(Item item) {
                final Ref relationship = (Ref) item.getModelObject();
                final Label relationshipNameLabel = new Label("relationshipName", new RefPropertyModel(relationship, "name"));
                AjaxLink relationshipLink = new AjaxLink("relationshipLink") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        String description = (String) RefUtils.get(relationship, "description");
                        relationshipLabel.setModelObject(description);
                        target.addComponent(relationshipLabel);
                    }
                };
                relationshipLink.add(relationshipNameLabel);
                item.add(relationshipLink);
            }
        };
        add(relationshipsView);
        add(relationshipLabel);
        // Agreements
        agreementLabel = new Label("agreementDescription", new Model("Please select an agreement"));
        agreementsView = new RefreshingView("agreements", new RefPropertyModel(element, "agreements")) {
            protected Iterator getItemModels() {
                List<Ref> agreements = (List<Ref>) getModel().getObject();
                return new ModelIteratorAdapter(agreements.iterator()) {
                    protected IModel model(Object agreement) {
                        return new RefModel(agreement);
                    }
                };
            }

            protected void populateItem(Item item) {
                final Ref agreement = (Ref) item.getModelObject();
                final Label agreementNameLabel = new Label("agreementName", new RefPropertyModel(agreement, "name"));
                AjaxLink agreementLink = new AjaxLink("agreementLink") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        String description = (String) RefUtils.get(agreement, "description");
                        agreementLabel.setModelObject(description);
                        target.addComponent(agreementLabel);
                    }
                };
                agreementLink.add(agreementNameLabel);
                item.add(agreementLink);
            }
        };
        add(agreementsView);
        add(agreementLabel);
    }
}
