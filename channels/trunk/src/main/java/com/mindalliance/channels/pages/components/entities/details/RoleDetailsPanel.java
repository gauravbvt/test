package com.mindalliance.channels.pages.components.entities.details;

import com.mindalliance.channels.core.model.ModelEntity;
import org.apache.wicket.model.IModel;

import java.util.Set;

/**
 * Role details panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 9, 2009
 * Time: 1:30:49 PM
 */
public class RoleDetailsPanel extends EntityDetailsPanel {

    public RoleDetailsPanel( String id, IModel<? extends ModelEntity> model, Set<Long> expansions ) {
        super( id, model, expansions );
    }

    // Nothing specific to role details.
}
