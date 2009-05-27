package com.mindalliance.channels.pages.components.entities;

import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import org.apache.wicket.model.IModel;

import java.util.Set;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 27, 2009
 * Time: 2:29:36 PM
 */
public class OrgChartPanel extends AbstractUpdatablePanel {

    public OrgChartPanel( String id, IModel<? extends Identifiable> model, Set<Long> expansions ) {
        super( id, model, expansions );
    }
}
