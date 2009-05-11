package com.mindalliance.channels.pages.components.scenario;

import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.pages.components.AbstractMultiAspectPanel;
import com.mindalliance.channels.pages.components.menus.MenuPanel;
import com.mindalliance.channels.pages.components.scenario.menus.ScenarioActionsMenuPanel;
import com.mindalliance.channels.pages.components.scenario.menus.ScenarioShowMenuPanel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.util.Set;

/**
 * Scenario edit panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 8, 2009
 * Time: 11:14:14 AM
 */
public class ScenarioEditPanel extends AbstractMultiAspectPanel {

    /**
     * Incidents aspect.
     */
    public static final String RISKS = "risks";

    public ScenarioEditPanel( String id, IModel<? extends Identifiable> model, Set<Long> expansions ) {
        super( id, model, expansions );
    }

    public ScenarioEditPanel( String id, IModel<? extends Identifiable> model, Set<Long> expansions, String aspect ) {
        super( id, model, expansions, aspect );
    }

    protected String getDefaultAspect() {
        return DETAILS;
    }

    protected boolean objectNeedsLocking() {
        return false;
    }

    protected int getMaxTitleNameLength() {
        return 20;
    }

    protected String getObjectClassName() {
        return null;
    }

    protected String getCssClass() {
        return "scenario";  //To change body of implemented methods use File | Settings | File Templates.
    }

    protected MenuPanel makeShowMenu( String menuId ) {
        ScenarioShowMenuPanel showMenu = new ScenarioShowMenuPanel(
                menuId,
                new PropertyModel<Scenario>( this, "scenario" ),
                getExpansions() );
        showMenu.setScenarioEditPanel( this );
        return showMenu;
    }

    protected MenuPanel makeActionMenu( String menuId ) {
        return new ScenarioActionsMenuPanel(
                menuId,
                new PropertyModel<ModelObject>( this, "scenario" ),
                getExpansions() );
    }


    protected Component makeAspectPanel( String aspect ) {
        if ( aspect.equals( DETAILS ) ) {
            return getScenarioDetailsPanel();
        } else if ( aspect.equals( RISKS ) ) {
            return getScenarioRisksPanel();
        } else {
            // Should never happen
            throw new RuntimeException( "Unknown aspect " + aspect );
        }
    }

    private Component getScenarioDetailsPanel() {
        return new ScenarioEditDetailsPanel( "aspect", getModel(), getExpansions() );
    }

    private Component getScenarioRisksPanel() {
        return new RiskListPanel(
                "aspect", 
                new PropertyModel<Scenario>(this, "scenario"),
                getExpansions() );
    }

    /**
     * Get scenario from model.
     *
     * @return a scenario
     */
    public Scenario getScenario() {
        return (Scenario) getModel().getObject();
    }

    /**
     * Change visibility.
     *
     * @param target  an ajax request target
     * @param visible a boolean
     */
    public void setVisibility( AjaxRequestTarget target, boolean visible ) {
        makeVisible( target, this, visible );
        /*if ( visible )
            makeVisible( issuesPanel, getAnalyst().hasIssues( model.getObject(), false ) );*/
    }

}
