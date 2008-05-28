package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.ElementPanel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.ifm.model.EventType;
import com.mindalliance.channels.playbook.query.Query;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 28, 2008
 * Time: 8:34:37 AM
 */
public class InformationNeedPanel extends AbstractComponentPanel {

    EventSpecPanel eventSpecPanel;
    EOIsPanel eoisPanel;
    AgentSpecPanel agentSpecPanel;
    LocationPanel locationPanel;

    public InformationNeedPanel(String id, ElementPanel parentPanel, String propPath, boolean readOnly, FeedbackPanel feedback) {
        super(id, parentPanel, propPath, readOnly, feedback);
    }

    protected void load() {
        super.load();
        eventSpecPanel = new EventSpecPanel("eventSpec", this, propPath + ".eventSpec", isReadOnly(), feedback);
        addReplaceable(eventSpecPanel);
        locationPanel = new LocationPanel("eventLocation", this, propPath+".eventLocation", isReadOnly(), feedback);
        addReplaceable(locationPanel);
        RefQueryModel topicChoicesModel = new RefQueryModel(EventType.class,
                                                            new Query("findAllTopicsIn",
                                                                       new RefPropertyModel(getElement(), propPath + ".eventSpec.eventTypes")));
        eoisPanel = new EOIsPanel("eventDetails", this, propPath + ".eventDetails", readOnly, feedback, topicChoicesModel);
        addReplaceable(eoisPanel);
        agentSpecPanel = new AgentSpecPanel("sourceSpec", this, propPath+".sourceSpec", isReadOnly(), feedback);
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
