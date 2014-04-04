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
public class SupplyChainsGraphBuilder implements GraphBuilder<Assignment, AssetSupplyCommitment> {

    private MaterialAsset assetFocus;

    private final boolean summarizedByOrgType;
    private final boolean summarizedByOrg;
    private final boolean summarizedByRole;

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
    public DirectedGraph<Assignment, AssetSupplyCommitment> buildDirectedGraph() {
        DirectedGraph<Assignment, AssetSupplyCommitment> digraph =
                new DirectedMultigraph<Assignment, AssetSupplyCommitment>( new EdgeFactory<Assignment, AssetSupplyCommitment>() {

                    @Override
                    public AssetSupplyCommitment createEdge( Assignment sourceAssignment, Assignment targetAssignment ) {
                        return new AssetSupplyCommitment( sourceAssignment, targetAssignment, MaterialAsset.UNKNOWN );
                    }
                } );
        populateProceduresGraph( digraph );
        return digraph;
    }

    private void populateProceduresGraph( DirectedGraph<Assignment, AssetSupplyCommitment> graph ) {
        List<AssetSupplyCommitment> assetSupplyCommitments = findAssetSupplyCommitments();
        for ( AssetSupplyCommitment assetSupplyCommitment : assetSupplyCommitments ) {
            graph.addVertex( assetSupplyCommitment.getSupplier() );
            graph.addVertex( assetSupplyCommitment.getSupplied() );
            graph.addEdge(
                    assetSupplyCommitment.getSupplier(),
                    assetSupplyCommitment.getSupplied(),
                    assetSupplyCommitment );
        }
    }

    private List<AssetSupplyCommitment> findAssetSupplyCommitments() {

        List<AssetSupplyCommitment> assetSupplyCommitments = new ArrayList<AssetSupplyCommitment>();
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
                                assetSupplyCommitments.add( new AssetSupplyCommitment( supplier, supplied, asset ) );
                        }
                    }
                }
            }
        }
        List<AssetSupplyCommitment> results = new ArrayList<AssetSupplyCommitment>();
        for ( AssetSupplyCommitment commitment : assetSupplyCommitments )
            results.add( summarize( commitment ) );
        return results;
    }

    private AssetSupplyCommitment summarize( AssetSupplyCommitment assetSupplyCommitment ) {
        Assignment supplier = new Assignment( assetSupplyCommitment.getSupplier() );
        Assignment supplied = new Assignment( assetSupplyCommitment.getSupplied() );
        if ( summarizedByOrgType ) {
            Organization supplierOrg = getOrganizationType( supplier.getPart() );
            Role supplierRole = summarizedByRole ? supplier.getRole() : null;
            if ( supplierRole != null )
                supplier.setEmployment( new Employment( supplierOrg, supplierRole ) );
            else
                supplier.setEmployment( new Employment( supplierOrg ) );
            Organization suppliedOrg = getOrganizationType( supplied.getPart() );
            Role suppliedRole = summarizedByRole ? supplied.getRole() : null;
            if ( suppliedRole != null )
                supplied.setEmployment( new Employment( suppliedOrg, suppliedRole ) );
            else
                supplied.setEmployment( new Employment( suppliedOrg ) );
        } else if ( summarizedByOrg ) {
            Organization supplierOrg = supplier.getOrganization();
            Organization suppliedOrg = supplied.getOrganization();
            supplier.setEmployment( new Employment( supplierOrg ) );
            supplied.setEmployment( new Employment( suppliedOrg ) );
        } else if ( summarizedByRole ) {
            supplier.setEmployment( new Employment( supplier.getOrganization(), supplier.getRole() ) );
            supplied.setEmployment( new Employment( supplied.getOrganization(), supplied.getRole() ) );
        }
        return new AssetSupplyCommitment( supplier, supplied, assetSupplyCommitment.getMaterialAsset() );
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
