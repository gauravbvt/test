package com.mindalliance.channels.playbook.pages.forms.tabs.taskType;

import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.panels.MultipleStringChooser;
import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.query.Query;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved. Proprietary
 * and Confidential. User: jf Date: May 5, 2008 Time: 8:23:13 PM
 */
public class TaskTypeIntentTab extends AbstractFormTab {

    private DynamicFilterTree eventTypesTree = null;
    private static final long serialVersionUID = -3691453910834696507L;

    public TaskTypeIntentTab( String id, AbstractElementForm elementForm ) {
        super( id, elementForm );
    }

    @Override
    protected void load() {
        super.load();
        addReplaceable(
                new MultipleStringChooser(
                        "purposes", this, "purposes",
                        new RefQueryModel(
                            getScope(), new Query( "findAllPurposes" ) ) ) );
        eventTypesTree = new DynamicFilterTree(
                "eventTypes",
                new RefPropertyModel( getElement(), "eventTypes" ),
                new RefQueryModel(
                        getScope(),
                        new Query( "findAllTypes", "EventType" ) ) ) {
            private static final long serialVersionUID = -3296320861876097150L;

            @Override
            public void onFilterSelect(
                    AjaxRequestTarget target, Filter filter ) {
                setProperty( "eventTypes", eventTypesTree.getNewSelections() );
            }
        };
        addReplaceable( eventTypesTree );
    }
}

