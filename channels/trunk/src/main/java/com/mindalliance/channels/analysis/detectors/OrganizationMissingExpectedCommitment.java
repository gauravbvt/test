package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Assignment;
import com.mindalliance.channels.model.Commitment;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.Level;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.util.ChannelsUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Expected sharing commitment missing between tasks assigned to organizations
 * based on task categories and flow intents.
 * given their respective categories.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Sep 10, 2010
 * Time: 2:05:24 PM
 */
public class OrganizationMissingExpectedCommitment extends AbstractIssueDetector {

    // todo - OPTIMIZE before reactivation

    private static Object[][] Expectations = {
            {
                    // If agents of an organization are assigned an operations management task,
                    // they are expected to have at least one sharing commitment
                    // with agents of the same organization or a child organization
                    // assigned to an operations task
                    // where the intent of the flow is a command
                    Part.Category.OperationsManagement,  // if task of this category assigned to fromOrg
                    Part.Category.Operations,            // expected commitment expected with task assigned to toOrg
                    Organization.FamilyRelationship.Identity,
                    Organization.FamilyRelationship.Child, // if one of alternate relationships toOrg -> fromOrg. None = any
                    Flow.Intent.Command     // alternate expected sharing flow intents. None = any.
            },
            {
                    // If agents of an organization are assigned an operations task,
                    // they are expected to have at least one sharing commitment
                    // with agents of the same organization or a parent organization
                    // assigned to an operations management task
                    // where the intent of the info flow is a report
                    Part.Category.Operations,
                    Part.Category.OperationsManagement,
                    Organization.FamilyRelationship.Identity,
                    Organization.FamilyRelationship.Parent,
                    Flow.Intent.Report
            },
            {
                    // If agents of an organization are assigned a coordination task,
                    // they are expected to have at least one sharing commitment
                    // with agents of the same organization
                    // assigned to an operations management task
                    // where the intent of the info flow is an announcement or a report
                    Part.Category.InterOperationsCoordination,
                    Part.Category.OperationsManagement,
                    Organization.FamilyRelationship.Identity,
                    Flow.Intent.Announcement,
                    Flow.Intent.Report
            },
            {
                    // If agents of an organization are assigned a coordination task,
                    // they are expected to have at least one sharing commitment
                    // with agents of a sibling organization
                    // assigned to another coordination task
                    // where the intent of the info flow is an announcement or a report
                    Part.Category.InterOperationsCoordination,
                    Part.Category.InterOperationsCoordination,
                    Organization.FamilyRelationship.Sibling,
                    Organization.FamilyRelationship.Cousin,
                    Flow.Intent.Announcement,
                    Flow.Intent.Report
            },
            {
                    // If agents of an organization are assigned a direction task,
                    // they are expected to have at least one sharing commitment
                    // with agents of a descendant organization
                    // assigned to an operations management task
                    // where the intent of the info flow is a command
                    Part.Category.Direction,
                    Part.Category.OperationsManagement,
                    Organization.FamilyRelationship.Descendant,
                    Flow.Intent.Command
            },
            {
                    // If agents of an organization are assigned a operations management task,
                    // they are expected to have at least one sharing commitment
                    // with agents of an ancestor organization
                    // assigned to a direction task
                    // where the intent of the info flow is a command
                    Part.Category.OperationsManagement,
                    Part.Category.Direction,
                    Organization.FamilyRelationship.Ancestor,
                    Flow.Intent.Report
            },
            {
                    // If agents of an organization are assigned an operations task,
                    // they are expected to have at least one sharing commitment
                    // with agents of an ancestor organization
                    // assigned to an audit task
                    // where the intent of the info flow is a report
                    Part.Category.Operations,
                    Part.Category.Audit,
                    Organization.FamilyRelationship.Parent,
                    Organization.FamilyRelationship.Ancestor,
                    Flow.Intent.Report
            },
            {
                    // If agents of an organization are assigned an analysis task,
                    // they are expected to have at least one sharing commitment
                    // with agents of an ancestor, sibling or same organization
                    // assigned to a planning/preparing task
                    // where the intent of the info flow is a report
                    Part.Category.Analysis,
                    Part.Category.PlanningPreparing,
                    Organization.FamilyRelationship.Identity,
                    Organization.FamilyRelationship.Ancestor,
                    Organization.FamilyRelationship.Sibling,
                    Flow.Intent.Report
            },
            {
                    // If agents of an organization are assigned a policy setting task,
                    // they are expected to have at least one sharing commitment
                    // with agents of the same organization or of an organization they are an ancestor of
                    // assigned to a direction task
                    // where the intent of the info flow is a feedback
                    Part.Category.PolicySetting,
                    Part.Category.Direction,
                    Organization.FamilyRelationship.Identity,
                    Organization.FamilyRelationship.Ancestor,
                    Flow.Intent.Feedback
            },
    };

