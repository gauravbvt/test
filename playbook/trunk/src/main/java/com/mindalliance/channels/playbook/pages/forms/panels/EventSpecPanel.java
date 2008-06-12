package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.ElementPanel;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ifm.spec.EventSpec;
import com.mindalliance.channels.playbook.ifm.spec.Spec;
import com.mindalliance.channels.playbook.ifm.spec.LocationSpec;
import com.mindalliance.channels.playbook.ifm.IfmElement;
import com.mindalliance.channels.playbook.ifm.playbook.Event;
import com.mindalliance.channels.playbook.ifm.info.Location;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.query.Query;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
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
    protected AjaxCheckBox anyCauseCheckBox;
    protected DynamicFilterTree causeTree;
    protected WebMarkupContainer causeDiv;
    protected AjaxCheckBox inSpecifiedLocationCheckBox;
    protected AjaxCheckBox inSpecificLocationCheckBox;
    protected LocationSpecPanel locationSpecPanel;
    protected LocationPanel locationPanel;
    protected Location priorLocation;
    protected LocationSpec priorLocationSpec;
    protected TimingPanel timingPanel;

    public EventSpecPanel(String id, ElementPanel parentPanel, String propPath, boolean readOnly, FeedbackPanel feedback) {
        super(id, parentPanel, propPath, readOnly, feedback);
    }

    protected void load() {
        super.load();
        eventSpec = (EventSpec)getComponent();
        // event types
        eventTypesTree = new DynamicFilterTree("eventTypes", new RefPropertyModel(eventSpec, "eventTypes"),
                                               new RefQueryModel(getScope(), new Query("findAllTypes", "EventType"))) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                List<Ref> newSelections = eventTypesTree.getNewSelections();
                RefUtils.set(eventSpec, "eventTypes", newSelections);
                elementChanged(propPath+".eventTypes", target);
            }
        };
        addReplaceable(eventTypesTree);
        // cause
        causeDiv = new WebMarkupContainer("causeDiv");
        addReplaceable(causeDiv);
        anyCauseCheckBox = new AjaxCheckBox("anyCause", new Model(true)) {
            protected void onUpdate(AjaxRequestTarget target) {
                setCauseVisibility();
                target.addComponent(causeTree);
            }
        };
        addReplaceableTo(anyCauseCheckBox, causeDiv);
        if (definesPlaybookElement()) {
            assert getElement() instanceof Event;
            causeTree = new DynamicFilterTree("cause", new RefPropertyModel(getComponent(),"causeEvent"),
                                           new RefQueryModel(getPlaybook(), new Query("findAllPriorOccurrencesOf", getElement())),
                                            SINGLE_SELECTION) {
                public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                    Ref cause = causeTree.getNewSelection();
                    RefUtils.set(eventSpec, "causeEvent", cause);
                    elementChanged(propPath+".causeEvent", target);
                }
            };
            addReplaceableTo(causeTree, causeDiv);
        }
        else {
            Label causeLabel = new Label("cause", new Model("This should not be displayed"));
            addReplaceableTo(causeLabel, causeDiv);
        }
        setCauseVisibility();
        setCauseDivVisibility();
        // location
        inSpecifiedLocationCheckBox = new AjaxCheckBox("inSpecifiedLocation", new Model(!eventSpec.isLocationSpecific())) {
            protected void onUpdate(AjaxRequestTarget target) {
                boolean isSpecifiedLocation = (Boolean)inSpecifiedLocationCheckBox.getModelObject();
                inSpecificLocationCheckBox.setModelObject(!isSpecifiedLocation);
                updateLocation(isSpecifiedLocation, target);
                target.addComponent(inSpecificLocationCheckBox);
            }
        };
        addReplaceable(inSpecifiedLocationCheckBox);
        inSpecificLocationCheckBox = new AjaxCheckBox("inSpecificLocation", new Model(eventSpec.isLocationSpecific())) {
            protected void onUpdate(AjaxRequestTarget target) {
                boolean isSpecificLocation = (Boolean)inSpecificLocationCheckBox.getModelObject();
                inSpecifiedLocationCheckBox.setModelObject(!isSpecificLocation);
                updateLocation(!isSpecificLocation, target);
                target.addComponent(inSpecifiedLocationCheckBox);
            }
        };
        addReplaceable(inSpecificLocationCheckBox);
        locationSpecPanel = new LocationSpecPanel("locationSpec", this, propPath + ".locationSpec", isReadOnly(), feedback);
        addReplaceable(locationSpecPanel);
        locationPanel = new LocationPanel("location", this, propPath + ".location", isReadOnly(), feedback);
        addReplaceable(locationPanel);
        timingPanel = new TimingPanel("timing", this, propPath + ".timing", isReadOnly(), feedback);
        addReplaceable(timingPanel);
        setLocationVisibility(!eventSpec.isLocationSpecific());
        priorLocationSpec = eventSpec.getLocationSpec();
        priorLocation = eventSpec.getLocation();
    }

    private void setLocationVisibility(boolean isSpecifiedLocation) {
        if (isSpecifiedLocation) {
           locationPanel.add(new AttributeModifier("style", true, new Model("display:none")));
           locationSpecPanel.add(new AttributeModifier("style", true, new Model("display:block")));
        }
        else {
           locationPanel.add(new AttributeModifier("style", true, new Model("display:block")));
           locationSpecPanel.add(new AttributeModifier("style", true, new Model("display:none"))); 
        }
    }

    private void updateLocation(boolean isSpecifiedLocation, AjaxRequestTarget target) {
        if (isSpecifiedLocation) {
            priorLocation = eventSpec.getLocation();
            eventSpec.setLocationSpec(priorLocationSpec);    // resets location
            elementChanged(propPath+".locationSpec", target);
            locationSpecPanel = new LocationSpecPanel("locationSpec", this, propPath + ".locationSpec", isReadOnly(), feedback);
            addReplaceable(locationSpecPanel);
        }
        else {
            priorLocationSpec = eventSpec.getLocationSpec();
            eventSpec.setLocation(priorLocation); // resets locationSpec
            elementChanged(propPath+".location", target);
            locationPanel = new LocationPanel("location", this, propPath + ".location", isReadOnly(), feedback);
            addReplaceable(locationPanel);
        }
        setLocationVisibility(isSpecifiedLocation);
        target.addComponent(locationPanel);
        target.addComponent(locationSpecPanel);
    }

    private void setCauseVisibility() {
        boolean isAnyCause = (Boolean)anyCauseCheckBox.getModelObject();
        if (isAnyCause) {
            causeTree.add(new AttributeModifier("style", true, new Model("display:none")));
        }
        else {
            causeTree.add(new AttributeModifier("style", true, new Model("display:block")));
        }
    }

    private void setCauseDivVisibility() {
        if (definesPlaybookElement()) {
            causeDiv.add(new AttributeModifier("style", true, new Model("display:block")));
        }
        else {
            causeDiv.add(new AttributeModifier("style", true, new Model("display:none")));
        }
    }

    private boolean definesPlaybookElement() {
        IfmElement ifmElement = (IfmElement)getElement().deref();
        return ifmElement.isPlaybookElement();
    }

    protected Spec makeNewSpec() {
        return new EventSpec();
    }

    @Override
    protected String getAnyLabelString() {
        return "of any kind";
    }

}
