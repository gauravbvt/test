package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.PlaceReference;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

import java.util.ArrayList;
import java.util.List;

/**
 * The plan locale is defined as within or without another place.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 26, 2010
 * Time: 12:38:24 PM
 */
public class PlanLocaleIsRelative extends AbstractIssueDetector {

    public PlanLocaleIsRelative() {
    }

    public List<Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        QueryService queryService = communityService.getPlanService();
        List<Issue> issues = new ArrayList<Issue>();
        Plan plan = (Plan) modelObject;
        Place locale = queryService.getPlanLocale();
        if ( locale != null ) {
            PlaceReference ref = locale.getMustBeContainedIn();
            if ( ref != null && ref.getReferencedPlace( locale ) != null ) {
                Issue issue = makeIssue( communityService, Issue.VALIDITY, plan );
                issue.setSeverity( Level.High );
                issue.setDescription( "The template's locale ("
                        + locale.getName()
                        + ") is defined in terms of another place that contains it. It must not." );
                issue.setRemediation( "Remove the containment constraint from the definition of " + locale.getName()
                        + "\nor use another place as the template's locale"
                        + "\nor leave the template's locale unnamed."
                );
                issues.add( issue );
            }
            ref = locale.getMustContain();
            if ( ref != null && ref.getReferencedPlace( locale ) != null ) {
                Issue issue = makeIssue( communityService, Issue.VALIDITY, plan );
                issue.setSeverity( Level.High );
                issue.setDescription( "The template's locale ("
                        + locale.getName()
                        + ") is defined in terms of another place that it contains. It must not." );
                issue.setRemediation( "Remove the containment constraint from the definition of " + locale.getName()
                        + "\nor use another place as the template's locale"
                        + "\nor leave the template's locale unnamed."
                );
                issues.add( issue );
            }
            Place within = locale.getWithin();
            if ( within != null ) {
                if ( !within.isAbsolute( locale ) ) {
                    Issue issue = makeIssue( communityService, Issue.VALIDITY, plan );
                    issue.setSeverity( Level.High );
                    issue.setDescription( "The template's locale ("
                            + locale.getName()
                            + ") is within "
                            + within.getName()
                            + " which is specified as containing or being contained in another. It must not." );
                    issue.setRemediation( "Change the definition of "
                            + within.getName()
                            + " so that it is not relative"
                            + "\nor use another place as the template's locale"
                            + "\nor leave the template's locale unnamed."
                    );
                    issues.add( issue );
                }
            }
            for ( ModelEntity type : locale.getAllTypes() ) {
                Place placeType = (Place) type;
                if ( !placeType.isAbsolute( locale ) ) {
                    Issue issue = makeIssue( communityService, Issue.VALIDITY, plan );
                    issue.setSeverity( Level.High );
                    issue.setDescription( "The template's locale ("
                            + locale.getName()
                            + ") is categorized with "
                            + placeType.getName()
                            + " which is specified as containing or being contained in another. It must not." );
                    issue.setRemediation( "Change the definition of "
                            + placeType.getName()
                            + " so that it is not relative"
                            + "\nor use another place as the template's locale"
                            + "\nor leave the template's locale unnamed."
                    );
                    issues.add( issue );
                }
            }
        }
        return issues;
    }

    public boolean appliesTo( Identifiable modelObject ) {
        return modelObject instanceof Plan;
    }

    public String getTestedProperty() {
        return null;
    }

    protected String getKindLabel() {
        return "Collaboration template as a relative locale";
    }

}
