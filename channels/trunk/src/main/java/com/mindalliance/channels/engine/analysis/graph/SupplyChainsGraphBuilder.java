package com.mindalliance.channels.engine.analysis.graph;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Employment;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.model.asset.MaterialAsset;
import com.mindalliance.channels.core.query.Assignments;
import com.mindalliance.channels.core.query.Commitments;
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
    private boolean showingOrphans;
    private boolean showingAvailability;
    private Assignments assignmentsUsingAssets;

    private CommunityService communityService;

    public SupplyChainsGraphBuilder( MaterialAsset assetFocus,
                                     boolean summarizedByOrgType,
                                     boolean summarizedByOrg,
                                     boolean summarizedByRole,
                                     boolean showingOrphans,
                                     boolean showingAvailability ) {
        this.assetFocus = assetFocus;
        this.summarizedByOrgType = summarizedByOrgType;
        this.summarizedByOrg = summarizedByOrg;
        this.summarizedByRole = summarizedByRole;
        this.showingOrphans = showingOrphans;
        this.showingAvailability = showingAvailability;
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
        Assignments allAssignments = getModelService().getAssignments( false );
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
        if ( showingAvailability ) {
            // Asset availability to use
            ModelService modelService = getModelService();
            Commitments allCommitments = modelService.getAllCommitments( false );
            List<AssetSupplyRelationship> allSupplyRelationships =
                    communityService.getModelService().findAllAssetSupplyRelationships( allAssignments, allCommitments );
            List<AssignmentAssetLink> assetAvailabilityToUse = findAssetAvailabilityToUseLinks(
                    allAssignments,
                    allSupplyRelationships
            );
            for ( AssignmentAssetLink assignmentAssetLink : assetAvailabilityToUse ) {
                graph.addVertex( assignmentAssetLink.getFromAssignment() );
                graph.addVertex( assignmentAssetLink.getToAssignment() );
                graph.addEdge(
                        assignmentAssetLink.getFromAssignment(),
                        assignmentAssetLink.getToAssignment(),
                        assignmentAssetLink );
            }
        }
        if ( showingOrphans ) {
            // Producing assignments
            if ( assetFocus == null ) {
                for ( Assignment assignment : allAssignments.producesAssets() ) {
                    graph.addVertex( summarize( assignment ) );
                }
            } else {
                for ( Assignment assignment : allAssignments.producesAssets().producesAsset( assetFocus ) ) {
                    graph.addVertex( summarize( assignment ) );
                }
            }
            // Using assignments
            for ( Assignment assignment : findAssignmentsOnlyUsingAssets() ) {
                graph.addVertex( summarize( assignment ) );
            }
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
                                                                       List<AssetSupplyRelationship> allAssetSupplyRelationships ) {
        List<AssignmentAssetLink> links = new ArrayList<AssignmentAssetLink>();
        for ( Assignment assignment : allAssignments ) {
            for ( MaterialAsset assetNeeded : findAssetsNeededBy( assignment, allAssetSupplyRelationships ) ) { // assets used but not directly available
                if ( assetFocus == null || assetFocus.narrowsOrEquals( assetNeeded ) ) {
                    for ( Assignment precedingAssignment
                            : getModelService().findPrecedingAssignmentsWithAssetDirectlyAvailable(
                            assignment,
                            assetNeeded,
                            allAssetSupplyRelationships,
                            allAssignments

                    ) ) {
                        links.add( new AssignmentAssetLink(
                                precedingAssignment,
                                assignment,
                                assetNeeded,
                                AssignmentAssetLink.Type.AvailabilityToUse ) );
                    }
                }
            }
        }
        List<AssignmentAssetLink> results = new ArrayList<AssignmentAssetLink>();
        for ( AssignmentAssetLink link : links )
            results.add( summarize( link ) );
        return results;
    }

    @SuppressWarnings( "unchecked" )
    private List<MaterialAsset> findAssetsNeededBy( final Assignment assignment,
                                                    final List<AssetSupplyRelationship> allAssetSupplyRelationships ) {
        return (List<MaterialAsset>) CollectionUtils.select(
                assignment.getPart().findAssetsUsed(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        MaterialAsset asset = (MaterialAsset) object;
                        return !getModelService().isAssetDirectlyAvailableToAssignment(
                                assignment,
                                asset,
                                allAssetSupplyRelationships );
                    }
                }
        );
    }

    private List<AssignmentAssetLink> findAssetSupplyCommitments() {
        List<AssignmentAssetLink> assignmentAssetLinks = new ArrayList<AssignmentAssetLink>();
        ModelService modelService = getModelService();
        Assignments allAssignments = modelService.getAssignments( false );
        Commitments allCommitments = modelService.getAllCommitments( false );
        List<AssetSupplyRelationship> allSupplyRelationships =
                communityService.getModelService().findAllAssetSupplyRelationships( allAssignments, allCommitments );
        for ( AssetSupplyRelationship assetSupplyRel : allSupplyRelationships ) {
            if ( assetFocus == null || ( assetFocus != null && assetSupplyRel.isAssetSupplied( assetFocus ) ) ) {
                List<MaterialAsset> assetsSupplied = new ArrayList<MaterialAsset>();
                if ( assetFocus == null ) {
                    assetsSupplied.addAll( assetSupplyRel.getAssets() );
                } else {
                    assetsSupplied.add( assetFocus );
                }
                for ( MaterialAsset asset : assetsSupplied ) {
                    assignmentAssetLinks.add( new AssignmentAssetLink(
                            assetSupplyRel.getSupplier(),
                            assetSupplyRel.getSupplied(),
                            asset,
                            AssignmentAssetLink.Type.SupplyCommitment ) );
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
