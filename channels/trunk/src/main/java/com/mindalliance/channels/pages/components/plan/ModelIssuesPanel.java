package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.core.Matcher;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.model.SegmentObject;
import com.mindalliance.channels.engine.analysis.Doctor;
import com.mindalliance.channels.pages.components.AbstractIssueTablePanel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Plan issues panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 1, 2009
 * Time: 11:43:52 AM
 */
public class ModelIssuesPanel extends AbstractIssueTablePanel {
    /**
     * Maximum number of rows of issues to show at a time.
     */
    private static final int MAX_ROWS = 10;
    /**
     * Whether to show waived issues.
     */
    private boolean includeWaived = false;
    /**
     * Segment filtered on, or null.
     */
    private Segment segment;
    private Level severity;
    private List<String> kindHints = new ArrayList<String>();
    private static final String ANY = "Any";

    public ModelIssuesPanel( String id ) {
        super( id, null, MAX_ROWS );
    }

    protected void addFilters() {
        addIncludeWaived();
        addInSegment();
        addOfSeverity();
        addOfKind();
    }

    private void addInSegment() {
        DropDownChoice<String> segmentChoice = new DropDownChoice<String>(
                "segment",
                new PropertyModel<String>( this, "segmentName" ),
                getSegmentsNames()
        );
        segmentChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                updateIssuesTable( target );
            }
        } );
        add( segmentChoice );
    }

    private List<String> getSegmentsNames() {
        List<String> segmentNames = new ArrayList<String>();
        segmentNames.add( ANY );
        List<String> sorted = new ArrayList<String>();
        for ( Segment seg : getSegments() ) {
            sorted.add( StringUtils.abbreviate( seg.getName(), 20 ) );
        }
        Collections.sort( sorted );
        segmentNames.addAll( sorted );
        return segmentNames;
    }

    private List<? extends Segment> getSegments() {
        return new ArrayList<Segment>( getCollaborationModel().getSegments() );
    }

    private void addOfSeverity() {
        DropDownChoice<String> severityChoice = new DropDownChoice<String>(
                "severity",
                new PropertyModel<String>( this, "severityName" ),
                getSeverityNames()
        );
        severityChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                updateIssuesTable( target );
            }
        } );
        add( severityChoice );
    }

    private List<String> getSeverityNames() {
        List<String> severityNames = new ArrayList<String>();
        severityNames.add( ANY );
        for ( Level level : Level.values() ) {
            severityNames.add( level.getNegativeLabel() );
        }
        return severityNames;
    }


    private void addOfKind() {
        TextField<String> kindHintField = new TextField<String>(
                "kind",
                new PropertyModel<String>( this, "kindHint" ) );
        kindHintField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                updateIssuesTable( target );
            }
        } );
        add( kindHintField );
    }

    private void addIncludeWaived() {
        CheckBox includeWaivedCheckBox = new CheckBox(
                "includeWaived",
                new PropertyModel<Boolean>( this, "includeWaived" ) );
        includeWaivedCheckBox.setOutputMarkupId( true );
        includeWaivedCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                updateIssuesTable( target );
            }
        } );
        addOrReplace( includeWaivedCheckBox );
    }

    public boolean isIncludeWaived() {
        return includeWaived;
    }

    public void setIncludeWaived( boolean includeWaived ) {
        this.includeWaived = includeWaived;
    }

    public String getSegmentName() {
        return segment == null ? ANY : segment.getName();
    }

    public void setSegmentName( final String val ) {
        if ( val.equals( ANY ) ) {
            segment = null;
        } else {
            segment = (Segment) CollectionUtils.find(
                    getSegments(),
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            return ( (Segment) object ).getName().equals( val );
                        }
                    }
            );

        }
    }


    public String getSeverityName() {
        return severity == null ? ANY : severity.getNegativeLabel();
    }

    public void setSeverityName( final String val ) {
        if ( val.equals( ANY ) ) {
            severity = null;
        } else {
            severity = (Level) CollectionUtils.find(
                    Arrays.asList( Level.values() ),
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            return ( (Level) object ).getNegativeLabel().equals( val );
                        }
                    }
            );

        }
    }

    public String getKindHint() {
        return StringUtils.join( kindHints, " " );
    }

    public void setKindHint( String val ) {
        this.kindHints =
                val == null
                        ? new ArrayList<String>() :
                        Arrays.asList( StringUtils.split( val.toLowerCase(), ' ' ) );
    }

    /**
     * Get all issues, possibly filtered on the model object they are about.
     *
     * @return a list of issues
     */
    public List<? extends Issue> getIssues() {
        // Get issues by about and waived
        Identifiable about = getAbout();
        Doctor doctor = getCommunityService().getDoctor();
        List<? extends Issue> issues = about != null ? doctor.listIssues( getCommunityService(), about, true, includeWaived ) :
                             includeWaived ? doctor.findAllIssues( getCommunityService() )
                                           : doctor.findAllUnwaivedIssues(getCommunityService());

        issues = filterByType( issues, getIssueType() );
        issues = filterBySegment( issues );
        issues = filterBySeverity( issues );
        issues = filterByKind( issues );

        return issues;
    }

    @SuppressWarnings( "unchecked" )
    private List<Issue> filterByType( List<? extends Issue> issues, final String issueType ) {
        return (List<Issue>) CollectionUtils.select(
                issues,
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        return ( issueType.equals( ALL )
                                || ( (Issue) obj ).getType().equals( issueType ) );
                    }
                }
        );
    }

    @SuppressWarnings( "unchecked" )
    private List<? extends Issue> filterBySegment( List<? extends Issue> issues ) {
        return (List<? extends Issue>) CollectionUtils.select(
                issues,
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        Identifiable about = ( (Issue) obj ).getAbout();
                        return segment == null ||
                                about instanceof SegmentObject
                                        && ( (SegmentObject) about ).getSegment().equals( segment );
                    }
                }
        );
    }

    @SuppressWarnings( "unchecked" )
    private List<? extends Issue> filterBySeverity( List<? extends Issue> issues ) {
        return (List<? extends Issue>) CollectionUtils.select(
                issues,
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        return severity == null
                                || ( (Issue) obj ).getSeverity().equals( severity );
                    }
                }
        );
    }

    @SuppressWarnings( "unchecked" )
    private List<Issue> filterByKind( List<? extends Issue> issues ) {
        return (List<Issue>) CollectionUtils.select( issues, new Predicate() {
            public boolean evaluate( Object obj ) {
                return kindHints == null || kindHints.isEmpty() || Matcher.matchesAll(
                        ( (Issue) obj ).getDetectorLabel(), kindHints );
            }
        } );
    }

}
