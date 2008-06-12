package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.ElementPanel;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.ifm.model.EventType;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.query.Query;
import com.mindalliance.channels.playbook.ref.Ref;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;

import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 9, 2008
 * Time: 2:47:26 PM
 */
public class InformationPanel extends AbstractComponentPanel {

    DynamicFilterTree eventTree; // what the info is about
    DynamicFilterTree eventTypesTree;
    EOIsPanel eoisPanel;  // content
    DynamicFilterTree sourceAgentsTree;
    TimingPanel timeToLivePanel;

    public InformationPanel(String id, ElementPanel parentPanel, String propPath, boolean readOnly, FeedbackPanel feedback) {
        super(id, parentPanel, propPath, readOnly, feedback);
    }

    protected void load() {
        super.load();
        eventTree = new DynamicFilterTree("event", new RefPropertyModel(getElement(), propPath+".event"),
                                           new RefQueryModel(getPlaybook(), new Query("findAllOccurrences")), SINGLE_SELECTION) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                 Ref selected = eventTree.getNewSelection();
                RefUtils.set(getElement(), "event", selected);
                elementChanged(propPath+".event", target);
            }
        };
        addReplaceable(eventTree);
        eventTypesTree = new DynamicFilterTree("eventTypes", new RefPropertyModel(getElement(), propPath+".eventTypes"),
                                            new RefQueryModel(getProject(), new Query("findAllTypes", "EventType"))) {
             public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                 List<Ref> selected = eventTypesTree.getNewSelections();
                 RefUtils.set(getElement(), "eventTypes", selected);
                 elementChanged(propPath+".eventTypes", target);
                 target.addComponent(eoisPanel);
             }
         };
         addReplaceable(eventTypesTree);
        RefQueryModel topicChoicesModel = new RefQueryModel(EventType.class,
                new Query("findAllTopicsIn",
                        new RefPropertyModel(getElement(), propPath + ".eventTypes")));
        eoisPanel = new EOIsPanel("eventDetails", this, propPath + ".eventDetails", isReadOnly(), feedback, topicChoicesModel);
        addReplaceable(eoisPanel);
        sourceAgentsTree = new DynamicFilterTree("sourceAgents", new RefPropertyModel(getComponent(), "sourceAgents"),
                                                 new RefQueryModel(getScope(), new Query("findAllAgents"))) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                List<Ref> selected = sourceAgentsTree.getNewSelections();
                RefUtils.set(getElement(), propPath+".sourceAgents", selected);
            }
        };
        addReplaceable(sourceAgentsTree);
        timeToLivePanel = new TimingPanel("timeToLive", this, propPath+".timeToLive", isReadOnly(), feedback);
        addReplaceable(timeToLivePanel);
    }

    @Override
    public void elementChanged(String propPath, AjaxRequestTarget target) {
        super.elementChanged(propPath, target);
        if (propPath.endsWith(".event.eventTypes")) {
            target.addComponent(eoisPanel);
        }
    }

}

