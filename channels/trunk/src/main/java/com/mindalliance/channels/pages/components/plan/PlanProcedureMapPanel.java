package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.analysis.graph.ProceduresGraphBuilder;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.diagrams.ProcedureMapDiagramPanel;
import com.mindalliance.channels.pages.components.diagrams.Settings;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Procedure map panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/7/11
 * Time: 2:35 PM
 */
public class PlanProcedureMapPanel extends AbstractUpdatablePanel {

    /**
     * Expected screen resolution.
     */
    static private double DPI = 96.0;
    private static final Segment NONE = new Segment();
    private static final String ORG = "Organization";
    private static final String ACTOR = "Agent";
    private static final String[] FOCUS_KINDS = {ORG, ACTOR};
    private static final String BY_ORG_TYPE = "by type of organization";
    private static final String BY_ORG = "by organization";
    private static final String BY_ROLE = "by role";
    private static final String BY_NONE = "Don't summarize";
    private static final String[] SUMMARY_CHOICES = {BY_ORG_TYPE, BY_ORG, BY_ROLE, BY_NONE };
    private static Collator collator = Collator.getInstance();
    /**
     * DOM identifier for resizeable element.
     */
    private static final String DOM_IDENTIFIER = ".procedureMap";

    /**
     * Whether plan map is reduced to fit.
     */
    private boolean reducedToFit = false;


    private Segment segment = NONE; // new Segment stands for all segments

    private String focusKind = ORG;

    private String focus = "";

    private DropDownChoice<Segment> segmentChoice;

    private boolean summarizeByOrgType = true;
    private boolean summarizeByOrg = false;

    private boolean summarizeByRole = false;

    private DropDownChoice<String> summarizeChoice;

    private DropDownChoice<String> focusKindChoice;
    private AutoCompleteTextField<String> focusField;

    private Label sizingLabel;

    private Component procedureMapDiagramPanel;

    /**
     * Width, height dimension constraints on the plan map diagram.
     * In inches.
     * None if any is 0.
     */
    private double[] diagramSize = new double[2];


    public PlanProcedureMapPanel( String id ) {
        super( id );
        init();
    }

    private void init() {
        addSegmentChoice();
        addSummarizeChoice();
        addFocusChoice();
        addFocusEntityField();
        addProcedureMapDiagramPanel();
        addMapSizing();
    }

