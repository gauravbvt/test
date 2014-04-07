package com.mindalliance.channels.engine.analysis.graph;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Employment;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.model.asset.MaterialAsset;
import com.mindalliance.channels.core.query.Assignments;
import com.mindalliance.channels.core.query.ModelService;
import com.mindalliance.channels.engine.analysis.GraphBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/3/14
 * Time: 9:50 PM
 */
public class SupplyChainsGraphBuilder implements GraphBuilder<Assignment, AssignmentAssetLink> {

    private MaterialAsset assetFocus;

    private final boolean summarizedByOrgType;
    private final boolean summarizedByOrg;
    private final boolean summarizedByRole;
    private Assignments assignmentsUsingAssets;

    private CommunityService communityService;

    public SupplyChainsGraphBuilder( MaterialAsset assetFocus,
                                     boolean summarizedByOrgType,
                                     boolean summarizedByOrg,
                                     boolean summarizedByRole ) {
        this.assetFocus = assetFocus;
        this.summarizedByOrgType = summarizedByOrgType;
        this.summarizedByOrg = summarizedByOrg;
        this.summarizedByRole = summarizedByRole;
    }

    public void setCommunityService( CommunityService communityService ) {
        this.communityService = communityService;
    }

    @Override
    public DirectedGraph<Assignment, AssignmentAssetLink> buildDirectedGraph() {
        DirectedGraph<Assignment, AssignmentAssetLink> digraph =
                new DirectedMultigraph<Assignment, AssignmentAssetLink>( new EdgeFactory<Assignment, AssignmentAssetLink>() {

                    @Override
                    public AssignmentAssetLink createEdge( Assignment sourceAssignment, Assignment targetAssignment ) {
                        return new AssignmentAssetLink(
                                sourceAssignment,
                                targetAssignment,
                                MaterialAsset.UNKNOWN,
                                AssignmentAssetLink.Type.SupplyCommitment );
                    }
                } );
        populateProceduresGraph( digraph );
        return digraph;
    }

    private void populateProceduresGraph( DirectedGraph<Assignment, AssignmentAssetLink> graph ) {
        // Asset supply commitments
        List<AssignmentAssetLink> assetSupplyCommitments = findAssetSupplyCommitments();
        for ( AssignmentAssetLink assignmentAssetLink : assetSupplyCommitments ) {
            graph.addVertex( assignmentAssetLink.getFromAssignment() );
            graph.addVertex( assignmentAssetLink.getToAssignment() );
            graph.addEdge(
                    assignmentAssetLink.getFromAssignment(),
                    assignmentAssetLink.getToAssignment(),
                    assignmentAssetLink );
        }
        // Asset availability to use
        List<AssignmentAssetLink> assetAvailabilityToUse = findAssetAvailabilityToUseLinks(
                getModelService().getAssignments(),
                getModelService().findAllAssetSupplyRelationships()
        );
        for ( AssignmentAssetLink assignmentAssetLink : assetAvailabilityToUse ) {
            graph.addVertex( assignmentAssetLink.getFromAssignment() );
            graph.addVertex( assignmentAssetLink.getToAssignment() );
            graph.addEdge(
                    assignmentAssetLink.getFromAssignment(),
                    assignmentAssetLink.getToAssignment(),
                    assignmentAssetLink );
        }
        Assignments assignments = getModelService().getAssignments();
        // Producing assignments
        if ( assetFocus == null ) {
            for ( Assignment assignment : assignments.producesAssets() ) {
                graph.addVertex( summarize( assignment ) );
            }
        } else {
            for ( Assignment assignment : assignments.producesAssets().producesAsset( assetFocus ) ) {
                graph.addVertex( summarize( assignment ) );
            }
        }
        // Using assignments
        for ( Assignment assignment : findAssignmentsOnlyUsingAssets( ) ) {
            graph.addVertex( summarize( assignment ) );
        }

    }

    private Assignments findAssignmentsOnlyUsingAssets() {
        if ( assignmentsUsingAssets == null ) {
            assignmentsUsingAssets = new Assignments( getModelService().getPlanLocale() );
            Assignments usingAssignments = assetFocus == null
                    ? getModelService().getAssignments().onlyUsesAssets()
                    : getModelService().getAssignments().onlyUsesAsset( assetFocus );
            for ( final Assignment assignment : usingAssignments ) { // uses but does not produce
                boolean supplied = CollectionUtils.exists(
                        findAssetSupplyCommitments(),
                        new Predicate() {
                            @Override
                            public boolean evaluate( Object object ) {
                                AssignmentAssetLink assetSupplyCommitment = (AssignmentAssetLink) object;
                                return assetSupplyCommitment.getToAssignment().equals( assignment );
                            }
                        }
                );
                if ( !supplied ) {
                    assignmentsUsingAssets.add( assignment );
                }
            }
        }
        return assignmentsUsingAssets;
    }

