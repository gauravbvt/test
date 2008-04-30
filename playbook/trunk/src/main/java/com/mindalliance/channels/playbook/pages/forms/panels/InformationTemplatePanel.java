package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.AbstractComponentPanel;
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

    public InformationTemplatePanel(String id, Ref element, String propPath, boolean readOnly, FeedbackPanel feedback) {
        super(id, element, propPath, readOnly, feedback);
    }

    protected void load() {
        super.load();
        informationTemplate = (InformationTemplate) RefUtils.get(element, propPath);
        eventSpecPanel = new EventSpecPanel("eventSpec", element, propPath + ".eventSpec", readOnly, feedback);
        addReplaceable(eventSpecPanel);
        List<Ref> eventTypes = (List<Ref>)RefUtils.get(element, propPath + ".eventSpec.eventTypes");
        eoisPanel = new EOIsPanel("eventDetails", element, propPath + ".eventDetails", readOnly, feedback, eventTypes);
        addReplaceable(eoisPanel);
        List<Ref> allOrganizationTypes = project.findAllTypes("OrganizationType");
        credibleSourcesTree = new DynamicFilterTree("credibleSources", new RefPropertyModel(informationTemplate, "sourceOrganizationTypes"), new Model((Serializable)allOrganizationTypes)) {
             public void onFilterSelect( AjaxRequestTarget target, Filter filter ) {
                List<Ref> newSelections = credibleSourcesTree.getNewSelections();
                RefUtils.set(element, "sourceOrganizationTypes", newSelections);
                elementChanged();
             }
        };
        addReplaceable(credibleSourcesTree);
    }
}
