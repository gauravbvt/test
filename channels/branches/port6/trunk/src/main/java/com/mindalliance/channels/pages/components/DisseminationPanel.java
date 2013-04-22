package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Dissemination;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.SegmentObject;
import com.mindalliance.channels.core.model.Subject;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.core.util.SortableBeanProvider;
import com.mindalliance.channels.pages.components.diagrams.DisseminationDiagramPanel;
import com.mindalliance.channels.pages.components.diagrams.Settings;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.cycle.RequestCycle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Information dissemination panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Oct 10, 2010
 * Time: 5:27:56 PM
 */
public class DisseminationPanel extends AbstractFloatingCommandablePanel {

    /**
     * Min width on resize.
     */
    private static final int MIN_WIDTH = 300;
    /**
     * Min height on resize.
     */
    private static final int MIN_HEIGHT = 300;

    private Component disseminationDiagramPanel;

    private Component disseminationTablePanel;

    /**
     * Width, height dimension contraints on the flow diagram.
     * In inches.
     * None if any is 0.
     */
    private double[] diagramSize = new double[2];
    /**
     * Diagram container dom identifier.
     */
    private static final String DOM_IDENTIFIER = ".aspect .picture";

    /**
     * Whether the flow map was resized to fit.
     */
    private boolean reducedToFit = false;
    /**
     * Sizing toggle label..
     */
    private Label sizingLabel;
    /**
     * The subject being tracked.
     */
    private Subject subject;
    /**
     * Whether to show targets vs sources.
     */
    private boolean showTargets = true;
    /**
     * How much to display of a very long info.
     */
    private static final int MAX_INFO_LENGTH = 20;
    /**
     * Show sources checkbox.
     */
    private AjaxCheckBox showSourcesCheckBox;
    /**
     * Show targets checkbox.
     */
    private AjaxCheckBox showTargetsCheckBox;
    /**
     * Caption label.
     */
    private Label captionLabel;
    /**
     * Subject choice.
     */
    private DropDownChoice<Subject> subjectChoice;
    /**
     * Table caption label.
     */
    private Label tableCaptionLabel;

    public DisseminationPanel(
            String id,
            IModel<SegmentObject> model,
            Subject subject,
            boolean showTargets,
            Set<Long> expansions ) {
        super( id, model, expansions );
        this.subject = subject;
        this.showTargets = showTargets;
        init();
    }

    @Override
    public String getHelpSectionId() {
        return "analyzing";
    }

    @Override
    public String getHelpTopicId() {
        return "dissemination";
    }

    private void init() {
        addCaption();
        addViewingControls();
        addDisseminationDiagram();
        addSubjectChoice();
        addSourcesOrTarget();
        addTableCaption();
        addDisseminationTable();
    }

    private void addCaption() {
        captionLabel = new Label( "caption", new Model<String>( getCaption() ) );
        captionLabel.setOutputMarkupId( true );
        getContentContainer().addOrReplace( captionLabel );
    }

    private void addTableCaption() {
        tableCaptionLabel = new Label(
                "tableCaption",
                new Model<String>(
                        showTargets ? "Known recipients" : "Known sources"
                ) );
        tableCaptionLabel.setOutputMarkupId( true );
        getContentContainer().addOrReplace( tableCaptionLabel );
    }

    private String getCaption() {
        StringBuffer sb = new StringBuffer();
        Subject subj = getSubject();
        if ( subj != null ) {
            sb.append( showTargets ? "Dissemination " : "Sources " );
            sb.append( "of element " );
            sb.append( subj.getLabel( Integer.MAX_VALUE ) );
        }
        return sb.toString();
    }


    private void addViewingControls() {
        // Reduce to fit
        sizingLabel = new Label(
                "fit",
                new Model<String>( reducedToFit ? "Full size" : "Reduce to fit" ) );
        sizingLabel.setOutputMarkupId( true );
        // TODO - Copy & Paste - abstract this out
        sizingLabel.add( new DomElementSizeAjaxBehavior( DOM_IDENTIFIER, reducedToFit ) {
            @Override
            protected void respond( AjaxRequestTarget target ) {
                RequestCycle requestCycle = RequestCycle.get();
                if ( !reducedToFit ) {
                    String swidth = requestCycle.getRequest().getRequestParameters().getParameterValue( "width" ).toString();
                    String sheight = requestCycle.getRequest().getRequestParameters().getParameterValue( "height" ).toString();
                    diagramSize[0] = ( Double.parseDouble( swidth ) - 20 ) / DPI;
                    diagramSize[1] = ( Double.parseDouble( sheight ) - 20 ) / DPI;
                } else {
                    diagramSize = new double[2];
                }
                reducedToFit = !reducedToFit;
                addDisseminationDiagram();
                target.add( disseminationDiagramPanel );
                addViewingControls();
                target.add( sizingLabel );
            }
        } );
       // makeVisible( sizingLabel, getSubject() != null );
        getContentContainer().addOrReplace( sizingLabel );
    }


