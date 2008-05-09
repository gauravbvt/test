package com.mindalliance.channels.playbook.pages.forms.tabs.purposeType;

import com.mindalliance.channels.playbook.pages.forms.tabs.elementType.ElementTypeBasicTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
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
 * Time: 4:20:41 PM
 */
public class PurposeTypeBasicTab extends ElementTypeBasicTab {

    protected DynamicFilterTree domainTree;

    public PurposeTypeBasicTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        domainTree = new DynamicFilterTree("domain", new RefPropertyModel(getElement(), "domain"),
                                           new RefQueryModel(getScope(), new Query("findAllTypes", "Domain")),
                                           SINGLE_SELECTION) {
            public void onFilterSelect( AjaxRequestTarget target, Filter filter ) {
                Ref selectedDomain = domainTree.getNewSelection();
                RefUtils.set(getElement(), "domain", selectedDomain);
            }
        };
        addReplaceable(domainTree);
    }
}
