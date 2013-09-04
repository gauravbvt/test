/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.model.Flow.Intent;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Organization.FamilyRelationship;
import com.mindalliance.channels.core.model.Part.Category;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Expected sharing commitment missing between tasks assigned to organizations based on task categories and flow
 * intents. given their respective categories.
 */
public class OrganizationMissingExpectedCommitment extends AbstractIssueDetector {

    // todo - OPTIMIZE before reactivation

    private static final Object[][] Expectations = {
            {
                    // If agents of an organization are assigned an operations management task,
                    // they are expected to have at least one sharing commitment
                    // with agents of the same organization or a child organization
                    // assigned to an operations task
                    // where the intent of the flow is a command
                    Category.OperationsManagement,  // if task of this category assigned to fromOrg
                    Category.Operations,            // expected commitment expected with task assigned to toOrg
                    FamilyRelationship.Identity, FamilyRelationship.Child,
                    // if one of alternate relationships toOrg -> fromOrg. None = any
                    Intent.Command     // alternate expected sharing flow intents. None = any.
            }, {
            // If agents of an organization are assigned an operations task,
            // they are expected to have at least one sharing commitment
            // with agents of the same organization or a parent organization
            // assigned to an operations management task
            // where the intent of the info flow is a report
            Category.Operations, Category.OperationsManagement, FamilyRelationship.Identity, FamilyRelationship.Parent,
            Intent.Report
    }, {
            // If agents of an organization are assigned a coordination task,
            // they are expected to have at least one sharing commitment
            // with agents of the same organization
            // assigned to an operations management task
            // where the intent of the info flow is an announcement or a report
            Category.InterOperationsCoordination, Category.OperationsManagement, FamilyRelationship.Identity,
            Intent.Announcement, Intent.Report
    }, {
            // If agents of an organization are assigned a coordination task,
            // they are expected to have at least one sharing commitment
            // with agents of a sibling organization
            // assigned to another coordination task
            // where the intent of the info flow is an announcement or a report
            Category.InterOperationsCoordination, Category.InterOperationsCoordination, FamilyRelationship.Sibling,
            FamilyRelationship.Cousin, Intent.Announcement, Intent.Report
    }, {
            // If agents of an organization are assigned a direction task,
            // they are expected to have at least one sharing commitment
            // with agents of a descendant organization
            // assigned to an operations management task
            // where the intent of the info flow is a command
            Category.Direction, Category.OperationsManagement, FamilyRelationship.Descendant, Intent.Command
    }, {
            // If agents of an organization are assigned a operations management task,
            // they are expected to have at least one sharing commitment
            // with agents of an ancestor organization
            // assigned to a direction task
            // where the intent of the info flow is a command
            Category.OperationsManagement, Category.Direction, FamilyRelationship.Ancestor, Intent.Report
    }, {
            // If agents of an organization are assigned an operations task,
            // they are expected to have at least one sharing commitment
            // with agents of an ancestor organization
            // assigned to an audit task
            // where the intent of the info flow is a report
            Category.Operations, Category.Audit, FamilyRelationship.Parent, FamilyRelationship.Ancestor, Intent.Report
    }, {
            // If agents of an organization are assigned an analysis task,
            // they are expected to have at least one sharing commitment
            // with agents of an ancestor, sibling or same organization
            // assigned to a planning/preparing task
            // where the intent of the info flow is a report
            Category.Analysis, Category.PlanningPreparing, FamilyRelationship.Identity, FamilyRelationship.Ancestor,
            FamilyRelationship.Sibling, Intent.Report
    }, {
            // If agents of an organization are assigned a policy setting task,
            // they are expected to have at least one sharing commitment
            // with agents of the same organization or of an organization they are an ancestor of
            // assigned to a direction task
            // where the intent of the info flow is a feedback
            Category.PolicySetting, Category.Direction, FamilyRelationship.Identity, FamilyRelationship.Ancestor,
            Intent.Feedback
    },
    };

    @Override
    public List<Issue> detectIssues( QueryService queryService, ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Organization org = (Organization) modelObject;
        if ( queryService.isInvolvementExpected( org ) ) {
            List<Commitment> commitments = queryService.findAllCommitmentsOf( org,
                                                                              queryService.getAssignments( false ),
                                                                              queryService.findAllFlows() );
            for ( Object[] expectation : Expectations ) {
                List<FamilyRelationship> familyRels = new ArrayList<FamilyRelationship>();
                List<Intent> intents = new ArrayList<Intent>();
                Iterator<Object> iterator = Arrays.asList( expectation ).iterator();
                Category fromCat = (Category) iterator.next();
                Category toCat = (Category) iterator.next();
                while ( iterator.hasNext() ) {
                    Object next = iterator.next();
                    if ( next instanceof FamilyRelationship )
                        familyRels.add( (FamilyRelationship) next );
                    if ( next instanceof Intent )
                        intents.add( (Intent) next );
                }
                verifyExpectation( org, fromCat, toCat, familyRels, intents, commitments, issues, queryService );
            }
        }
        return issues;
    }

