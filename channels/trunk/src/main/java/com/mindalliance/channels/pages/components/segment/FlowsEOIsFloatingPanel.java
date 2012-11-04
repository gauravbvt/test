package com.mindalliance.channels.pages.components.segment;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.EOIsHolder;
import com.mindalliance.channels.pages.components.AbstractFloatingCommandablePanel;
import com.mindalliance.channels.pages.components.EOIsEditPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.Set;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 5, 2009
 * Time: 9:01:12 AM
 */
public class FlowsEOIsFloatingPanel extends AbstractFloatingCommandablePanel {

    /**
     * Min width on resize.
     */
    private static final int MIN_WIDTH = 500;
    /**
     * Min height on resize.
     */
    private static final int MIN_HEIGHT = 300;

    private EOIsEditPanel eoisEditPanel;

    /**
     * Whether the eois are updated.
     */
    private boolean eoisUpdated = false;
    private Model<EOIsHolder> eoiHolderModel;
    private boolean canTransform;
    private Label aboutHolderLabel;


    public FlowsEOIsFloatingPanel(
            String id,
            Model<EOIsHolder> eoiHolderModel,
            boolean canTransform,
            Set<Long> expansions ) {
        super( id, eoiHolderModel, expansions );
        this.eoiHolderModel = eoiHolderModel;
        this.canTransform = canTransform;
        init();
     }

    private void init() {
        addDoneButton();
        addAboutEOIHolder();
        addEOIsEditPanel( "eois", eoiHolderModel, canTransform, getExpansions() );

    }

    private void addAboutEOIHolder() {
        aboutHolderLabel = new Label( "aboutEoiHolder", getEOIHolder().getEOIHolderLabel() );
        aboutHolderLabel.setOutputMarkupId( true );
        getContentContainer().addOrReplace( aboutHolderLabel );
    }



    private void addEOIsEditPanel(
            String id,
            IModel<EOIsHolder> eoiHolderModel,
            boolean canTransform,
            Set<Long> expansions ) {
        eoisEditPanel = new EOIsEditPanel( id, eoiHolderModel, canTransform, expansions );
        getContentContainer().addOrReplace( eoisEditPanel );
    }

    /**
     * {@inheritDoc}
     */
    protected String getTitle() {
        return getEOIHolder().getName() + " - EOIs";
    }

    private void addDoneButton() {
        AjaxFallbackLink doneLink = new AjaxFallbackLink( "done" ) {
            public void onClick( AjaxRequestTarget target ) {
                close( target );
            }
        };
        getContentContainer().add( doneLink );
    }

    public void changed( Change change ) {
        if ( change.isUpdated() && change.isForInstanceOf( EOIsHolder.class ) ) {
            eoisUpdated = true;
        }
        super.changed( change );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void refresh( AjaxRequestTarget target, Change change, String aspect ) {
        addAboutEOIHolder();
        target.add( aboutHolderLabel );
        eoisEditPanel.refresh( target, change, aspect );
    }

        /**
        * {@inheritDoc}
        */
    protected void doClose( AjaxRequestTarget target ) {
        Change change = new Change( Change.Type.AspectClosed, getEOIHolder(), ExpandedFlowPanel.EOIS );
        change.addQualifier( "updated", eoisUpdated );
        update( target, change );
    }

    private EOIsHolder getEOIHolder() {
        return (EOIsHolder) getModel().getObject();
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
