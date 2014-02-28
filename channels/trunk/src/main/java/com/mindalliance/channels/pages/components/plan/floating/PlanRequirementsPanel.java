package com.mindalliance.channels.pages.components.plan.floating;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.GeoLocatable;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.CollaborationModel;
import com.mindalliance.channels.core.model.Requirement;
import com.mindalliance.channels.pages.components.AbstractFloatingMultiAspectPanel;
import com.mindalliance.channels.pages.components.community.requirements.RequirementDefinitionsPanel;
import com.mindalliance.channels.pages.components.community.requirements.RequirementsAnalysisPanel;
import com.mindalliance.channels.pages.components.menus.MenuPanel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Collaboration requirements panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 9/29/11
 * Time: 1:54 PM
 */
public class PlanRequirementsPanel extends AbstractFloatingMultiAspectPanel {

    public static final String DEFINITIONS = "Definitions";
    public static final String SATISFACTION = "Satisfaction";

    private static final String[] ASPECTS = {DEFINITIONS, SATISFACTION};


    public PlanRequirementsPanel(
            String id,
            IModel<? extends Identifiable> model,
            Set<Long> expansions ) {
        super( id, model, expansions, DEFINITIONS );
    }

    public PlanRequirementsPanel(
            String id,
            IModel<? extends Identifiable> model,
            Set<Long> expansions,
            String aspect ) {
        super( id, model, expansions, aspect );
    }

    @Override
    protected List<String> getAllAspects() {
        return Arrays.asList( ASPECTS );
    }

    @Override
    protected List<String> getActionableAspects() {
        return new ArrayList<String>(  );  // requirement aspect panels take care of locking, unlocking
    }

    @Override
    protected Change getClosingChange() {
        return new Change( Change.Type.Collapsed, Requirement.UNKNOWN );
    }


    @Override
    public void redisplay( AjaxRequestTarget target ) {
        init();
        super.redisplay( target );
    }

    @Override
    protected String getCssClass() {
        return "requirements";
    }

    @Override
    protected MenuPanel makeActionMenu( String menuId ) {
        return null;
    }

    @Override
    protected Component makeAspectPanel( String aspect, Change change ) {
         if ( aspect.equals( SATISFACTION )) {
             return new RequirementsAnalysisPanel( "aspect", new Model<CollaborationModel>( getCollaborationModel() ), getExpansions() );
         } else {
             return new RequirementDefinitionsPanel(
                     "aspect",
                     new Model<Requirement>( getRequirement()),
                     getExpansions() );
         }
    }

    @Override
    protected String getMapTitle() {
        return "";
    }

    @Override
    protected List<? extends GeoLocatable> getGeoLocatables() {
        return new ArrayList<GeoLocatable>(  );
    }

    @Override
    protected String getTitle() {
        return "Information sharing requirements";
    }

    private Requirement getRequirement() {
        return (Requirement) getModel().getObject();
    }

    @Override
    protected Identifiable getTabChangeDefaultSubject() {
        return Requirement.UNKNOWN;
    }


}
