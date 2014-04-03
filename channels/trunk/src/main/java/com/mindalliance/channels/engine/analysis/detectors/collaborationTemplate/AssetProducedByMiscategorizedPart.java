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
 * Time: 4:51 PM
 */
public class AssetProducedByMiscategorizedPart extends AbstractIssueDetector {

    public AssetProducedByMiscategorizedPart() {
    }

    @Override
    public List<? extends Issue> detectIssues( CommunityService communityService, Identifiable identifiable ) {
        List<Issue> issues = new ArrayList<Issue>();
        Part part = (Part) identifiable;
        if ( part.getCategory() != null && part.getCategory() != Part.Category.Operations ) {
            List<MaterialAsset> assetsProduced = part.getAssetConnections().producing().getAllAssets();
            for ( MaterialAsset assetProduced : assetsProduced ) {
                Issue issue = makeIssue( communityService, Issue.VALIDITY, part );
                issue.setDescription( "Task \"" + part.getTitle() + "\" produces asset \"" + assetProduced.getName()
                        + " and yet is categorized other than as \"operations\""
                );
                issue.setSeverity( Level.Low );
                issue.setRemediation( "Categorize the task as \"operations\"" +
                        "\nor remove the current categorization" +
                        "\nor have another task produce the asset.");
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
        return "Task categorized other than operational produces assets";
    }

    @Override
    public boolean canBeWaived() {
        return true;
    }
}
