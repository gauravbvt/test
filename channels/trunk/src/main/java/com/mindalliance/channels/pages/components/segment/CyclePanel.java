package com.mindalliance.channels.pages.components.segment;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.commands.UpdateObject;
import com.mindalliance.channels.core.command.commands.UpdateSegmentObject;
import com.mindalliance.channels.core.model.time.Cycle;
import com.mindalliance.channels.core.model.time.Cyclic;
import com.mindalliance.channels.core.model.time.TimeUnit;
import com.mindalliance.channels.core.model.time.Tranche;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.TabIndexable;
import com.mindalliance.channels.pages.components.TabIndexer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Cycle editor for cyclic object.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/23/14
 * Time: 4:07 PM
 */
public class CyclePanel extends AbstractCommandablePanel implements TabIndexable {

    private final IModel<Cyclic> cyclicModel;
    private String cycleProperty;
    private WebMarkupContainer cycleContainer;
    private WebMarkupContainer cycleEditor;
    private Label changeOrDoneLabel;
    private boolean editing = false;
    private boolean enabled = true;
    private AjaxLink<String> changeLink;
    private Label summaryLabel;
    private Label ofEveryLabel;
    private TextField<String> tranchesField;
    private DropDownChoice<TimeUnit> timeUnitDropDownChoice;
    private TextField<String> skipField;
    private TabIndexer tabIndexer;

    public CyclePanel( String id, IModel<Cyclic> cyclicModel, String cycleProperty ) {
        super( id );
        this.cyclicModel = cyclicModel;
        this.cycleProperty = cycleProperty;
        init();
    }

    @Override
    public void initTabIndexing( TabIndexer tabIndexer ) {
        this.tabIndexer = tabIndexer;
        tabIndexer.giveTabIndexTo( tranchesField );
        tabIndexer.giveTabIndexTo( skipField );
        tabIndexer.giveTabIndexTo( timeUnitDropDownChoice );
    }

    private void init() {
        cycleContainer = new WebMarkupContainer( "cycleContainer" );
        cycleContainer.setOutputMarkupId( true );
        add( cycleContainer );
        addSummary();
        addEditButton();
        addEditor();
    }

    private void addSummary() {
        Cycle cycle = getCycle();
        summaryLabel = new Label( "summary", cycle.getLabel() );
        summaryLabel.setOutputMarkupId( true );
        makeVisible( summaryLabel, !editing );
        cycleContainer.addOrReplace( summaryLabel );
    }

