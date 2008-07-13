package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.ElementPanel;
import com.mindalliance.channels.playbook.ifm.definition.EventDefinition;
import com.mindalliance.channels.playbook.ifm.definition.EventSpecification;
import com.mindalliance.channels.playbook.ifm.definition.LocationDefinition;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.Model;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 1, 2008
 * Time: 10:10:59 AM
 */
public class EventDefinitionPanel extends AbstractDefinitionPanel {

    protected EventDefinition eventDefinition;
    protected AjaxCheckBox anyLocationCheckBox;
    protected WebMarkupContainer locationDefinitionDiv;
    protected LocationDefinitionPanel locationDefinitionPanel;
    protected AjaxCheckBox anyCauseCheckBox;
    protected WebMarkupContainer causeSpecificationDiv;
    protected EventSpecificationPanel causeSpecificationPanel;


    public EventDefinitionPanel(String id, ElementPanel parentPanel, String propPath, boolean readOnly, FeedbackPanel feedback) {
        super(id, parentPanel, propPath, readOnly, feedback);
    }

    protected void load() {
        super.load();
        eventDefinition = (EventDefinition)getComponent();
        anyLocationCheckBox = new AjaxCheckBox("anyLocation", new Model((Boolean)eventDefinition.getLocationDefinition().matchesAll())){
            protected void onUpdate(AjaxRequestTarget target) {
                boolean anyLocation = (Boolean)anyLocationCheckBox.getModelObject();
                if (anyLocation) {
                    setProperty("locationDefinition", new LocationDefinition());
                    locationDefinitionPanel = new LocationDefinitionPanel("locationDefinition", EventDefinitionPanel.this, propPath+".locationDefinition", isReadOnly(), feedback);
                    addReplaceableTo(locationDefinitionPanel, locationDefinitionDiv);
                }
                setVisibility(locationDefinitionDiv, !anyLocation, target);
            }
        };
        addReplaceable(anyLocationCheckBox);
        locationDefinitionDiv = new WebMarkupContainer("locationDefinitionDiv");
        setVisibility(locationDefinitionDiv, !eventDefinition.getLocationDefinition().matchesAll());
        addReplaceable(locationDefinitionDiv);
        locationDefinitionPanel = new LocationDefinitionPanel("locationDefinition", this, propPath+".locationDefinition", isReadOnly(), feedback);
        addReplaceableTo(locationDefinitionPanel, locationDefinitionDiv);
        anyCauseCheckBox = new AjaxCheckBox("anyCause", new Model((Boolean)eventDefinition.getCauseEventSpec().matchesAll())){
            protected void onUpdate(AjaxRequestTarget target) {
                boolean anyCause = (Boolean)anyCauseCheckBox.getModelObject();
                if (anyCause) {
                    setProperty("causeEventSpec", new EventSpecification());
                    causeSpecificationPanel = new EventSpecificationPanel("causeSpecification", EventDefinitionPanel.this, propPath+".causeEventSpec", isReadOnly(), feedback);
                    addReplaceableTo(causeSpecificationPanel, causeSpecificationDiv);
                }
                setVisibility(causeSpecificationDiv, !anyCause, target);
            }
        };
        addReplaceable(anyCauseCheckBox);
        causeSpecificationDiv = new WebMarkupContainer("causeSpecificationDiv");
        setVisibility(causeSpecificationDiv, !eventDefinition.getCauseEventSpec().matchesAll());
        addReplaceable(causeSpecificationDiv);
        causeSpecificationPanel = new EventSpecificationPanel("causeSpecification", this, propPath+".causeEventSpec", isReadOnly(), feedback);
        addReplaceableTo(causeSpecificationPanel, causeSpecificationDiv);
    }

}