    private void addSegmentChoice() {
        segmentChoice = new DropDownChoice<Segment>(
                "segment",
                new PropertyModel<Segment>( this, "segment" ),
                getAllSegments(),
                new ChoiceRenderer<Segment>() {
                    @Override
                    public Object getDisplayValue( Segment seg ) {
                        if ( seg.getName().isEmpty() ) {
                            return "the entire plan";
                        } else {
                            return "segment \"" + seg.getName() + "\"";
                        }
                    }
                }
        );
        segmentChoice.setOutputMarkupId( true );
        segmentChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addProcedureMapDiagramPanel();
                target.addComponent( procedureMapDiagramPanel );
                Change change = isPlanSelected()
                        ? new Change( Change.Type.Selected, getPlan() )
                        : new Change( Change.Type.Selected, segment );
                change.addQualifier( "focus", getFocusEntity() );
                update( target, change );
            }
        } );
        addOrReplace( segmentChoice );
    }

    private List<Segment> getAllSegments() {
        List<Segment> allSegments = new ArrayList<Segment>();
        List<Segment> planSegments = new ArrayList<Segment>( getPlan().getSegments() );
        Collections.sort( planSegments, new Comparator<Segment>() {
            @Override
            public int compare( Segment s1, Segment s2 ) {
                return collator.compare( s1.getName(), s2.getName() );
            }
        } );
        allSegments.add( NONE );
        allSegments.addAll( planSegments );
        return allSegments;
    }

    private void addFocusChoice() {
        focusKindChoice = new DropDownChoice<String>(
                "focusChoice",
                new PropertyModel<String>( this, "focusKind" ),
                getFocusKindChoices() );
        focusKindChoice.setOutputMarkupId( true );
        focusKindChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addFocusEntityField();
                target.addComponent( focusField );
            }
        } );
        addOrReplace( focusKindChoice );
    }

    private boolean isSummarized() {
        return summarizeByOrgType || summarizeByOrg || summarizeByRole;
    }

    private void addFocusEntityField() {
        final List<String> choices = getFocusChoices();
        focusField = new AutoCompleteTextField<String>(
                "focus",
                new PropertyModel<String>( this, "focus" ) ) {
            @Override
            protected Iterator<String> getChoices( String input ) {
                List<String> candidates = new ArrayList<String>();
                for ( String choice : choices ) {
                    if ( getQueryService().likelyRelated( input, choice ) ) candidates.add( choice );
                }
                return candidates.iterator();
            }
        };
        focusField.setEnabled( getFocusKind() != null );
        focusField.setOutputMarkupId( true );
        focusField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addProcedureMapDiagramPanel();
                target.addComponent( procedureMapDiagramPanel );
                Change change = isPlanSelected()
                        ? new Change( Change.Type.Selected, getPlan() )
                        : new Change( Change.Type.Selected, segment );
                change.addQualifier( "focus", getFocusEntity() );
                update( target, change );
            }
        } );
        addOrReplace( focusField );
    }

    private List<String> getFocusKindChoices() {
        return Arrays.asList( FOCUS_KINDS );
    }

    private void addSummarizeChoice() {
      summarizeChoice = new DropDownChoice<String>(
              "summarizeChoice",
              new PropertyModel<String>( this, "summarizeChoice"),
              Arrays.asList( SUMMARY_CHOICES )
      );
        summarizeChoice.add(new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addProcedureMapDiagramPanel();
                target.addComponent( procedureMapDiagramPanel );
            }
        });
        add( summarizeChoice );
    }

    private void addProcedureMapDiagramPanel() {
        if ( focus == null || focus.isEmpty() ) {
            procedureMapDiagramPanel = new Label( "procedure-map", "" );
            procedureMapDiagramPanel.add( new AttributeModifier(
                    "style",
                    true,
                    new Model<String>( "background:url('../images/map-loading.png') 270px 0 no-repeat #ffffff;" ) ) );
        } else {
            Settings settings = diagramSize[0] <= 0.0 || diagramSize[1] <= 0.0 ? new Settings(
                    DOM_IDENTIFIER, null, null, true, true )
                    : new Settings( DOM_IDENTIFIER, null, diagramSize, true, true );
            procedureMapDiagramPanel =
                    new ProcedureMapDiagramPanel(
                            "procedure-map",
                            isPlanSelected() ? null : segment,
                            isSummarizeByOrgType(),
                            isSummarizeByOrg(),
                            isSummarizeByRole(),
                            getFocusEntity(),
                            settings );
            ProceduresGraphBuilder graphBuilder = new ProceduresGraphBuilder(
                    isPlanSelected() ? null : segment,
                    isSummarizeByOrgType(),
                    isSummarizeByOrg(),
                    isSummarizeByRole(),
                    getFocusEntity() );
            graphBuilder.setQueryService( getQueryService() );
            boolean noProcedures = !graphBuilder.hasCommitments();
            if ( noProcedures ) {
                procedureMapDiagramPanel.add( new AttributeModifier(
                        "style",
                        true,
                        new Model<String>( "background:url('../images/no-procedures.png') 270px 0 no-repeat #ffffff;" ) ) );
            }
        }
        procedureMapDiagramPanel.setOutputMarkupId( true );
        addOrReplace( procedureMapDiagramPanel );
    }

    public boolean isPlanSelected() {
        return segment == null || segment.getName().isEmpty();
    }

    private void addMapSizing() {
        sizingLabel = new org.apache.wicket.markup.html.basic.Label(
                "fit",
                new Model<String>( reducedToFit ? "Full size" : "Reduce to fit" ) );
        sizingLabel.setOutputMarkupId( true );
        sizingLabel.add( new AbstractDefaultAjaxBehavior() {
            @Override
            protected void onComponentTag( ComponentTag tag ) {
                super.onComponentTag( tag );
                String script;
                if ( !reducedToFit ) {
                    String domIdentifier = DOM_IDENTIFIER;
                    script = "wicketAjaxGet('"
                            + getCallbackUrl( true )
                            + "&width='+$('" + domIdentifier + "').width()+'"
                            + "&height='+$('" + domIdentifier + "').height()";
                } else {
                    script = "wicketAjaxGet('"
                            + getCallbackUrl( true )
                            + "'";
                }
                String onclick = ( "{" + generateCallbackScript( script ) + " return false;}" )
                        .replaceAll( "&amp;", "&" );
                tag.put( "onclick", onclick );
            }

            @Override
            protected void respond( AjaxRequestTarget target ) {
                RequestCycle requestCycle = RequestCycle.get();
                if ( !reducedToFit ) {
                    String swidth = requestCycle.getRequest().getParameter( "width" );
                    String sheight = requestCycle.getRequest().getParameter( "height" );
                    diagramSize[0] = ( Double.parseDouble( swidth ) - 20 ) / DPI;
                    diagramSize[1] = ( Double.parseDouble( sheight ) - 20 ) / DPI;
                } else {
                    diagramSize = new double[2];
                }
                reducedToFit = !reducedToFit;
                addProcedureMapDiagramPanel();
                target.addComponent( procedureMapDiagramPanel );
                addMapSizing();
                target.addComponent( sizingLabel );
            }
        } );
        addOrReplace( sizingLabel );
    }

    public Segment getSegment() {
        return segment;
    }

    public void setSegment( Segment segment ) {
        this.segment = segment;
    }

    public String getFocusKind() {
        return focusKind;
    }

    public void setFocusKind( String focusKind ) {
        this.focusKind = focusKind;
        focus = "";
    }

    public String getFocus() {
        return focus;
    }

    public String getSummarizeChoice() {
        return summarizeByOrgType
                ? BY_ORG_TYPE
                : summarizeByOrg
                ? BY_ORG
                : summarizeByRole
                ? BY_ROLE
                : BY_NONE;
    }

    public void setSummarizeChoice( String val ) {
        if ( val.equals( BY_ORG_TYPE ) )
            setSummarizeByOrgType( true );
        else if ( val.equals( BY_ORG ) )
            setSummarizeByOrg( true );
        else if ( val.equals( BY_ROLE ) )
            setSummarizeByRole( true );
        else if ( val.equals( BY_NONE ) ) {
            setSummarizeByRole( false );
            setSummarizeByOrg( false );
            setSummarizeByOrgType( false );
        }
    }

    public boolean isSummarizeByOrg() {
        return summarizeByOrg;
    }

    public void setSummarizeByOrg( boolean summarizeByOrg ) {
        this.summarizeByOrg = summarizeByOrg;
        if ( summarizeByOrg )  {
            summarizeByOrgType = false;
            summarizeByRole = false;
        }
    }

    public boolean isSummarizeByOrgType() {
        return summarizeByOrgType;
    }

    public void setSummarizeByOrgType( boolean summarizeByOrgType ) {
        this.summarizeByOrgType = summarizeByOrgType;
        if ( summarizeByOrgType )  {
            summarizeByOrg = false;
            summarizeByRole = false;
        }
    }


    public boolean isSummarizeByRole() {
        return summarizeByRole;
    }

    public void setSummarizeByRole( boolean summarizeByRole ) {
        this.summarizeByRole = summarizeByRole;
        if ( summarizeByRole ) {
            summarizeByOrgType = false;
            summarizeByOrg = false;
        }
    }

    private ModelEntity getFocusEntity() {
        ModelEntity focusEntity = null;
        if ( focusKind != null && focus != null && !focus.isEmpty() ) {
            if ( focusKind.equals( ORG ) ) {
                focusEntity = getQueryService().findActualEntity( Organization.class, focus );
            } else if ( focusKind.equals( ACTOR ) ) {
                focusEntity = getQueryService().findActualEntity( Actor.class, focus );
            }
        }
        return focusEntity;
    }

    private List<String> getFocusChoices() {
        List<String> focusChoices = new ArrayList<String>();
        List<? extends ModelEntity> entities = new ArrayList<ModelEntity>();
        if ( focusKind != null ) {
            if ( focusKind.equals( ORG ) ) {
                entities = getQueryService().listActualEntities( Organization.class );
            } else if ( focusKind.equals( ACTOR ) ) {
                entities = getQueryService().listActualEntities( Actor.class );
            }
            for ( ModelEntity entity : entities ) {
                if ( !entity.isUnknown() )
                    focusChoices.add( entity.getName() );
            }
            Collections.sort( focusChoices );
        }
        return focusChoices;

    }


    public void refreshSegment( AjaxRequestTarget target, Segment segment ) {
        this.segment = segment;
        addSegmentChoice();
        target.addComponent( segmentChoice );
        addProcedureMapDiagramPanel();
        target.addComponent( procedureMapDiagramPanel );
    }
}

