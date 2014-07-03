package com.mindalliance.channels.pages.components.segment;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.pages.components.AbstractFloatingCommandablePanel;
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
public class SharingCommitmentsPanel extends AbstractFloatingCommandablePanel {

    /**
     * Min width on resize.
     */
    private static final int MIN_WIDTH = 300;
    /**
     * Min height on resize.
     */
    private static final int MIN_HEIGHT = 300;
    /**
     * Commitments table panel.
     */
    private CommitmentsTablePanel commitmentsTablePanel;

    public SharingCommitmentsPanel( String id, IModel<Flow> flowModel, Set<Long> expansions ) {
        super( id, flowModel, expansions );
        init();
    }

    @Override
    public String getHelpSectionId() {
        return "concepts";
    }

    @Override
    public String getHelpTopicId() {
        return "commitment";
    }

    private void init() {
        addAbout();
        addCommitmentsTable();
    }

    /**
     * {@inheritDoc}
     */
    protected String getTitle() {
        return getFlow().getName() + " - commitments";
    }

    private void addAbout() {
        Label fromTask = new Label( "fromTask", new Model<String>( ( (Part) getFlow().getSource() ).getTaskLabel() ) );
        fromTask.setOutputMarkupId( true );
        getContentContainer().addOrReplace( fromTask );
        Label toTask = new Label( "toTask", new Model<String>( ( (Part) getFlow().getTarget() ).getTaskLabel() ) );
        toTask.setOutputMarkupId( true );
        getContentContainer().addOrReplace( toTask );
    }

    private void addCommitmentsTable() {
        commitmentsTablePanel = new CommitmentsTablePanel(
                "commitments",
                new PropertyModel<List<Commitment>>( this, "commitments" )
        );
        commitmentsTablePanel.setOutputMarkupId( true );
        getContentContainer().addOrReplace( commitmentsTablePanel );
    }

    public List<Commitment> getCommitments() {
        // exclude commitments to self and to or by unknown actors.
        QueryService queryService = getQueryService();
        return queryService.findAllCommitments( getFlow(),
                                                false,
                                                queryService.getAssignments( false ) );
    }


    /**
     * {@inheritDoc}
     */
    protected void doClose( AjaxRequestTarget target ) {
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
        if ( change.isUnknown() || change.isModified() || change.isRefresh() ) {
            addAbout();
            addCommitmentsTable();
            target.add( commitmentsTablePanel );
        }
    }


}
