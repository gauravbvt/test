package com.mindalliance.channels.pages.components.segment;

import com.mindalliance.channels.core.model.Cycle;
import com.mindalliance.channels.core.model.Cyclic;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

/**
 * Cycle editor for cyclic object.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/23/14
 * Time: 4:07 PM
 */
public class CyclePanel extends AbstractCommandablePanel {

    private final IModel<Cyclic> cyclicModel;
    private String property;
    private WebMarkupContainer cycleContainer;
    private WebMarkupContainer cycleEditor;
    private Label changeOrDoneLabel;
    private boolean editing = false;
    private boolean enabled = true;
    private AjaxLink<String> changeLink;

    public CyclePanel( String id, IModel<Cyclic> cyclicModel, String property ) {
        super( id );
        this.cyclicModel = cyclicModel;
        this.property = property;
        init();
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
        Label summaryLabel = new Label( "summary", cycle == null ? "" : cycle.getLabel() );
        summaryLabel.setOutputMarkupId( true );
        cycleContainer.addOrReplace( summaryLabel );
    }

    private void addEditButton() {
        changeLink = new AjaxLink<String>( "edit" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                editing = !editing;
                addChangeOrDone();
                target.add( changeOrDoneLabel );
                addEditor();
                target.add( cycleEditor );
            }
        };
        addChangeOrDone();
        cycleContainer.add( changeLink );
    }

    private void addChangeOrDone() {
        Label changeOrDoneLabel = new Label( "changeOrDone", editing ? "Done" : "Change" );
        changeOrDoneLabel.setOutputMarkupId( true );
        changeLink.addOrReplace( changeOrDoneLabel );
    }

    private void addEditor() {
        cycleEditor = new WebMarkupContainer( "cycleEditor" );
        cycleEditor.setOutputMarkupId( true );
        makeVisible( cycleEditor, editing );
        cycleContainer.addOrReplace( cycleEditor );
        addTranches();
        addOfEvery();
        addSkip();
        addTimeUnit();
    }

    private void addTranches() {
        cycleEditor.add( new Label("tranches", "TBD"));//Todo
    }

    private void addOfEvery() {
        cycleEditor.add( new Label("ofEvery", "TBD"));//Todo
    }

    private void addSkip() {
        cycleEditor.add( new Label("skip", "TBD"));//Todo
    }

    private void addTimeUnit() {
        cycleEditor.add( new Label("timeUnit", "TBD"));//Todo
    }

    public void enable( boolean enabled ) {
        this.enabled = enabled;
    }

    public Cyclic getCyclic() {
        return cyclicModel.getObject();
    }

    public Cycle getCycle() {
        return getCyclic().getCycle();
    }
}
