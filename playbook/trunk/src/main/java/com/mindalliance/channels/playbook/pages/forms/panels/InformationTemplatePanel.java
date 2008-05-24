package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.panels.AbstractComponentPanel;
import com.mindalliance.channels.playbook.pages.forms.ElementPanel;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ifm.info.InformationTemplate;
import com.mindalliance.channels.playbook.ifm.model.EventType;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.query.Query;
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
    AgentSpecPanel agentSpecPanel;

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
        eventSpecPanel = new EventSpecPanel("eventSpec", this, propPath + ".eventSpec", isReadOnly(), feedback);
        addReplaceable(eventSpecPanel);
        RefQueryModel topicChoicesModel = new RefQueryModel(EventType.class,
                                                            new Query("findAllTopicsIn",
                                                                       new RefPropertyModel(getElement(), propPath + ".eventSpec.eventTypes")));
        eoisPanel = new EOIsPanel("eventDetails", this, propPath + ".eventDetails", readOnly, feedback, topicChoicesModel);
        addReplaceable(eoisPanel);
        agentSpecPanel = new AgentSpecPanel("sourceAgentSpec", this, propPath+".sourceAgentSpec", isReadOnly(), feedback);
        addReplaceable(agentSpecPanel);
    }

    @Override
    public void elementChanged(String propPath, AjaxRequestTarget target) {
        super.elementChanged(propPath, target);
        if (propPath.endsWith(".eventSpec.eventTypes")) {
            target.addComponent(eoisPanel);
        }
    }

}
