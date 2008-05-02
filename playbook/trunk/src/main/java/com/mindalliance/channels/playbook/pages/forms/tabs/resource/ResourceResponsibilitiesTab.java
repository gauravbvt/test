package com.mindalliance.channels.playbook.pages.forms.tabs.resource;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractProjectElementFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import org.apache.wicket.model.Model;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.tree.DefaultAbstractTree;

import java.util.List;
import java.io.Serializable;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 24, 2008
 * Time: 9:21:40 AM
 */
public class ResourceResponsibilitiesTab extends AbstractProjectElementFormTab {

    DynamicFilterTree roleTree;

    public ResourceResponsibilitiesTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        List<Ref> allRoles = getProject().findAllTypes("Role");
        roleTree = new DynamicFilterTree("roles", new RefPropertyModel(getElement(), "roles"), new Model((Serializable) allRoles)) {
             public void onFilterSelect( AjaxRequestTarget target, Filter filter ) {
                List<Ref> newSelections = roleTree.getNewSelections();
                RefUtils.set(getElement(), "roles", newSelections);
             }
        };
        roleTree.setLinkType( DefaultAbstractTree.LinkType.AJAX_FALLBACK );
        addReplaceable(roleTree);
    }
}
