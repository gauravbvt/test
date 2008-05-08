package com.mindalliance.channels.playbook.pages.forms.tabs.person;

import com.mindalliance.channels.playbook.pages.forms.tabs.resource.ResourceResponsibilitiesTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.query.Query;
import org.apache.wicket.ajax.AjaxRequestTarget;

import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 24, 2008
 * Time: 3:26:52 PM
 */
public class PersonResponsibilitiesTab  extends ResourceResponsibilitiesTab {

    DynamicFilterTree positionTree;

    public PersonResponsibilitiesTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        positionTree = new DynamicFilterTree("positions", new RefPropertyModel(getElement(), "positions"),
                                             new RefQueryModel(getProject(), new Query("findAlPositions"))) {   // TODO
             public void onFilterSelect( AjaxRequestTarget target, Filter filter ) {
                List<Ref> newSelections = positionTree.getNewSelections();
                RefUtils.set(getElement(), "positions", newSelections);
             }
        };
        addReplaceable(positionTree);
     }
}
