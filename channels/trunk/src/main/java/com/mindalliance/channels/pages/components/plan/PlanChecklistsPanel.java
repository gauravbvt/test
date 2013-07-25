package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.model.checklist.Checklist;
import com.mindalliance.channels.core.util.SortableBeanProvider;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.entities.AbstractFilterableTablePanel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.io.Serializable;
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
    /**
     * Show only checklist that have issues.
     */
    private boolean onlyIfWithIssues = false;

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
        // status
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
       // issues
        AjaxCheckBox withIssuesCheckBox = new AjaxCheckBox(
                "withIssues",
                new PropertyModel<Boolean>( this, "onlyIfWithIssues" )
        ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addChecklistsTable();
                target.add( checklistsTable );
            }
        };
        withIssuesCheckBox.setOutputMarkupId( true );
        addOrReplace( withIssuesCheckBox );
    }

    private void addChecklistsTable() {
        checklistsTable = new ChecklistsTable(
                "checklistTable",
                new PropertyModel<List<PartWrapper>>( this, "checklistedParts")
        );
        addOrReplace( checklistsTable );
    }

    @SuppressWarnings( "unchecked" )
    public List<PartWrapper> getChecklistedParts() {
        List<PartWrapper> wrappers = new ArrayList<PartWrapper>(  );
        List<Part> parts = (List<Part>) CollectionUtils.select(
                getCommunityService().getPlanService().list( Part.class ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        Part part = (Part)object;
                        return ( status.equals( ALL )
                                || ( status.equals( CONFIRMED )
                                ? part.getEffectiveChecklist().isConfirmed()
                                : !part.getEffectiveChecklist().isConfirmed() )
                                )
                                && ( !isOnlyIfWithIssues() || hasChecklistIssues( part ) );
                    }
                }
        );
        for (Part part : parts) {
            wrappers.add(  new PartWrapper( part ) );
        }
        return wrappers;
    }

    private boolean hasChecklistIssues( Part part ) {
        return countChecklistIssues( part ) > 0;
    }

    public int countChecklistIssues( Part part ) {
        return part.countChecklistIssues( getAnalyst(), getPlanService() );
    }


    public String getStatus() {
        return status;
    }

    public void setStatus( String status ) {
        this.status = status;
    }

    public boolean isOnlyIfWithIssues() {
        return onlyIfWithIssues;
    }

    public void setOnlyIfWithIssues( boolean onlyIfWithIssues ) {
        this.onlyIfWithIssues = onlyIfWithIssues;
    }

    public void update( AjaxRequestTarget target, Object object, String action ) {
        if ( object instanceof PartWrapper && action.equals( "showFlow" ) ) {
            update( target, new Change( Change.Type.AspectViewed, ((PartWrapper)object).getPart(), "checklist-flow" ) );
        }
    }

    public class PartWrapper implements Serializable {

        private Part part;

        PartWrapper( Part part ) {
            this.part = part;
        }

        public Part getPart() {
            return part;
        }

        public String getLabel() {
            return part.getLabel();
        }

        public Segment getSegment() {
            return part.getSegment();
        }

        public Checklist getChecklist() {
            return part.getEffectiveChecklist();
        }

        public int getChecklistIssueCount() {
            return countChecklistIssues( part );
        }

        public String getConfirmed() {
            return getChecklist().isConfirmed() ? "Yes" : "No";
        }
    }

    private class ChecklistsTable extends AbstractFilterableTablePanel {

        private IModel<List<PartWrapper>> partsModel;

        private ChecklistsTable( String id, IModel<List<PartWrapper>> partsModel  ) {
            super( id, PAGE_SIZE );
            this.partsModel = partsModel;
            initTable();
        }

        @SuppressWarnings( "unchecked" )
        private void initTable() {
            List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
            // columns
            columns.add( makeLinkColumn( "Checklist for task", "part", "label", EMPTY ) );
            columns.add( makeFilterableLinkColumn(
                    "in segment",
                    "segment",
                    "segment.name",
                    EMPTY,
                    ChecklistsTable.this ));
            columns.add( makeColumn( "has issues", "checklistIssueCount", EMPTY ) );
            columns.add( makeColumn( "is confirmed", "confirmed", EMPTY )  );
            columns.add( makeActionLinkColumn(
                    "",
                    "Show",
                    "showFlow",
                    null,
                    "part",
                    "more",
                    PlanChecklistsPanel.this
            ));
            // provider and table
            addOrReplace( new AjaxFallbackDefaultDataTable(
                    "checklists",
                    columns,
                    new SortableBeanProvider<PartWrapper>(
                            getFilteredParts(),
                            "label" ),
                    getPageSize() ) );

        }

        @SuppressWarnings( "unchecked" )
        private List<PartWrapper> getFilteredParts() {
            return (List<PartWrapper>)CollectionUtils.select(
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
