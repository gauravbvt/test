package com.mindalliance.channels.pages.profiles;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.PageParameters;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.LoggerFactory;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.Dao;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.Actor;
import com.mindalliance.channels.Role;
import com.mindalliance.channels.Organization;
import com.mindalliance.channels.Jurisdiction;
import com.mindalliance.channels.ResourceSpec;
import com.mindalliance.channels.pages.components.ResourceProfilePanel;
import com.mindalliance.channels.pages.Project;
import com.mindalliance.channels.pages.ModelObjectLink;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 14, 2009
 * Time: 12:24:02 PM
 */
public class ResourceSpecPage extends WebPage {

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

    public ResourceSpecPage( PageParameters parameters ) {
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
        ResourceSpec resource = makeResource( params );
        add( new Label( "title", new Model<String>( "Resource: " + resource.getName() ) ) );
        add( new Label( "resource-name", new Model<String>( resource.getName() ) ) );
        WebMarkupContainer actorRow = new WebMarkupContainer("actor");
        add(actorRow);
        actorRow.add( new ModelObjectLink( "actor-link",                                           // NON-NLS
                                 new PropertyModel<Actor>( resource, "actor" ) ) );
        actorRow.add( new Label( "actor-name", new PropertyModel<String>( resource, "actor")));
        actorRow.setVisible( !resource.isAnyActor() );
        WebMarkupContainer roleRow = new WebMarkupContainer("role");
        add(roleRow);
        roleRow.add( new ModelObjectLink( "role-link",                                            // NON-NLS
                                 new PropertyModel<Role>( resource, "role" ) ) );
        roleRow.add( new Label( "role-name", new PropertyModel<String>( resource, "role")));
        roleRow.setVisible( !resource.isAnyRole() );
        WebMarkupContainer organizationRow = new WebMarkupContainer("organization");
        add(organizationRow);
        organizationRow.add( new ModelObjectLink( "org-link",                                             // NON-NLS
                                 new PropertyModel<Organization>( resource, "organization" ) ) );
        organizationRow.add( new Label( "organization-name", new PropertyModel<String>( resource, "organization")));
        organizationRow.setVisible( !resource.isAnyOrganization() );
        WebMarkupContainer jurisdictionRow = new WebMarkupContainer("jurisdiction");
        add(jurisdictionRow);
        jurisdictionRow.add( new ModelObjectLink( "juris-link",                                           // NON-NLS
                                 new PropertyModel<Jurisdiction>( resource, "jurisdiction" ) ) );
        jurisdictionRow.add( new Label( "jurisdiction-name", new PropertyModel<String>( resource, "jurisdiction")));
        jurisdictionRow.setVisible( !resource.isAnyJurisdiction() );
        add( new ResourceProfilePanel( "profile", new Model<ResourceSpec>( resource ) ) );
    }

    private ResourceSpec makeResource( PageParameters params ) throws NotFoundException {
        ResourceSpec resource;
        Dao dao = Project.getProject().getDao();
        if ( params.containsKey( SCENARIO_PARM ) && params.containsKey( PART_PARM )) {
            Scenario scenario = dao.findScenario( params.getLong( SCENARIO_PARM ) );
            Node node = scenario.getNode( params.getLong( PART_PARM ));
            if ( !node.isPart() ) throw new NotFoundException();
            resource = ((Part)node).resourceSpec();
        }
        else {
            resource = new ResourceSpec();
            if ( params.containsKey( ACTOR_PARM ) ) {
                Actor actor = dao.findActor( params.getLong( ACTOR_PARM ) );
                resource.setActor( actor );
            }
            if ( params.containsKey( ROLE_PARM ) ) {
                Role role = dao.findRole( params.getLong( ROLE_PARM ) );
                resource.setRole( role );
            }
            if ( params.containsKey( ORGANIZATION_PARM ) ) {
                Organization organization = dao.findOrganization( params.getLong( ORGANIZATION_PARM ) );
                resource.setOrganization( organization );
            }
            if ( params.containsKey( JURISDICTION_PARM ) ) {
                Jurisdiction jurisdiction = dao.findJurisdiction( params.getLong( JURISDICTION_PARM ) );
                resource.setJurisdiction( jurisdiction );
            }
            if ( resource.isEmpty() ) throw new NotFoundException();
        }
        return resource;
    }
}
