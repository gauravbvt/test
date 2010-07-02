package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.Level;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.model.PlaceReference;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.dao.User;

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

    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Plan plan = (Plan) modelObject;
        Place locale = plan.getLocale();
        if ( locale != null ) {
            PlaceReference ref = locale.getMustBeContainedIn();
            if ( ref != null && ref.getReferencedPlace( plan ) != null ) {
                Issue issue = makeIssue( Issue.VALIDITY, plan );
                issue.setSeverity( Level.High );
                issue.setDescription( "The plan's locale ("
                        + locale.getName()
                        + ") is defined in terms of another place that contains it. It must not." );
                issue.setRemediation( "Remove the containment constraint from the definition of " + locale.getName()
                        + "\nor use another place as the plan's locale"
                        + "\nor leave the plan's locale unnamed."
                );
                issues.add( issue );
            }
            ref = locale.getMustContain();
            if ( ref != null && ref.getReferencedPlace( plan ) != null ) {
                Issue issue = makeIssue( Issue.VALIDITY, plan );
                issue.setSeverity( Level.High );
                issue.setDescription( "The plan's locale ("
                        + locale.getName()
                        + ") is defined in terms of another place that it contains. It must not." );
                issue.setRemediation( "Remove the containment constraint from the definition of " + locale.getName()
                        + "\nor use another place as the plan's locale"
                        + "\nor leave the plan's locale unnamed."
                );
                issues.add( issue );
            }
            Place within = locale.getWithin();
            if ( within != null ) {
                if ( !within.isAbsolute( User.current().getPlan() ) ) {
                    Issue issue = makeIssue( Issue.VALIDITY, plan );
                    issue.setSeverity( Level.High );
                    issue.setDescription( "The plan's locale ("
                            + locale.getName()
                            + ") is within "
                            + within.getName()
                            + " which is specified as containing or being contained in another. It must not." );
                    issue.setRemediation( "Change the definition of "
                            + within.getName()
                            + " so that it is not relative"
                            + "\nor use another place as the plan's locale"
                            + "\nor leave the plan's locale unnamed."
                    );
                    issues.add( issue );
                }
            }
            for ( ModelEntity tag : locale.getAllTags() ) {
                Place placeTag = (Place) tag;
                if ( !placeTag.isAbsolute( User.current().getPlan() ) ) {
                    Issue issue = makeIssue( Issue.VALIDITY, plan );
                    issue.setSeverity( Level.High );
                    issue.setDescription( "The plan's locale ("
                            + locale.getName()
                            + ") is tagged with "
                            + placeTag.getName()
                            + " which is specified as containing or being contained in another. It must not." );
                    issue.setRemediation( "Change the definition of "
                            + placeTag.getName()
                            + " so that it is not relative"
                            + "\nor use another place as the plan's locale"
                            + "\nor leave the plan's locale unnamed."
                    );
                    issues.add( issue );
                }
            }
        }
        return issues;
    }

    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Plan;
    }

    public String getTestedProperty() {
        return null;
    }

    protected String getLabel() {
        return "Plan locale is relative";
    }

}
