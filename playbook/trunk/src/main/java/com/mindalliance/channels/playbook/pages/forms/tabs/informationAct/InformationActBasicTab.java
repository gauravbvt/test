package com.mindalliance.channels.playbook.pages.forms.tabs.informationAct;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.panels.TimingPanel;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.query.Query;
import com.mindalliance.channels.playbook.ref.Ref;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 9, 2008
 * Time: 1:24:16 PM
 */
public class InformationActBasicTab extends AbstractFormTab {

    protected TextArea descriptionField;
    protected TimingPanel durationPanel;
    protected DynamicFilterTree resourceTree;
    protected DynamicFilterTree causeTree;
    protected TimingPanel delayPanel;

    public InformationActBasicTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        descriptionField = new TextArea("description", new RefPropertyModel(getElement(), "description"));
        addInputField(descriptionField);
        durationPanel = new TimingPanel("duration", this, "duration", EDITABLE, feedback);
        addReplaceable(durationPanel);
        resourceTree = new DynamicFilterTree("resource", new RefPropertyModel(getElement(), "resource"),
                                             new RefQueryModel(getScope(), new Query("findAllTypes", "Resource")),
                                             SINGLE_SELECTION) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                Ref selectedResource = resourceTree.getNewSelection();
                RefUtils.set(getElement(), "resource", selectedResource);
            }
        };
        addReplaceable(resourceTree);
        causeTree = new DynamicFilterTree("causeInformationAct", new RefPropertyModel(getElement(), "causeInformationAct"),
                                           new RefQueryModel(getScope(), new Query("findCandidateCauses", getElement())),
                                           SINGLE_SELECTION) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                 Ref selectedAct = causeTree.getNewSelection();
                 RefUtils.set(getElement(), "causeInformationAct", selectedAct);
             }
        };
        addReplaceable(causeTree);
        delayPanel = new TimingPanel("delay", this, "delay", EDITABLE, feedback);
        addReplaceable(delayPanel);
    }
}
