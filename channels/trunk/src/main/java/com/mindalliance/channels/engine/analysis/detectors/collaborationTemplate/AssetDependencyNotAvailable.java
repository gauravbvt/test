package com.mindalliance.channels.engine.analysis.detectors.collaborationTemplate;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.asset.MaterialAsset;
import com.mindalliance.channels.core.query.Assignments;
import com.mindalliance.channels.core.query.ModelService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.engine.analysis.graph.AssetSupplyRelationship;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/2/14
 * Time: 10:29 AM
 */
public class AssetDependencyNotAvailable extends AbstractIssueDetector {

    public AssetDependencyNotAvailable() {
    }

    @Override
    public List<? extends Issue> detectIssues( CommunityService communityService, Identifiable identifiable ) {
        List<Issue> issues = new ArrayList<Issue>();
        Part part = (Part) identifiable;
        ModelService modelService = communityService.getModelService();
        List<MaterialAsset> assetsUsed = part.findAssetsUsed();
        Assignments allAssignments = modelService.getAssignments();
        Assignments allPartAssignments = allAssignments.assignedTo( part );
        List<AssetSupplyRelationship> assetSupplyRelationships = modelService.findAllAssetSupplyRelationships(
                allAssignments,
                modelService.getAllCommitments( false, allAssignments )
        );
        for ( MaterialAsset assetUsed : assetsUsed ) {
            if ( !assetUsed.getDependencies().isEmpty() ) {
                for ( MaterialAsset dependency : assetUsed.allDependencies() ) {
                    for ( Assignment assignment : allPartAssignments ) {
                        if ( !modelService.isAssetAvailableToAssignment(
                                assignment,
                                dependency,
                                assetSupplyRelationships,
                                allPartAssignments ) ) {
                            Issue issue = makeIssue( communityService, Issue.COMPLETENESS, part );
                            issue.setDescription(
                                    assignment.getEmployment().getLabel()
                                            + " uses asset \"" + assetUsed.getName()
                                            + "\" in task \"" + part.getTitle()
                                            + "\" which depends on asset \"" + dependency.getName()
                                            + "\" that is not available."
                            );
                            boolean critical = part.getAssetConnections().using().isCritical( assetUsed );
                            issue.setSeverity( critical
                                    ? computeTaskFailureSeverity( communityService.getModelService(), part )
                                    : Level.Medium );
                            issue.setRemediation( "Have the task or a prior task produce asset \"" + dependency.getName() + "\""
                                            + "\nor have the task or a prior task request that the asset be supplied"
                                            + "\nor have the organizations assigned to the task stock the asset."
                            );
                            issues.add( issue );
                        }
                    }
                }
            }
        }
        return issues;


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
        return "Asset used while another it depends on is unavailable";
    }

    @Override
    public boolean canBeWaived() {
        return true;
    }

}
