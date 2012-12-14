package com.mindalliance.channels.pages.components.manager;

import com.mindalliance.channels.core.community.participation.Agency;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import org.apache.wicket.model.IModel;

/**
 * Agency profile panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/13/12
 * Time: 10:54 AM
 */
public class  AgencyProfilePanel extends AbstractUpdatablePanel {
    public AgencyProfilePanel( String id, IModel<Agency> agencyModel ) {
        super( id, agencyModel );
        init();
    }

    private void init() {
        // todo
    }
}
