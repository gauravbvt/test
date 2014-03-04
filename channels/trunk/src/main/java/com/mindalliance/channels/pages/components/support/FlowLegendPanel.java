package com.mindalliance.channels.pages.components.support;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.pages.components.AbstractFloatingCommandablePanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 3, 2010
 * Time: 7:22:38 PM
 */
public class FlowLegendPanel extends AbstractFloatingCommandablePanel {

     /**
     * Min width on resize.
     */
    private static final int MIN_WIDTH = 300;
    /**
     * Min height on resize.
     */
    private static final int MIN_HEIGHT = 300;

    public FlowLegendPanel( String id, IModel<Segment> model ) {
        super( id, model, null );
    }

    @Override
    public String getHelpSectionId() {
        return "info-sharing";
    }

    @Override
    public String getHelpTopicId() {
        return "flow-map";
    }

    /**
     * {@inheritDoc}
     */
    protected String getTitle() {
        return "Flow map legend";
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

    /** {@inheritDoc} */
    protected void doClose( AjaxRequestTarget target ) {
        Change change = new Change( Change.Type.Unexplained, getModel().getObject(), "legend" );
        update( target, change );
    }
}
