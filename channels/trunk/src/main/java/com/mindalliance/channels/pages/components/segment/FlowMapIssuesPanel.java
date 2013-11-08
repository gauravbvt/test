package com.mindalliance.channels.pages.components.segment;

import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.SegmentObject;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.pages.components.AbstractIssueTablePanel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/7/13
 * Time: 10:43 AM
 */
public class FlowMapIssuesPanel extends AbstractIssueTablePanel {

    private final IModel<Part> selectedPart;
    private final IModel<Flow> selectedFlow;

    private static final String DETECTED_OR_REPORTED = "Detected or reported";
    private static final String DETECTED = "Detected";
    private static final String REPORTED = "Reported";

    private static final String[] DETECTED_VS_REPORTED_OPTIONS = {
            DETECTED_OR_REPORTED,
            DETECTED,
            REPORTED
    };
    private static final String ANY = "Any";

    private Level severity;

    private boolean peripheralIssuesIncluded = false;
    /**
     * Whether to show waived issues.
     */
    private boolean includeWaived = false;

    private String detectedOrReported = DETECTED_OR_REPORTED;

    /**
     * Maximum number of rows of issues to show at a time.
     */
    private static final int MAX_ROWS = 10;

    public FlowMapIssuesPanel( String id, IModel<Part> selectedPart, IModel<Flow> selectedFlow ) {
        super( id, null, MAX_ROWS );
        this.selectedPart = selectedPart;
        this.selectedFlow = selectedFlow;
        doInit();
    }

    protected void init() {
        // do nothing to defer initialization
    }

    private void doInit() {
        super.init();
    }

    @Override
    public void redisplay( AjaxRequestTarget target ) {
        super.init();
        super.redisplay( target );
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

    private void addReportedVsDetected() {
        DropDownChoice<String> reportedVsDetectedChoice = new DropDownChoice<String>(
                "detectedReported",
                new PropertyModel<String>( this, "detectedOrReported" ),
                Arrays.asList( DETECTED_VS_REPORTED_OPTIONS )
        );
        reportedVsDetectedChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                updateIssuesTable( target );
            }
        } );
        add( reportedVsDetectedChoice );
    }

    public String getDetectedOrReported() {
        return detectedOrReported;
    }

    public void setDetectedOrReported( String detectedOrReported ) {
        this.detectedOrReported = detectedOrReported;
    }

    private void addSelectedLabel() {
        SegmentObject segmentObject = getSelectedSegmentObject();
        String label = segmentObject.getTypeName()
                + " \""
                + ( segmentObject instanceof Part ? ( (Part) segmentObject ).getTask() : segmentObject.getName() )
                + "\"";
        add( new Label( "about", label ) );
    }

    private void addIncludePeripheralIssues() {
        AjaxCheckBox includePeripheralCheckBox = new AjaxCheckBox(
                "includePeripheral",
                new PropertyModel<Boolean>( this, "peripheralIssuesIncluded" )
        ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                updateIssuesTable( target );
            }
        };
        add( includePeripheralCheckBox );
    }

    private void addIncludeWaived() {
        AjaxCheckBox includeWaivedCheckBox = new AjaxCheckBox(
                "includeWaived",
                new PropertyModel<Boolean>( this, "includeWaived" ) ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                updateIssuesTable( target );
            }
        };
        add( includeWaivedCheckBox );
    }


    public boolean isPeripheralIssuesIncluded() {
        return peripheralIssuesIncluded;
    }

    public void setPeripheralIssuesIncluded( boolean peripheralIssuesIncluded ) {
        this.peripheralIssuesIncluded = peripheralIssuesIncluded;
    }

    public boolean isIncludeWaived() {
        return includeWaived;
    }

    public void setIncludeWaived( boolean includeWaived ) {
        this.includeWaived = includeWaived;
    }

    @Override
    protected void addFilters() {
        addOfSeverity();
        addReportedVsDetected();
        addSelectedLabel();
        addIncludePeripheralIssues();
        addIncludeWaived();
    }

    @Override
    public List<? extends Issue> getIssues() {
        SegmentObject about = getSelectedSegmentObject();
        List<Issue> issues = new ArrayList<Issue>();
        for ( Issue issue : getAnalyst().listIssues( getQueryService(), (ModelObject) about, true, includeWaived ) ) {
            issues.add( issue );
        }
        if ( isPeripheralIssuesIncluded() ) {
            for ( ModelObject peripheral : findPeripheralModelObjects( about ) ) {
                for ( Issue issue : getAnalyst().listIssues( getQueryService(), peripheral, true, includeWaived ) ) {
                    issues.add( issue );
                }
            }
        }
        issues = filterByType( issues, getIssueType() );
        issues = filterBySeverity( issues );
        issues = filterByReportedVsDetected( issues );
        return issues;
    }

    private Set<ModelObject> findPeripheralModelObjects( SegmentObject segmentObject ) {
        Set<ModelObject> mos = new HashSet<ModelObject>();
        mos.add( getPlan() );
        mos.add( segmentObject.getSegment() );
        if ( segmentObject instanceof Part ) {
            Part part = (Part) segmentObject;
            ChannelsUtils.addIfNotNull( mos,
                    part.getFunction(),
                    part.getActor(),
                    part.getInitiatedEvent(),
                    part.getJurisdiction(),
                    part.getOrganization(),
                    part.getRole(),
                    part.getKnownLocation()
            );
        } else if ( segmentObject instanceof Flow ) {
            Flow flow = (Flow) segmentObject;
            ChannelsUtils.addIfNotNull( mos,
                    flow.getInfoProduct()
            );
            for ( Channel channel : flow.getEffectiveChannels() ) {
                ChannelsUtils.addIfNotNull( mos,
                        channel.getFormat(),
                        channel.getMedium()
                );
            }
        }
        return mos;
    }

    @SuppressWarnings( "unchecked" )
    private List<Issue> filterByType( List<Issue> issues, final String issueType ) {
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
    private List<Issue> filterBySeverity( List<Issue> issues ) {
        return (List<Issue>) CollectionUtils.select(
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
    private List<Issue> filterByReportedVsDetected( List<Issue> issues ) {
        return (List<Issue>) CollectionUtils.select(
                issues,
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        return getDetectedOrReported().equals( DETECTED_OR_REPORTED )
                                || ( ( getDetectedOrReported().equals( DETECTED ) ) && ( (Issue) obj ).isDetected() )
                                || ( ( getDetectedOrReported().equals( REPORTED ) ) && !( (Issue) obj ).isDetected() );
                    }
                }
        );
    }


    private SegmentObject getSelectedSegmentObject() {
        Flow flow = selectedFlow.getObject();
        return flow != null
                ? flow
                : selectedPart.getObject();
    }


}
