package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.model.Commitment;
import com.mindalliance.channels.model.Flow;
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

    public FlowCommitmentsPanel( String id, IModel<Flow> flowModel, Set<Long> expansions ) {
        super(id, flowModel, expansions);
        init();
    }

    private void init() {
        addAbout();
        addCommitments();
    }

    private void addAbout() {
        Label infoLabel = new Label( "info", new Model<String>(getFlow().getName()));
        add(infoLabel);
        Label fromTask = new Label("fromTask", new Model<String>(getFlow().getSource().getTitle()));
        add( fromTask);
        Label toTask = new Label("toTask", new Model<String>(getFlow().getTarget().getTitle()));
        add( toTask);
    }

    private void addCommitments() {
        CommitmentsPanel commitmentsPanel = new CommitmentsPanel(
                "commitments",
                new PropertyModel<List<Commitment>>( this, "commitments")
        );
        add( commitmentsPanel );
    }

    public List<Commitment>getCommitments() {
        return getQueryService().findAllCommitments( getFlow() );
    }


    /**
      * {@inheritDoc}
      */
    protected void close( AjaxRequestTarget target ) {
        Change change = new Change( Change.Type.AspectViewed, getFlow(), null );
        update( target, change );
    }

    private Flow getFlow() {
        return (Flow)getModel().getObject();
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

}
