package com.mindalliance.channels.pages.components.entities.analytics;

import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.InfoFormat;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.core.util.SortableBeanProvider;
import com.mindalliance.channels.pages.components.AbstractTablePanel;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.Filterable;
import com.mindalliance.channels.pages.components.guide.Guidable;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Info format analytics panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/6/12
 * Time: 1:45 PM
 */
public class InfoFormatAnalyticsPanel extends AbstractUpdatablePanel implements Filterable, Guidable {

    /**
     * Maximum number of rows in references table.
     */
    private static final int MAX_ROWS = 20;
    /**
     * Format references table.
     */
    private FormatReferenceTable formatReferenceTable;

    /**
     * Model objects filtered on (show only where so and so is the actor etc.)
     */
    private List<Identifiable> filters;


    public InfoFormatAnalyticsPanel( String id, IModel<ModelEntity> model, Set<Long> expansions ) {
        super(id, model, expansions);
        init();
    }

    @Override
    public String getHelpSectionId() {
        return "analyzing";
    }

    @Override
    public String getHelpTopicId() {
        return "entity-analytics";
    }

    private void init() {
        filters = new ArrayList<Identifiable>();
        addFormatReferenceTable();
        addValueAnalysis();
    }

    private void addFormatReferenceTable() {
        formatReferenceTable = new FormatReferenceTable(
                "formatReferences",
                new PropertyModel<List<FormatReference>>( this, "formatReferences" ),
                MAX_ROWS
        );
        addOrReplace( formatReferenceTable );
    }

    private boolean isFilteredOut( FormatReference formatReference ) {
        Segment sc = formatReference.getSegment();
        TransmissionMedium medium = formatReference.getMedium();
        boolean filteredOut = false;
        for ( Identifiable filter : filters ) {
            filteredOut = filteredOut ||
                    ( filter instanceof Segment && !filter.equals( sc )
                            || ( filter instanceof TransmissionMedium && !filter.equals( medium ) ) );
        }
        return filteredOut;
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
        addFormatReferenceTable();
        target.add( formatReferenceTable );
    }

    @SuppressWarnings( "unchecked" )
    public List<FormatReference> getFormatReferences() {
        List<FormatReference> references = new ArrayList<FormatReference>();
        final InfoFormat infoFormat = getInfoFormat();
        List<Flow> flows = getQueryService().findAllReferencing( infoFormat, Flow.class );
        for ( Flow flow : flows ) {
            for (Channel channel : flow.getEffectiveChannels() ) {
                if ( channel.getFormat() != null
                        && channel.getFormat().equals( infoFormat ) ) {
                    FormatReference formatRef = new FormatReference( flow, channel );
                    if ( !isFilteredOut( formatRef ) )
                        references.add( formatRef );
                }
            }
        }
        return references;
    }

    private void addValueAnalysis() {
        add( new Label( "valueAnalysisTitle", "Value of info format \"" + getInfoFormat().getName() + "\"" ) );
        EntityValueAnalysisPanel valuePanel = new EntityValueAnalysisPanel<InfoFormat>(
                "valueAnalysis",
                new PropertyModel<InfoFormat>( this, "infoFormat" ),
                getExpansions()
        );
        add( valuePanel );
    }

    public InfoFormat getInfoFormat() {
        return (InfoFormat) getModel().getObject();
    }

    /**
     * Format reference table.
     */
    public class FormatReferenceTable extends AbstractTablePanel<FormatReference> {
        /**
         * Format reference model.
         */
        private IModel<List<FormatReference>> formatReferencesModel;

        public FormatReferenceTable(
                String id,
                IModel<List<FormatReference>> formatReferencesModel,
                int pageSize ) {
            super( id, null, pageSize, null );
            this.formatReferencesModel = formatReferencesModel;
            initialize();
        }

        @SuppressWarnings( "unchecked" )
        private void initialize() {
            final List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
            // columns
            columns.add( makeFilterableLinkColumn(
                    "Segment",
                    "segment",
                    "segment.name",
                    EMPTY,
                    InfoFormatAnalyticsPanel.this ) );
            columns.add( makeLinkColumn(
                    "Flow",
                    "flow",
                    "flow.name",
                    EMPTY ) );
            columns.add( makeFilterableLinkColumn(
                    "For medium",
                    "medium",
                    "medium.name",
                    EMPTY,
                    InfoFormatAnalyticsPanel.this ) );
            // provider and table
            add( new AjaxFallbackDefaultDataTable(
                    "formatReferences",
                    columns,
                    new SortableBeanProvider<FormatReference>(
                            formatReferencesModel.getObject(),
                            "segment.name" ),
                    getPageSize() ) );

        }
    }

    /**
     * A channel in a flow referencing an information format.
     */
    public class FormatReference implements Serializable {


        private final Flow flow;
        private final Channel channel;

        public FormatReference( Flow flow, Channel channel ) {
            this.flow = flow;
            this.channel = channel;
        }

        public Flow getFlow() {
            return flow;
        }

        public TransmissionMedium getMedium() {
            return channel.getMedium();
        }

        public Segment getSegment() {
            return flow.getSegment();
        }
    }

}
