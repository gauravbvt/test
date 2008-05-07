package com.mindalliance.channels.playbook.pages.forms.tabs.organizationType;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractModelElementFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.panels.LocationPanel;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.query.Query;
import com.mindalliance.channels.playbook.ref.Ref;
import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 5, 2008
 * Time: 4:12:56 PM
 */
public class OrganizationTypeScopeTab extends AbstractModelElementFormTab {

    protected DynamicFilterTree domainTree;
    protected DynamicFilterTree jurisdictionTypeTree;
    protected LocationPanel withinPanel;

    public OrganizationTypeScopeTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        domainTree = new DynamicFilterTree("domain", new RefPropertyModel(getElement(), "domain"),
                new RefQueryModel(getIfmModel(), new Query("findAllTypes", "Domain")), SINGLE_SELECTION) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                Ref selectedDomain = domainTree.getNewSelection();
                RefUtils.set(getElement(), "domain", selectedDomain);
            }
        };
        addReplaceable(domainTree);
        jurisdictionTypeTree = new DynamicFilterTree("jurisdictionType", new RefPropertyModel(getElement(), "jurisdictionType"),
                new RefQueryModel(getIfmModel(), new Query("findAllTypes", "JurisdictionType")), SINGLE_SELECTION) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                Ref selectedType = jurisdictionTypeTree.getNewSelection();
                RefUtils.set(getElement(), "jurisdictionTyp", selectedType);
            }
        };
        addReplaceable(domainTree);
        withinPanel = new LocationPanel("within", this, "within", false, feedback);
        addReplaceable(withinPanel);
    }
}
