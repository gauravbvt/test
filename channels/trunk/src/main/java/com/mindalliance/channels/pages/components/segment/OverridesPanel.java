package com.mindalliance.channels.pages.components.segment;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.util.SortableBeanProvider;
import com.mindalliance.channels.pages.components.AbstractFloatingCommandablePanel;
import com.mindalliance.channels.pages.components.entities.AbstractFilterableTablePanel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Part overrides panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/19/11
 * Time: 1:10 PM
 */
public class OverridesPanel  extends AbstractFloatingCommandablePanel {

    /**
      * Min width on resize.
      */
     private static final int MIN_WIDTH = 300;
     /**
      * Min height on resize.
      */
     private static final int MIN_HEIGHT = 300;

    private Label partTitleLabel;
    private WebMarkupContainer overridingContainer;
    private WebMarkupContainer overriddenContainer;


    public OverridesPanel( String id, IModel<? extends Identifiable> model, Set<Long> expansions ) {
        super( id, model, expansions );
        init();
    }

    @Override
    public String getHelpSectionId() {
        return "info-sharing";
    }

    @Override
    public String getHelpTopicId() {
        return "task-overrides";
    }

    private void init() {
        addAbout();
        addOverriddenTable();
        addOverridingTable();
    }

    private void addAbout() {
        partTitleLabel = new Label(
                "partTitle",
                new Model<String>( getPart().getTitle() )
        );
         partTitleLabel.setOutputMarkupId( true );
         getContentContainer().addOrReplace( partTitleLabel );
     }

    private void addOverriddenTable() {
        overridingContainer = new WebMarkupContainer( "overriding" );
        overridingContainer.setOutputMarkupId( true );
        List<Part> overriddenParts = getOverriddenParts();
        overridingContainer.setVisible( !overriddenParts.isEmpty() );
        overridingContainer.add( new OverridesTablePanel( "overridingTable", overriddenParts ) );
        getContentContainer().addOrReplace( overridingContainer );
    }

    private List<Part> getOverriddenParts() {
        return getQueryService().findAllOverriddenParts(
                getPart(),
                getQueryService().findSynonymousParts(
                        getPart() ) );
    }

    private void addOverridingTable() {
        overriddenContainer = new WebMarkupContainer( "overridden" );
        overriddenContainer.setOutputMarkupId( true );
        List<Part> overridingParts = getOverridingParts();
        overriddenContainer.setVisible( !overridingParts.isEmpty() );
        overriddenContainer.add( new OverridesTablePanel( "overriddenTable", overridingParts ) );
        getContentContainer().addOrReplace( overriddenContainer );
    }

    private List<Part> getOverridingParts() {
        return getQueryService().findAllOverridingParts(
                getPart(),
                getQueryService().findSynonymousParts(
                        getPart() ) );
    }

    public Part getPart() {
        return (Part) getModel().getObject();
    }

    /**
     * {@inheritDoc}
     */
    protected void doClose( AjaxRequestTarget target ) {
        Change change = new Change( Change.Type.AspectClosed, getPart(), "overrides" );
        update( target, change );
    }


    /**
     * {@inheritDoc}
     */
    protected String getTitle() {
        return getPart().getTaskLabel() + " - overrides";
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
            addOverridingTable();
            addOverriddenTable();
            target.add( partTitleLabel );
            target.add( overridingContainer );
            target.add( overriddenContainer );
        }
    }

    private class OverridesTablePanel extends AbstractFilterableTablePanel {

        private final List<Part> parts;

        public OverridesTablePanel( String id, List<Part> parts ) {
            super( id );
            this.parts = parts;
            init();
        }

        @SuppressWarnings( "unchecked" )
        public List<Part> getFilteredParts() {
            return (List<Part>) CollectionUtils.select(
                    parts,
                    new Predicate() {
                        public boolean evaluate( Object obj ) {
                            return !isFilteredOut( obj );
                        }
                    }
            );
        }

        @SuppressWarnings( "unchecked" )
        private void init() {
            List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
             // columns
            columns.add( this.makeFilterableLinkColumn(
                    "Task",
                    "",
                    "task",
                    EMPTY,
                    OverridesTablePanel.this ) );
            columns.add( this.makeFilterableLinkColumn(
                    "Segment",
                    "segment",
                    "segment.name",
                    EMPTY,
                    OverridesTablePanel.this ) );
             columns.add( this.makeFilterableLinkColumn(
                     "Agent",
                     "actor",
                     "actor.normalizedName",
                     EMPTY,
                     OverridesTablePanel.this ) );
             columns.add( this.makeFilterableLinkColumn(
                     "Role",
                     "role",
                     "role.name",
                     EMPTY,
                     OverridesTablePanel.this ) );
             columns.add( makeFilterableLinkColumn(
                     "Jurisdiction",
                     "jurisdiction",
                     "jurisdiction.name",
                     EMPTY,
                     OverridesTablePanel.this ) );
             columns.add( makeFilterableLinkColumn(
                     "Organization",
                     "organization",
                     "organization.name",
                     EMPTY,
                     OverridesTablePanel.this ) );
             // provider and table
             addOrReplace( new AjaxFallbackDefaultDataTable(
                     "overrides",
                     columns,
                     new SortableBeanProvider<Part>(
                             getFilteredParts(),
                             "task" ),
                     getPageSize() ) );
         }

         /**
          * {@inheritDoc}
          */
         protected void resetTable( AjaxRequestTarget target ) {
             init();
             target.add( this );
         }
     }
    }

