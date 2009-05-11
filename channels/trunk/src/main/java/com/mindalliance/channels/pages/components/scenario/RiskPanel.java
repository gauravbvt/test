package com.mindalliance.channels.pages.components.scenario;

import com.mindalliance.channels.model.Risk;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * Risk panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 8, 2009
 * Time: 3:33:08 PM
 */
public class RiskPanel extends Panel {

    public RiskPanel( String id, IModel<Risk> iModel ) {
        super( id );
    }
}
