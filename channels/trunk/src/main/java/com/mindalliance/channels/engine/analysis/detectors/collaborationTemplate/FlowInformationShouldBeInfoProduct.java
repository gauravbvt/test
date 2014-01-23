package com.mindalliance.channels.engine.analysis.detectors.collaborationTemplate;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.InfoProduct;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

import java.util.ArrayList;
import java.util.List;

/**
 * Flow information has same name as existing info product but is not an info product.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 1/23/14
 * Time: 4:46 PM
 */
public class FlowInformationShouldBeInfoProduct extends AbstractIssueDetector {

    public FlowInformationShouldBeInfoProduct() {
    }

    @Override
    public boolean appliesTo( Identifiable identifiable ) {
        return identifiable instanceof Flow;
    }

    @Override
    public List<? extends Issue> detectIssues( CommunityService communityService, Identifiable identifiable ) {
        List<Issue> issues = new ArrayList<Issue>(  );
        Flow flow = (Flow)identifiable;
        if ( flow.getInfoProduct() == null ) {
            String info = flow.getName();
            InfoProduct infoProduct = communityService.findEntityType( InfoProduct.class, info );
            if ( infoProduct != null ) {
                Issue issue = makeIssue( communityService, Issue.VALIDITY, flow );
                issue.setDescription( "An info product named \"" + info + "\" exists yet the information of that name is not an info product.");
                issue.setSeverity( Level.Low );
                issue.setRemediation( "Mark the information as an info product\nor change the name of the information." );
                issues.add( issue );
            }
        }
        return issues;
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "Information should be an info product";
    }

    @Override
    public boolean canBeWaived() {
        return true;
    }
}
