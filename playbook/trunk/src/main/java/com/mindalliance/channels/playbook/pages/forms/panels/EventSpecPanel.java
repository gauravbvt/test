package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.panels.AbstractComponentPanel;
import com.mindalliance.channels.playbook.pages.forms.ElementPanel;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ifm.info.EventSpec;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.query.Query;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.Model;

import java.util.List;
import java.io.Serializable;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 29, 2008
 * Time: 9:01:14 PM
 */
public class EventSpecPanel extends AbstractComponentPanel {

    EventSpec eventSpec;
    DynamicFilterTree eventTypesTree;
    LocationSpecPanel locationSpecPanel;
    TimingPanel timingPanel;
    DynamicFilterTree relationshipTypesTree;

    public EventSpecPanel(String id, ElementPanel parentPanel, String propPath, boolean readOnly, FeedbackPanel feedback) {
        super(id, parentPanel, propPath, readOnly, feedback);
    }

    protected void load() {
        super.load();
        eventSpec = (EventSpec) RefUtils.get(getElement(), propPath);
        if (eventSpec == null) {
            eventSpec = new EventSpec();
            RefUtils.set(getElement(), propPath, eventSpec);
        }
        // List<Ref> allEventTypes = project.findAllTypes("EventType");
        eventTypesTree = new DynamicFilterTree("eventTypes", new RefPropertyModel(eventSpec, "eventTypes"),
                                               new RefQueryModel(getScope(), new Query("findAllTypes", "EventType"))) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                List<Ref> newSelections = eventTypesTree.getNewSelections();
                RefUtils.set(eventSpec, "eventTypes", newSelections);
                elementChanged(propPath+".eventTypes", target);
            }
        };
        addReplaceable(eventTypesTree);
        locationSpecPanel = new LocationSpecPanel("locationSpec", this, propPath + ".locationSpec", readOnly, feedback);
        addReplaceable(locationSpecPanel);
        timingPanel = new TimingPanel("timing", this, propPath + ".timing", readOnly, feedback);
        addReplaceable(timingPanel);
        // List<Ref> allRelationshipTypes = project.findAllTypes("RelationshipType");
        relationshipTypesTree = new DynamicFilterTree("relationshipTypes", new RefPropertyModel(eventSpec, "relationshipTypes"),
                                new RefQueryModel(getScope(), new Query("findAllTypes", "RelationshipType"))) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                List<Ref> newSelections = relationshipTypesTree.getNewSelections();
                RefUtils.set(eventSpec, "relationshipTypes", newSelections);
                elementChanged(propPath+".relationshipTypes", target);
            }
        };
        addReplaceable(relationshipTypesTree);
    }
}
