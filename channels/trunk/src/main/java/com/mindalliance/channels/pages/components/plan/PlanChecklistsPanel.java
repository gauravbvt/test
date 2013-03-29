package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.util.SortableBeanProvider;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.entities.AbstractFilterableTablePanel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/29/13
 * Time: 8:18 AM
 */
public class PlanChecklistsPanel extends AbstractUpdatablePanel {

    private static final int PAGE_SIZE = 10;

    private static final String ALL = "all";
    private static final String CONFIRMED = "confirmed";
    private static final String UNCONFIRMED = "unconfirmed";

    private static  final String[] STATUS_CHOICES = {ALL, CONFIRMED, UNCONFIRMED};

    /**
     * Category of issues to show.
     */
    private String status = ALL;

    private ChecklistsTable checklistsTable;



    public PlanChecklistsPanel( String id ) {
        super( id );
        init();
    }

    @Override
    public void redisplay( AjaxRequestTarget target ) {
        init();
        super.redisplay( target );
    }

    private void init() {
        addFilters();
        addChecklistsTable();
    }

    private void addFilters() {
        DropDownChoice<String> statusChoice = new DropDownChoice<String>(
                "status",
                new PropertyModel<String>( this, "status"),
                Arrays.asList( STATUS_CHOICES )
        );
        statusChoice.setOutputMarkupId( true );
        statusChoice.add(  new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addChecklistsTable();
                target.add( checklistsTable );
            }
        } );
        addOrReplace( statusChoice );

    }

    private void addChecklistsTable() {
        checklistsTable = new ChecklistsTable(
                "checklistTable",
                new PropertyModel<List<Part>>( this, "checklistedParts")
        );
        addOrReplace( checklistsTable );
    }

    @SuppressWarnings( "unchecked" )
    public List<Part> getChecklistedParts() {
        return (List<Part>) CollectionUtils.select(
                getCommunityService().getPlanService().list( Part.class ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        Part part = (Part)object;
                        return status.equals( ALL )
                                || ( status.equals( CONFIRMED )
                                ? part.getChecklist().isConfirmed()
                                : !part.getChecklist().isConfirmed() );
                    }
                }
        );
    }

    private class ChecklistsTable extends AbstractFilterableTablePanel {

        private IModel<List<Part>> partsModel;

        private ChecklistsTable( String id, IModel<List<Part>> partsModel  ) {
            super( id, PAGE_SIZE );
            this.partsModel = partsModel;
            initTable();
        }

        @SuppressWarnings( "unchecked" )
        private void initTable() {
            List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
            // columns
            columns.add( makeLinkColumn( "Checklist for task", "", "label", EMPTY ) );
            columns.add( makeFilterableColumn(
                    "in segment",
                    "segment",
                    "segment.name",
                    EMPTY,
                    "segment.description",
                    ChecklistsTable.this ));
            columns.add( makeColumn( "is confirmed", "checklist.confirmed", EMPTY )
            );
            // provider and table
            addOrReplace( new AjaxFallbackDefaultDataTable(
                    "checklists",
                    columns,
                    new SortableBeanProvider<Part>(
                            getFilteredParts(),
                            "label" ),
                    getPageSize() ) );

        }

        @SuppressWarnings( "unchecked" )
        private List<Part> getFilteredParts() {
            return (List<Part>)CollectionUtils.select(
                    partsModel.getObject(),
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            return !isFilteredOut( object );
                        }
                    }
            );
        }

        @Override
        protected void resetTable( AjaxRequestTarget target ) {
            initTable();
            target.add( this );
        }
    }
}
