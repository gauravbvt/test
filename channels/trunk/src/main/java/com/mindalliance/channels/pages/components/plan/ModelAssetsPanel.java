package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.core.model.AbstractIdentifiable;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Function;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.core.model.asset.AssetConnectable;
import com.mindalliance.channels.core.model.asset.AssetConnection;
import com.mindalliance.channels.core.model.asset.MaterialAsset;
import com.mindalliance.channels.core.util.NameRange;
import com.mindalliance.channels.core.util.SortableBeanProvider;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.AbstractTablePanel;
import com.mindalliance.channels.pages.components.Filterable;
import com.mindalliance.channels.pages.components.NameRangePanel;
import com.mindalliance.channels.pages.components.NameRangeable;
import com.mindalliance.channels.pages.components.guide.Guidable;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;
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
 * Panel with how all material assets in the model are used, produces, provisioned etc..
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/18/14
 * Time: 5:17 PM
 */
public class ModelAssetsPanel extends AbstractCommandablePanel implements NameRangeable, Filterable, Guidable {

    /**
     * Indexing choice.
     */
    private static final String ELEMENTS = "Elements";
    /**
     * Indexing choice.
     */
    private static final String ASSETS = "Assets";
    /**
     * Indexing choices.
     */
    private static final String[] indexingChoices = {ASSETS, ELEMENTS};
    /**
     * Maximum number of rows shown in table at a time.
     */
    private static final int MAX_ROWS = 13;
    /**
     * What "column" to index names on.
     */
    private String indexedOn = indexingChoices[0];
    /**
     * Name index panel.
     */
    private NameRangePanel nameRangePanel;
    /**
     * Selected name range.
     */
    private NameRange nameRange = new NameRange();
    /**
     * Model objects filtered on (show only where so and so is the actor etc.)
     */
    private List<Identifiable> filters = new ArrayList<Identifiable>();

    private AssetConnectionTable assetConnectionTable;

    private List<AssetConnectionWrapper> assetConnectionWrappers;

    public ModelAssetsPanel( String id ) {
        super( id );
        init();
    }

    @Override
    public String getHelpSectionId() {
        return "searching";
    }

    @Override
    public String getHelpTopicId() {
        return "all-material-assets";
    }

    @Override
    public void redisplay( AjaxRequestTarget target ) {
        init();
        super.redisplay( target );
    }

    private void init() {
        addIndexedOnChoice();
        addNameRangePanel();
        addAssetConnectionTable();
    }

