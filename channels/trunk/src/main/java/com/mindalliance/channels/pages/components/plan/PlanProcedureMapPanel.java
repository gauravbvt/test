package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.diagrams.ProcedureMapDiagramPanel;
import com.mindalliance.channels.pages.components.diagrams.Settings;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
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
    private static Collator collator = Collator.getInstance();
    /**
     * DOM identifier for resizeable element.
     */
    private static final String DOM_IDENTIFIER = ".plan .picture";

    /**
     * Whether plan map is reduced to fit.
     */
    private boolean reducedToFit = false;


    private Segment segment = NONE; // new Segment stands for all segments

    private String focusKind;

    private String focus = "";

    private boolean summarizeByOrg = false;

    private boolean summarizeByRole = false;

    private CheckBox summarizeByRoleCheckBox;

    private CheckBox summarizeByOrgCheckBox;

    private DropDownChoice<String> focusKindChoice;
    private AutoCompleteTextField<String> focusField;

    private Label sizingLabel;

    private ProcedureMapDiagramPanel procedureMapDiagramPanel;

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
        addSummarize();
        addFocusChoice();
        addFocusEntityField();
        addProcedureMapDiagramPanel();
        addMapSizing();
    }

    private void addSegmentChoice() {
        DropDownChoice<Segment> segmentChoice = new DropDownChoice<Segment>(
                "segment",
                new PropertyModel<Segment>( this, "segment" ),
                getAllSegments(),
                new ChoiceRenderer<Segment>() {
                    @Override
                    public Object getDisplayValue( Segment seg ) {
                        if ( seg == NONE ) {
                            return "the entire plan";
                        } else {
                            return "segment \"" + seg.getName() + "\"";
                        }
                    }
                }
        );
        segmentChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addProcedureMapDiagramPanel();
                target.addComponent( procedureMapDiagramPanel );
            }
        } );
        add( segmentChoice );
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
        return summarizeByOrg || summarizeByRole;
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
            }
        } );
        addOrReplace( focusField );
    }

    private List<String> getFocusKindChoices() {
        return Arrays.asList( FOCUS_KINDS );
    }

    private void addSummarize() {
        summarizeByOrgCheckBox = new CheckBox(
                "summarizeByOrg",
                new PropertyModel<Boolean>( this, "summarizeByOrg" )
        );
        summarizeByOrgCheckBox.setOutputMarkupId( true );
        summarizeByOrgCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addProcedureMapDiagramPanel();
                target.addComponent( procedureMapDiagramPanel );
                target.addComponent( summarizeByRoleCheckBox );
            }
        } );
        add( summarizeByOrgCheckBox );
        summarizeByRoleCheckBox = new CheckBox(
                "summarizeByRole",
                new PropertyModel<Boolean>( this, "summarizeByRole" )
        );
        summarizeByRoleCheckBox.setOutputMarkupId( true );
        summarizeByRoleCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addProcedureMapDiagramPanel();
                target.addComponent( procedureMapDiagramPanel );
                target.addComponent( summarizeByOrgCheckBox );
            }
        } );
        add( summarizeByRoleCheckBox );
    }

    private void addProcedureMapDiagramPanel() {
        Settings settings = diagramSize[0] <= 0.0 || diagramSize[1] <= 0.0 ? new Settings(
                ".plan .picture", null, null, true, true )
                : new Settings( ".plan .picture", null, diagramSize, true, true );
        procedureMapDiagramPanel = new ProcedureMapDiagramPanel(
                "procedure-map",
                segment == NONE ? null : segment,
                isSummarizeByOrg(),
                isSummarizeByRole(),
                getFocusEntity(),
                settings );
        procedureMapDiagramPanel.setOutputMarkupId( true );
        addOrReplace( procedureMapDiagramPanel );
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

    public boolean isSummarizeByOrg() {
        return summarizeByOrg;
    }

    public void setSummarizeByOrg( boolean summarizeByOrg ) {
        this.summarizeByOrg = summarizeByOrg;
        if ( summarizeByOrg ) summarizeByRole = false;
    }

    public boolean isSummarizeByRole() {
        return summarizeByRole;
    }

    public void setSummarizeByRole( boolean summarizeByRole ) {
        this.summarizeByRole = summarizeByRole;
        if ( summarizeByRole ) summarizeByOrg = false;
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

}

