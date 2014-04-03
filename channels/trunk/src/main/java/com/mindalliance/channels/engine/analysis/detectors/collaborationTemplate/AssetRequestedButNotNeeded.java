package com.mindalliance.channels.engine.analysis.detectors.collaborationTemplate;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.asset.MaterialAsset;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/2/14
 * Time: 12:18 PM
 */
public class AssetRequestedButNotNeeded extends AbstractIssueDetector {

    public AssetRequestedButNotNeeded() {
    }

    @Override
    public List<? extends Issue> detectIssues( CommunityService communityService, Identifiable identifiable ) {
        List<Issue> issues = new ArrayList<Issue>(  );
        Part part = (Part)identifiable;
        List<MaterialAsset> assetsNeeded = part.findNeededAssets();
        List<MaterialAsset> assetsDirectlyRequested = part.getInitiatedAssetConnections().demanding()
                .notForwarding().getAllAssets();
        for ( final MaterialAsset assetDirectlyRequested : assetsDirectlyRequested ) {
            boolean requestedButNoNeeded = !CollectionUtils.exists(
                assetsNeeded,
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            MaterialAsset assetNeeded = (MaterialAsset)object;
                            return assetDirectlyRequested.narrowsOrEquals( assetNeeded );
                        }
                    }
            );
            if ( requestedButNoNeeded ) {
                Issue issue = makeIssue( communityService, Issue.COMPLETENESS, part );
                issue.setDescription( "Task \"" + part.getTitle() + "\" requests "
                        + "\"" + assetDirectlyRequested.getName()
                        + "\" for itself but does not need to use it or supply it." );
                issue.setSeverity( Level.Low );
                issue.setRemediation( "Have the task use or supply asset \"" + assetDirectlyRequested.getName() + "\""
                                + "\nor remove the task's request for it."
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
        return "Asset requested yet not needed";
    }
}
