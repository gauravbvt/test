package com.mindalliance.channels.pages.components.entities;

import org.apache.wicket.model.IModel;
import com.mindalliance.channels.pages.components.AbstractTablePanel;

/**
 * A table showing an organiztion's jobs, confirmed or not. 
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 19, 2009
 * Time: 10:12:09 AM
 */
public class JobsTablePanel extends AbstractTablePanel {

    public JobsTablePanel( String s, IModel iModel, int pageSize ) {
        super( s, iModel, pageSize );
    }

    public JobsTablePanel( String s, IModel iModel ) {
        super( s, iModel );
    }
}
