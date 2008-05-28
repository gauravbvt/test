package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.ElementPanel;
import com.mindalliance.channels.playbook.ifm.model.EventType;
import com.mindalliance.channels.playbook.ifm.spec.Spec;
import com.mindalliance.channels.playbook.ifm.spec.InformationSpec;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.query.Query;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 29, 2008
 * Time: 8:41:52 PM
 */
public class InformationSpecPanel extends AbstractSpecComponentPanel {

    EventSpecPanel eventSpecPanel;
    EOIsPanel eoisPanel;
    ResourceSpecPanel resourceSpecPanel;

    public InformationSpecPanel(String id, ElementPanel parentPanel, String propPath, boolean readOnly, FeedbackPanel feedback) {
        super(id, parentPanel, propPath, readOnly, feedback);
    }

    protected void load() {
        super.load();
        eventSpecPanel = new EventSpecPanel("eventSpec", this, propPath + ".eventSpec", isReadOnly(), feedback);
        addReplaceable(eventSpecPanel);
        RefQueryModel topicChoicesModel = new RefQueryModel(EventType.class,
                                                            new Query("findAllTopicsIn",
                                                                       new RefPropertyModel(getElement(), propPath + ".eventSpec.eventTypes")));
        eoisPanel = new EOIsPanel("eventDetails", this, propPath + ".eventDetails", readOnly, feedback, topicChoicesModel);
        addReplaceable(eoisPanel);
        resourceSpecPanel = new ResourceSpecPanel("resourceSpec", this, propPath+".sourceSpec", isReadOnly(), feedback);
        addReplaceable(resourceSpecPanel);
    }

    protected Spec makeNewSpec() {
        return new InformationSpec();
    }

    @Override
    protected String getAnyLabelString() {
        return "any information";
    }

    @Override
    public void elementChanged(String propPath, AjaxRequestTarget target) {
        super.elementChanged(propPath, target);
        if (propPath.endsWith(".eventSpec.eventTypes")) {
            target.addComponent(eoisPanel);
        }
    }

}
