package com.mindalliance.channels.pages.components;

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
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.model.TransmissionMedium;
import com.mindalliance.channels.pages.ModelObjectLink;
import com.mindalliance.channels.util.NameRange;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Oct 15, 2009
 * Time: 2:31:41 PM
 */
public abstract class AbstractIndexPanel extends AbstractCommandablePanel implements NameRangeable {

    /**
     * Indexing choice.
     */
    protected static final String ALL = "All";
    /**
     * Indexing choice.
     */
    protected static final String ACTORS = "Agents";
    /**
     * Indexing choice.
     */
    protected static final String ROLES = "Roles";
    /**
     * Indexing choice.
     */
    protected static final String ORGANIZATIONS = "Organizations";
    /**
     * Indexing choice.
     */
    protected static final String PLACES = "Places";
    /**
     * Indexing choice.
     */
    protected static final String EVENTS = "Events";
    /**
     * Indexing choice.
     */
    protected static final String TASKS = "Tasks";
    /**
     * Indexing choice.
     */
    protected static final String FLOWS = "Flows";
    /**
     * Indexing choice.
     */
    protected static final String PHASES = "Phases";
    /**
     * Indexing choice.
     */
    protected static final String SEGMENTS = "Segments";
    /**
     * Indexing choice.
     */
    protected static final String MEDIA = "Media";
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
     * What kind of model objects to index on.
     */
    private String indexedOn = ALL;
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

    public AbstractIndexPanel( String id, IModel<? extends Identifiable> model, Set<Long> expansions ) {
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
                getIndexingChoices() );
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
        if ( indexedOn.equals( ALL ) ) {
            names = getAllNames();
        } else if ( indexedOn.equals( ACTORS ) ) {
            names = indexNamesFor( findIndexedActors() );
        } else if ( indexedOn.equals( ROLES ) ) {
            names = indexNamesFor( findIndexedRoles() );
        } else if ( indexedOn.equals( ORGANIZATIONS ) ) {
            names = indexNamesFor( findIndexedOrganizations() );
        } else if ( indexedOn.equals( PLACES ) ) {
            names = indexNamesFor( findIndexedPlaces() );
        } else if ( indexedOn.equals( EVENTS ) ) {
            names = indexNamesFor( findIndexedEvents() );
        } else if ( indexedOn.equals( PHASES ) ) {
            names = indexNamesFor( findIndexedPhases() );
        } else if ( indexedOn.equals( MEDIA ) ) {
            names = indexNamesFor( findIndexedMedia() );
        }  else if ( indexedOn.equals( TASKS ) ) {
            names = indexNamesFor( findIndexedParts() );
        } else if ( indexedOn.equals( FLOWS ) ) {
            names = indexNamesFor( findIndexedFlows() );
        } else if ( indexedOn.equals( SEGMENTS ) ) {
            names = indexNamesFor( findIndexedSegments() );
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
        Set<String> names = new HashSet<String>();
        names.addAll( indexNamesFor( findIndexedActors() ) );
        names.addAll( indexNamesFor( findIndexedEvents() ) );
        names.addAll( indexNamesFor( findIndexedOrganizations() ) );
        names.addAll( indexNamesFor( findIndexedPhases() ) );
        names.addAll( indexNamesFor( findIndexedMedia() ) );
        names.addAll( indexNamesFor( findIndexedPlaces() ) );
        names.addAll( indexNamesFor( findIndexedRoles() ) );
        names.addAll( indexNamesFor( findIndexedFlows() ) );
        names.addAll( indexNamesFor( findIndexedParts() ) );
        names.addAll( indexNamesFor( findIndexedSegments() ) );
        return new ArrayList<String>( names );
    }

