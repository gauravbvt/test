package com.mindalliance.channels.pages.components.entities;

import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.Identifiable;
import com.mindalliance.channels.ResourceSpec;
import com.mindalliance.channels.ModelObject;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.util.Set;

/**
 * Entity flows panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 26, 2009
 * Time: 2:22:21 PM
 */
public class EntityFlowsPanel extends AbstractCommandablePanel {

    public EntityFlowsPanel( String id, IModel<? extends Identifiable> iModel, Set<Long> expansions ) {
        super( id, iModel, expansions );
        init( expansions );
    }

    private void init( Set<Long> expansions ) {
        PlaybookPanel playbookPanel = new PlaybookPanel(
                "flows",
                new PropertyModel<ResourceSpec>( this, "resourceSpec"),
                20,
                expansions);
        add( playbookPanel );
    }

    public ResourceSpec getResourceSpec() {
        return ResourceSpec.with( (ModelObject) getModel().getObject() );
    }
}
