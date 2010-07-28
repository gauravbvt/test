package com.mindalliance.channels.pages.components.segment;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.model.Goal;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.model.SegmentObject;
import com.mindalliance.channels.pages.components.AbstractTablePanel;
import com.mindalliance.channels.pages.components.FloatingCommandablePanel;
import com.mindalliance.channels.pages.components.diagrams.FailureImpactsDiagramPanel;
import com.mindalliance.channels.pages.components.diagrams.Settings;
import com.mindalliance.channels.util.SortableBeanProvider;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Failure impact analysis panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 14, 2010
 * Time: 8:40:48 PM
 */
public class FailureImpactsPanel extends FloatingCommandablePanel {
    /**
     * Expected screen resolution.
     */
    static private double DPI = 96.0;

    /**
     * Pad top on move.
     */
    private static final int PAD_TOP = 68;
    /**
     * Pad left on move.
     */
    private static final int PAD_LEFT = 5;
    /**
     * Pad bottom on move and resize.
     */
    private static final int PAD_BOTTOM = 5;
    /**
     * Pad right on move and resize.
     */
    private static final int PAD_RIGHT = 6;
    /**
     * Min width on resize.
     */
    private static final int MIN_WIDTH = 300;
    /**
     * Min height on resize.
     */
    private static final int MIN_HEIGHT = 300;
    /**
     * Is assumption taken that all alternate flows to downstream, critical flows will fail?
     */
    private boolean assumeFails;
    /**
     * Essential flows diagram panel.
     */
    private FailureImpactsDiagramPanel failureImpactsDiagramPanel;
    /**
     * Failures table panel.
     */
    private FailureImpactsTablePanel failuresTablePanel;
    /**
     * Width, height dimension contraints on the flow diagram.
     * In inches.
     * None if any is 0.
     */
    private double[] flowDiagramDim = new double[2];
    /**
     * Diagram container dom identifier.
     */
    private static final String DOM_IDENTIFIER = ".aspect .picture";

    /**
     * Whether the flow map was resized to fit.
     */
    private boolean resizedToFit = false;


    public FailureImpactsPanel( String id, IModel<SegmentObject> model, Set<Long> expansions ) {
        super( id, model, expansions );
        init();
    }

    private void init() {
        addCaption();
        addDoneButton();
        addAssumeFail();
        addFlowViewingControls();
        addEssentialFlowMap();
        addFailedTasks();
    }

    protected String getTitle() {
        return "Failure impacts";
    }

    private void addCaption() {
        add( new Label( "caption", new Model<String>( getCaption() ) ) );
    }

    private void addDoneButton() {
        AjaxFallbackLink doneLink = new AjaxFallbackLink( "done" ) {
            public void onClick( AjaxRequestTarget target ) {
                close( target );
            }
        };
        add( doneLink );
    }

