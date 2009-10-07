package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Event;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Phase;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.pages.ModelObjectLink;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.NameRangePanel;
import com.mindalliance.channels.pages.components.NameRangeable;
import com.mindalliance.channels.util.NameRange;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.io.Serializable;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Plan index panel
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 1, 2009
 * Time: 11:41:35 AM
 */
public class PlanIndexPanel extends AbstractCommandablePanel implements NameRangeable {

    /**
     * Indexing choice.
     */
    private static final String ALL = "All";
    /**
     * Indexing choice.
     */
    private static final String ACTORS = "Actors";
    /**
     * Indexing choice.
     */
    private static final String ROLES = "Roles";
    /**
     * Indexing choice.
     */
    private static final String ORGANIZATIONS = "Organizations";
    /**
     * Indexing choice.
     */
    private static final String PLACES = "Places";
    /**
     * Indexing choice.
     */
    private static final String EVENTS = "Events";
    /**
     * Indexing choice.
     */
    private static final String TASKS = "Tasks";
    /**
     * Indexing choice.
     */
    private static final String FLOWS = "Flows";
    /**
     * Indexing choice.
     */
    private static final String PHASES = "Phases";
    /**
     * Indexing choices.
     */
    private static final String[] indexingChoices =
            {ALL, ACTORS, EVENTS, FLOWS, PHASES, PLACES, ORGANIZATIONS, ROLES, TASKS};
    /**
     * Maximum number of rows shown in table at a time.
     */
    private static final int MAX_INDEX_ROWS = 23;
    /**
     * Length at which a name is abbreviated.
     */
    private static final int MAX_NAME_LENGTH = 30;
    /**
     * A collator.
     */
    private static Collator collator = Collator.getInstance();
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
     * Filter string.
     */
    private String filter = "";
    /**
     * Name index panel.
     */
    private WebMarkupContainer indicesContainer;
    /**
     * Cached index entries.
     */
    private List<IndexEntry> indices;

    public PlanIndexPanel( String id, IModel<? extends Identifiable> model, Set<Long> expansions ) {
        super( id, model, expansions );
        init();
    }

    private void init() {
        addIndexChoice();
        addFilterField();
        addNameRangePanel();
        addIndices();
    }

