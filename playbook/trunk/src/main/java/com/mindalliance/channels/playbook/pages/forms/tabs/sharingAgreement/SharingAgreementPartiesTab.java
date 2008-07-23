package com.mindalliance.channels.playbook.pages.forms.tabs.sharingAgreement;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.query.Query;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.TextArea;

import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 14, 2008
 * Time: 2:01:12 PM
 */
public class SharingAgreementPartiesTab extends AbstractFormTab {

    protected TextArea descriptionField;
    protected DynamicFilterTree sourceTree;
    protected DynamicFilterTree recipientTree;

    public SharingAgreementPartiesTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        descriptionField = new TextArea("description", new RefPropertyModel(getElement(), "description"));
        addInputField(descriptionField);
        CheckBox formalizedCheckBox = new CheckBox("formalized", new RefPropertyModel(getElement(), "formalized"));
        formalizedCheckBox.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            protected void onUpdate(AjaxRequestTarget target) {
                // Do nothing more
            }
        });
        addReplaceable(formalizedCheckBox);
        sourceTree = new DynamicFilterTree("source", new RefPropertyModel(getElement(), "source"),
                                            new RefQueryModel(getProject(), new Query("findAllAgentsExcept", getElement(), "recipient")), SINGLE_SELECTION) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                Ref selectedSource = sourceTree.getNewSelection();
                if (selectedSource != null) setProperty("source", selectedSource);
            }
        };
        addReplaceable(sourceTree);
        recipientTree = new DynamicFilterTree("recipient", new RefPropertyModel(getElement(), "recipient"),
                                            new RefQueryModel(getProject(), new Query("findAllAgentsExcept", getElement(), "source")),
                                            SINGLE_SELECTION) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                Ref selectedRecipient = recipientTree.getNewSelection();
                if (selectedRecipient != null) setProperty("recipient", selectedRecipient);
            }
        };
        addReplaceable(recipientTree);
    }
}
