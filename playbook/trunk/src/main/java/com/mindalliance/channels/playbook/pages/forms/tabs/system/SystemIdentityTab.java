package com.mindalliance.channels.playbook.pages.forms.tabs.system;

import com.mindalliance.channels.playbook.pages.forms.tabs.resource.ResourceIdentityTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.ref.Ref;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.AjaxRequestTarget;

import java.util.ArrayList;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 5, 2008
 * Time: 9:59:15 AM
 */
public class SystemIdentityTab extends ResourceIdentityTab {

    TextArea accessField;
    DynamicFilterTree adminPositionTree;

    public SystemIdentityTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        AjaxLink organizationLink = new AjaxLink("organizationLink") {
            public void onClick(AjaxRequestTarget target) {
                edit((Ref)getProperty("organization"), target);
            }
        };
        Label organizationNameLabel = new Label("organizationName", new RefPropertyModel(getElement(), "organization.name"));
        organizationLink.add(organizationNameLabel);
        addReplaceable(organizationLink);
        accessField = new TextArea("instructions", new RefPropertyModel(getElement(), "instructions"));
        addInputField(accessField);
        adminPositionTree = new DynamicFilterTree("adminPosition", new RefPropertyModel(getElement(), "adminPosition"),
                                                   new RefPropertyModel(getElement(), "organization.positions", new ArrayList<Ref>()), SINGLE_SELECTION) {
             public void onFilterSelect( AjaxRequestTarget target, Filter filter ) {
                 Ref position = adminPositionTree.getNewSelection();
                 setProperty("adminPosition", position);
             }
        };
        addReplaceable(adminPositionTree);
    }

}
