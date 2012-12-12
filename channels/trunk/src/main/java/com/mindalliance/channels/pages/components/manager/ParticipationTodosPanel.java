package com.mindalliance.channels.pages.components.manager;

import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import org.apache.wicket.model.IModel;

/**
 * Participation todos panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/4/12
 * Time: 11:07 AM
 */
public class ParticipationTodosPanel extends AbstractUpdatablePanel {

    public ParticipationTodosPanel( String id, IModel<? extends Identifiable> model ) {
        super( id, model );
        init();
    }

    private void init() {
        // todo
    }
}
