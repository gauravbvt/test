package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.model.Function;
import com.mindalliance.channels.core.model.Information;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Objective;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

import java.util.ArrayList;
import java.util.List;

/**
 * Part does not implement a function fully.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/23/13
 * Time: 4:45 PM
 */
public class PartDoesNotImplementFunction extends AbstractIssueDetector {

    public PartDoesNotImplementFunction() {
    }

    @Override
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Part;
    }

    @Override
    public boolean canBeWaived() {
        return true;
    }

    @Override
    public List<? extends Issue> detectIssues( QueryService queryService, ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Part part = (Part) modelObject;
        Function function = part.getFunction();
        if ( function != null ) {
            // objectives
            for ( Objective objective : function.getEffectiveObjectives() ) {
                if ( !objective.implementedBy( part ) ) {
                    Issue issue = makeIssue( queryService, Issue.COMPLETENESS, part );
                    issue.setDescription( "Task \""
                            + part.getTask()
                            + "\" does not achieve goal \""
                            + objective.getLabel()
                            + "\" of the function it implements." );
                    issue.setRemediation( makeRemediation( part, objective ) );
                    issue.setSeverity( Level.High );
                    issues.add( issue );
                }
            }
            // info needs
            for ( Information infoNeeded : function.getEffectiveInfoNeeded() ) {
                if ( !neededInfoImplementedBy( infoNeeded, part ) ) {
                    Issue issue = makeIssue( queryService, Issue.COMPLETENESS, part );
                    issue.setDescription( "Task \""
                            + part.getTask()
                            + "\" does not fully implement the required information need \""
                            + infoNeeded.getName()
                            + "\" of the function it implements." );
                    issue.setRemediation( makeInfoNeedRemediation( part, infoNeeded ) );
                    issue.setSeverity( Level.High );
                    issues.add( issue );
                }
            }
            // info acquired (to share)
            for ( Information infoAcquired : function.getEffectiveInfoAcquired() ) {
                if ( !acquiredInfoImplementedBy( infoAcquired, part ) ) {
                    Issue issue = makeIssue( queryService, Issue.COMPLETENESS, part );
                    issue.setDescription( "Task \""
                            + part.getTask()
                            + "\" does not fully implement the required information capability \""
                            + infoAcquired.getName()
                            + "\" of the function it implements." );
                    issue.setRemediation( makeInfoAcquiredRemediation( part, infoAcquired ) );
                    issue.setSeverity( Level.High );
                    issues.add( issue );
                }
            }
        }
        return issues;
    }

    private String makeInfoNeedRemediation( Part part, Information infoNeeded ) {
        StringBuilder sb = new StringBuilder();
        sb.append( "Add to task \"" )
                .append( part.getTask() )
                .append( "\" an info need named \"" )
                .append( infoNeeded.getName() )
                .append( "\" with elements " )
                .append( ChannelsUtils.listToString( infoNeeded.getEoiNames(), ",'", " and " ) )
                .append( "\nor add a \"receive\" flow named \"" )
                .append( infoNeeded.getName() )
                .append( "\" with the elements above." );
        return sb.toString();
    }

    private String makeInfoAcquiredRemediation( Part part, Information infoNeeded ) {
        StringBuilder sb = new StringBuilder();
        sb.append( "Add to task \"" )
                .append( part.getTask() )
                .append( "\" an info capability named \"" )
                .append( infoNeeded.getName() )
                .append( "\" with elements " )
                .append( ChannelsUtils.listToString( infoNeeded.getEoiNames(), ",'", " and " ) )
                .append( "\nor add a \"send\" flow named \"" )
                .append( infoNeeded.getName() )
                .append( "\" with the elements above." );
        return sb.toString();
    }

    private boolean neededInfoImplementedBy( final Information info, Part part ) {
        // No EOI in info needed per function does not have a counterpart in an info need or sharing receive in the part.
        return info.implementedBy( part.getNeeds() ) ||
                info.implementedBy( part.getAllSharingReceives() );
    }

    private boolean acquiredInfoImplementedBy( final Information info, Part part ) {
        // No EOI in info acquired per function does not have a counterpart in an info capability or sharing send in the part.
        return info.implementedBy( part.getCapabilities() ) ||
                info.implementedBy( part.getAllSharingSends() );
    }

    private String makeRemediation( Part part, Objective objective ) {
        StringBuilder sb = new StringBuilder();
        sb.append( "Have task \"" )
                .append( part.getTask() )
                .append( "\" directly achieve goal \"" )
                .append( objective.getLabel() )
                .append( "\", \nor set the goal as achieved on terminating the segment's scenario " +
                        "and have the task terminate the segment/scenario." );
        return sb.toString();
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "Task does not fully implement its function";
    }
}
