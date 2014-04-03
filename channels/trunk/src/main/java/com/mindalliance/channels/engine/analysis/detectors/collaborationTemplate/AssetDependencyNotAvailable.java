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
 * Time: 10:29 AM
 */
public class AssetDependencyNotAvailable extends AbstractIssueDetector {

    public AssetDependencyNotAvailable() {
    }

    @Override
    public List<? extends Issue> detectIssues( CommunityService communityService, Identifiable identifiable ) {
        List<Issue> issues = new ArrayList<Issue>(  );
        Part part = (Part)identifiable;
        List<MaterialAsset> assetsUsed = part.getAssetConnections().using().getAllAssets();
        for ( MaterialAsset assetUsed : assetsUsed ) {
            if ( !assetUsed.getDependencies().isEmpty()/* && part.isAssetAvailable( assetUsed, communityService )*/ ) {
                for ( MaterialAsset dependency : assetUsed.getDependencies() ) {
                    if ( !part.isAssetAvailable( dependency, communityService ) ) {
                        Issue issue = makeIssue( communityService, Issue.COMPLETENESS, part );
                        issue.setDescription( "Task \"" + part.getTitle() + "\" uses " + "\"" + assetUsed.getName()
                                + "\" which depends on \"" + dependency.getName() + "\" that is not made available to the task." );
                        boolean critical = part.getAssetConnections().using().isCritical( assetUsed );
                        issue.setSeverity( critical
                                ? computeTaskFailureSeverity( communityService.getModelService(), part )
                                : Level.Medium );
                        issue.setRemediation( "Have the task produce asset \"" + dependency.getName() + "\""
                                        + "\nor have the task request that the asset be supplied"
                                        + "\nor have the organizations assigned to the task stock the asset."
                        );
                        issues.add( issue );
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
