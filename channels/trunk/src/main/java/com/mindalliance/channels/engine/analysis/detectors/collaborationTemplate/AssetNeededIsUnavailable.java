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
 * Time: 9:40 AM
 */
public class AssetNeededIsUnavailable extends AbstractIssueDetector {

    public AssetNeededIsUnavailable() {
    }

    @Override
    public List<? extends Issue> detectIssues( CommunityService communityService, Identifiable identifiable ) {
        List<Issue> issues = new ArrayList<Issue>(  );
        Part part = (Part)identifiable;
        for ( MaterialAsset assetNeeded : part.findNeededAssets() ) {
            if ( !part.isAssetAvailable( assetNeeded, communityService ) ) {
                Issue issue = makeIssue( communityService, Issue.COMPLETENESS, part );
                issue.setDescription( "Task \"" + part.getTitle() + "\" needs to use or supply \"" + assetNeeded.getName()
                        + "\" but the asset is not available to it." );
                boolean critical = part.getAssetConnections().using().isCritical( assetNeeded );
                issue.setSeverity( critical
                        ? computeTaskFailureSeverity( communityService.getModelService(), part )
                        : Level.Medium );
                issue.setRemediation( "Have the task produce asset \"" + assetNeeded.getName() + "\""
                        + "\nor have the task request that the asset be supplied"
                        + "\nor have the organization of agents assigned to the task stock the asset."
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
        return "Needed asset is not available";
    }

    @Override
    public boolean canBeWaived() {
        return true;
    }
}
