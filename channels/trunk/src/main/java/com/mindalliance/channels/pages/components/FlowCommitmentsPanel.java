package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.model.Commitment;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Part;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.List;
import java.util.Set;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Oct 28, 2009
 * Time: 1:29:01 PM
 */
public class FlowCommitmentsPanel extends FloatingCommandablePanel {

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
     * About label.
     */
    private Label infoLabel;
    /**
     * Commitments table panel.
     */
    private CommitmentsTablePanel commitmentsTablePanel;

    public FlowCommitmentsPanel( String id, IModel<Flow> flowModel, Set<Long> expansions ) {
        super( id, flowModel, expansions );
        init();
    }

    private void init() {
        addAbout();
        addCommitmentsTable();
    }

    private void addAbout() {
        infoLabel = new Label( "info", new Model<String>( getFlow().getName() ) );
        infoLabel.setOutputMarkupId( true );
        addOrReplace( infoLabel );
        Label fromTask = new Label( "fromTask", new Model<String>( ( (Part) getFlow().getSource() ).getTask() ) );
        fromTask.setOutputMarkupId( true );
        addOrReplace( fromTask );
        Label toTask = new Label( "toTask", new Model<String>( ( (Part) getFlow().getTarget() ).getTask() ) );
        toTask.setOutputMarkupId( true );
        addOrReplace( toTask );
        Label anyOrAllLabel = new Label(
                "anyOrAll",
                new Model<String>( getFlow().isAll() ? "all" : "any" ) );
        anyOrAllLabel.setOutputMarkupId( true );
        addOrReplace( anyOrAllLabel );
    }

    private void addCommitmentsTable() {
        commitmentsTablePanel = new CommitmentsTablePanel(
                "commitments",
                new PropertyModel<List<Commitment>>( this, "commitments" )
        );
        commitmentsTablePanel.setOutputMarkupId( true );
        addOrReplace( commitmentsTablePanel );
    }

    public List<Commitment> getCommitments() {
        return getQueryService().findAllCommitments( getFlow() );
    }


    /**
     * {@inheritDoc}
     */
    protected void close( AjaxRequestTarget target ) {
        Change change = new Change( Change.Type.AspectClosed, getFlow(), "commitments" );
        update( target, change );
    }

    private Flow getFlow() {
        return (Flow) getModel().getObject();
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
    @Override
    protected void refresh( AjaxRequestTarget target, Change change, String aspect ) {
        if ( change.isModified() ) {
            addAbout();
            addCommitmentsTable();
            target.addComponent( infoLabel );
            target.addComponent( commitmentsTablePanel );
        }
    }


}