    private void addEditButton() {
        changeLink = new AjaxLink<String>( "edit" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                editing = !editing;
                addChangeOrDone();
                target.add( changeOrDoneLabel );
                addSummary();
                target.add( summaryLabel );
                addEditor();
                target.add( cycleEditor );
            }
        };
        changeLink.setVisible( enabled );
        changeLink.setOutputMarkupId( true );
        addChangeOrDone();
        cycleContainer.addOrReplace( changeLink );
    }

    private void addChangeOrDone() {
        changeOrDoneLabel = new Label( "changeOrDone", editing ? "Done" : "Change" );
        changeOrDoneLabel.setOutputMarkupId( true );
        changeLink.addOrReplace( changeOrDoneLabel );
    }

    private void addEditor() {
        cycleEditor = new WebMarkupContainer( "cycleEditor" );
        cycleEditor.setOutputMarkupId( true );
        makeVisible( cycleEditor, enabled && editing );
        cycleContainer.addOrReplace( cycleEditor );
        addTranches();
        addOfEvery();
        addSkip();
        addTimeUnit();
    }

    private void addTranches() {
        tranchesField = new TextField<String>(
                "tranches",
                new PropertyModel<String>( this, "tranchesString" )
        );
        tranchesField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addSummary();
                target.add( summaryLabel );
                addTranches();
                target.add( tranchesField );
                addOfEvery();
                target.add( ofEveryLabel );
                update( target, new Change( Change.Type.Updated, getCyclic(), cycleProperty ) );
            }
        } );
        addInputHint( tranchesField, getTranchesHint() );
        makeVisible( tranchesField, true );
        makeVisible( tranchesField, hasTranches() );
        tranchesField.setOutputMarkupId( true );
        cycleEditor.addOrReplace( tranchesField );
        applyTabIndexTo( tranchesField, tabIndexer );
    }

    private String getTranchesHint() {
        StringBuilder sb = new StringBuilder();
        List<Tranche> tranches = getCycle().getAllPossibleTranches();
        if ( tranches.size() > 1 ) {
            sb.append( tranches.get( 0 ).getLabel() )
                    .append( ", " )
                    .append( tranches.get( 1 ).getLabel() )
                    .append( "..." );
        }
        return sb.toString();
    }

    private boolean hasTranches() {
        return getCycle() != null && getCycle().getTimeUnit().compareTo( TimeUnit.Minute ) < 0;
    }

    private void addOfEvery() {
        ofEveryLabel = new Label( "ofEvery", "of every" );
        ofEveryLabel.setOutputMarkupId( true );
        makeVisible( ofEveryLabel, !getCycle().getTranches().isEmpty() );
        cycleEditor.addOrReplace( ofEveryLabel );
    }

    private void addSkip() {
        skipField = new TextField<String>(
                "skip",
                new PropertyModel<String>( this, "skip" )
        );
        skipField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addSummary();
                target.add( summaryLabel );
                addTimeUnit();
                target.add( timeUnitDropDownChoice );
                addSkip();
                target.add( skipField );
                update( target, new Change( Change.Type.Updated, getCyclic(), cycleProperty ) );
            }
        } );
        skipField.setOutputMarkupId( true );
        cycleEditor.addOrReplace( skipField );
        applyTabIndexTo( skipField, tabIndexer );
    }

    private void addTimeUnit() {
        timeUnitDropDownChoice = new DropDownChoice<TimeUnit>(
                "timeUnit",
                new PropertyModel<TimeUnit>( this, "timeUnit" ),
                Arrays.asList( TimeUnit.values() ),
                new IChoiceRenderer<TimeUnit>() {
                    @Override
                    public Object getDisplayValue( TimeUnit timeUnit ) {
                        return timeUnit.name().toLowerCase()
                                + ( getCycle().getSkip() > 2 ? "s" : "" );
                    }

                    @Override
                    public String getIdValue( TimeUnit object, int index ) {
                        return Integer.toString( index );
                    }
                }
        );
        timeUnitDropDownChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addTranches();
                addOfEvery();
                addSummary();
                target.add( tranchesField );
                target.add( ofEveryLabel );
                target.add( summaryLabel );
                update( target, new Change( Change.Type.Updated, getCyclic(), cycleProperty ) );
            }
        } );
        timeUnitDropDownChoice.setOutputMarkupId( true );
        cycleEditor.addOrReplace( timeUnitDropDownChoice );
        applyTabIndexTo( timeUnitDropDownChoice, tabIndexer );
    }

    public void enable( boolean enabled ) {
        this.enabled = enabled;
    }

    public boolean isHasCycle() {
        return getCycle() != null;
    }

    public String getTranchesString() {
        StringBuilder sb = new StringBuilder();
        Iterator<Tranche> iterator = getTranches().iterator();
        while ( iterator.hasNext() ) {
            Tranche tranche = iterator.next();
            sb.append( tranche.getLabel() );
            if ( iterator.hasNext() )
                sb.append( ", " );
        }
        return sb.toString();
    }

    public void setTranchesString( String val ) {
        Set<Integer> trancheIndices = new HashSet<Integer>();
        if ( val != null ) {
            List<String> trancheLabels = Arrays.asList( val.split( "," ) );
            for ( String trancheLabel : trancheLabels ) {
                int index = guessTrancheIndexFromLabel( trancheLabel );
                if ( index >= 0 ) {
                    trancheIndices.add( index );
                }
            }
        }
        setTrancheIndices( new ArrayList<Integer>( trancheIndices ) );
    }

    private int guessTrancheIndexFromLabel( String trancheLabel ) {
        String input = trancheLabel.toLowerCase().trim();
        List<Tranche> allTranches = getCycle().getAllPossibleTranches();
        for ( int i = 0; i < allTranches.size(); i++ ) {
            Tranche tranche = allTranches.get( i );
            if ( tranche.getLabel().toLowerCase().startsWith( input ) ) {
                return i;
            }
        }
        return -1;
    }

    private List<Tranche> getTranches() {
        return getCycle().getTranches();
    }

    private void setTrancheIndices( List<Integer> val ) {
        doCommand(
                new UpdateSegmentObject(
                        getUsername(),
                        getCyclic(),
                        cycleProperty + ".trancheIndices",
                        val,
                        UpdateObject.Action.Set
                )
        );
    }

    public TimeUnit getTimeUnit() {
        return getCycle().getTimeUnit();
    }

    public void setTimeUnit( TimeUnit val ) {
        doCommand(
                new UpdateSegmentObject(
                        getUsername(),
                        getCyclic(),
                        cycleProperty + ".timeUnit",
                        val,
                        UpdateObject.Action.Set
                )
        );
    }

    public String getSkip() {
        Cycle cycle = getCycle();
        int skip = cycle.getSkip();
        return skip == 1
                ? "single"
                : skip == 2
                ? "other"
                : Integer.toString( skip );
    }

    public void setSkip( String val ) {
        String value = val == null ? "1" : val.toLowerCase();
        int skip = 1;
        try {
            skip = value.equals( "single" )
                    ? 1
                    : value.equals( "other" )
                    ? 2
                    : value.equals( "alternate" )
                    ? 2
                    : Integer.parseInt( value );
        } catch ( NumberFormatException e ) {
            // do nothing
        }
        doCommand(
                new UpdateSegmentObject(
                        getUsername(),
                        getCyclic(),
                        cycleProperty + ".skip",
                        skip,
                        UpdateObject.Action.Set
                )
        );
    }


    public Cyclic getCyclic() {
        return cyclicModel.getObject();
    }

    public Cycle getCycle() {
        return getCyclic().getCycle();
    }

    private List<Tranche> queryMatches( String term, int page, int pageSize ) {
        List<Tranche> result = new ArrayList<Tranche>();
        term = term.toUpperCase();
        final int offset = page * pageSize;
        int matched = 0;
        for ( Tranche tranche : getCycle().getAllPossibleTranches() ) { // todo - cache this
            if ( result.size() == pageSize ) {
                break;
            }
            if ( tranche.getLabel().toUpperCase().contains( term ) ) {
                matched++;
                if ( matched > offset ) {
                    result.add( tranche );
                }
            }
        }
        return result;
    }


    public List<Tranche> toChoices( List<String> ids ) {
        ArrayList<Tranche> tranches = new ArrayList<Tranche>();
        for ( String id : ids ) {
            tranches.add( getCycle().trancheFromLabel( id ) );
        }
        return tranches;
    }

}
