package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.analysis.Analyst;
import com.mindalliance.channels.analysis.graph.SegmentRelationship;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.model.Event;
import com.mindalliance.channels.model.ExternalFlow;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Phase;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.diagrams.PlanMapDiagramPanel;
import com.mindalliance.channels.pages.components.diagrams.Settings;
import com.mindalliance.channels.pages.components.segment.ExternalFlowsPanel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * Plan map panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 31, 2009
 * Time: 1:18:49 PM
 */
public class PlanMapPanel extends AbstractUpdatablePanel {

    /**
     * Default page size for external flows panel.
     */
    private static final int PAGE_SIZE = 10;
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
     * Width, height dimension contraints on the plan map diagram.
     * In inches.
     * None if any is 0.
     */
    private double[] diagramSize = new double[2];

    @SpringBean
    private Analyst analyst;

    public PlanMapPanel( String id, IModel<? extends Identifiable> model, Set<Long> expansions ) {
        super( id, model, expansions );
        init();
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
        CheckBox groupByPhaseCheckBox = new CheckBox(
                "groupByPhase",
                new PropertyModel<Boolean>( this, "groupByPhase" ) );
        groupByPhaseCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                refresh( target );
            }
        } );
        add( groupByPhaseCheckBox );
        CheckBox groupByEventCheckBox = new CheckBox(
                "groupByEvent",
                new PropertyModel<Boolean>( this, "groupByEvent" ) );
        groupByEventCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                refresh( target );
            }
        } );
        add( groupByEventCheckBox );
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
        WebMarkupContainer reduceToFit = new WebMarkupContainer( "fit" );
        reduceToFit.add( new AbstractDefaultAjaxBehavior() {
            @Override
            protected void onComponentTag( ComponentTag tag ) {
                super.onComponentTag( tag );
                String domIdentifier = ".plan .picture";
                String script = "wicketAjaxGet('"
                        + getCallbackUrl( true )
                        + "&width='+$('" + domIdentifier + "').width()+'"
                        + "&height='+$('" + domIdentifier + "').height()";
                String onclick = ( "{" + generateCallbackScript( script ) + " return false;}" )
                        .replaceAll( "&amp;", "&" );
                tag.put( "onclick", onclick );
            }

            @Override
            protected void respond( AjaxRequestTarget target ) {
                RequestCycle requestCycle = RequestCycle.get();
                String swidth = requestCycle.getRequest().getParameter( "width" );
                String sheight = requestCycle.getRequest().getParameter( "height" );
                diagramSize[0] = ( Double.parseDouble( swidth ) - 20 ) / 96.0;
                diagramSize[1] = ( Double.parseDouble( sheight ) - 20 ) / 96.0;
                addPlanMapDiagramPanel();
                target.addComponent( planMapDiagramPanel );
            }
        } );
        add( reduceToFit );
        WebMarkupContainer fullSize = new WebMarkupContainer( "full" );
        fullSize.add( new AjaxEventBehavior( "onclick" ) {
            @Override
            protected void onEvent( AjaxRequestTarget target ) {
                diagramSize = new double[2];
                addPlanMapDiagramPanel();
                target.addComponent( planMapDiagramPanel );
            }
        } );
        add( fullSize );
    }


    private void addPlanMapDiagramPanel() {
        Settings settings = diagramSize[0] <= 0.0 || diagramSize[1] <= 0.0 ? new Settings(
                ".plan .picture", null, null, true, true )
                : new Settings( ".plan .picture", null, diagramSize, true, true );
        planMapDiagramPanel = new PlanMapDiagramPanel(
                "plan-map",
                new PropertyModel<ArrayList<Segment>>( this, "allSegments" ),
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
        Label flowsTitleLabel = new Label( "flows-title",
                new PropertyModel<String>( this, "flowsTitle" ) );
        flowsTitleLabel.setOutputMarkupId( true );
        addOrReplace( flowsTitleLabel );
    }

    private void addCausesTitleLabel() {
        Label causesTitleLabel = new Label( "causes-title",
                new PropertyModel<String>( this, "causesTitle" ) );
        causesTitleLabel.setOutputMarkupId( true );
        addOrReplace( causesTitleLabel );
    }

    private void addExternalFlowsPanel() {
        ExternalFlowsPanel externalFlowsPanel = new ExternalFlowsPanel(
                "flows",
                new PropertyModel<ArrayList<ExternalFlow>>( this, "externalFlows" ),
                PAGE_SIZE,
                getExpansions()
        );
        externalFlowsPanel.setOutputMarkupId( true );
        addOrReplace( externalFlowsPanel );
    }

    private void addCausesPanel() {
        SegmentCausesPanel segmentCausesPanel = new SegmentCausesPanel(
                "causes",
                getSegmentRelationships(),
                PAGE_SIZE,
                getExpansions()
        );
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
                return "Flows connecting plan segments in phase \""
                        + selectedGroup.getName()
                        + "\"";
            } else {
                return "Flows connecting plan segments about event \""
                        + selectedGroup.getName()
                        + "\"";
            }
        } else if ( selectedSegment != null ) {
            return "Flows connecting \""
                    + selectedSegment.getName()
                    + "\" to other plan segments";
        } else if ( selectedSgRel != null ) {
            Segment fromSegment = selectedSgRel.getFromSegment( getQueryService() );
            Segment toSegment = selectedSgRel.getToSegment( getQueryService() );
            if ( fromSegment == null || toSegment == null ) {
                return "*** You need to refresh ***";
            } else {
                return "Flows connecting \""
                        + fromSegment.getName()
                        + "\" to \""
                        + toSegment.getName()
                        + "\"";
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
                return "Causations for plan segments in phase \""
                        + selectedGroup.getName()
                        + "\"";
            } else {
                return "Causations for plan segments about event \""
                        + selectedGroup.getName()
                        + "\"";
            }
        } else if ( selectedSegment != null ) {
            return "Causations for plan segment \""
                    + selectedSegment.getName()
                    + "\"";
        } else if ( selectedSgRel != null ) {
            Segment fromSegment = selectedSgRel.getFromSegment( getQueryService() );
            Segment toSegment = selectedSgRel.getToSegment( getQueryService() );
            if ( fromSegment == null || toSegment == null ) {
                return "*** You need to refresh ***";
            } else {
                return "How \""
                        + fromSegment.getName()
                        + "\" impacts \""
                        + toSegment.getName()
                        + "\"";
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
        if ( selectedSgRel != null ) {
            return selectedSgRel.getExternalFlows();
        } else if ( selectedGroup != null ) {
            List<ExternalFlow> externalFlows = new ArrayList<ExternalFlow>();
            List<Segment> segmentsInGroup = getSegmentsInGroup();
            List<Segment> allSegments = getAllSegments();
            for ( Segment segment : allSegments ) {
                for ( Segment other : allSegments ) {
                    if ( !segment.equals( other )
                            &&
                            ( segmentsInGroup.contains( segment )
                                    || segmentsInGroup.contains( other ) ) ) {
                        SegmentRelationship scRel = analyst.findSegmentRelationship( segment, other );
                        if ( scRel != null ) externalFlows.addAll( scRel.getExternalFlows() );
                    }
                }
            }
            return externalFlows;
        } else if ( selectedSegment != null ) {
            List<ExternalFlow> externalFlows = new ArrayList<ExternalFlow>();
            List<Segment> allSegments = getAllSegments();
            for ( Segment other : allSegments ) {
                if ( !selectedSegment.equals( other ) ) {
                    SegmentRelationship scRel = analyst.findSegmentRelationship( selectedSegment, other );
                    if ( scRel != null ) externalFlows.addAll( scRel.getExternalFlows() );
                    scRel = analyst.findSegmentRelationship( other, selectedSegment );
                    if ( scRel != null ) externalFlows.addAll( scRel.getExternalFlows() );
                }
            }
            return externalFlows;
        } else {
            List<ExternalFlow> externalFlows = new ArrayList<ExternalFlow>();
            List<Segment> allSegments = getAllSegments();
            for ( Segment segment : allSegments ) {
                for ( Segment other : allSegments ) {
                    if ( !segment.equals( other ) ) {
                        SegmentRelationship scRel = analyst.findSegmentRelationship( segment, other );
                        if ( scRel != null ) externalFlows.addAll( scRel.getExternalFlows() );
                    }
                }
            }
            return externalFlows;
        }
    }

    @SuppressWarnings( "unchecked" )
    private List<Segment> getSegmentsInGroup() {
        return (List<Segment>) CollectionUtils.select(
                getAllSegments(),
                new Predicate() {
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
                }
        );
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
                    if ( !segment.equals( other )
                            && ( segmentsInGroup.contains( segment )
                                    || segmentsInGroup.contains( other ) ) ) {
                        SegmentRelationship scRel = analyst.findSegmentRelationship( segment, other );
                        if ( scRel != null ) scRels.add( scRel );
                    }
                }
            }
        } else if ( selectedSegment != null ) {
            List<Segment> allSegments = getAllSegments();
            for ( Segment other : allSegments ) {
                if ( !selectedSegment.equals( other ) ) {
                    SegmentRelationship scRel = analyst.findSegmentRelationship( selectedSegment, other );
                    if ( scRel != null ) scRels.add( scRel );
                    scRel = analyst.findSegmentRelationship( other, selectedSegment );
                    if ( scRel != null ) scRels.add( scRel );
                }
            }
        } else {
            List<Segment> allSegments = getAllSegments();
            for ( Segment segment : allSegments ) {
                for ( Segment other : allSegments ) {
                    if ( !segment.equals( other ) ) {
                        SegmentRelationship scRel = analyst.findSegmentRelationship( segment, other );
                        if ( scRel != null ) scRels.add( scRel );
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
        target.addComponent( this );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void changed( Change change ) {
        if ( change.isSelected() ) {
            Identifiable changed = change.getSubject( getQueryService() );
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
            } else if ( changed instanceof SegmentRelationship ) {
                selectedGroup = null;
                selectedSegment = null;
                selectedSgRel = (SegmentRelationship) changed;
            }
            // Don't percolate chane on selection of app, segment or segment relationship.
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
            // Don't percolate update on selection unless a part was selected.
            if ( change.isForInstanceOf( Part.class ) ) {
                super.updateWith( target, change, updated );
            } else {
                refresh( target );
                if ( change.getScript() != null ) {
                    target.appendJavascript( change.getScript() );
                }
            }
        } else {
            super.updateWith( target, change, updated );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Plan getPlan() {
        return (Plan) getModel().getObject();
    }

}
