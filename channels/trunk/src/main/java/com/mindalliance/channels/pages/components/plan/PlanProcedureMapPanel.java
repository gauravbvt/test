package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
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

    private static final Segment NONE = new Segment();
    private static final String ORG = "Organization";
    private static final String ACTOR = "Agent";
    private static final String[] FOCUS_KINDS = { ORG, ACTOR};
    private static Collator collator = Collator.getInstance();

    private Segment segment = NONE; // new Segment stands for all segments

    private String focusKind = null;

    private String focus = "";

    private boolean summarizeByOrg = false;

    private boolean summarizeByRole = false;

    public PlanProcedureMapPanel( String id ) {
        super( id );
        init();
    }

    private void init() {
        addSegmentChoice();
        addFocus();
        addSummarize();
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
                // todo
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

    private void addFocus() {
        DropDownChoice<String> focusKindChoice = new DropDownChoice<String>(
                "focusChoice",
                new PropertyModel<String>( this, "focusKind"),
                getFocusKindChoices() );
        focusKindChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                 // todo
            }
        });
        add( focusKindChoice );
        final List<String> choices = getFocusChoices();
        AutoCompleteTextField<String> focusField = new AutoCompleteTextField<String>( "focus" ) {
            @Override
            protected Iterator<String> getChoices( String input ) {
                List<String> candidates = new ArrayList<String>();
                for ( String choice : choices ) {
                    if ( getQueryService().likelyRelated( input, choice ) ) candidates.add( choice );
                }
                return candidates.iterator();
            }
        };
        focusField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                // todo
            }
        });
    }

    private List<String> getFocusKindChoices() {
        return Arrays.asList( FOCUS_KINDS );
    }

    private void addSummarize() {

    }

    private void addProcedureMapDiagramPanel() {

    }

    private void addMapSizing() {

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
    }

    public String getFocus() {
        return focus;
    }

    public void setFocus( String focus ) {
        this.focus = focus;
    }

    private ModelEntity getFocusEntity() {
        ModelEntity focusEntity = null;
        if ( focusKind != null && focus != null && !focus.isEmpty() ) {
            if ( focusKind.equals( ORG) ) {
                focusEntity = getQueryService().findActualEntity( Organization.class, focus );
            } else if (focusKind.equals( ACTOR ) ) {
                focusEntity = getQueryService().findActualEntity( Actor.class, focus);
            }
        }
        return focusEntity;
    }

    private List<String> getFocusChoices() {
        List<String> focusChoices = new ArrayList<String>();
        List<? extends ModelEntity> entities = new ArrayList<ModelEntity>();
        if ( focusKind != null && focus != null && !focus.isEmpty() ) {
            if ( focusKind.equals( ORG) ) {
                entities = getQueryService().listActualEntities( Organization.class );
            } else if (focusKind.equals( ACTOR ) ) {
                entities = getQueryService().listActualEntities( Actor.class );
            }
            for (ModelEntity entity : entities ) {
                focusChoices.add( entity.getName() );
            }
            Collections.sort( focusChoices );
        }
        return focusChoices;

    }

}

