package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.ifm.definition.EventDefinition;
import com.mindalliance.channels.playbook.ifm.definition.EventSpecification;
import com.mindalliance.channels.playbook.ifm.definition.LocationDefinition;
import com.mindalliance.channels.playbook.pages.forms.AbstractPlaybookPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.WebMarkupContainer;
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
    private static final long serialVersionUID = -6293435118509434852L;

    public EventDefinitionPanel(String id, AbstractPlaybookPanel parentPanel, String propPath) {
        super(id, parentPanel, propPath);
    }

    @Override
    protected void load() {
        super.load();
        eventDefinition = (EventDefinition)getComponent();
        anyLocationCheckBox = new AjaxCheckBox("anyLocation", new Model<Boolean>(
                eventDefinition.getLocationDefinition().matchesAll() )){
            private static final long serialVersionUID = 3219468629111919516L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                boolean anyLocation = anyLocationCheckBox.getModelObject();
                if (anyLocation) {
                    setProperty("locationDefinition", new LocationDefinition());
                    locationDefinitionPanel = new LocationDefinitionPanel("locationDefinition", EventDefinitionPanel.this, propPath+".locationDefinition");
                    addReplaceableTo(locationDefinitionPanel, locationDefinitionDiv);
                }
                setVisibility(locationDefinitionDiv, !anyLocation, target);
            }
        };
        addReplaceable(anyLocationCheckBox);
        locationDefinitionDiv = new WebMarkupContainer("locationDefinitionDiv");
        setVisibility(locationDefinitionDiv, !eventDefinition.getLocationDefinition().matchesAll());
        addReplaceable(locationDefinitionDiv);
        locationDefinitionPanel = new LocationDefinitionPanel("locationDefinition", this, propPath+".locationDefinition");
        addReplaceableTo(locationDefinitionPanel, locationDefinitionDiv);
        anyCauseCheckBox = new AjaxCheckBox("anyCause", new Model<Boolean>(
                eventDefinition.getCauseEventSpec().matchesAll() )){
            private static final long serialVersionUID = 2667243296378551691L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                boolean anyCause = anyCauseCheckBox.getModelObject();
                if (anyCause) {
                    setProperty("causeEventSpec", new EventSpecification());
                    causeSpecificationPanel = new EventSpecificationPanel("causeSpecification", EventDefinitionPanel.this, propPath+".causeEventSpec");
                    addReplaceableTo(causeSpecificationPanel, causeSpecificationDiv);
                }
                setVisibility(causeSpecificationDiv, !anyCause, target);
            }
        };
        addReplaceable(anyCauseCheckBox);
        causeSpecificationDiv = new WebMarkupContainer("causeSpecificationDiv");
        setVisibility(causeSpecificationDiv, !eventDefinition.getCauseEventSpec().matchesAll());
        addReplaceable(causeSpecificationDiv);
        causeSpecificationPanel = new EventSpecificationPanel("causeSpecification", this, propPath+".causeEventSpec");
        addReplaceableTo(causeSpecificationPanel, causeSpecificationDiv);
    }

}
