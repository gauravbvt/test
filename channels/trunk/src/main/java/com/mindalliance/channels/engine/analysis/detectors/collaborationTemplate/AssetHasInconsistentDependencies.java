package com.mindalliance.channels.engine.analysis.detectors.collaborationTemplate;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.asset.MaterialAsset;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
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
 * Date: 4/9/14
 * Time: 9:07 AM
 */
public class AssetHasInconsistentDependencies extends AbstractIssueDetector {

    public AssetHasInconsistentDependencies() {
    }

    @Override
    public List<? extends Issue> detectIssues( CommunityService communityService, Identifiable identifiable ) {
        List<Issue> issues = new ArrayList<Issue>(  );
        MaterialAsset asset = (MaterialAsset)identifiable;
        Set<MaterialAsset> dependenciesFromTypes = new HashSet<MaterialAsset>(  );
        for ( ModelEntity category : asset.getAllTypes() ) {
            dependenciesFromTypes.addAll( ((MaterialAsset)category).getDependencies() );
        }
        for ( final MaterialAsset dependencyFromType : dependenciesFromTypes ) {
            boolean covered = CollectionUtils.exists(
                    asset.getDependencies(),
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            MaterialAsset dependency = (MaterialAsset)object;
                            return dependency.narrowsOrEquals( dependencyFromType );
                        }
                    }
            );
            if ( !covered ) {
                Issue issue = makeIssue( communityService, Issue.COMPLETENESS, asset );
                issue.setDescription( "Asset \""
                        + asset.getName()
                        + "\" does not depend on \""
                        + dependencyFromType.getName()
                        + "\" as expected from its categorization"
                );
                issue.setRemediation( "Add \"" + dependencyFromType.getName() + "\" as a dependency"
                        + "\nor change the categorization of \"" + asset.getName() + "\""
                        + "\nor remove the dependency from its categorization."
                );
                issue.setSeverity( Level.Medium );
                issues.add( issue );
            }
        }
        return issues;
    }

    @Override
    public boolean appliesTo( Identifiable identifiable ) {
        return identifiable instanceof MaterialAsset && ((MaterialAsset)identifiable).isActual();
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "Actual asset has dependencies inconsistent with its categorization";
    }
}
