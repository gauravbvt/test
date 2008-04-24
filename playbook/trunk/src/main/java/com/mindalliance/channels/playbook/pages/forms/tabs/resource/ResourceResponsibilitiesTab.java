package com.mindalliance.channels.playbook.pages.forms.tabs.resource;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.util.ModelIteratorAdapter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.AjaxRequestTarget;

import java.util.Iterator;
import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 24, 2008
 * Time: 9:21:40 AM
 */
public class ResourceResponsibilitiesTab extends AbstractFormTab {

    RefreshingView rolesView;
    Label roleLabel;

    public ResourceResponsibilitiesTab(String id, Ref element) {
        super(id, element);
    }

    protected void load() {
        super.load();
        roleLabel = new Label("roleDescription", new Model("Please select a role"));
        rolesView = new RefreshingView("roles", new RefPropertyModel(element, "roles")) {
            protected Iterator getItemModels() {
                List<Ref> roles = (List<Ref>) getModel().getObject();
                return new ModelIteratorAdapter(roles.iterator()) {
                    protected IModel model(Object role) {
                        return new RefModel(role);
                    }
                };
            }

            protected void populateItem(Item item) {
                final Ref role = (Ref) item.getModelObject();
                final Label roleNameLabel = new Label("roleName", new RefPropertyModel(role, "name"));
                AjaxLink roleLink = new AjaxLink("roleLink") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        String description = (String) RefUtils.get(role, "description");
                        roleLabel.setModelObject(description);
                        target.addComponent(roleLabel);
                    }
                };
                roleLink.add(roleNameLabel);
                item.add(roleLink);
            }
        };
        add(rolesView);
        add(roleLabel);
    }
}
