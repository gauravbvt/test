/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.pages.components.segment;

import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.engine.analysis.graph.EntityRelationship;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.GeomapLinkPanel;
import com.mindalliance.channels.pages.components.entities.EntitiesPanel;
import com.mindalliance.channels.pages.components.guide.Guidable;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Panel showing organizations and relationships involved in a segment.
 */
public class SegmentOrganizationsPanel extends AbstractUpdatablePanel implements Guidable {

    /**
     * DOM identifier prefix for containment of diagram.
     */
    private static final String PREFIX_DOM_IDENTIFIER = ".segment-organizations";

    /**
     * Segment model.
     */
    private IModel<Segment> segmentModel;

    public SegmentOrganizationsPanel( String id, IModel<Segment> segmentModel, Set<Long> expansions ) {
        super( id, segmentModel, expansions );
        this.segmentModel = segmentModel;
        init();
    }

    @Override
    public String getHelpSectionId() {
        return "analyzing";
    }

    @Override
    public String getHelpTopicId() {
        return "segment-organizations";
    }

    private void init() {
        addGeomapLink();
        addEntitiesPanel();
    }

    private void addGeomapLink() {
        GeomapLinkPanel geomapLink = new GeomapLinkPanel( "geomapLink",
                                                          new Model<String>( "Organizations with known locations" ),
                                                          getActualOrganizations(),
                                                          new Model<String>( "Locate organizations on a map" ) );
        geomapLink.setOutputMarkupId( true );
        makeVisible( geomapLink, !getActualOrganizations().isEmpty() );
        addOrReplace( geomapLink );
    }

    private void addEntitiesPanel() {
        EntitiesPanel<Organization> entitiesPanel = new EntitiesPanel<Organization>( "organizations",
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
        return getQueryService().listEntitiesTaskedInSegment( Organization.class,
                                                              getSegment(),
                                                              ModelEntity.Kind.Actual );
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
                            getAnalyst().findEntityRelationshipInPlan( getCommunityService(), org, other, getSegment() );
                    if ( sendRel != null )
                        orgRels.add( sendRel );
                }
            }
        }
        return orgRels;
    }

    private Segment getSegment() {
        return segmentModel.getObject();
    }
}
