package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

import java.util.Set;

/**
 * Plan participations panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 22, 2010
 * Time: 9:50:48 AM
 */
public class PlanParticipationPanel extends AbstractCommandablePanel {

    public PlanParticipationPanel( String id, IModel<? extends Identifiable> model, Set<Long> expansions ) {
        super( id, model, expansions );
        init();
    }

    @Override
    public void redisplay( AjaxRequestTarget target ) {
        init();
        super.redisplay( target );
    }



    private void init() {
        addParticipations();
    }

    private void addParticipations() {
        ParticipationsPanel participationsPanel = new ParticipationsPanel( "participations" );
        addOrReplace( participationsPanel );
    }

    /**
     * Get the plan being edited.
     *
     * @return a plan
     */
    public Plan getPlan() {
        return (Plan) getModel().getObject();
    }


}
