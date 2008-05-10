package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.ElementPanel;
import com.mindalliance.channels.playbook.ifm.model.EventType;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.query.Query;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 9, 2008
 * Time: 2:47:26 PM
 */
public class InformationPanel extends AbstractComponentPanel {

    EventPanel eventPanel;
    EOIsPanel eoisPanel;

    public InformationPanel(String id, ElementPanel parentPanel, String propPath, boolean readOnly, FeedbackPanel feedback) {
        super(id, parentPanel, propPath, readOnly, feedback);
    }

    protected void load() {
        super.load();
        eventPanel = new EventPanel("event", this, propPath + ".event", isReadOnly(), feedback);
        addReplaceable(eventPanel);
        RefQueryModel topicChoicesModel = new RefQueryModel(EventType.class,
                new Query("findAllTopicsIn",
                        new RefPropertyModel(getElement(), propPath + ".event.eventTypes")));
        eoisPanel = new EOIsPanel("eventDetails", this, propPath + ".eventDetails", isReadOnly(), feedback, topicChoicesModel);
        addReplaceable(eoisPanel);
    }

    @Override
    public void elementChanged(String propPath, AjaxRequestTarget target) {
        super.elementChanged(propPath, target);
        if (propPath.endsWith(".event.eventTypes")) {
            target.addComponent(eoisPanel);
        }
    }

}

