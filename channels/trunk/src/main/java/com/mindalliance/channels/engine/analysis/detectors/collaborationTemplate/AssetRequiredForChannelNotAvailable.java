package com.mindalliance.channels.engine.analysis.detectors.collaborationTemplate;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.core.model.asset.MaterialAsset;
import com.mindalliance.channels.core.query.Assignments;
import com.mindalliance.channels.core.query.Commitments;
import com.mindalliance.channels.core.query.ModelService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.engine.analysis.graph.AssetSupplyRelationship;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/6/14
 * Time: 11:32 AM
 */
public class AssetRequiredForChannelNotAvailable extends AbstractIssueDetector {

    public AssetRequiredForChannelNotAvailable() {
    }

    @Override
    public List<? extends Issue> detectIssues( CommunityService communityService, Identifiable identifiable ) {
        List<Issue> issues = new ArrayList<Issue>();
        Flow flow = (Flow) identifiable;
        Part source = flow.isSharing() || flow.isCapability()
                ? (Part) flow.getSource()
                : null;
        Part target = flow.isSharing() || flow.isNeed()
                ? (Part) flow.getTarget()
                : null;
        for ( Channel channel : flow.getEffectiveChannels() ) {
            TransmissionMedium medium = channel.getMedium();
            Set<MaterialAsset> allNeededAssets = new HashSet<MaterialAsset>(  );
            for ( MaterialAsset materialAsset : medium.getAssetConnections().using().getAllAssets() ) {
                allNeededAssets.add( materialAsset );
                allNeededAssets.addAll ( materialAsset.allDependencies() );
            }
            for ( MaterialAsset asset : allNeededAssets ) {
                if ( source != null )
                    findIssues( source, medium, true, asset, issues, communityService );
                if ( target != null )
                    findIssues( target, medium, false, asset, issues, communityService );
            }
        }
        return issues;
    }

    private void findIssues( Part part,
                             TransmissionMedium medium,
                             boolean sending,
                             MaterialAsset assetNeeded,
                             List<Issue> issues,
                             CommunityService communityService ) {
        ModelService modelService = communityService.getModelService();
        Assignments assignments = modelService.getAssignments().assignedTo( part );
        Assignments allAssignments = communityService.getModelService().getAssignments( false );
        Commitments allCommitments = communityService.getModelService().getAllCommitments( false );
        List<AssetSupplyRelationship> allSupplyRelationships =
                communityService.getModelService().findAllAssetSupplyRelationships( allAssignments, allCommitments );
        for ( Assignment assignment : assignments ) {
            if ( !modelService.isAssetAvailableToAssignment( assignment, assetNeeded, allSupplyRelationships, allAssignments ) ) {
                Issue issue = makeIssue( communityService, Issue.COMPLETENESS, part );
                issue.setDescription(
                        assignment.getEmployment().getLabel()
                        + " needs asset \"" + assetNeeded.getName()
                        + "\" in task \"" + part.getTitle()
                        + "\" to "
                        + ( sending ? "transmit" : "receive transmissions" )
                        + " via \"" + medium.getName()
                        + "\" but the asset is not available." );
                boolean critical = part.getAssetConnections().using().isCritical( assetNeeded );
                issue.setSeverity( critical
                        ? computeTaskFailureSeverity( communityService.getModelService(), part )
                        : Level.High );
                issue.setRemediation( "Do not use a channel based on \"" + medium.getName() + "\""
                                + "\nor remove the need of \"" + medium.getName() + "\" for asset \"" + assetNeeded.getName() + "\""
                                + "\nor have the task or a prior task produce asset \"" + assetNeeded.getName() + "\""
                                + "\nor have the task or a prior task request that the asset be supplied"
                                + "\nor have the organization of agents assigned to the task stock the asset."
                );
                issues.add( issue );
            }
        }
    }

    @Override
    public boolean appliesTo( Identifiable identifiable ) {
        return identifiable instanceof Flow;
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "Asset needed for use of a transmission medium is unavailable";
    }

    @Override
    public boolean canBeWaived() {
        return true;
    }

}
