package com.mindalliance.channels.engine.analysis.detectors.collaborationTemplate;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.asset.MaterialAsset;
import com.mindalliance.channels.core.query.Assignments;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.engine.analysis.graph.AssetSupplyRelationship;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/2/14
 * Time: 12:34 PM
 */
public class AssetIsCriticalYetHasInsufficientSuppliers extends AbstractIssueDetector {

    public AssetIsCriticalYetHasInsufficientSuppliers() {
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<? extends Issue> detectIssues( final CommunityService communityService, Identifiable identifiable ) {
        List<Issue> issues = new ArrayList<Issue>();
        final Part part = (Part) identifiable;
        List<MaterialAsset> criticalAssets = part.getAssetConnections().using().critical().getAllAssets();
        Assignments allAssignments = communityService.getModelService().getAssignments( false );
        Place locale = communityService.getModelService().getPlanLocale( );
        List<AssetSupplyRelationship<Part>> supplyRels =
                (List<AssetSupplyRelationship<Part>>) CollectionUtils.select(
                        communityService.getModelService().findAllAssetSupplyRelationships(),
                        new Predicate() {
                            @Override
                            public boolean evaluate( Object object ) {
                                return ( (AssetSupplyRelationship<Part>) object ).getSupplied( communityService.getModelService() ).equals( part );
                            }
                        }
                );
        for ( final MaterialAsset criticalAsset : criticalAssets ) {
            List<AssetSupplyRelationship<Part>> applicableSupplyRels = new ArrayList<AssetSupplyRelationship<Part>>();
            for ( AssetSupplyRelationship<Part> supplyRel : supplyRels ) {
                if ( supplyRel.isAssetSupplied( criticalAsset ) ) {
                    applicableSupplyRels.add( supplyRel );
                }
            }
            for ( Assignment suppliedAssignment : allAssignments.assignedTo( part ) ) {
                Set<Actor> supplyingActors = new HashSet<Actor>();
                for ( AssetSupplyRelationship<Part> applicableSupplyRel : applicableSupplyRels ) {
                    Part supplyingPart = applicableSupplyRel.getSupplier( communityService.getModelService() );
                    for ( Assignment supplyingAssignment : allAssignments.assignedTo( supplyingPart ) ) {
                        if ( communityService.getModelService().allowsCommitment(
                                supplyingAssignment,
                                suppliedAssignment,
                                locale,
                                applicableSupplyRel.getRestrictions() ) )
                            supplyingActors.add( supplyingAssignment.getActor() );
                    }
                }
                if ( supplyingActors.size() < 2 ) {
                    if ( !isMultipleParticipation( supplyingActors ) ) {
                        Issue issue = makeIssue( communityService, Issue.ROBUSTNESS, part );
                        issue.setDescription( "Task \"" + part.getTitle() + "\" uses critical asset " + "\"" + criticalAsset.getName()
                                + "\" and has " + ( supplyingActors.size() == 0 ? "no" : "only one" ) + " identified supplier." );
                        issue.setSeverity( computeTaskFailureSeverity( communityService.getModelService(), part ) );
                        issue.setRemediation( "Make used asset \"" + criticalAsset.getName() + "\" not critical to the task"
                                        + "\nor add another task with different agents assigned to it that supplies the asset."
                        );
                        issues.add( issue );
                    }
                }
            }
        }
        return issues;
    }

    private boolean isMultipleParticipation( Set<Actor> supplyingActors ) {
        if ( supplyingActors.size() == 1 ) {
            Actor actor = (Actor) supplyingActors.toArray()[0];
            return !actor.isSingularParticipation();
        }
        return false;
    }

    @Override
    public boolean appliesTo( Identifiable identifiable ) {
        return identifiable instanceof Part;
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "Too few suppliers for critical asset";
    }

    @Override
    public boolean canBeWaived() {
        return true;
    }

}