    private void addAssumeFail() {
        CheckBox assumeFailsCheckBox = new CheckBox(
                "assumeFails",
                new PropertyModel<Boolean>( this, "assumeFails" ) );
        assumeFailsCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                addEssentialFlowMap();
                addFailedTasks();
                target.addComponent( failureImpactsDiagramPanel );
                target.addComponent( failuresTablePanel );
            }
        } );
        add( assumeFailsCheckBox );
    }

    private void addFlowViewingControls() {
        WebMarkupContainer reduceToFit = new WebMarkupContainer( "fit" );
        reduceToFit.add( new AbstractDefaultAjaxBehavior() {
            @Override
            protected void onComponentTag( ComponentTag tag ) {
                super.onComponentTag( tag );
                String domIdentifier = DOM_IDENTIFIER;
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
                if ( !resizedToFit ) {
                    flowDiagramDim[0] = ( Double.parseDouble( swidth ) - 20 ) / DPI;
                    flowDiagramDim[1] = ( Double.parseDouble( sheight ) - 20 ) / DPI;
                } else {
                    flowDiagramDim = new double[2];
                }
                resizedToFit = !resizedToFit;
                addEssentialFlowMap();
                target.addComponent( failureImpactsDiagramPanel );
            }
        } );
        add( reduceToFit );
        WebMarkupContainer legend = new WebMarkupContainer( "legend" );
        legend.add( new AjaxEventBehavior( "onclick" ) {
            @Override
            protected void onEvent( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Explained, getSegment(), "legend" ) );
            }
        } );
        add( legend );
    }

    private Segment getSegment() {
        return ((SegmentObject)getModel().getObject()).getSegment();
    }

    private void addEssentialFlowMap() {
        double[] dim = flowDiagramDim[0] <= 0.0 || flowDiagramDim[1] <= 0.0 ? null : flowDiagramDim;
        Settings settings = new Settings( DOM_IDENTIFIER, null, dim, true, true );

        failureImpactsDiagramPanel = new FailureImpactsDiagramPanel(
                "essentialFlowMap",
                new Model<SegmentObject>( getSegmentObject() ),
                assumeFails,
                settings );
        failureImpactsDiagramPanel.setOutputMarkupId( true );
        addOrReplace( failureImpactsDiagramPanel );
    }

    private void addFailedTasks() {
        failuresTablePanel = new FailureImpactsTablePanel(
                "failures",
                new Model<SegmentObject>( getSegmentObject() ),
                assumeFails,
                getExpansions()
        );
        failuresTablePanel.setOutputMarkupId( true );
        addOrReplace( failuresTablePanel );
    }

    private String getCaption() {
        SegmentObject so = getSegmentObject();
        StringBuffer sb = new StringBuffer();
        sb.append( "Impacts of failing" );
        sb.append( isTask() ? " task " : " to share information " );
        sb.append( "\"" );
        sb.append( isTask() ? ( (Part) so ).getTask() : so.getName() );
        sb.append( "\"" );
        return sb.toString();
    }

    public boolean isAssumeFails() {
        return assumeFails;
    }

    public void setAssumeFails( boolean assumeFails ) {
        this.assumeFails = assumeFails;
    }

    /**
     * {@inheritDoc}
     */
    protected int getPadTop() {
        return PAD_TOP;
    }

    /**
     * {@inheritDoc}
     */
    protected int getPadLeft() {
        return PAD_LEFT;
    }

    /**
     * {@inheritDoc}
     */
    protected int getPadBottom() {
        return PAD_BOTTOM;
    }

    /**
     * {@inheritDoc}
     */
    protected int getPadRight() {
        return PAD_RIGHT;
    }

    /**
     * {@inheritDoc}
     */
    protected int getMinWidth() {
        return MIN_WIDTH;
    }

    /**
     * {@inheritDoc}
     */
    protected int getMinHeight() {
        return MIN_HEIGHT;
    }

    /**
     * {@inheritDoc}
     */
    protected void close( AjaxRequestTarget target ) {
        Change change = new Change( Change.Type.AspectClosed, getSegmentObject(), "failure" );
        update( target, change );
    }

    private SegmentObject getSegmentObject() {
        return (SegmentObject) getModel().getObject();
    }

    private boolean isTask() {
        return getSegmentObject() instanceof Part;
    }

    /**
     * A task failure.
     */
    public class PartFailure implements Serializable {
        /**
         * A part.
         */
        private Part part;
        /**
         * A goal normally achieved by the part.
         */
        private Goal goal;

        public PartFailure( Part part, Goal goal ) {
            this.part = part;
            this.goal = goal;
        }

        public Part getPart() {
            return part;
        }

        public void setPart( Part part ) {
            this.part = part;
        }

        public Goal getGoal() {
            return goal;
        }

        public void setGoal( Goal goal ) {
            this.goal = goal;
        }

        public String getImpact() {
            if ( part.getGoals().contains( goal ) ) {
                return goal.isPositive()
                        ? "Opportunity lost"
                        : "Risk not mitigated";
            } else {
                if ( !goal.isPositive() && goal.isEndsWithSegment() )
                    return "Risk remains because \"" + part.getSegment().getPhaseEventTitle() + "\" is not ended";
                else
                    return "";
            }
        }
    }

    /**
     * Failure impacts table panel.
     */
    public class FailureImpactsTablePanel extends AbstractTablePanel<PartFailure> {
        /**
         * Segment object presumed failing.
         */
        private SegmentObject segmentObject;
        /**
         * Whether alternate flows are also assumed to fail.
         */
        private boolean assumeFails;

        public FailureImpactsTablePanel(
                String id,
                IModel<SegmentObject> iModel,
                boolean assumeFails,
                Set<Long> expansions ) {
            super( id, iModel, expansions );
            segmentObject = iModel.getObject();
            this.assumeFails = assumeFails;
            initTable();
        }

        @SuppressWarnings( "unchecked" )
        private void initTable() {
            List<PartFailure> failures = new ArrayList<PartFailure>();
            List<Part> impacts = new ArrayList<Part>( getQueryService().findFailureImpacts(
                    segmentObject,
                    assumeFails ) );
            if ( segmentObject instanceof Part && ( (Part) segmentObject ).isUseful() ) {
                impacts.add( (Part) segmentObject );
            }
            for ( Part part : impacts ) {
                for ( Goal goal : part.getGoalsAchieved() ) {
                    failures.add( new PartFailure( part, goal ) );
                }
            }
            List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
            columns.add( makeLinkColumn( "Necessary task", "part", "part.task", EMPTY ) );
            columns.add( makeLinkColumn( "Plan segment", "part.segment", "part.segment.name", EMPTY ) );
            columns.add( makeColumn( "Risk/opportunity", "goal.categoryLabel", "goal.categoryLabel", EMPTY ) );
            columns.add( makeColumn( "Impact", "impact", "impact", EMPTY ) );
            columns.add( makeColumn( "Severity", "goal.severityLabel", null, EMPTY, null, "goal.level" ) );
            columns.add( makeLinkColumn( "Organization", "goal.organization", "goal.organization.name", EMPTY ) );
            add( new AjaxFallbackDefaultDataTable(
                    "failures",
                    columns,
                    new SortableBeanProvider<PartFailure>( failures, "part.task" ),
                    getPageSize() ) );
        }
    }
}


