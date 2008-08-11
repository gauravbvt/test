package com.mindalliance.channels.playbook.pages.forms.tabs.sharingCommitment;

import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.tabs.flowAct.FlowActBasicTab;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 16, 2008
 * Time: 2:25:44 PM
 */
public class SharingCommitmentBasicTab extends FlowActBasicTab {

    private DynamicFilterTree approvedByTree;
    private static final long serialVersionUID = -1146865203210427229L;

    public SharingCommitmentBasicTab(
            String id, AbstractElementForm elementForm ) {
        super( id, elementForm );
    }

    @Override
    protected void load() {
        super.load();
        approvedByTree = new DynamicFilterTree(
                "approvedBy",
                new RefPropertyModel( getElement(), "approvedBy" ),
                new RefPropertyModel( getProject(), "positions" ),
                SINGLE_SELECTION ) {
            private static final long serialVersionUID = -3103659188760093080L;

            @Override
            public void onFilterSelect(
                    AjaxRequestTarget target, Filter filter ) {
                Object selected = approvedByTree.getNewSelection();
                setProperty( "approvedBy", selected );
            }
        };
        addReplaceable( approvedByTree );
    }
}
