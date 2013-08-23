/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.ExternalFlow;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.engine.analysis.graph.SegmentRelationship;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.diagrams.AbstractDiagramAjaxBehavior;
import com.mindalliance.channels.pages.components.diagrams.PlanMapDiagramPanel;
import com.mindalliance.channels.pages.components.diagrams.Settings;
import com.mindalliance.channels.pages.components.segment.ExternalFlowsPanel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Plan map panel.
 */
public class PlanSegmentsMapPanel extends AbstractUpdatablePanel {

    /**
     * Expected screen resolution.
     */
    private static final double DPI = 96.0;

    /**
     * Default page size for external flows panel.
     */
    private static final int PAGE_SIZE = 10;

    /**
     * DOM identifier for resizeable element.
     */
    private static final String DOM_IDENTIFIER = ".all-segments .picture";

    /**
     * Whether to group segments by phase.
     */
    private boolean groupByPhase;

    /**
     * Whether to group by event.
     */
    private boolean groupByEvent = true;

    /**
     * Selected phase or event in plan.
     */
    private ModelObject selectedGroup;

    /**
     * Selected segment in plan.
     */
    private Segment selectedSegment;

    /**
     * Selected segment relationship in plan.
     */
    private SegmentRelationship selectedSgRel;

    /**
     * Plan map diagram panel.
     */
    private PlanMapDiagramPanel planMapDiagramPanel;

    /**
     * Width, height dimension constraints on the plan map diagram. In inches. None if any is 0.
     */
    private double[] diagramSize = new double[2];

    @SpringBean
    private Analyst analyst;

    /**
     * Whether plan map is reduced to fit.
     */
    private boolean reducedToFit;

    /**
     * Sizing toggle label..
     */
    private Label sizingLabel;

    public PlanSegmentsMapPanel( String id, IModel<? extends Identifiable> model, Set<Long> expansions ) {
        super( id, model, expansions );
        init();
    }

    @Override
    public void redisplay( AjaxRequestTarget target ) {
        init();
        super.redisplay( target );
    }

    private void init() {
        addGroupingChoices();
        addPlanSizing();
        addPlanMapDiagramPanel();
        addFlowsTitleLabel();
        addExternalFlowsPanel();
        addCausesTitleLabel();
        addCausesPanel();
    }