    private void addIndexedOnChoice() {
        DropDownChoice<String> indexedOnChoices = new DropDownChoice<String>(
                "indexed",
                new PropertyModel<String>( this, "indexedOn" ),
                Arrays.asList( indexingChoices ) );
        indexedOnChoices.setOutputMarkupId( true );
        indexedOnChoices.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                nameRange = new NameRange();
                addNameRangePanel();
                addAssetConnectionTable();
                target.add( nameRangePanel );
                target.add( assetConnectionTable );
            }
        } );
        addOrReplace( indexedOnChoices );
    }

    private void addNameRangePanel() {
        nameRangePanel = new NameRangePanel(
                "nameRanges",
                new PropertyModel<List<String>>( this, "indexedNames" ),
                MAX_ROWS,
                this,
                "All names"
        );
        nameRangePanel.setOutputMarkupId( true );
        addOrReplace( nameRangePanel );
    }

    private void addAssetConnectionTable() {
        assetConnectionTable = new AssetConnectionTable(
                "assetConnections",
                new PropertyModel<List<AssetConnectionWrapper>>( this, "assetConnections" ),
                MAX_ROWS

        );
        assetConnectionTable.setOutputMarkupId( true );
        addOrReplace( assetConnectionTable );
    }

    private List<AssetConnectionWrapper> getAllAssetConnections() {
        if ( assetConnectionWrappers == null ) {
            assetConnectionWrappers = new ArrayList<AssetConnectionWrapper>();
            for ( Part part : getQueryService().list( Part.class ) ) {
                for ( AssetConnection assetConnection : part.getAssetConnections() ) {
                    assetConnectionWrappers.add( new AssetConnectionWrapper( part, assetConnection ) );
                }
            }
            for ( Flow flow : getQueryService().list( Flow.class ) ) {
                for ( AssetConnection assetConnection : flow.getAssetConnections() ) {
                    assetConnectionWrappers.add( new AssetConnectionWrapper( flow, assetConnection ) );
                }
            }
            for ( Organization org : getQueryService().listActualEntities( Organization.class ) ) {
                for ( AssetConnection assetConnection : org.getAssetConnections() ) {
                    assetConnectionWrappers.add( new AssetConnectionWrapper( org, assetConnection ) );
                }
            }
            for ( Function function : getQueryService().listTypeEntities( Function.class ) ) {
                for ( AssetConnection assetConnection : function.getAssetConnections() ) {
                    assetConnectionWrappers.add( new AssetConnectionWrapper( function, assetConnection ) );
                }
            }
            for ( TransmissionMedium medium : getQueryService().listTypeEntities( TransmissionMedium.class ) ) {
                for ( AssetConnection assetConnection : medium.getAssetConnections() ) {
                    assetConnectionWrappers.add( new AssetConnectionWrapper( medium, assetConnection ) );
                }
            }

        }
        return assetConnectionWrappers;
    }

    @SuppressWarnings( "unchecked" )
    public List<AssetConnectionWrapper> getAssetConnections() {
        return (List<AssetConnectionWrapper>) CollectionUtils.select(
                getAllAssetConnections(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        AssetConnectionWrapper assetConnectionWrapper = (AssetConnectionWrapper) object;
                        return !isFilteredOut( assetConnectionWrapper )
                                && isInNameRange( assetConnectionWrapper );
                    }
                }
        );
    }

    private boolean isFilteredOut( AssetConnectionWrapper wrapper ) {
        boolean filteredOut = false;
        for ( Identifiable filter : filters ) {
            filteredOut = filteredOut ||
                    ( filter instanceof AssetConnectable && !wrapper.getAssetConnectable().equals( filter ) )
                    || ( filter instanceof MaterialAsset && wrapper.getAsset() != filter );
        }
        return filteredOut;
    }

    private boolean isInNameRange( AssetConnectionWrapper wrapper ) {
        if ( indexedOn.equals( ASSETS ) ) {
            return nameRange.contains( wrapper.getAsset().getName() );
        } else if ( indexedOn.equals( ELEMENTS ) ) {
            return nameRange.contains( wrapper.getAssetConnectable().getLabel() );
        } else {
            throw new IllegalStateException( "Can't index on " + indexedOn );
        }
    }

    /**
     * Find all names to be indexed.
     *
     * @return a list of strings
     */
    @SuppressWarnings( "unchecked" )
    public List<String> getIndexedNames() {
        if ( indexedOn.equals( ASSETS ) ) {
            return (List<String>) CollectionUtils.collect(
                    getAssetConnections(),
                    new Transformer() {
                        public Object transform( Object obj ) {
                            return ( (AssetConnectionWrapper) obj ).getAsset().getName();
                        }
                    } );
        } else if ( indexedOn.equals( ELEMENTS ) ) {
            return (List<String>) CollectionUtils.collect(
                    getAssetConnections(),
                    new Transformer() {
                        public Object transform( Object obj ) {
                            return ( (AssetConnectionWrapper) obj ).getAssetConnectable().getLabel();
                        }

                    } );
        } else {
            throw new IllegalStateException( "Can't index on " + indexedOn );
        }
    }

    public String getIndexedOn() {
        return indexedOn;
    }

    public void setIndexedOn( String val ) {
        indexedOn = val;
    }

    /**
     * Change the selected name range.
     *
     * @param target an ajax request target
     * @param range  a name range
     */
    public void setNameRange( AjaxRequestTarget target, NameRange range ) {
        nameRange = range;
        nameRangePanel.setSelected( target, range );
        addAssetConnectionTable();
        target.add( assetConnectionTable );
    }

    /**
     * {@inheritDoc}
     */
    public void toggleFilter( Identifiable identifiable, String property, AjaxRequestTarget target ) {
        // Property ignored since no two properties filtered are ambiguous on type.
        if ( isFiltered( identifiable, property ) ) {
            filters.remove( identifiable );
        } else {
            filters.add( identifiable );
        }
        addAssetConnectionTable();
        target.add( assetConnectionTable );
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFiltered( Identifiable identifiable, String property ) {
        return filters.contains( identifiable );
    }


    public class AssetConnectionWrapper extends AbstractIdentifiable {

        private AssetConnectable assetConnectable;
        private AssetConnection assetConnection;

        private AssetConnectionWrapper( AssetConnectable assetConnectable, AssetConnection assetConnection ) {
            this.assetConnectable = assetConnectable;
            this.assetConnection = assetConnection;
        }

        public AssetConnectable getAssetConnectable() {
            return assetConnectable;
        }

        public String getAssetConnectableType() {
            return StringUtils.capitalize( assetConnectable.getTypeName() );
        }

        public AssetConnection getAssetConnection() {
            return assetConnection;
        }

        public MaterialAsset getAsset() {
            return assetConnection.getAsset();
        }

        public String getTypeLabel() {
            return assetConnection.getTypeLabel();
        }

        public String getProperties() {
            StringBuilder sb = new StringBuilder();
            if ( assetConnection.isCritical() ) {
                sb.append( "critical" );
            }
            if ( assetConnection.isConsuming() ) {
                if ( sb.length() > 0 )
                    sb.append( " and " );
                sb.append( "consumes" );
            }
            if ( assetConnection.isForwarding() ) {
                sb.append( "forwarded" );
            }
            return StringUtils.capitalize( sb.toString() );
        }
    }

    public class AssetConnectionTable extends AbstractTablePanel<AssetConnectionWrapper> {
        private IModel<List<AssetConnectionWrapper>> assetConnectionsModel;

        public AssetConnectionTable( String id, IModel<List<AssetConnectionWrapper>> assetConnectionsModel, int pageSize ) {
            super( id, null, pageSize, null );
            this.assetConnectionsModel = assetConnectionsModel;
            initTable();
        }

        @SuppressWarnings( "unchecked" )
        private void initTable() {
            final List<IColumn<AssetConnectionWrapper>> columns = new ArrayList<IColumn<AssetConnectionWrapper>>();
            columns.add( makeColumn( "Kind", "assetConnectableType", EMPTY ));
            columns.add( makeFilterableLinkColumn(
                    "Model element",
                    "assetConnectable",
                    "assetConnectable.label",
                    EMPTY,
                    ModelAssetsPanel.this
            ) );
            columns.add( makeColumn( "Relation", "typeLabel", EMPTY ) );
            columns.add( makeFilterableLinkColumn(
                    "Material asset",
                    "asset",
                    "asset.name",
                    EMPTY,
                    ModelAssetsPanel.this
            ) );
            columns.add( makeColumn( "Properties", "properties", EMPTY ) );
            // provider and table
            add( new AjaxFallbackDefaultDataTable(
                    "assetConnectionsTable",
                    columns,
                    new SortableBeanProvider<AssetConnectionWrapper>(
                            assetConnectionsModel.getObject(),
                            "asset.name" ),
                    getPageSize()
            )
            );
        }
    }
}
