package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.AbstractComponentPanel;
import com.mindalliance.channels.playbook.pages.forms.ElementPanel;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ifm.info.InformationTemplate;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.ajax.AjaxRequestTarget;

import java.util.List;
import java.io.Serializable;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 29, 2008
 * Time: 8:41:52 PM
 */
public class InformationTemplatePanel extends AbstractComponentPanel {

    InformationTemplate informationTemplate;

    EventSpecPanel eventSpecPanel;
    EOIsPanel eoisPanel;
    DynamicFilterTree credibleSourcesTree;

    public InformationTemplatePanel(String id, ElementPanel parentPanel, String propPath, boolean readOnly, FeedbackPanel feedback) {
        super(id, parentPanel, propPath, readOnly, feedback);
    }

    protected void load() {
        super.load();
        informationTemplate = (InformationTemplate) RefUtils.get(getElement(), propPath);
        if (informationTemplate == null) {
            informationTemplate = new InformationTemplate();
            RefUtils.set(getElement(), propPath, informationTemplate);
        }
        eventSpecPanel = new EventSpecPanel("eventSpec", this, propPath + ".eventSpec", readOnly, feedback);
        addReplaceable(eventSpecPanel);
        List<Ref> eventTypes = (List<Ref>) RefUtils.get(getElement(), propPath + ".eventSpec.eventTypes");
        eoisPanel = new EOIsPanel("eventDetails", this, propPath + ".eventDetails", readOnly, feedback, eventTypes);
        addReplaceable(eoisPanel);
        List<Ref> allOrganizationTypes = project.findAllTypes("OrganizationType");
        credibleSourcesTree = new DynamicFilterTree("credibleSources", new RefPropertyModel(informationTemplate, "organizationTypes"), new Model((Serializable) allOrganizationTypes)) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                List<Ref> newSelections = credibleSourcesTree.getNewSelections();
                RefUtils.set(getElement(), "organizationTypes", newSelections);
                elementChanged(propPath, target);
            }
        };
        addReplaceable(credibleSourcesTree);
    }

    @Override
    public void elementChanged(String propPath, AjaxRequestTarget target) {
        super.elementChanged(propPath, target);
        if (propPath.endsWith(".eventSpec")) {
            target.addComponent(eoisPanel);
        }
    }
}
