package com.mindalliance.channels.pages;

import com.mindalliance.channels.Actor;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.Organization;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Place;
import com.mindalliance.channels.ResourceSpec;
import com.mindalliance.channels.Role;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Service;
import com.mindalliance.channels.pages.components.ResourceProfilePanel;
import com.mindalliance.channels.pages.components.ResourceSpecPanel;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.model.Model;
import org.slf4j.LoggerFactory;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 14, 2009
 * Time: 12:24:02 PM
 */
public class ProfilePage extends WebPage {

    /**
     * A scenario's id URL parameter.
     */
    static final String SCENARIO_PARM = "scenario";                               // NON-NLS
    /**
     * A part's id URL parameter.
     */
    static final String PART_PARM = "part";                                       // NON-NLS
    /**
     * An actor's id URL parameter.
     */
    static final String ACTOR_PARM = "actor";                                     // NON-NLS
    /**
     * A role's id URL parameter.
     */
    static final String ROLE_PARM = "role";                                       // NON-NLS
    /**
     * An organization's id URL parameter.
     */
    static final String ORGANIZATION_PARM = "organization";                      // NON-NLS
    /**
     * A jurisdiction's id URL parameter.
     */
    static final String JURISDICTION_PARM = "jurisdiction";                      // NON-NLS

    public ProfilePage( PageParameters parameters ) {
        super( parameters );
        try {
            init( parameters );
        } catch ( NotFoundException e ) {
            LoggerFactory.getLogger( getClass() ).error( "Actor not found", e );
        }
    }

/*    public ResourcePage( Part part ) {
        super( new CompoundPropertyModel<Part>( part ) );

        add( new Label( "title" ) );                                                      // NON-NLS
    }   */

    private void init( PageParameters params ) throws NotFoundException {
        ResourceSpec resourceSpec = makeResource( params );
        add( new Label( "title", new Model<String>( "Profile: " + resourceSpec.getName() ) ) );
        add ( new ExternalLink("index", "index.html"));
        add( new Label( "resourceSpec-name", new Model<String>( resourceSpec.getName() ) ) );
        add( new ResourceSpecPanel( "resourceSpec", new Model<ResourceSpec>( resourceSpec ) ) );
        add( new ResourceProfilePanel( "profile", new Model<ResourceSpec>( resourceSpec ) ) );
    }

    private Service getService() {
        return ( (Project) getApplication() ).getService();
    }

    private ResourceSpec makeResource( PageParameters params ) throws NotFoundException {
        ResourceSpec resourceSpec;
        Service service = getService();
        if ( params.containsKey( SCENARIO_PARM ) && params.containsKey( PART_PARM ) ) {
            Scenario scenario = service.find( Scenario.class, params.getLong( SCENARIO_PARM ) );
            Node node = scenario.getNode( params.getLong( PART_PARM ) );
            if ( !node.isPart() ) throw new NotFoundException();
            resourceSpec = ( (Part) node ).resourceSpec();
        } else {
            resourceSpec = new ResourceSpec();
            if ( params.containsKey( ACTOR_PARM ) ) {
                Actor actor = service.find( Actor.class, params.getLong( ACTOR_PARM ) );
                resourceSpec.setActor( actor );
            }
            if ( params.containsKey( ROLE_PARM ) ) {
                Role role = service.find( Role.class, params.getLong( ROLE_PARM ) );
                resourceSpec.setRole( role );
            }
            if ( params.containsKey( ORGANIZATION_PARM ) ) {
                Organization organization = service.find(
                        Organization.class,
                        params.getLong( ORGANIZATION_PARM ) );
                resourceSpec.setOrganization( organization );
            }
            if ( params.containsKey( JURISDICTION_PARM ) ) {
                Place jurisdiction = service.find( Place.class, params.getLong( JURISDICTION_PARM ) );
                resourceSpec.setJurisdiction( jurisdiction );
            }
        }
        if ( resourceSpec.isEmpty() ) throw new NotFoundException();
        ResourceSpec permanentResourceSpec = service.findPermanentResourceSpec( resourceSpec );
        return permanentResourceSpec != null ? permanentResourceSpec : resourceSpec;
    }
}