    public OrganizationMissingExpectedCommitment() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Organization org = (Organization) modelObject;
        if ( getQueryService().isInvolvementExpected( org ) ) {
            List<Commitment> commitments = getQueryService().findAllCommitmentsOf(
                    org,
                    getQueryService().getAssignments( false ),
                    getQueryService().findAllFlows() );
            for ( Object[] expectation : Expectations ) {
                List<Organization.FamilyRelationship> familyRels = new ArrayList<Organization.FamilyRelationship>();
                List<Flow.Intent> intents = new ArrayList<Flow.Intent>();
                Iterator<Object> iterator = Arrays.asList( expectation ).iterator();
                Part.Category fromCat = (Part.Category) iterator.next();
                Part.Category toCat = (Part.Category) iterator.next();
                while ( iterator.hasNext() ) {
                    Object next = iterator.next();
                    if ( next instanceof Organization.FamilyRelationship ) {
                        familyRels.add( (Organization.FamilyRelationship) next );
                    }
                    if ( next instanceof Flow.Intent ) {
                        intents.add( (Flow.Intent) next );
                    }
                }
                verifyExpectation( org, fromCat, toCat, familyRels, intents, commitments, issues );
            }
        }
        return issues;
    }

    private void verifyExpectation(
            Organization fromOrg,
            Part.Category fromCat,
            Part.Category toCat,
            List<Organization.FamilyRelationship> familyRels,
            List<Flow.Intent> intents,
            List<Commitment> commitments,
            List<Issue> issues ) {
        // Verify that there is at least one commitment with an org
        // with a required family relationship
        // and assigned a task of a required category
        boolean isExpected = false;
        for ( Commitment commitment : commitments ) {
            Assignment committer = commitment.getCommitter();
            Part.Category committerPartCat = committer.getPart().getCategory();
            if ( committerPartCat != null && committerPartCat.equals( fromCat ) ) {
                isExpected = true;
                Assignment beneficiary = commitment.getBeneficiary();
                Part.Category beneficiaryPartCat = beneficiary.getPart().getCategory();
                Flow.Intent intent = commitment.getSharing().getIntent();
                if ( beneficiaryPartCat != null && beneficiaryPartCat.equals( toCat ) ) {
                    Organization.FamilyRelationship familyRel = getQueryService().findFamilyRelationship(
                            beneficiary.getOrganization(),
                            fromOrg
                    );
                    if ( familyRels.contains( familyRel ) ) {
                        if ( intents.isEmpty() || ( intent != null && intents.contains( intent ) ) )
                            return; // expectation met
                    }
                }
            }
        }
        if ( isExpected ) {
            // expected commitment not found
            Issue issue = makeIssue( Issue.COMPLETENESS, fromOrg );
            issue.setDescription( issueDescription( fromOrg, fromCat, toCat, familyRels, intents ) );
            issue.setRemediation( issueRemediation( fromOrg, fromCat, toCat, familyRels, intents ) );
            issue.setSeverity( Level.Low );
            issues.add( issue );
        }
    }

    private String issueDescription(
            Organization fromOrg,
            Part.Category fromCat,
            Part.Category toCat,
            List<Organization.FamilyRelationship> familyRels,
            List<Flow.Intent> intents ) {
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

    private String intentDescription( List<Flow.Intent> intents ) {
        StringBuilder sb = new StringBuilder();
        if ( intents.isEmpty() ) {
            sb.append( " any " );
        } else {
            Iterator<Flow.Intent> iterator = intents.iterator();
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

    private String familyRelsDescription( List<Organization.FamilyRelationship> familyRels ) {
        Collections.sort( familyRels );
        StringBuilder sb = new StringBuilder();
        if ( !familyRels.isEmpty() ) {
            Iterator<Organization.FamilyRelationship> iterator = familyRels.iterator();
            while ( iterator.hasNext() ) {
                Organization.FamilyRelationship rel = iterator.next();
                if ( rel.equals( Organization.FamilyRelationship.Identity ) ) {
                    sb.append( " the same " );
                } else {
                    String relLabel = rel.name().toLowerCase();
                    sb.append( ChannelsUtils.startsWithVowel( relLabel ) ? " an " : " a " );
                    sb.append( relLabel );
                }
                if ( iterator.hasNext() ) sb.append( " or" );
            }
        }
        return sb.toString();
    }

    private String issueRemediation(
            Organization fromOrg,
            Part.Category fromCat,
            Part.Category toCat,
            List<Organization.FamilyRelationship> familyRels,
            List<Flow.Intent> intents ) {
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

    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Organization;
    }

    /**
     * {@inheritDoc}
     */
    public String getTestedProperty() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    protected String getKindLabel() {
        return "Expected sharing commitments missing";
    }

    /**
     * {@inheritDoc}
     */
    public boolean canBeWaived() {
        return true;
    }
}
