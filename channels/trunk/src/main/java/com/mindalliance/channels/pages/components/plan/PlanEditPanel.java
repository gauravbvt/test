package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractMultiAspectPanel;
import com.mindalliance.channels.pages.components.menus.MenuPanel;
import com.mindalliance.channels.pages.components.plan.menus.PlanEditActionsMenuPanel;
import com.mindalliance.channels.pages.components.plan.menus.PlanEditShowMenuPanel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

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
public class PlanEditPanel extends AbstractMultiAspectPanel {


    /**
     * Actionable aspects.
     */
    private static final String[] ACTIONABLE_ASPECTS = {DETAILS};


    public PlanEditPanel( String id, IModel<? extends Identifiable> iModel, Set<Long> expansions ) {
        super( id, iModel, expansions );
    }

    public PlanEditPanel( String id, IModel<? extends Identifiable> iModel,
                          Set<Long> expansions, String aspect, Change change ) {
        super( id, iModel, expansions, aspect, change );
    }

/*
    protected void annotateHeaderTitle( ModelObject object, Analyst analyst ) {
        // Show no issue indicator - too costly! --todo : review
    }
*/

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getDefaultAspect() {
        return DETAILS;
    }

    protected int getWidth() {
        return 930;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getCssClass() {
        return "plan";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getObjectClassName() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected MenuPanel makeShowMenu( String menuId ) {
        PlanEditShowMenuPanel showMenu = new PlanEditShowMenuPanel(
                menuId,
                new PropertyModel<ModelObject>( this, "object" ) );
        showMenu.setPlanEditPanel( this );
        return showMenu;
    }

    @Override
    protected boolean isAspectShownEditable() {
        return Arrays.asList( ACTIONABLE_ASPECTS ).contains( getAspectShown() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int getMaxTitleNameLength() {
        return 25;
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
         }else {
            // Should never happen
            throw new RuntimeException( "Unknown aspect " + aspect );
        }
    }

     private Component getPlanDetailsPanel() {
        return new PlanEditDetailsPanel( "aspect", getModel(), getExpansions() );
    }

    public Plan getPlan() {
        return (Plan) getModel().getObject();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        if ( change.isForInstanceOf( Issue.class )
                && change.getSubject( getQueryService() ) instanceof Plan )
            setAspectShown( target, DETAILS );
        if ( !( change.isSelected() && ( change.isForInstanceOf( Plan.class ) ) ) ) {
            super.updateWith( target, change, updated );
        }
    }

}