    private void verifyExpectation( Organization fromOrg, Category fromCat, Category toCat,
                                    List<FamilyRelationship> familyRels, List<Intent> intents,
                                    List<Commitment> commitments, List<Issue> issues, QueryService queryService ) {

        // Verify that there is at least one commitment with an org
        // with a required family relationship
        // and assigned a task of a required category
        boolean isExpected = false;
        for ( Commitment commitment : commitments ) {
            Assignment committer = commitment.getCommitter();
            Category committerPartCat = committer.getPart().getCategory();
            if ( committerPartCat != null && committerPartCat.equals( fromCat ) ) {
                isExpected = true;
                Assignment beneficiary = commitment.getBeneficiary();
                Category beneficiaryPartCat = beneficiary.getPart().getCategory();
                Intent intent = commitment.getSharing().getIntent();
                if ( beneficiaryPartCat != null && beneficiaryPartCat.equals( toCat ) ) {
                    FamilyRelationship familyRel =
                            queryService.findFamilyRelationship( beneficiary.getOrganization(), fromOrg );
                    if ( familyRels.contains( familyRel )
                         && ( intents.isEmpty() || intent != null && intents.contains( intent ) ) )
                        return; // expectation met
                }
            }
        }
        if ( isExpected ) {
            // expected commitment not found
            Issue issue = makeIssue( queryService, Issue.COMPLETENESS, fromOrg );
            issue.setDescription( issueDescription( fromOrg, fromCat, toCat, familyRels, intents ) );
            issue.setRemediation( issueRemediation( fromOrg, fromCat, toCat, familyRels, intents ) );
            issue.setSeverity( Level.Low );
            issues.add( issue );
        }
    }

    private String issueDescription( Organization fromOrg, Category fromCat, Category toCat,
                                     List<FamilyRelationship> familyRels, List<Intent> intents ) {
        StringBuilder sb = new StringBuilder();
        sb.append( "The organization \"" );
        sb.append( fromOrg.getName() );
        sb.append( "\" is assigned at least one " );
        sb.append( fromCat.getLabel().toLowerCase() );
        sb.append( " task but it does not commit to share " );
        sb.append( intentDescription( intents ) );
        sb.append( "information with " );
        sb.append( ChannelsUtils.startsWithVowel( toCat.getLabel() ) ? "an " : "a " );
        sb.append( toCat.getLabel().toLowerCase() );
        sb.append( " task assigned to " );
        sb.append( familyRelsDescription( familyRels ) );
        sb.append( " organization." );
        return sb.toString();
    }

    private static String intentDescription( List<Intent> intents ) {
        StringBuilder sb = new StringBuilder();
        if ( intents.isEmpty() ) {
            sb.append( " any " );
        } else {
            Iterator<Intent> iterator = intents.iterator();
            while ( iterator.hasNext() ) {
                sb.append( iterator.next().getLabel().toLowerCase() );
                if ( iterator.hasNext() )
                    sb.append( " or " );
                else
                    sb.append( " " );
            }
        }
        return sb.toString();
    }

    private static String familyRelsDescription( List<FamilyRelationship> familyRels ) {
        Collections.sort( familyRels );
        StringBuilder sb = new StringBuilder();
        if ( !familyRels.isEmpty() ) {
            Iterator<FamilyRelationship> iterator = familyRels.iterator();
            while ( iterator.hasNext() ) {
                FamilyRelationship rel = iterator.next();
                if ( rel.equals( FamilyRelationship.Identity ) )
                    sb.append( " the same " );
                else {
                    String relLabel = rel.name().toLowerCase();
                    sb.append( ChannelsUtils.startsWithVowel( relLabel ) ? " an " : " a " );
                    sb.append( relLabel );
                }
                if ( iterator.hasNext() )
                    sb.append( " or" );
            }
        }
        return sb.toString();
    }

    private String issueRemediation( Organization fromOrg, Category fromCat, Category toCat,
                                     List<FamilyRelationship> familyRels, List<Intent> intents ) {
        StringBuilder sb = new StringBuilder();
        sb.append( "Connect " );
        sb.append( ChannelsUtils.startsWithVowel( fromCat.getLabel() ) ? "an " : "a " );
        sb.append( fromCat.getLabel().toLowerCase() );
        sb.append( " task assigned to \"" );
        sb.append( fromOrg.getName() );
        sb.append( "\" to " );
        sb.append( ChannelsUtils.startsWithVowel( toCat.getLabel() ) ? "an " : "a " );
        sb.append( toCat.getLabel().toLowerCase() );
        sb.append( " task assigned to " );
        sb.append( familyRelsDescription( familyRels ) );
        sb.append( " organization, and share " );
        sb.append( intentDescription( intents ) );
        sb.append( "information with it." );
        return sb.toString();
    }

    @Override
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Organization;
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "Expected information sharing commitments is missing";
    }

    @Override
    public boolean canBeWaived() {
        return true;
    }
}
