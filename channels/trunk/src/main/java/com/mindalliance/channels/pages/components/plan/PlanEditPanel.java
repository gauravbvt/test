package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.GeoLocatable;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractFloatingMultiAspectPanel;
import com.mindalliance.channels.pages.components.menus.MenuPanel;
import com.mindalliance.channels.pages.components.plan.menus.PlanEditActionsMenuPanel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Plan edit panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 6, 2009
 * Time: 5:08:07 PM
 */
public class PlanEditPanel extends AbstractFloatingMultiAspectPanel {


    /**
     * Aspects.
     */
    private static final String[] ASPECTS = {DETAILS};


    public PlanEditPanel( String id, IModel<? extends Identifiable> iModel, Set<Long> expansions ) {
        super( id, iModel, expansions );
    }

    public PlanEditPanel( String id, IModel<? extends Identifiable> iModel,
                          Set<Long> expansions, String aspect, Change change ) {
        super( id, iModel, expansions, aspect, change );
    }

    @Override
    protected List<? extends GeoLocatable> getGeoLocatables() {
        List<GeoLocatable> geoLocatables = new ArrayList<GeoLocatable>(  );
        Place planLocale = getPlan().getLocale();
        if ( planLocale != null && planLocale.isActual() && !planLocale.isUnknown() ) {
            geoLocatables.add( planLocale );
        }
        return geoLocatables;
    }

    @Override
    protected String getMapTitle() {
        return "The locale of the model";
    }

    @Override
    protected List<String> getAllAspects() {
        return Arrays.asList( ASPECTS );
    }

    @Override
    protected List<String> getActionableAspects() {
        return Arrays.asList( ASPECTS );
    }

    @Override
    protected int getWidth() {
        return 930;
    }

    @Override
    protected String getCssClass() {
        return "plan";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected MenuPanel makeActionMenu( String menuId ) {
        return new PlanEditActionsMenuPanel(
                menuId,
                new PropertyModel<ModelObject>( this, "object" ));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Component makeAspectPanel( String aspect, Change change ) {
        if ( aspect.equals( DETAILS ) ) {
            return getPlanDetailsPanel();
         } else {
            // Should never happen
            throw new RuntimeException( "Unknown aspect " + aspect );
        }
    }

     private Component getPlanDetailsPanel() {
        return new PlanEditDetailsPanel( "aspect", getModel(), getExpansions() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        if ( change.isForInstanceOf( Issue.class )
                && change.getSubject( getCommunityService() ) instanceof Plan )
            setAspectShown( target, DETAILS );
        if ( !( change.isSelected() && ( change.isForInstanceOf( Plan.class ) ) ) ) {
            super.updateWith( target, change, updated );
        }
    }

}
