package com.mindalliance.channels.pages.components.entities;

import org.apache.wicket.model.IModel;
import com.mindalliance.channels.pages.components.AbstractTablePanel;
import com.mindalliance.channels.Identifiable;

import java.util.Set;

/**
 * A table showing an organiztion's jobs, confirmed or not. 
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 19, 2009
 * Time: 10:12:09 AM
 */
public class JobsTablePanel extends AbstractTablePanel {

    public JobsTablePanel( String s, IModel<? extends Identifiable> iModel, int pageSize, Set<Long> expansions ) {
        super( s, iModel, pageSize, expansions );
    }

    public JobsTablePanel( String s, IModel<? extends Identifiable> iModel, Set<Long> expansions ) {
        super( s, iModel, expansions );
    }
}
