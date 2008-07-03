package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.ElementPanel;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.ifm.model.EventType;
import com.mindalliance.channels.playbook.ifm.info.InformationNeed;
import com.mindalliance.channels.playbook.query.Query;
import com.mindalliance.channels.playbook.ref.Ref;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;

import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 28, 2008
 * Time: 8:34:37 AM
 */
public class InformationNeedPanel extends AbstractComponentPanel {

    protected InformationNeed informationNeed;
    protected DynamicFilterTree agentTree;
    protected EventSpecificationPanel eventSpecPanel;
    protected EOIsPanel eoisPanel;
    protected AgentSpecificationPanel agentSpecPanel;
    protected TimingPanel deadlinePanel;

    public InformationNeedPanel(String id, ElementPanel parentPanel, String propPath, boolean readOnly, FeedbackPanel feedback) {
        super(id, parentPanel, propPath, readOnly, feedback);
    }

    protected void load() {
        super.load();
        informationNeed = (InformationNeed)getComponent();
        agentTree = new DynamicFilterTree("agent", new RefPropertyModel(informationNeed, "agent"),
                                          new RefQueryModel(getPlaybook(), new Query("findAllAgents")),
                                          SINGLE_SELECTION) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                Ref selected = agentTree.getNewSelection();
                informationNeed.setAgent(selected);
                elementChanged(propPath+".agent", target);
            }
        };
        addReplaceable(agentTree);
        eventSpecPanel = new EventSpecificationPanel("eventSpec", this, propPath + ".eventSpec", isReadOnly(), feedback);
        this.addReplaceable(eventSpecPanel);
        deadlinePanel = new TimingPanel("deadline", this, propPath+".deadline", isReadOnly(), feedback);
        addReplaceable(deadlinePanel);
        RefQueryModel topicChoicesModel = new RefQueryModel(this, new Query("findAllKnownTopics"));
        eoisPanel = new EOIsPanel("eventDetails", this, propPath + ".eventDetails", readOnly, feedback, topicChoicesModel);
        addReplaceable(eoisPanel);
        agentSpecPanel = new AgentSpecificationPanel("sourceSpec", this, propPath+".sourceSpec", isReadOnly(), feedback);
        addReplaceable(agentSpecPanel);
    }

    @Override
    public void elementChanged(String propPath, AjaxRequestTarget target) {
        super.elementChanged(propPath, target);
        if (propPath.endsWith(".eventTypes")) {
            target.addComponent(eoisPanel);
        }
        if (propPath.endsWith(".event")) {
            target.addComponent(eoisPanel);
        }
    }

    private List<String> findAllKnownTopics() {
        List<Ref> allEventTypes = informationNeed.getEventSpec().allEventTypes();
        return (List<String>)Query.execute(EventType.class, "findAllTopicsIn", allEventTypes);
    }

}
