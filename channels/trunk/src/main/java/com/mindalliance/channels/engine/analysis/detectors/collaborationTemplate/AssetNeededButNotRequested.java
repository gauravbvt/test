package com.mindalliance.channels.engine.analysis.detectors.collaborationTemplate;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.asset.MaterialAsset;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/2/14
 * Time: 11:00 AM
 */
public class AssetNeededButNotRequested extends AbstractIssueDetector {

    public AssetNeededButNotRequested() {
    }

    @Override
    public List<? extends Issue> detectIssues( CommunityService communityService, Identifiable identifiable ) {
        List<Issue> issues = new ArrayList<Issue>(  );
        Part part = (Part)identifiable;
        for ( MaterialAsset assetNeeded : part.findNeededAssets() ) {
            boolean directlyRequested = !part.getInitiatedAssetConnections().demanding().about( assetNeeded ).notForwarding().isEmpty();
            if ( !directlyRequested ) {
                Issue issue = makeIssue( communityService, Issue.COMPLETENESS, part );
                issue.setDescription( "Task \"" + part.getTitle() + "\" needs " + "\"" + assetNeeded.getName()
                        + "\" but never requests it." );
                boolean critical = part.getAssetConnections().using().isCritical( assetNeeded );
                issue.setSeverity( critical
                        ? computeTaskFailureSeverity( communityService.getModelService(), part )
                        : Level.Medium );
                issue.setRemediation( "Have the task request asset \"" + assetNeeded.getName() + "\""
                                + "\nor remove the need for the asset (by either not using it or not supplying it)."
                );
                issues.add( issue );
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
        return "Asset is needed but not requested";
    }

    @Override
    public boolean canBeWaived() {
        return true;
    }
}
