package com.mindalliance.channels.pages.components.segment;

import com.mindalliance.channels.analysis.graph.EntityRelationship;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.GeomapLinkPanel;
import com.mindalliance.channels.pages.components.entities.EntitiesPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * panel showing organizations and relationships involved in a segment.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 8, 2010
 * Time: 11:33:33 AM
 */
public class SegmentOrganizationsPanel extends AbstractUpdatablePanel {
    /**
     * DOM identifier prefix for containment of diagram.
     */
    private static final String PREFIX_DOM_IDENTIFIER = ".segment-organizations";
    /**
     * Segment model.
     */
    private IModel<Segment> segmentModel;

    public SegmentOrganizationsPanel( String id, IModel<Segment> segmentModel, Set<Long> expansions ) {
        super(id, segmentModel, expansions);
        this.segmentModel = segmentModel;
        init();
    }

    private void init() {
        addGeomapLink();
        addEntitiesPanel();
    }

    private void addGeomapLink() {
        GeomapLinkPanel geomapLink = new GeomapLinkPanel(
                "geomapLink",
                new Model<String>( "Organizations with known locations" ),
                getActualOrganizations(),
                new Model<String>( "Locate organizations on a map" ) );
        geomapLink.setOutputMarkupId( true );
        makeVisible( geomapLink, !getActualOrganizations().isEmpty() );
        addOrReplace( geomapLink );
    }

    private void addEntitiesPanel() {
        EntitiesPanel<Organization> entitiesPanel = new EntitiesPanel<Organization>(
                        "organizations",
                        Organization.class,
                        getSegment(),
                        getExpansions(),
                        PREFIX_DOM_IDENTIFIER );
        add( entitiesPanel );
    }

    /**
     * Get network's domain.
     *
     * @return a list or organizations
     */
    @SuppressWarnings( "unchecked" )
    public List<Organization> getActualOrganizations() {
        return getQueryService().listActualEntitiesTaskedInSegment(
                Organization.class,
                getSegment() );
    }

    /**
     * Get relationships defining the network.
     *
     * @return a list of (sharing flow) relationships between organizations
     */
    public List<EntityRelationship<Organization>> getOrganizationRelationships() {
        List<EntityRelationship<Organization>> orgRels = new ArrayList<EntityRelationship<Organization>>();
        List<Organization> orgs = getActualOrganizations();
        for ( Organization org : orgs ) {
            for ( Organization other : orgs ) {
                if ( org != other ) {
                    EntityRelationship<Organization> sendRel =
                            getQueryService().findEntityRelationship( org, other, getSegment() );
                    if ( sendRel != null ) orgRels.add( sendRel );
                }
            }
        }
        return orgRels;
    }

    private Segment getSegment() {
        return segmentModel.getObject();
    }

}