    private List<AssignmentAssetLink> findAssetAvailabilityToUseLinks( Assignments allAssignments,
                                                                       List<AssetSupplyRelationship<Part>> allAssetSupplyRelationships ) {
        List<AssignmentAssetLink> links = new ArrayList<AssignmentAssetLink>();
        for ( Assignment assignmentUsingAssets : findAssignmentsOnlyUsingAssets() ) {
            for ( Part precedingPart : getModelService().findPartsPreceding( assignmentUsingAssets.getPart() ) ) {
                for ( Assignment precedingAssignment : allAssignments.assignedTo( precedingPart ).with( assignmentUsingAssets.getActor() ) ) {
                    for ( MaterialAsset asset : assignmentUsingAssets.getPart().findAssetsUsed() ) {
                        if ( assetFocus == null || asset.narrowsOrEquals( assetFocus ) ) {
                            if ( getModelService().isAssetAvailableToAssignment(
                                    precedingAssignment,
                                    asset,
                                    allAssignments,
                                    allAssetSupplyRelationships ) ) {
                                links.add( new AssignmentAssetLink(
                                        precedingAssignment,
                                        assignmentUsingAssets,
                                        asset,
                                        AssignmentAssetLink.Type.AvailabilityToUse ) );
                            }
                        }
                    }
                }
            }
        }
        List<AssignmentAssetLink> results = new ArrayList<AssignmentAssetLink>();
        for ( AssignmentAssetLink link : links )
            results.add( summarize( link ) );
        return results;
    }

    private List<AssignmentAssetLink> findAssetSupplyCommitments() {
        List<AssignmentAssetLink> assignmentAssetLinks = new ArrayList<AssignmentAssetLink>();
        ModelService modelService = getModelService();
        Assignments allAssignments = modelService.getAssignments( false );
        List<AssetSupplyRelationship<Part>> assetSupplyRels = getModelService().findAllAssetSupplyRelationships();
        Place locale = getModelService().getPlanLocale();
        for ( AssetSupplyRelationship<Part> assetSupplyRel : assetSupplyRels ) {
            if ( assetFocus == null || ( assetFocus != null && assetSupplyRel.isAssetSupplied( assetFocus ) ) ) {
                Assignments supplierAssignments = allAssignments.assignedTo( assetSupplyRel.getSupplier( modelService ) );
                Assignments suppliedAssignments = allAssignments.assignedTo( assetSupplyRel.getSupplied( modelService ) );
                List<MaterialAsset> assetsSupplied = new ArrayList<MaterialAsset>();
                if ( assetFocus == null ) {
                    assetsSupplied.addAll( assetSupplyRel.getAssets() );
                } else {
                    assetsSupplied.add( assetFocus );
                }
                for ( MaterialAsset asset : assetsSupplied ) {
                    for ( Assignment supplier : supplierAssignments ) {
                        for ( Assignment supplied : suppliedAssignments ) {
                            if ( modelService.allowsCommitment( supplier, supplied, locale, assetSupplyRel.getRestrictions() ) )
                                assignmentAssetLinks.add( new AssignmentAssetLink(
                                        supplier,
                                        supplied,
                                        asset,
                                        AssignmentAssetLink.Type.SupplyCommitment ) );
                        }
                    }
                }
            }
        }
        List<AssignmentAssetLink> results = new ArrayList<AssignmentAssetLink>();
        for ( AssignmentAssetLink link : assignmentAssetLinks )
            results.add( summarize( link ) );
        return results;
    }

    private AssignmentAssetLink summarize( AssignmentAssetLink assignmentAssetLink ) {
        Assignment supplier = summarize( assignmentAssetLink.getFromAssignment() );
        Assignment supplied = summarize( assignmentAssetLink.getToAssignment() );
        return new AssignmentAssetLink(
                supplier,
                supplied,
                assignmentAssetLink.getMaterialAsset(),
                assignmentAssetLink.getType() );
    }

    private Assignment summarize( Assignment assignment ) {
        Assignment summarizedAssignment = new Assignment( assignment );
        if ( summarizedByOrgType ) {
            Organization org = getOrganizationType( assignment.getPart() );
            Role role = summarizedByRole ? assignment.getRole() : null;
            if ( role != null )
                summarizedAssignment.setEmployment( new Employment( org, role ) );
            else
                summarizedAssignment.setEmployment( new Employment( org ) );
        } else if ( summarizedByOrg ) {
            Organization org = assignment.getOrganization();
            summarizedAssignment.setEmployment( new Employment( org ) );
        } else if ( summarizedByRole ) {
            summarizedAssignment.setEmployment( new Employment( assignment.getOrganization(), assignment.getRole() ) );
        }
        return summarizedAssignment;
    }

    private Organization getOrganizationType( Part part ) {
        Organization org = part.getOrganization();
        if ( org == null || org.isUnknown() )
            return Organization.getUniversalTypeFor( Organization.class );
        else if ( org.isType() )
            return org;
        else {
            // choose one
            List<ModelEntity> types = org.getAllTypes();
            if ( types.isEmpty() ) {
                return Organization.getUniversalTypeFor( Organization.class );
            } else {
                // get last one for now -- todo: something smarter?
                return (Organization) types.get( types.size() - 1 );
            }
        }
    }


    private ModelService getModelService() {
        return communityService.getModelService();
    }
}
