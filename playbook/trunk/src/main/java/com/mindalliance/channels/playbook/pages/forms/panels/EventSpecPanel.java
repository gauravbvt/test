package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.AbstractComponentPanel;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ifm.info.EventSpec;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
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

    public EventSpecPanel(String id, Ref element, String propPath, boolean readOnly, FeedbackPanel feedback) {
        super(id, element, propPath, readOnly, feedback);
    }

    protected void load() {
        super.load();
        eventSpec = (EventSpec) RefUtils.get(element, propPath);
        if (eventSpec == null) {
            eventSpec = new EventSpec();
            RefUtils.set(element, propPath, eventSpec);
        }
        List<Ref> allEventTypes = project.findAllTypes("EventType");
        eventTypesTree = new DynamicFilterTree("eventTypes", new RefPropertyModel(eventSpec, "eventTypes"), new Model((Serializable)allEventTypes)) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                List<Ref> newSelections = eventTypesTree.getNewSelections();
                RefUtils.set(eventSpec, "eventTypes", newSelections);
                elementChanged();
            }
        };
        addReplaceable(eventTypesTree);
        locationSpecPanel = new LocationSpecPanel("locationSpec", element, propPath + ".locationSpec", readOnly, feedback);
        addReplaceable(locationSpecPanel);
        timingPanel = new TimingPanel("timing", element, propPath + ".timing", readOnly, feedback);
        addReplaceable(timingPanel);
        List<Ref> allRelationshipTypes = project.findAllTypes("RelationshipType");
        relationshipTypesTree = new DynamicFilterTree("relationshipTypes", new RefPropertyModel(eventSpec, "relationshipTypes"), new Model((Serializable)allRelationshipTypes)) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                List<Ref> newSelections = relationshipTypesTree.getNewSelections();
                RefUtils.set(eventSpec, "relationshipTypes", newSelections);
                elementChanged();
            }
        };
        addReplaceable(relationshipTypesTree);
    }
}
