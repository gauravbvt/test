package com.mindalliance.channels.pages.components.entities.hierarchy;

import com.mindalliance.channels.core.model.Hierarchical;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import org.apache.wicket.model.IModel;

import java.util.Set;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 8/8/13
 * Time: 5:13 PM
 */
public class ActorHierarchyPanel extends AbstractUpdatablePanel {

    private String prefixDomIdentifier;

    public ActorHierarchyPanel( String id,
                                IModel<Hierarchical> hierarchicalModel,
                               Set<Long> expansions,
                                String prefixDomIdentifier ) {
        super( id, hierarchicalModel, expansions );
        this.prefixDomIdentifier = prefixDomIdentifier;
        init();
    }

    private void init() {
        //Todo
    }
}
