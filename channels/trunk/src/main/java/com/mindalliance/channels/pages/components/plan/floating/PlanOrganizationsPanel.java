/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.pages.components.plan.floating;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.GeoLocatable;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.components.AbstractFloatingMultiAspectPanel;
import com.mindalliance.channels.pages.components.entities.EntitiesPanel;
import com.mindalliance.channels.pages.components.menus.MenuPanel;
import com.mindalliance.channels.pages.components.plan.PlanOrganizationScopePanel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.Arrays;
import java.util.List;

/**
 * Plan organizations panel.
 */
public class PlanOrganizationsPanel extends AbstractFloatingMultiAspectPanel {

    private static final String PREFIX_DOM_IDENTIFIER = ".entities";

    public static final String SCOPE = "Scope";
    public static final String NETWORK = "Network";

    private static final String[] ASPECTS = {SCOPE, NETWORK};
    private static final String[] ACTIONABLE_ASPECTS = {SCOPE};


    public PlanOrganizationsPanel( String id, IModel<? extends Identifiable> model ) {
        super( id, model, null, SCOPE );
    }

    public PlanOrganizationsPanel( String id, IModel<? extends Identifiable> model, String aspect ) {
        super( id, model, null, aspect );
    }

/*
    @Override
    public void redisplay( AjaxRequestTarget target ) {
        init();
        super.redisplay( target );
    }
*/

    @Override

    protected long getTabIdentifiableId() {
        return Channels.ALL_ORGANIZATIONS;
    }

    @Override
    protected String getCssClass() {
        return "organization_scope";
    }


    @Override
    protected List<String> getAllAspects() {
        return Arrays.asList( ASPECTS );
    }

    @Override
    protected List<String> getActionableAspects() {
        return Arrays.asList( ACTIONABLE_ASPECTS );
    }

    @Override
    protected MenuPanel makeActionMenu( String menuId ) {
        return null;
    }

    @Override
    protected String getMapTitle() {
        return "Locations of organizations in scope";
    }

    @Override
    protected List<? extends GeoLocatable> getGeoLocatables() {
        return getActualOrganizations();
    }

    @Override
    protected String getTitle() {
        return "Organizations in scope";
    }

    @Override
    protected Component makeAspectPanel( String aspect, Change change ) {
        if ( aspect.equals( NETWORK ) ) {
            return new EntitiesPanel<Organization>( "aspect",
                    Organization.class,
                    null,
                    getExpansions(),
                    PREFIX_DOM_IDENTIFIER );
        } else {
            return new PlanOrganizationScopePanel( "aspect", new Model<Plan>( getPlan() ), getExpansions() );
        }
    }

    @Override
    protected void doClose( AjaxRequestTarget target ) {
        Change change = new Change( Change.Type.Collapsed, Channels.ALL_ORGANIZATIONS );
        update( target, change );
    }

    @Override
    protected PathIcon getSurveysPathIcon( String id ) {
        return null;
    }

    @Override
    protected PathIcon getIssuesPathIcon( String id ) {
        return null;
    }

    /**
     * Get network's domain.
     *
     * @return a list or organizations
     */
    @SuppressWarnings( "unchecked" )
    public List<Organization> getActualOrganizations() {
        return (List<Organization>) CollectionUtils.select( getQueryService().listActualEntities( Organization.class ),
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        return !( (Organization) object ).isUnknown();
                    }
                } );
    }

}
