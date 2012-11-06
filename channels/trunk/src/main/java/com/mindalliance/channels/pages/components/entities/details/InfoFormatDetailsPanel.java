package com.mindalliance.channels.pages.components.entities.details;

import com.mindalliance.channels.core.model.ModelEntity;
import org.apache.wicket.model.PropertyModel;

import java.util.Set;

/**
 * Info format details panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/1/12
 * Time: 1:26 PM
 */
public class InfoFormatDetailsPanel extends EntityDetailsPanel {


    public InfoFormatDetailsPanel( String id, PropertyModel<ModelEntity> model, Set<Long> expansions ) {
        super( id, model, expansions );
    }

    // Nothing specific.

}
