package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.model.Assignment;
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
 * Time: 1:25:24 PM
 */
public class PartAssignmentsPanel extends FloatingCommandablePanel {

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

    public PartAssignmentsPanel( String id, IModel<Part> partModel, Set<Long> expansions ) {
        super(id, partModel, expansions);
        init();
    }

    private void init() {
        addAbout();
        addAssignments();
    }

    private void addAbout() {
        Label partTitleLabel = new Label(
                "partTitle",
                new Model<String>(getPart().getTitle())
        );
        add( partTitleLabel );
    }

    private void addAssignments() {
        AssignmentsPanel assignmentsPanel = new AssignmentsPanel(
                "assignments",
                new PropertyModel<List<Assignment>>(this, "assignments")
        );
        add( assignmentsPanel );
    }

    public List<Assignment> getAssignments() {
        return getQueryService().findAllAssignments( getPart(), false );
    }

    /**
      * {@inheritDoc}
      */
    protected void close( AjaxRequestTarget target ) {
        Change change = new Change( Change.Type.AspectViewed, getPart(), null );
        update( target, change );
    }

    public Part getPart() {
        return (Part)getModel().getObject();
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
