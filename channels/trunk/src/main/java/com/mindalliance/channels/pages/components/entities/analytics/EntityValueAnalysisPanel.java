package com.mindalliance.channels.pages.components.entities.analytics;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.core.util.SortableBeanProvider;
import com.mindalliance.channels.pages.components.AbstractTablePanel;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.Filterable;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Entity value analysis panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/8/12
 * Time: 11:59 AM
 */
public class EntityValueAnalysisPanel<T extends ModelEntity> extends AbstractUpdatablePanel implements Filterable {

    /**
     * Maximum number of rows in value analysis table.
     */
    private static final int MAX_ROWS = 20;

    /**
     * Model objects filtered on
     */
    private List<Identifiable> filters;
    private ValueAnalysisTable valueAnalysisTable;


    private IModel<T> entityModel;

    public EntityValueAnalysisPanel( String id, IModel<T> model, Set<Long> expansions ) {
        super( id, model, expansions );
        entityModel = model;
        init();
    }

    private void init() {
        filters = new ArrayList<Identifiable>();
        addValueAnalysisTable();
    }

    private void addValueAnalysisTable() {
        valueAnalysisTable = new ValueAnalysisTable(
                "valueAnalysis",
                new PropertyModel<List<ValueAnalysis>>( this, "allValueAnalysis" ),
                MAX_ROWS
        );
        addOrReplace( valueAnalysisTable );
    }

    public List<ValueAnalysis> getAllValueAnalysis() {
        List<ValueAnalysis> list = new ArrayList<ValueAnalysis>();
        QueryService queryService = getQueryService();
        for ( Segment segment : getPlan().getSegments() ) {
            for ( Part part : segment.listParts() ) {
                if ( part.dependsOnEntity( getEntity( ), queryService ) ) {
                    boolean redundant = part. hasAlternativesForEntity( getEntity(), getQueryService() );
                    Level value = getQueryService().computePartPriority( part );
                    list.add( new ValueAnalysis( part, redundant, value ) );
                }
            }
        }
        return list;
    }

    @Override
    public boolean isFiltered( Identifiable identifiable, String property ) {
        return filters.contains( identifiable );
    }

    @Override
    public void toggleFilter( Identifiable identifiable, String property, AjaxRequestTarget target ) {
        // Property ignored since no two properties filtered are ambiguous on type.
        if ( isFiltered( identifiable, property ) ) {
            filters.remove( identifiable );
        } else {
            filters.add( identifiable );
        }
        addValueAnalysisTable();
        target.add( valueAnalysisTable );
    }

    public T getEntity() {
        return entityModel.getObject();
    }

    @Override
    public void changed( Change change ) {
        if ( change.isForInstanceOf( ValueAnalysis.class ) ) {
            if ( change.hasQualifier( "failure" ) ) {
                Part part = ( (ValueAnalysis) change.getSubject( getCommunityService() ) ).getPart();
                change.setSubject( part );
                change.setType( Change.Type.AspectViewed );
                change.setProperty( "failure" );
                super.changed( change );
            }
        } else {
            super.changed( change );
        }
    }

    public class ValueAnalysis implements Identifiable {

        private Part part;
        private boolean redundant;
        private Level value;

        public ValueAnalysis( Part part, boolean redundant, Level value ) {
            this.part = part;
            this.redundant = redundant;
            this.value = value;
        }

        public Part getPart() {
            return part;
        }

        public boolean isRedundant() {
            return redundant;
        }

        public Level getValue() {
            return value;
        }

        public Segment getSegment() {
            return part.getSegment();
        }

        public String getAlternativesExist() {
            return isRedundant() ? "Yes" : "No";
        }

        public String getFailure() {
            return "failure";
        }

        // Identifiable


        @Override
        public String getClassLabel() {
            return "Value analysis";
        }

        @Override
        public long getId() {
            return part.getId();
        }

        @Override
        public String getDescription() {
            return "";
        }

        @Override
        public String getTypeName() {
            return getClassLabel();
        }

        @Override
        public boolean isModifiableInProduction() {
            return false;
        }

        @Override
        public String getName() {
            return "Analysis of value to task " + part.getLabel();
        }

        @Override
        public String getKindLabel() {
            return getTypeName();
        }

        @Override
        public String getUid() {
            return Long.toString( getId() );
        }


    }

    private class ValueAnalysisTable extends AbstractTablePanel<ValueAnalysis> {

        private IModel<List<ValueAnalysis>> allValueAnalysis;

        public ValueAnalysisTable(
                String id,
                IModel<List<ValueAnalysis>> allValueAnalysis,
                int maxRows ) {
            super( id, null, maxRows, null );
            this.allValueAnalysis = allValueAnalysis;
            initialize();
        }

        @SuppressWarnings( "unchecked" )
        private void initialize() {
            final List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
            // columns
            columns.add( makeFilterableLinkColumn(
                    "Value to task",
                    "part",
                    "part.label",
                    EMPTY,
                    EntityValueAnalysisPanel.this ) );
            columns.add( makeFilterableLinkColumn(
                    "In segment",
                    "segment",
                    "segment.name",
                    EMPTY,
                    EntityValueAnalysisPanel.this ) );
            columns.add( makeColumn(
                    "Is",
                    "value",
                    "value.label",
                    EMPTY ) );
            columns.add( makeColumn(
                    "Alternatives?",
                    "alternativesExist",
                    "alternativesExist",
                    EMPTY ) );
            columns.add( makeExpandLinkColumn( "", "", "Failure impact analysis", "failure" ) );
            add( new AjaxFallbackDefaultDataTable(
                    "valueAnalysisTable",
                    columns,
                    new SortableBeanProvider<ValueAnalysis>(
                            allValueAnalysis.getObject(),
                            "part.label" ),
                    getPageSize() ) );

        }
    }
}