    private void addGroupingChoices() {
        CheckBox groupByPhaseCheckBox =
                new CheckBox( "groupByPhase", new PropertyModel<Boolean>( this, "groupByPhase" ) );
        groupByPhaseCheckBox.setOutputMarkupId( true );
        groupByPhaseCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                refresh( target );
            }
        } );
        addOrReplace( groupByPhaseCheckBox );
        CheckBox groupByEventCheckBox =
                new CheckBox( "groupByEvent", new PropertyModel<Boolean>( this, "groupByEvent" ) );
        groupByEventCheckBox.setOutputMarkupId( true );
        groupByEventCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                refresh( target );
            }
        } );
        addOrReplace( groupByEventCheckBox );
    }

    public boolean isGroupByPhase() {
        return groupByPhase;
    }

    public void setGroupByPhase( boolean groupByPhase ) {
        this.groupByPhase = groupByPhase;
        groupByEvent = !groupByPhase;
        selectedGroup = null;
    }

    public boolean isGroupByEvent() {
        return groupByEvent;
    }

    public void setGroupByEvent( boolean groupByEvent ) {
        this.groupByEvent = groupByEvent;
        groupByPhase = !groupByEvent;
        selectedGroup = null;
    }

    private void addPlanSizing() {
        sizingLabel = new Label( "fit", new Model<String>( reducedToFit ? "Full size" : "Reduce to fit" ) );
        sizingLabel.setOutputMarkupId( true );
        sizingLabel.add( new AbstractDiagramAjaxBehavior( DOM_IDENTIFIER, reducedToFit ) {
            @Override
            protected void respond( AjaxRequestTarget target ) {
                RequestCycle requestCycle = RequestCycle.get();
                if ( !reducedToFit ) {
                    String swidth = requestCycle.getRequest().getQueryParameters().getParameterValue( "width" ).toString();
                    String sheight = requestCycle.getRequest().getQueryParameters().getParameterValue( "height" ).toString();
                    diagramSize[0] = ( Double.parseDouble( swidth ) - 20 ) / DPI;
                    diagramSize[1] = ( Double.parseDouble( sheight ) - 20 ) / DPI;
                } else
                    diagramSize = new double[2];

                reducedToFit = !reducedToFit;
                addPlanMapDiagramPanel();
                target.add( planMapDiagramPanel );
                addPlanSizing();
                target.add( sizingLabel );
            }
        } );
        addOrReplace( sizingLabel );
    }

    private void addPlanMapDiagramPanel() {
        Settings settings = diagramSize[0] <= 0.0 || diagramSize[1] <= 0.0 ?
                            new Settings( DOM_IDENTIFIER, null, null, true, true ) :
                            new Settings( DOM_IDENTIFIER, null, diagramSize, true, true );
        planMapDiagramPanel = new PlanMapDiagramPanel( "plan-map",
                                                       new PropertyModel<List<Segment>>( this, "allSegments" ),
                                                       groupByPhase,
                                                       groupByEvent,
                                                       selectedGroup,
                                                       selectedSegment,
                                                       selectedSgRel,
                                                       settings );
        planMapDiagramPanel.setOutputMarkupId( true );
        addOrReplace( planMapDiagramPanel );
    }

    private void addFlowsTitleLabel() {
        Label flowsTitleLabel = new Label( "flows-title", new PropertyModel<String>( this, "flowsTitle" ) );
        flowsTitleLabel.setOutputMarkupId( true );
        addOrReplace( flowsTitleLabel );
    }

    private void addCausesTitleLabel() {
        Label causesTitleLabel = new Label( "causes-title", new PropertyModel<String>( this, "causesTitle" ) );
        causesTitleLabel.setOutputMarkupId( true );
        addOrReplace( causesTitleLabel );
    }

    private void addExternalFlowsPanel() {
        addOrReplace( new ExternalFlowsPanel( "flows",
                                              new PropertyModel<ArrayList<ExternalFlow>>( this, "externalFlows" ),
                                              PAGE_SIZE,
                                              getExpansions() )
                              .setOutputMarkupId( true ) );
    }

    private void addCausesPanel() {
        SegmentCausesPanel segmentCausesPanel =
                new SegmentCausesPanel( "causes", getSegmentRelationships(), PAGE_SIZE, getExpansions() );
        segmentCausesPanel.setOutputMarkupId( true );
        addOrReplace( segmentCausesPanel );
    }

    /**
     * Get segment to map.
     *
     * @return an array list of segment
     */
    public List<Segment> getAllSegments() {
        return getQueryService().list( Segment.class );
    }

    /**
     * Get flows title.
     *
     * @return a string
     */
    public String getFlowsTitle() {
        if ( selectedGroup != null ) {
            if ( groupByPhase ) {
                return "Flows connecting plan segments in phase \"" + selectedGroup.getName() + "\"";
            } else {
                return "Flows connecting plan segments about event \"" + selectedGroup.getName() + "\"";
            }
        } else if ( selectedSegment != null ) {
            return "Flows connecting \"" + selectedSegment.getName() + "\" to other plan segments";
        } else if ( selectedSgRel != null ) {
            Segment fromSegment = selectedSgRel.getFromSegment( getQueryService() );
            Segment toSegment = selectedSgRel.getToSegment( getQueryService() );
            if ( fromSegment == null || toSegment == null ) {
                return "*** You need to refresh ***";
            } else {
                return "Flows connecting \"" + fromSegment.getName() + "\" to \"" + toSegment.getName() + "\"";
            }
        } else {
            return "All inter-segment flows";
        }
    }

    /**
     * Get flows title.
     *
     * @return a string
     */
    public String getCausesTitle() {
        if ( selectedGroup != null ) {
            if ( groupByPhase ) {
                return "Causations for segments in phase \"" + selectedGroup.getName() + "\"";
            } else {
                return "Causations for segments about event \"" + selectedGroup.getName() + "\"";
            }
        } else if ( selectedSegment != null ) {
            return "Causations for segment \"" + selectedSegment.getName() + "\"";
        } else if ( selectedSgRel != null ) {
            Segment fromSegment = selectedSgRel.getFromSegment( getQueryService() );
            Segment toSegment = selectedSgRel.getToSegment( getQueryService() );
            if ( fromSegment == null || toSegment == null ) {
                return "*** You need to refresh ***";
            } else {
                return "How \"" + fromSegment.getName() + "\" impacts \"" + toSegment.getName() + "\"";
            }
        } else {
            return "All causations";
        }
    }

    /**
     * Get external flows.
     *
     * @return a list of external flows
     */
    public List<ExternalFlow> getExternalFlows() {
        if ( selectedSgRel != null )
            return selectedSgRel.getExternalFlows();

        QueryService queryService = getQueryService();
        if ( selectedGroup != null ) {
            List<ExternalFlow> externalFlows = new ArrayList<ExternalFlow>();
            List<Segment> segmentsInGroup = getSegmentsInGroup();
            List<Segment> allSegments = getAllSegments();
            for ( Segment segment : allSegments ) {
                for ( Segment other : allSegments ) {
                    if ( !segment.equals( other ) && ( segmentsInGroup.contains( segment ) || segmentsInGroup.contains(
                            other ) ) )
                    {
                        SegmentRelationship scRel = analyst.findSegmentRelationship( queryService, segment, other );
                        if ( scRel != null )
                            externalFlows.addAll( scRel.getExternalFlows() );
                    }
                }
            }
            return externalFlows;
        } else if ( selectedSegment != null ) {
            List<ExternalFlow> externalFlows = new ArrayList<ExternalFlow>();
            List<Segment> allSegments = getAllSegments();
            for ( Segment other : allSegments ) {
                if ( !selectedSegment.equals( other ) ) {
                    SegmentRelationship scRel = analyst.findSegmentRelationship( queryService, selectedSegment, other );
                    if ( scRel != null )
                        externalFlows.addAll( scRel.getExternalFlows() );
                    scRel = analyst.findSegmentRelationship( queryService, other, selectedSegment );
                    if ( scRel != null )
                        externalFlows.addAll( scRel.getExternalFlows() );
                }
            }
            return externalFlows;
        } else {
            List<ExternalFlow> externalFlows = new ArrayList<ExternalFlow>();
            List<Segment> allSegments = getAllSegments();
            for ( Segment segment : allSegments ) {
                for ( Segment other : allSegments ) {
                    if ( !segment.equals( other ) ) {
                        SegmentRelationship scRel = analyst.findSegmentRelationship( queryService, segment, other );
                        if ( scRel != null )
                            externalFlows.addAll( scRel.getExternalFlows() );
                    }
                }
            }
            return externalFlows;
        }
    }

    @SuppressWarnings( "unchecked" )
    private List<Segment> getSegmentsInGroup() {
        return (List<Segment>) CollectionUtils.select( getAllSegments(), new Predicate() {
            @Override
            public boolean evaluate( Object obj ) {
                if ( selectedGroup != null ) {
                    Segment segment = (Segment) obj;
                    if ( groupByPhase ) {
                        return segment.getPhase().equals( selectedGroup );
                    } else {
                        return segment.getEvent().equals( selectedGroup );
                    }
                } else {
                    return true;
                }
            }
        } );
    }

    /**
     * Get segment relationships.
     *
     * @return a list of segment relationships
     */
    public List<SegmentRelationship> getSegmentRelationships() {
        List<SegmentRelationship> scRels = new ArrayList<SegmentRelationship>();
        if ( selectedSgRel != null ) {
            scRels.add( selectedSgRel );
        } else if ( selectedGroup != null ) {
            List<Segment> segmentsInGroup = getSegmentsInGroup();
            List<Segment> allSegments = getAllSegments();
            for ( Segment segment : allSegments ) {
                for ( Segment other : allSegments ) {
                    if ( !segment.equals( other ) && ( segmentsInGroup.contains( segment ) || segmentsInGroup.contains(
                            other ) ) )
                    {
                        SegmentRelationship scRel =
                                analyst.findSegmentRelationship( getQueryService(), segment, other );
                        if ( scRel != null )
                            scRels.add( scRel );
                    }
                }
            }
        } else if ( selectedSegment != null ) {
            List<Segment> allSegments = getAllSegments();
            for ( Segment other : allSegments ) {
                if ( !selectedSegment.equals( other ) ) {
                    SegmentRelationship scRel =
                            analyst.findSegmentRelationship( getQueryService(), selectedSegment, other );
                    if ( scRel != null )
                        scRels.add( scRel );
                    scRel = analyst.findSegmentRelationship( getQueryService(), other, selectedSegment );
                    if ( scRel != null )
                        scRels.add( scRel );
                }
            }
        } else {
            List<Segment> allSegments = getAllSegments();
            for ( Segment segment : allSegments ) {
                for ( Segment other : allSegments ) {
                    if ( !segment.equals( other ) ) {
                        SegmentRelationship scRel =
                                analyst.findSegmentRelationship( getQueryService(), segment, other );
                        if ( scRel != null )
                            scRels.add( scRel );
                    }
                }
            }
        }
        return scRels;
    }

    /**
     * Get to-segment from selected segment relationship.
     *
     * @return a segment
     */
    public Segment getToSegment() {
        if ( selectedSgRel != null ) {
            return selectedSgRel.getToSegment( getQueryService() );
        } else {
            return null;
        }
    }

    public void refresh( AjaxRequestTarget target ) {
        addPlanMapDiagramPanel();
        addFlowsTitleLabel();
        addExternalFlowsPanel();
        addCausesTitleLabel();
        addCausesPanel();
        target.add( this );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void changed( Change change ) {
        if ( change.isSelected() ) {
            Identifiable changed = change.getSubject( getCommunityService() );
            if ( changed instanceof Plan ) {
                selectedGroup = null;
                selectedSegment = null;
                selectedSgRel = null;
            } else if ( changed instanceof Phase || changed instanceof Event ) {
                selectedGroup = (ModelObject) changed;
                selectedSegment = null;
                selectedSgRel = null;
            } else if ( changed instanceof Segment ) {
                selectedGroup = null;
                selectedSegment = (Segment) changed;
                selectedSgRel = null;
                super.changed( change );
            } else if ( changed instanceof SegmentRelationship ) {
                selectedGroup = null;
                selectedSegment = null;
                selectedSgRel = (SegmentRelationship) changed;
            }
            // Don't percolate chane on selection of app or segment relationship.
            else {
                super.changed( change );
            }
        } else {
            super.changed( change );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        if ( change.isSelected() ) {
            // Don't percolate update on selection unless a part or segment was selected.
            if ( change.isForInstanceOf( Part.class ) ) {
                super.updateWith( target, change, updated );
            } else {
                refresh( target );
                if ( change.getScript() != null ) {
                    target.appendJavaScript( change.getScript() );
                }
            }
            if ( change.isForInstanceOf( Segment.class ) ) {  // propagate up segment selection
                super.updateWith( target, change, updated );
            }
        } else {
            super.updateWith( target, change, updated );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Plan getPlan() {
        return (Plan) getModel().getObject();
    }
}
