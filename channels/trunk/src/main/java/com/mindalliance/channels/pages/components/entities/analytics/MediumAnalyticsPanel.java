package com.mindalliance.channels.pages.components.entities.analytics;

import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import org.apache.wicket.model.IModel;

import java.util.Set;

/**
 * Transmission medium analytics panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/6/12
 * Time: 1:37 PM
 */
public class MediumAnalyticsPanel extends AbstractUpdatablePanel {
    public MediumAnalyticsPanel( String id, IModel<ModelEntity> model, Set<Long> expansions ) {
        super( id, model, expansions );
        init();
    }

    private void init() {
        //todo
    }
}
