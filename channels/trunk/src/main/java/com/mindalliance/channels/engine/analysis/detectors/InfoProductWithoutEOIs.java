package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.InfoProduct;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

import java.util.ArrayList;
import java.util.List;

/**
 * Info product with no local element of information.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/2/12
 * Time: 5:37 PM
 */
public class InfoProductWithoutEOIs extends AbstractIssueDetector {

    public InfoProductWithoutEOIs() {
    }

    @Override
    public boolean appliesTo( Identifiable modelObject ) {
        return modelObject instanceof InfoProduct;
    }

    @Override
    public List<? extends Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        InfoProduct infoProduct = (InfoProduct)modelObject;
        if ( infoProduct.getLocalEois().isEmpty() ) {
            Issue issue = makeIssue( communityService, Issue.COMPLETENESS, infoProduct );
            issue.setDescription( "The information product \""
                    + infoProduct.getName()
                    + "\" defines no element of information" );
            issue.setRemediation( "Remove all references to the info product" +
                    "\nor add elements of information to its definition." );
            issue.setSeverity( Level.Low );
            issues.add( issue );
        }
        return issues;
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "Information product is defined without elements of information";
    }
}
