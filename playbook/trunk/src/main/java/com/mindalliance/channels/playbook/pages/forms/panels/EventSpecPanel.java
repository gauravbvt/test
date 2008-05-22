package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.ElementPanel;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ifm.info.EventSpec;
import com.mindalliance.channels.playbook.ifm.Channels;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.support.components.AutoCompleteTextFieldWithChoices;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.query.Query;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;

import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 29, 2008
 * Time: 9:01:14 PM
 */
public class EventSpecPanel extends AbstractComponentPanel {

    protected EventSpec eventSpec;
    protected DynamicFilterTree eventTypesTree;
    protected LocationSpecPanel locationSpecPanel;
    protected TimingPanel timingPanel;

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
    }
}
