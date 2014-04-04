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
 * Time: 11:20 AM
 */
public class AssetRequestNotReceivedYetForwarded extends AbstractIssueDetector {

    public AssetRequestNotReceivedYetForwarded() {
    }

    @Override
    public List<? extends Issue> detectIssues( CommunityService communityService, Identifiable identifiable ) {
        List<Issue> issues = new ArrayList<Issue>(  );
        Part part = (Part)identifiable;
        List<MaterialAsset> forwardRequestedAssets = part.getInitiatedAssetConnections().demanding()
                .forwarding().getAllAssets();
        for ( MaterialAsset assetForwardRequested : forwardRequestedAssets ) {
            boolean requestedOfPart = !part.getNonInitiatedAssetConnections().demanding().about( assetForwardRequested ).isEmpty();
            if ( !requestedOfPart ) {
                Issue issue = makeIssue( communityService, Issue.COMPLETENESS, part );
                issue.setDescription( "Task \"" + part.getTitle() + "\" forwards a request for "
                        + "\"" + assetForwardRequested.getName()
                        + "\" that is never requested of the task." );
                issue.setSeverity( Level.Low );
                issue.setRemediation( "Have the task receive a request for asset \"" + assetForwardRequested.getName() + "\""
                                + "\nor remove the request forwarding for the asset."
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
        return "Non-existent request for an asset is forwarded";
    }
}
