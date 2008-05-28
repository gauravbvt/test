package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.ElementPanel;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ifm.spec.EventSpec;
import com.mindalliance.channels.playbook.ifm.spec.Spec;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.query.Query;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.model.Model;

import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 29, 2008
 * Time: 9:01:14 PM
 */
public class EventSpecPanel extends AbstractSpecComponentPanel {

    protected EventSpec eventSpec;
    protected DynamicFilterTree eventTypesTree;
    protected WebMarkupContainer locationSpecDiv;
    protected LocationSpecPanel locationSpecPanel;
    protected WebMarkupContainer timingDiv;
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
                setVisibility();
                target.addComponent(locationSpecDiv);
                target.addComponent(timingDiv);
            }
        };
        addReplaceable(eventTypesTree);
        locationSpecDiv = new WebMarkupContainer("locationSpecDiv");
        addReplaceable(locationSpecDiv);
        locationSpecPanel = new LocationSpecPanel("locationSpec", this, propPath + ".locationSpec", readOnly, feedback);
        locationSpecDiv.add(locationSpecPanel);
        timingDiv = new WebMarkupContainer("timingDiv");
        addReplaceable(timingDiv);
        timingPanel = new TimingPanel("timing", this, propPath + ".timing", readOnly, feedback);
        timingDiv.add(timingPanel);
        setVisibility();
    }

    protected Spec makeNewSpec() {
        return new EventSpec();
    }

    @Override
    protected String getAnyLabelString() {
        return "of any kind";
    }

    private void setVisibility() {
        if (eventSpec.isDefined()) {
            locationSpecDiv.add(new AttributeModifier("style", true, new Model("display:block")));
            timingDiv.add(new AttributeModifier("style", true, new Model("display:block")));
        }
        else {
            locationSpecDiv.add(new AttributeModifier("style", true, new Model("display:none")));
            timingDiv.add(new AttributeModifier("style", true, new Model("display:none")));

        }

    }
}
