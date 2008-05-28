package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.ifm.spec.Spec;
import com.mindalliance.channels.playbook.ifm.spec.ResourceSpec;
import com.mindalliance.channels.playbook.pages.forms.ElementPanel;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.query.Query;
import com.mindalliance.channels.playbook.ref.Ref;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;

import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 28, 2008
 * Time: 7:38:23 AM
 */
public class ResourceSpecPanel extends AbstractSpecComponentPanel {

    protected DynamicFilterTree rolesTree;
    protected DynamicFilterTree organizationTypesTree;

    public ResourceSpecPanel(String id, ElementPanel parentPanel, String propPath, boolean readOnly, FeedbackPanel feedback) {
        super(id, parentPanel, propPath, readOnly, feedback);
    }

    protected void load() {
        super.load();
        rolesTree = new DynamicFilterTree("roles", new RefPropertyModel(getComponent(), "roles"),
                                           new RefQueryModel(getScope(), new Query("findAllTypes", "Role"))) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                List<Ref> selected = rolesTree.getNewSelections();
                RefUtils.set(getComponent(), "roles", selected);
                elementChanged(propPath+".roles", target);
            }
        };
        addReplaceable(rolesTree);
        organizationTypesTree = new DynamicFilterTree("organizationTypes", new RefPropertyModel(getComponent(), "organizationTypes"),
                                           new RefQueryModel(getScope(), new Query("findAllTypes", "OrganizationType"))) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                List<Ref> selected = organizationTypesTree.getNewSelections();
                RefUtils.set(getComponent(), "organizationTypes", selected);
                elementChanged(propPath+".organizationTypes", target);
            }
        };
        addReplaceable(organizationTypesTree);
    }

    protected Spec makeNewSpec() {
        return new ResourceSpec();
    }

    @Override
    protected String getAnyLabelString() {
        return "any resource";
    }

}