    private void addDisseminationDiagram() {
        if ( getSubject() == null ) {
            disseminationDiagramPanel = new Label( "disseminationMap", "Nothing to trace" );
        } else {
            double[] dim = diagramSize[0] <= 0.0 || diagramSize[1] <= 0.0
                    ? null
                    : diagramSize;
            Settings settings = new Settings( DOM_IDENTIFIER, null, dim, true, true );

            disseminationDiagramPanel = new DisseminationDiagramPanel(
                    "disseminationMap",
                    new Model<SegmentObject>( getSegmentObject() ),
                    getSubject(),
                    showTargets,
                    settings );
        }
        disseminationDiagramPanel.setOutputMarkupId( true );
        getContentContainer().addOrReplace( disseminationDiagramPanel );
    }

    private void addSubjectChoice() {
        subjectChoice = new DropDownChoice<Subject>(
                "subjects",
                new PropertyModel<Subject>( this, "subject" ),
                findAllSubjects(),
                new IChoiceRenderer<Subject>() {
                    public Object getDisplayValue( Subject subject ) {
                        return subject.getLabel( MAX_INFO_LENGTH );
                    }

                    public String getIdValue( Subject object, int index ) {
                        return "" + index;
                    }
                }
        );
        subjectChoice.setOutputMarkupId( true );
        subjectChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                addCaption();
                addDisseminationDiagram();
                addDisseminationTable();
                target.add( captionLabel );
                target.add( disseminationDiagramPanel );
                target.add( disseminationTablePanel );
            }
        } );
        getContentContainer().addOrReplace( subjectChoice );
    }

    private void addSourcesOrTarget() {
        // Show sources
        showSourcesCheckBox = new AjaxCheckBox(
                "showSources",
                new PropertyModel<Boolean>( this, "showSources" ) ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                addCaption();
                addTableCaption();
                addDisseminationDiagram();
                addDisseminationTable();
                addSubjectChoice();
                target.add( captionLabel );
                target.add( tableCaptionLabel );
                target.add( subjectChoice );
                target.add( showTargetsCheckBox );
                target.add( disseminationDiagramPanel );
                target.add( disseminationTablePanel );
            }
        };
        showSourcesCheckBox.setEnabled( isPart() );
        getContentContainer().add( showSourcesCheckBox );
        // Show targets
        showTargetsCheckBox = new AjaxCheckBox(
                "showTargets",
                new PropertyModel<Boolean>( this, "showTargets" ) ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                addCaption();
                addTableCaption();
                addDisseminationDiagram();
                addDisseminationTable();
                addSubjectChoice();
                target.add( captionLabel );
                target.add( tableCaptionLabel );
                target.add( subjectChoice );
                target.add( showSourcesCheckBox );
                target.add( disseminationDiagramPanel );
                target.add( disseminationTablePanel );
            }
        };
        showTargetsCheckBox.setEnabled( isPart() );
        getContentContainer().add( showTargetsCheckBox );
    }

    private void addDisseminationTable() {
        if ( getSubject() == null ) {
            disseminationTablePanel = new Label( "disseminationTable", "" );
        } else {
            disseminationTablePanel = new DisseminationTablePanel(
                    "disseminationTable",
                    new Model<SegmentObject>( getSegmentObject() ),
                    getSubject()
            );
        }
        disseminationTablePanel.setOutputMarkupId( true );
        getContentContainer().addOrReplace( disseminationTablePanel );
    }

    private List<Subject> findAllSubjects() {
        List<Subject> subjects;
        if ( isPart() ) {
            subjects = ( ( (Part) getSegmentObject() ).getAllSubjectsShared( showTargets ) );
        } else {
            assert getSegmentObject() instanceof Flow;
            subjects = ( ( (Flow) getSegmentObject() ).getAllSubjects() );
        }
        Collections.sort( subjects );
        return subjects;
    }

    protected String getTitle() {
        return "Information dissemination for "
                + ( isPart()
                ? "task"
                : ( showTargets ? "outgoing flow" : "incoming flow" ) );
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
    protected void doClose( AjaxRequestTarget target ) {
        Change change = new Change( Change.Type.AspectClosed, getSegmentObject(), "dissemination" );
        update( target, change );
    }

    private SegmentObject getSegmentObject() {
        return (SegmentObject) getModel().getObject();
    }

    private boolean isPart() {
        return getSegmentObject() instanceof Part;
    }

    public Subject getSubject() {
        if ( subject != null ) {
            return subject;
        } else {
            return getDefaultSubject();
        }
    }

    public void setSubject( Subject val ) {
        if ( val != null ) subject = val;
    }

    public boolean isShowTargets() {
        return showTargets;
    }

    public void setShowTargets( boolean showTargets ) {
        this.showTargets = showTargets;
    }

    public boolean isShowSources() {
        return !showTargets;
    }

    public void setShowSources( boolean val ) {
        this.showTargets = !val;
    }

    private Subject getDefaultSubject() {
        List<Subject> subjects;
        assert getSegmentObject() != null;
        if ( isPart() ) {
            subjects = ( (Part) getSegmentObject() ).getAllSubjectsShared( showTargets );
        } else {
            subjects = ( (Flow) getSegmentObject() ).getAllSubjects();
        }
        if ( subjects.isEmpty() ) {
            return null;
        } else {
            return subjects.get( 0 );
        }
    }

    /**
     * A assignment that's the source or target of a subject (info + eoi content), with cumulative transformation.
     */
    public class DisseminationAssignment implements Serializable {

        private Assignment assignment;
        private Dissemination dissemination;

        /**
         * Cumulative transformation (Aggregation absorbs Renaming absorbs Identity).
         */

        public DisseminationAssignment() {
        }

        public DisseminationAssignment( Assignment assignment, Dissemination dissemination ) {
            this.assignment = assignment;
            this.dissemination = dissemination;
        }

        public Assignment getAssignment() {
            return assignment;
        }

        public void setAssignment( Assignment assignment ) {
            this.assignment = assignment;
        }

        public Dissemination getDissemination() {
            return dissemination;
        }

        public void setDissemination( Dissemination dissemination ) {
            this.dissemination = dissemination;
        }

        /**
         * Get CSS class based on timeliness.
         * @return a string
         */
        public String getTimeliness() {
            return dissemination.isTimely() ? null : "late";
        }
    }

    public class DisseminationTablePanel extends AbstractTablePanel<DisseminationAssignment> {

        private Subject subject;

        public DisseminationTablePanel( String id, Model<SegmentObject> segmentObjectModel, Subject subject ) {
            super( id, segmentObjectModel, null );
            this.subject = subject;
            initTable();
        }

        @SuppressWarnings( "unchecked" )
        private void initTable() {
            List<DisseminationAssignment> disseminations = findAllDisseminationAssignments(
                    (SegmentObject) getModel().getObject(),
                    subject,
                    showTargets );
            List<IColumn<?,String>> columns = new ArrayList<IColumn<?,String>>();
            columns.add( makeLinkColumn(
                    showTargets ? "Recipient" : "Source",
                    "assignment.actor",
                    "assignment.actor.normalizedName",
                    EMPTY ) );
            columns.add( makeLinkColumn( "Role", "assignment.role", "assignment.role.name", EMPTY ) );
            columns.add( makeLinkColumn( "Jurisdiction", "assignment.jurisdiction", "assignment.jurisdiction.name", EMPTY ) );
            columns.add( makeLinkColumn( "Actual organization", "assignment.organization", "assignment.organization.name", EMPTY ) );
            columns.add( makeLinkColumn( "Task", "assignment.part", "assignment.part.task", EMPTY ) );
            columns.add( makeColumn( "Subject", "dissemination.subject", EMPTY ) );
            columns.add( makeColumn( "Transformation", "dissemination.transformationType.label", EMPTY ) );
            columns.add( makeColumn( "Max delay", "dissemination.delay", null, EMPTY, null, "dissemination.delay" ) );
            columns.add( makeColumn(
                    "Requirement",
                    "dissemination.needMaxDelay",
                    "@timeliness",
                    EMPTY,
                    null,
                    "dissemination.needMaxDelay" ) );
            add( new AjaxFallbackDefaultDataTable(
                    "disseminationTable",
                    columns,
                    new SortableBeanProvider<DisseminationAssignment,String>( disseminations, "assignment.actor.normalizedName" ),
                    getPageSize() ) );
        }

        private List<DisseminationAssignment> findAllDisseminationAssignments(
                SegmentObject segmentObject,
                Subject localSubject,
                boolean showTargets ) {
            QueryService queryService = getQueryService();
            List<Dissemination> disseminations = getQueryService().findAllDisseminations(
                    segmentObject,
                    localSubject,
                    showTargets
            );
            List<DisseminationAssignment> disseminationAssignments = new ArrayList<DisseminationAssignment>();
            for ( Dissemination dissemination : disseminations ) {
                Part part = dissemination.getPart( showTargets );
                // Include assignments to unknown actors
                List<Assignment> assignments = queryService.findAllAssignments( part, false );
                if ( assignments.isEmpty() )
                    assignments = queryService.findAllAssignments( part, true );
                for ( Assignment assignment : assignments ) {
                    DisseminationAssignment disseminationAssignment = new DisseminationAssignment(
                            assignment,
                            dissemination
                    );
                    disseminationAssignments.add( disseminationAssignment );
                }
            }
            return disseminationAssignments;
        }

    }
}