    private void addIndexChoice() {
        DropDownChoice<String> indexedOnChoices = new DropDownChoice<String>(
                "indexed",
                new PropertyModel<String>( this, "indexedOn" ),
                Arrays.asList( indexingChoices ) );
        indexedOnChoices.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                indices = null;
                nameRange = new NameRange();
                addNameRangePanel();
                addIndices();
                target.addComponent( nameRangePanel );
                target.addComponent( indicesContainer );
            }
        } );
        add( indexedOnChoices );
    }

    private void addNameRangePanel() {
        nameRangePanel = new NameRangePanel(
                "nameRanges",
                new PropertyModel<List<String>>( this, "indexedNames" ),
                MAX_INDEX_ROWS * 3,
                this,
                "All"
        );
        nameRangePanel.setOutputMarkupId( true );
        addOrReplace( nameRangePanel );
    }

    private void addFilterField() {
        TextField<String> filterField = new TextField<String>(
                "filter",
                new PropertyModel<String>( this, "filter" )
        );
        filterField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                addNameRangePanel();
                target.addComponent( nameRangePanel );
                addIndices();
                target.addComponent( indicesContainer );
            }
        } );
        add( filterField );
    }

    private void addIndices() {
        indicesContainer = new WebMarkupContainer( "indices" );
        indicesContainer.setOutputMarkupId( true );
        addOrReplace( indicesContainer );
        ListView<IndexEntry> indices1 = new ListView<IndexEntry>(
                "indices1",
                getIndices1()
        ) {
            protected void populateItem( ListItem<IndexEntry> item ) {
                IndexEntry indexEntry = item.getModelObject();
                item.add( indexEntry.isRepeated()
                        ? new RepeatedIndexEntryPanel( "indexEntry", new Model<IndexEntry>( indexEntry ) )
                        : new SingleIndexEntryPanel( "indexEntry", new Model<IndexEntry>( indexEntry ) ) );
            }
        };
        indicesContainer.addOrReplace( indices1 );
        ListView<IndexEntry> indices2 = new ListView<IndexEntry>(
                "indices2",
                getIndices2()
        ) {
            protected void populateItem( ListItem<IndexEntry> item ) {
                IndexEntry indexEntry = item.getModelObject();
                item.add( indexEntry.isRepeated()
                        ? new RepeatedIndexEntryPanel( "indexEntry", new Model<IndexEntry>( indexEntry ) )
                        : new SingleIndexEntryPanel( "indexEntry", new Model<IndexEntry>( indexEntry ) ) );
            }
        };
        indicesContainer.addOrReplace( indices2 );
        ListView<IndexEntry> indices3 = new ListView<IndexEntry>(
                "indices3",
                getIndices3()
        ) {
            protected void populateItem( ListItem<IndexEntry> item ) {
                IndexEntry indexEntry = item.getModelObject();
                item.add( indexEntry.isRepeated()
                        ? new RepeatedIndexEntryPanel( "indexEntry", new Model<IndexEntry>( indexEntry ) )
                        : new SingleIndexEntryPanel( "indexEntry", new Model<IndexEntry>( indexEntry ) ) );
            }
        };
        indicesContainer.addOrReplace( indices3 );
    }

    /**
     * Get all distinct, filtered names to be indexed, as lowercase.
     *
     * @return a list of strings
     */
    @SuppressWarnings( "unchecked" )
    public List<String> getIndexedNames() {
        List<String> names;
        QueryService queryService = getQueryService();
        if ( indexedOn.equals( ALL ) ) {
            names = getAllNames();
        } else if ( indexedOn.equals( ACTORS ) ) {
            names = queryService.findAllActorLastNames();
        } else if ( indexedOn.equals( ROLES ) ) {
            names = queryService.findAllNames( Role.class );
        } else if ( indexedOn.equals( ORGANIZATIONS ) ) {
            names = queryService.findAllNames( Organization.class );
        } else if ( indexedOn.equals( PLACES ) ) {
            names = queryService.findAllNames( Place.class );
        } else if ( indexedOn.equals( EVENTS ) ) {
            names = queryService.findAllNames( Event.class );
        } else if ( indexedOn.equals( PHASES ) ) {
            names = queryService.findAllNames( Phase.class );
        } else if ( indexedOn.equals( TASKS ) ) {
            names = queryService.findAllTasks();
        } else if ( indexedOn.equals( FLOWS ) ) {
            names = queryService.findAllFlowNames();
        } else {
            throw new IllegalStateException( "Can't index on " + indexedOn );
        }
        return (List<String>) CollectionUtils.collect(
                CollectionUtils.select( names, new Predicate() {
                    public boolean evaluate( Object obj ) {
                        return !isFilteredOut( (String) obj );
                    }
                } ),
                new Transformer() {
                    public Object transform( Object obj ) {
                        return ( (String) obj ).toLowerCase();
                    }
                } );
    }

    private List<String> getAllNames() {
        QueryService queryService = getQueryService();
        Set<String> names = new HashSet<String>();
        names.addAll( queryService.findAllActorLastNames() );
        names.addAll( queryService.findAllNames( Role.class ) );
        names.addAll( queryService.findAllNames( Organization.class ) );
        names.addAll( queryService.findAllNames( Place.class ) );
        names.addAll( queryService.findAllNames( Event.class ) );
        names.addAll( queryService.findAllNames( Phase.class ) );
        names.addAll( queryService.findAllTasks() );
        names.addAll( queryService.findAllFlowNames() );
        return new ArrayList<String>( names );
    }

    /**
     * {@inheritDoc}
     */
    public void setNameRange( AjaxRequestTarget target, NameRange range ) {
        indices = null;
        nameRange = range;
        nameRangePanel.setSelected( target, range );
        addIndices();
        target.addComponent( indicesContainer );
    }

    public void setFilter( String val ) {
        indices = null;
        filter = val == null ? "" : val.trim().toLowerCase();
    }

    public String getFilter() {
        return filter == null ? "" : filter;
    }

    /**
     * Get index entries for column 1.
     *
     * @return a list of index entries.
     */
    public List<IndexEntry> getIndices1() {
        List<IndexEntry> allIndices = getAllIndices();
        int fromIndex = 0;
        int toIndex = getRowCounts()[0];
        return ( toIndex > 0 )
                ? allIndices.subList( fromIndex, toIndex )
                : new ArrayList<IndexEntry>();
    }

    /**
     * Get index entries for column 2.
     *
     * @return a list of index entries.
     */
    public List<IndexEntry> getIndices2() {
        List<IndexEntry> allIndices = getAllIndices();
        int fromIndex = getRowCounts()[0];
        int toIndex = getRowCounts()[0] + getRowCounts()[1];
        return ( toIndex > 0 )
                ? allIndices.subList( fromIndex, toIndex )
                : new ArrayList<IndexEntry>();
    }

    /**
     * Get index entries for column 3.
     *
     * @return a list of index entries.
     */
    public List<IndexEntry> getIndices3() {
        List<IndexEntry> allIndices = getAllIndices();
        int fromIndex = getRowCounts()[0] + getRowCounts()[1];
        int toIndex = allIndices.size();
        return ( toIndex > 0 )
                ? allIndices.subList( fromIndex, toIndex )
                : new ArrayList<IndexEntry>();
    }

    private int[] getRowCounts() {
        int count = getAllIndices().size();
        int split = count / 3;
        int[] rowCounts = new int[]{split, split, split};
        count = count % 3;
        if ( count-- > 0 ) rowCounts[0]++;
        if ( count > 0 ) rowCounts[1]++;
        return rowCounts;
    }

    private List<IndexEntry> getAllIndices() {
        if ( indices == null ) {
            if ( indexedOn.equals( ALL ) ) {
                indices = getIndicesForAllNames();
            } else if ( indexedOn.equals( ACTORS ) ) {
                indices = getIndicesForAllActors();
            } else if ( indexedOn.equals( ROLES ) ) {
                indices = getIndicesForAllEntities( Role.class );
            } else if ( indexedOn.equals( ORGANIZATIONS ) ) {
                indices = getIndicesForAllEntities( Organization.class );
            } else if ( indexedOn.equals( PLACES ) ) {
                indices = getIndicesForAllEntities( Place.class );
            } else if ( indexedOn.equals( EVENTS ) ) {
                indices = getIndicesForAllEntities( Event.class );
            } else if ( indexedOn.equals( PHASES ) ) {
                indices = getIndicesForAllEntities( Phase.class );
            } else if ( indexedOn.equals( TASKS ) ) {
                indices = getIndicesForAllTasks();
            } else if ( indexedOn.equals( FLOWS ) ) {
                indices = getIndicesForAllFlows();
            } else {
                throw new IllegalStateException( "Can't index on " + indexedOn );
            }
        }
        Collections.sort( indices );
        return indices;
    }

    private List<IndexEntry> getIndicesForAllNames() {
        List<IndexEntry> indexEntries = new ArrayList<IndexEntry>();
        indexEntries.addAll( getIndicesForAllActors() );
        indexEntries.addAll( getIndicesForAllEntities( Role.class ) );
        indexEntries.addAll( getIndicesForAllEntities( Organization.class ) );
        indexEntries.addAll( getIndicesForAllEntities( Place.class ) );
        indexEntries.addAll( getIndicesForAllEntities( Event.class ) );
        indexEntries.addAll( getIndicesForAllEntities( Phase.class ) );
        indexEntries.addAll( getIndicesForAllTasks() );
        indexEntries.addAll( getIndicesForAllFlows() );
        return indexEntries;
    }

    @SuppressWarnings( "unchecked" )
    private List<IndexEntry> getIndicesForAllActors() {
        return (List<IndexEntry>) CollectionUtils.collect(
                CollectionUtils.select( getQueryService().listReferenced( Actor.class ), new Predicate() {
                    public boolean evaluate( Object obj ) {
                        return nameRange.contains( ( (Actor) obj ).getLastName() ) &&
                                !isFilteredOut( ( (Actor) obj ).getName() );
                    }
                } ),
                new Transformer() {
                    public Object transform( Object obj ) {
                        // Entity names are unique
                        return new IndexEntry( ( (Actor) obj ).getNormalizedName(), (Actor) obj );
                    }
                }
        );
    }

    @SuppressWarnings( "unchecked" )
    private List<IndexEntry> getIndicesForAllEntities( Class entityClass ) {
        return (List<IndexEntry>) CollectionUtils.collect(
                CollectionUtils.select( getQueryService().listReferenced( entityClass ), new Predicate() {
                    public boolean evaluate( Object obj ) {
                        return isNameIncluded( ( (ModelEntity) obj ).getName() );
                    }
                } ),
                new Transformer() {
                    public Object transform( Object obj ) {
                        // Entity names are unique
                        return new IndexEntry( ( (ModelObject) obj ).getName(), (ModelObject) obj );
                    }
                }
        );
    }

    private List<IndexEntry> getIndicesForAllTasks() {
        Map<String, IndexEntry> entries = new HashMap<String, IndexEntry>();
        for ( Scenario scenario : getQueryService().list( Scenario.class ) ) {
            Iterator<Part> parts = scenario.parts();
            while ( parts.hasNext() ) {
                Part part = parts.next();
                String task = part.getTask().toLowerCase();
                if ( isNameIncluded( task ) ) {
                    IndexEntry entry = entries.get( task );
                    if ( entry == null ) {
                        entry = new IndexEntry( task, part );
                        entries.put( task, entry );
                    } else {
                        entry.addNameHolder( part );
                    }
                }
            }
        }
        return new ArrayList<IndexEntry>( entries.values() );
    }

    private List<IndexEntry> getIndicesForAllFlows() {
        Map<String, IndexEntry> entries = new HashMap<String, IndexEntry>();
        for ( Scenario scenario : getQueryService().list( Scenario.class ) ) {
            Iterator<Flow> flows = scenario.flows();
            while ( flows.hasNext() ) {
                Flow flow = flows.next();
                String info = flow.getName().toLowerCase();
                if ( isNameIncluded( info ) ) {
                    IndexEntry entry = entries.get( info );
                    if ( entry == null ) {
                        entry = new IndexEntry( info, flow );
                        entries.put( info, entry );
                    } else {
                        entry.addNameHolder( flow );
                    }
                }
            }
        }
        return new ArrayList<IndexEntry>( entries.values() );
    }

    private boolean isNameIncluded( String name ) {
        return nameRange.contains( name ) && !isFilteredOut( name );
    }

    private boolean isFilteredOut( String name ) {
        return !filter.isEmpty() && name.toLowerCase().indexOf( filter ) < 0;
    }


    /**
     * An index entry.
     */
    public class IndexEntry implements Serializable, Comparable {
        /**
         * Name.
         */
        private String name;
        /**
         * Model object of this name.
         */
        private List<ModelObject> nameHolders;

        public IndexEntry( String name ) {
            this.name = name;
            nameHolders = new ArrayList<ModelObject>();
        }

        public IndexEntry( String name, ModelObject mo ) {
            this( name );
            addNameHolder( mo );
        }

        /**
         * Add model object holding name.
         *
         * @param mo a model object
         */
        public void addNameHolder( ModelObject mo ) {
            nameHolders.add( mo );
        }

        public String getName() {
            return name;
        }

        public List<ModelObject> getNameHolders() {
            return nameHolders;
        }

        /**
         * {@inheritDoc}
         */
        public int compareTo( Object obj ) {
            return collator.compare( name, ( (IndexEntry) obj ).getName() );
        }

        public boolean isRepeated() {
            return nameHolders.size() > 1;
        }
    }

    /**
     * Abstract class for index entry panels.
     */
    public abstract class IndexEntryPanel extends Panel {
        /**
         * Index entry model.
         */
        private IModel<IndexEntry> indexEntryModel;

        protected IndexEntryPanel( String id, IModel<IndexEntry> model ) {
            super( id, model );
            indexEntryModel = model;
        }

        protected boolean isNameAbbreviated() {
            return getFullName().length() > MAX_NAME_LENGTH;
        }

        /**
         * Get indexed model object.
         *
         * @return a model object
         */
        public ModelObject getIndexedModelObject() {
            return indexEntryModel.getObject().getNameHolders().get( 0 );
        }

        /**
         * Get full name of index entry.
         *
         * @return a string
         */
        public String getFullName() {
            return getIndexEntry().getName();
        }

        /**
         * Get abbreviated name of index entry.
         *
         * @return a string
         */
        public String getAbbreviatedName() {
            return StringUtils.abbreviate( getFullName(), MAX_NAME_LENGTH );
        }

        /**
         * Get index entry.
         *
         * @return an index entry
         */
        protected IndexEntry getIndexEntry() {
            return indexEntryModel.getObject();
        }

        /**
         * Get tooltip of possibly abbreviated index entry.
         *
         * @return a string
         */
        protected String getTitle() {
            ModelObject mo = getIndexedModelObject();
            String kind = ( mo instanceof Part
                    ? "Task"
                    : ( mo instanceof Flow )
                    ? "Flow"
                    : mo.getClass().getSimpleName()
            );
            return kind + ( isNameAbbreviated() ? ": " + getFullName() : "" );
        }

        /**
         * Get the kind of model oebjct.
         *
         * @return a string
         */
        protected String getKind() {
            ModelObject mo = getIndexedModelObject();
            return ( mo instanceof Part
                    ? "Task"
                    : ( mo instanceof Flow )
                    ? "Flow"
                    : mo.getClass().getSimpleName()
            );
        }
    }

    /**
     * A panel showing a unique index entry.
     */
    public class SingleIndexEntryPanel extends IndexEntryPanel {


        public SingleIndexEntryPanel( String id, IModel<IndexEntry> model ) {
            super( id, model );
            initialize();
        }

        private void initialize() {
            ModelObjectLink moLink = new ModelObjectLink(
                    "moLink",
                    new Model<ModelObject>( getIndexedModelObject() ),
                    new Model<String>( getAbbreviatedName() ),
                    getTitle() );
            add( moLink );
        }

    }

    /**
     * A planel showing a repeated index entry.
     */
    public class RepeatedIndexEntryPanel extends IndexEntryPanel {

        public RepeatedIndexEntryPanel( String id, IModel<IndexEntry> model ) {
            super( id, model );
            initialize();
        }

        private void initialize() {
            Label moLabel = new Label( "moLabel", new PropertyModel<String>( this, "abbreviatedName" ) );
            if ( isNameAbbreviated() ) {
                moLabel.add( new AttributeModifier( "title", true, new Model<String>( getFullName() ) ) );
            }
            add( moLabel );
            ListView refList = new ListView<ModelObject>(
                    "references",
                    new PropertyModel<List<ModelObject>>( this, "references" ) ) {
                protected void populateItem( ListItem<ModelObject> item ) {
                    ModelObjectLink moLink = new ModelObjectLink(
                            "moLink",
                            new Model<ModelObject>( getIndexedModelObject() ),
                            new Model<String>( getRank( item.getModelObject() ) ),
                            getKind() );
                    item.add( moLink );
                }
            };
            add( refList );
        }

        /**
         * Get model objects sharing a name.
         *
         * @return a list of model objects
         */
        public List<ModelObject> getReferences() {
            return getIndexEntry().getNameHolders();
        }

        /**
         * Get ordinal ranking of a name holding model object.
         *
         * @param mo a model object
         * @return a string
         */
        public String getRank( ModelObject mo ) {
            return "" + getReferences().indexOf( mo );
        }
    }

}
