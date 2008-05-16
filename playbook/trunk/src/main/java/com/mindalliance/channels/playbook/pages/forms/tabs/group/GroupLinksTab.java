package com.mindalliance.channels.playbook.pages.forms.tabs.group;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.panels.MultipleStringChooser;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.query.Query;
import com.mindalliance.channels.playbook.ref.Ref;
import org.apache.wicket.ajax.AjaxRequestTarget;

import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 15, 2008
 * Time: 4:10:47 PM
 */
public class GroupLinksTab extends AbstractFormTab {

    protected MultipleStringChooser relationshipNamesChooser;
    protected DynamicFilterTree resourceTree;

    public GroupLinksTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        relationshipNamesChooser = new MultipleStringChooser("relationshipNames", this, "relationshipNames", EDITABLE, feedback,
                                                             new RefQueryModel(getProject(), new Query("findAllResources")));
        addReplaceable(relationshipNamesChooser);
        resourceTree = new DynamicFilterTree("relatedResource", new RefPropertyModel(getElement(), "relatedResource"),
                                           new RefQueryModel(getProject(), new Query("findAllResources")),
                                           SINGLE_SELECTION) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                Ref selected = resourceTree.getNewSelection();
                RefUtils.set(getElement(), "relatedResource", selected);
            }
        };
        addReplaceable(resourceTree);        
    }
}
