package com.mindalliance.channels.playbook.pages.forms.tabs.organizationType;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.panels.AreaInfoPanel;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.query.Query;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ifm.model.OrganizationType;
import com.mindalliance.channels.playbook.ifm.info.AreaInfo;
import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 5, 2008
 * Time: 4:12:56 PM
 */
public class OrganizationTypeScopeTab extends AbstractFormTab {

    protected DynamicFilterTree domainTree;

    public OrganizationTypeScopeTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        domainTree = new DynamicFilterTree("domain", new RefPropertyModel(getElement(), "domain"),
                new RefQueryModel(getPlaybookModel(), new Query("findAllTypes", "Domain")), SINGLE_SELECTION) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                Ref selectedDomain = domainTree.getNewSelection();
                RefUtils.set(getElement(), "domain", selectedDomain);
            }
        };
        addReplaceable(domainTree);
    }
}