    @SuppressWarnings( "unchecked" )
    private List<String> indexNamesFor( List<? extends ModelObject> modelObjects ) {
        return (List<String>) CollectionUtils.collect(
                modelObjects,
                new Transformer() {
                    public Object transform( Object input ) {
                        if ( input instanceof Actor && ( (Actor) input ).isActual() ) {
                            return ( (Actor) input ).getLastName();
                        } else if ( input instanceof Part ) {
                            return ( (Part) input ).getTask().toLowerCase();
                        } else if ( input instanceof Flow ) {
                            return ( (ModelObject) input ).getName().toLowerCase();
                        } else {
                            return ( (ModelObject) input ).getName();
                        }
                    }
                }
        );
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
                indices = indicesFor( findIndexedActors() );
            } else if ( indexedOn.equals( ROLES ) ) {
                indices = indicesFor( findIndexedRoles() );
            } else if ( indexedOn.equals( ORGANIZATIONS ) ) {
                indices = indicesFor( findIndexedOrganizations() );
            } else if ( indexedOn.equals( PLACES ) ) {
                indices = indicesFor( findIndexedPlaces() );
            } else if ( indexedOn.equals( EVENTS ) ) {
                indices = indicesFor( findIndexedEvents() );
            } else if ( indexedOn.equals( PHASES ) ) {
                indices = indicesFor( findIndexedPhases() );
            }  else if ( indexedOn.equals( MEDIA ) ) {
                indices = indicesFor( findIndexedMedia() );
            } else if ( indexedOn.equals( TASKS ) ) {
                indices = indicesFor( findIndexedParts() );
            } else if ( indexedOn.equals( FLOWS ) ) {
                indices = indicesFor( findIndexedFlows() );
            } else if ( indexedOn.equals( SEGMENTS ) ) {
                indices = indicesFor( findIndexedSegments() );
            } else {
                throw new IllegalStateException( "Can't index on " + indexedOn );
            }
        }
        Collections.sort( indices );
        return indices;
    }

    private List<IndexEntry> getIndicesForAllNames() {
        List<IndexEntry> indexEntries = new ArrayList<IndexEntry>();
        indexEntries.addAll( indicesFor( findIndexedActors() ) );
        indexEntries.addAll( indicesFor( findIndexedRoles() ) );
        indexEntries.addAll( indicesFor( findIndexedOrganizations() ) );
        indexEntries.addAll( indicesFor( findIndexedPlaces() ) );
        indexEntries.addAll( indicesFor( findIndexedEvents() ) );
        indexEntries.addAll( indicesFor( findIndexedPhases() ) );
        indexEntries.addAll( indicesFor( findIndexedMedia() ) );
        indexEntries.addAll( indicesFor( findIndexedParts() ) );
        indexEntries.addAll( indicesFor( findIndexedFlows() ) );
        indexEntries.addAll( indicesFor( findIndexedSegments() ) );
        return indexEntries;
    }

    private String rangeName( ModelObject mo ) {
        if ( mo instanceof Actor && ( (Actor) mo ).isActual() ) {
            return ( (Actor) mo ).getLastName();
        } else if ( mo instanceof Part ) {
            return ( (Part) mo ).getTask().toLowerCase();
        } else if ( mo instanceof Flow ) {
            return mo.getName().toLowerCase();
        } else {
            return mo.getName();
        }
    }

    private String indexName( ModelObject mo ) {
        if ( mo instanceof Actor && ( (Actor) mo ).isActual() ) {
            return ( (Actor) mo ).getNormalizedName();
        } else if ( mo instanceof Part ) {
            return ( (Part) mo ).getTask().toLowerCase();
        } else if ( mo instanceof Flow ) {
            return mo.getName().toLowerCase();
        } else {
            return mo.getName();
        }

    }

    @SuppressWarnings( "unchecked" )
    private List<IndexEntry> indicesFor( List<? extends ModelObject> modelObjects ) {
        Map<String, IndexEntry> entries = new HashMap<String, IndexEntry>();
        for ( ModelObject mo : modelObjects ) {
            boolean included = nameRange.contains( rangeName( mo ) ) &&
                    !isFilteredOut( indexName( mo ) );
            if ( included ) {
                String indexName = indexName( mo );
                IndexEntry entry = entries.get( indexName );
                if ( entry == null ) {
                    entry = new IndexEntry( indexName, mo );
                    entries.put( indexName, entry );
                } else {
                    entry.addNameHolder( mo );
                }
            }
        }
        return new ArrayList<IndexEntry>( entries.values() );
    }

    protected boolean isNameIncluded( String name ) {
        return nameRange.contains( name ) && !isFilteredOut( name );
    }

    private boolean isFilteredOut( String name ) {
        return !filter.isEmpty() && name.toLowerCase().indexOf( filter ) < 0;
    }

    private void italicizeIfEntityType( Component component, ModelObject mo ) {
        if ( mo.isEntity() && ( (ModelEntity) mo ).isType() ) {
            component.add(
                    new AttributeModifier(
                            "style",
                            true,
                            new Model<String>( "font-style: oblique" ) ) );
        }
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
                    : ( mo instanceof Segment )
                    ? "Plan segment"
                    : ( !mo.isEntity() )
                    ? mo.getClass().getSimpleName()
                    : ( mo instanceof Actor && ( (Actor) mo ).isActual() )
                    ? ( ( (Actor) mo ).isPerson() ? "Person" : "System" )
                    : ( (ModelEntity) mo ).isActual()
                    ? "Actual " + mo.getClass().getSimpleName().toLowerCase()
                    : "Type of " + mo.getClass().getSimpleName().toLowerCase()
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
            italicizeIfEntityType( moLink, getIndexedModelObject() );
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
            italicizeIfEntityType( moLabel, getIndexedModelObject() );
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

    /**
     * Get all kinds of model objects to index on.
     *
     * @return a list of strings
     */
    protected List<String> getIndexingChoices() {
        List<String> choices = new ArrayList<String>();
        choices.add( ALL );
        if ( !findIndexedActors().isEmpty() ) choices.add( ACTORS );
        if ( !findIndexedEvents().isEmpty() ) choices.add( EVENTS );
        if ( !findIndexedPhases().isEmpty() ) choices.add( PHASES );
        if ( !findIndexedMedia().isEmpty() ) choices.add( MEDIA );
        if ( !findIndexedPlaces().isEmpty() ) choices.add( PLACES );
        if ( !findIndexedOrganizations().isEmpty() ) choices.add( ORGANIZATIONS );
        if ( !findIndexedRoles().isEmpty() ) choices.add( ROLES );
        if ( !findIndexedParts().isEmpty() ) choices.add( TASKS );
        if ( !findIndexedSegments().isEmpty() ) choices.add( SEGMENTS );
        return choices;
    }

    /**
     * Find all actors to index.
     *
     * @return a list of actors
     */
    abstract protected List<Actor> findIndexedActors();

    /**
     * Find all events to index.
     *
     * @return a list of events
     */
    abstract protected List<Event> findIndexedEvents();

    /**
     * Find all organizations to index.
     *
     * @return a list of organizations
     */
    abstract protected List<Organization> findIndexedOrganizations();

    /**
     * Find all phases to index.
     *
     * @return a list of phases
     */
    abstract protected List<Phase> findIndexedPhases();

    /**
     * Find all phases to index.
     *
     * @return a list of transmission media
     */
    abstract protected List<TransmissionMedium> findIndexedMedia();

    /**
     * Find all places to index.
     *
     * @return a list of places
     */
    abstract protected List<Place> findIndexedPlaces();

    /**
     * Find all roles to index.
     *
     * @return a list of roles
     */
    abstract protected List<Role> findIndexedRoles();

    /**
     * Find all flows to index.
     *
     * @return a list of flows
     */
    abstract protected List<Flow> findIndexedFlows();

    /**
     * Find all parts to index.
     *
     * @return a list of parts
     */
    abstract protected List<Part> findIndexedParts();

    /**
     * Find all segments to index.
     *
     * @return a list of segments
     */
    abstract protected List<Segment> findIndexedSegments();


}
