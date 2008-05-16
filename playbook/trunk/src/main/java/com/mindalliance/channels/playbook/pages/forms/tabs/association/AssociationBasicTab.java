package com.mindalliance.channels.playbook.pages.forms.tabs.association;

import com.mindalliance.channels.playbook.pages.forms.tabs.informationAct.InformationActBasicTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.support.components.AutoCompleteTextFieldWithChoices;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.query.Query;
import com.mindalliance.channels.playbook.ifm.Channels;
import com.mindalliance.channels.playbook.ref.Ref;
import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 15, 2008
 * Time: 9:26:25 PM
 */
public class AssociationBasicTab extends InformationActBasicTab {

    AutoCompleteTextFieldWithChoices relationshipNameField;
    DynamicFilterTree toAgentTree;

    public AssociationBasicTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        relationshipNameField = new AutoCompleteTextFieldWithChoices("relationshipName",
                new RefPropertyModel(getElement(), "relationshipName"),
                new RefQueryModel(Channels.instance(),
                        new Query("findAllRelationshipNames")));
        addInputField(relationshipNameField);
        toAgentTree = new DynamicFilterTree("toAgent", new RefPropertyModel(getElement(), "toAgent"),
                new RefQueryModel(getProject(),
                        new Query("findAllAgentsExcept", RefUtils.get(getElement(), "actorAgent"))),
                SINGLE_SELECTION) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                Ref selectedResource = toAgentTree.getNewSelection();
                RefUtils.set(getElement(), "toAgent", selectedResource);
            }
        };
        addReplaceable(toAgentTree);
    }
}
