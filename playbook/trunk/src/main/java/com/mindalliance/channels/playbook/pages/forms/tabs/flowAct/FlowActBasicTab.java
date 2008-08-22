package com.mindalliance.channels.playbook.pages.forms.tabs.flowAct;

import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.tabs.informationAct.InformationActBasicTab;
import com.mindalliance.channels.playbook.query.Query;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 9, 2008
 * Time: 8:40:30 PM
 */
public class FlowActBasicTab extends InformationActBasicTab {

    private DynamicFilterTree targetAgentTree;
    private static final long serialVersionUID = -3370326716881724968L;

    public FlowActBasicTab( String id, AbstractElementForm elementForm ) {
        super( id, elementForm );
    }

    @Override
    protected void load() {
        super.load();
        targetAgentTree = new DynamicFilterTree(
                "targetAgent",
                new RefPropertyModel( getElement(), "targetAgent" ),
                new RefQueryModel(
                        getPlaybook(), new Query(
                        "findAllAgentsExcept", getElement(), "actors" ) ),
                SINGLE_SELECTION ) {
            private static final long serialVersionUID = -2462883153913078647L;

            @Override
            public void onFilterSelect(
                    AjaxRequestTarget target, Filter filter ) {
                setProperty( "targetAgent", targetAgentTree.getNewSelection() );
            }
        };
        addReplaceable( targetAgentTree );
    }
}
