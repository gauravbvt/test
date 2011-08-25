package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.ElementOfInformation;
import com.mindalliance.channels.model.Event;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Modelable;
import com.mindalliance.channels.model.Nameable;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Phase;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.model.Tag;
import com.mindalliance.channels.model.Taggable;
import com.mindalliance.channels.model.TransmissionMedium;
import com.mindalliance.channels.nlp.Matcher;
import com.mindalliance.channels.pages.ModelObjectLink;
import com.mindalliance.channels.query.QueryService;
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
import org.apache.wicket.markup.html.form.CheckBox;
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
import java.util.Iterator;
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
    protected static final String EOIS = "Elements of information";
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
     * Filter by name checkbox.
     */
    private CheckBox byNameCheckBox;
    /**
     * Filter by tags checkbox.
     */
    private CheckBox byTagsCheckBox;
    /**
     * Filter string input field.
     */
    private TextField<String> filterField;
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
     * Whether filtering is by name vs by tags.
     */
    private boolean filteredByName = true;
    /**
     * Name index panel.
     */
    private WebMarkupContainer indicesContainer;
    /**
     * Cached index entries.
     */
    private List<IndexEntry> indices;
    private String css;

    public AbstractIndexPanel( String id, IModel<? extends Identifiable> model ) {
        this( id, model, null, null );
    }

    public AbstractIndexPanel( String id, IModel<? extends Identifiable> model, Set<Long> expansions ) {
        this( id, model, null, expansions );
    }

    public AbstractIndexPanel( String id, IModel<? extends Identifiable> model, String css, Set<Long> expansions ) {
        super( id, model, expansions );
        this.css = css;
        init();
    }

    /**
     * There is no tag from s such that there is no tag in tag that matches it
     * (all tags from s match at least of this mo's tags).
     *
     * @param matcher the matcher to use
     * @param taggable the object
     * @param s a string
     * @return true if the object is tagged with given string
     */
    private static boolean isTaggedWith( Matcher matcher, Taggable taggable, String s ) {
        List<Tag> otherTags = Tag.tagsFromString( s );

        for ( Tag otherTag : otherTags )
            if ( !isTaggedWith( matcher, taggable, otherTag ) )
                return false;

        return true;
    }

    public static boolean isTaggedWith( Matcher matcher, Taggable taggable, Tag tag ) {
        for ( Tag myTag : taggable.getTags() )
            if ( matches( matcher, myTag, tag ) )
                return true;

        return false;
    }

    /**
     * Whether two tags match.
     *
     * @param matcher the matcher to use
     * @param tag a tag
     * @param other another tag
     * @return a boolean
     */
    private static boolean matches( Matcher matcher, Tag tag, Tag other ) {
        if ( tag.equals( other ) )
            return true;

        List<String> elements = tag.getElements();
        List<String> otherElements = other.getElements();
        Iterator<String> shorter =
                elements.size() < otherElements.size() ? elements.iterator() : otherElements.iterator();
        Iterator<String> longer =
                elements.size() >= otherElements.size() ? elements.iterator() : otherElements.iterator();

        boolean matching = true;
        while ( matching && shorter.hasNext() )
            matching = matcher.same( shorter.next(), longer.next() );
        return matching;
    }

    @Override
    public void redisplay( AjaxRequestTarget target ) {
        init();
        super.redisplay( target );
    }

    protected void init() {
        addIndexChoice();
        addByNameOrTags();
        addFilterField();
        addNameRangePanel();
        addIndices();
    }

    private void addIndexChoice() {
        DropDownChoice<String> indexedOnChoices = new DropDownChoice<String>(
                "indexed",
                new PropertyModel<String>( this, "indexedOn" ),
                getIndexingChoices() );
        indexedOnChoices.setOutputMarkupId( true );
        indexedOnChoices.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                indices = null;
                byTagsCheckBox.setEnabled( !indexedOn.equals( EOIS ) );
                if ( indexedOn.equals( EOIS ) )  {
                    setFilteredByName( true );
                }
                target.addComponent( byNameCheckBox );
                target.addComponent( byTagsCheckBox );
                nameRange = new NameRange();
                addNameRangePanel();
                addIndices();
                target.addComponent( nameRangePanel );
                target.addComponent( indicesContainer );
            }
        } );
        addOrReplace( indexedOnChoices );
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
        filterField = new TextField<String>(
                "filter",
                new PropertyModel<String>( this, "filter" )
        );
        filterField.setOutputMarkupId( true );
        filterField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                addNameRangePanel();
                target.addComponent( nameRangePanel );
                addIndices();
                target.addComponent( indicesContainer );
            }
        } );
        addOrReplace( filterField );
    }

    private void addByNameOrTags() {
        byNameCheckBox = new CheckBox( "name", new PropertyModel<Boolean>( this, "filteredByName" ) );
        byNameCheckBox.setOutputMarkupId( true );
        byNameCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addNameRangePanel();
                target.addComponent( nameRangePanel );
                addIndices();
                target.addComponent( indicesContainer );
                target.addComponent( filterField );
                target.addComponent( byTagsCheckBox );
            }
        } );
        addOrReplace( byNameCheckBox );
        byTagsCheckBox = new CheckBox( "tags", new PropertyModel<Boolean>( this, "filteredByTags" ) );
        byTagsCheckBox.setOutputMarkupId( true );
        byTagsCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addNameRangePanel();
                target.addComponent( nameRangePanel );
                addIndices();
                target.addComponent( indicesContainer );
                target.addComponent( filterField );
                target.addComponent( byNameCheckBox );
            }
        } );
        addOrReplace( byTagsCheckBox );
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
                        ? new RepeatedIndexEntryPanel( "indexEntry", new Model<IndexEntry>( indexEntry ), css )
                        : new SingleIndexEntryPanel( "indexEntry", new Model<IndexEntry>( indexEntry ), css ) );
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
                        ? new RepeatedIndexEntryPanel( "indexEntry", new Model<IndexEntry>( indexEntry ), css )
                        : new SingleIndexEntryPanel( "indexEntry", new Model<IndexEntry>( indexEntry ), css ) );
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
                        ? new RepeatedIndexEntryPanel( "indexEntry", new Model<IndexEntry>( indexEntry ), css )
                        : new SingleIndexEntryPanel( "indexEntry", new Model<IndexEntry>( indexEntry ), css ) );
            }
        };
        indicesContainer.addOrReplace( indices3 );
    }

    public boolean isFilteredByName() {
        return filteredByName;
    }

    public void setFilteredByName( boolean val ) {
        filteredByName = val;
        filter = "";
        indices = null;
    }

    public boolean isFilteredByTags() {
        return !isFilteredByName();
    }

    public void setFilteredByTags( boolean val ) {
        filteredByName = !val;
        filter = "";
        indices = null;
    }

    public List<String> getIndexedNames() {
        if ( isFilteredByName() )
            return getIndexedNamesFilteredByName();
        else {
            assert isFilteredByTags();
            return getIndexedNamesFilteredByTags();
        }
    }

    @SuppressWarnings( "unchecked" )
    private List<String> getIndexedNamesFilteredByTags() {
        List<? extends Taggable> taggables;
        if ( indexedOn.equals( ALL ) ) {
            taggables = getAllTaggables();
        } else if ( indexedOn.equals( ACTORS ) ) {
            taggables = findIndexedActors();
        } else if ( indexedOn.equals( ROLES ) ) {
            taggables = findIndexedRoles();
        } else if ( indexedOn.equals( ORGANIZATIONS ) ) {
            taggables = findIndexedOrganizations();
        } else if ( indexedOn.equals( PLACES ) ) {
            taggables = findIndexedPlaces();
        } else if ( indexedOn.equals( EVENTS ) ) {
            taggables = findIndexedEvents();
        } else if ( indexedOn.equals( PHASES ) ) {
            taggables = findIndexedPhases();
        } else if ( indexedOn.equals( MEDIA ) ) {
            taggables = findIndexedMedia();
        } else if ( indexedOn.equals( TASKS ) ) {
            taggables = findIndexedParts();
        } else if ( indexedOn.equals( FLOWS ) ) {
            taggables = findIndexedFlows();
        } else if ( indexedOn.equals( EOIS ) ) {
            taggables = new ArrayList<Taggable>();
        } else if ( indexedOn.equals( SEGMENTS ) ) {
            taggables = findIndexedSegments();
        } else {
            throw new IllegalStateException( "Can't index on " + indexedOn );
        }
        return (List<String>) CollectionUtils.collect(
                CollectionUtils.select( taggables,
                        new Predicate() {
                            public boolean evaluate( Object obj ) {
                                return !isFilteredOutByTags( (Taggable) obj );
                            }
                        } ),
                new Transformer() {
                    public Object transform( Object obj ) {
                        return ( indexName( (Taggable) obj ) ).toLowerCase();
                    }
                } );

    }

    /**
     * Get all distinct, filtered names to be indexed, as lowercase.
     *
     * @return a list of strings
     */
    @SuppressWarnings( "unchecked" )
    private List<String> getIndexedNamesFilteredByName() {
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
        } else if ( indexedOn.equals( TASKS ) ) {
            names = indexNamesFor( findIndexedParts() );
        } else if ( indexedOn.equals( FLOWS ) ) {
            names = indexNamesFor( findIndexedFlows() );
        } else if ( indexedOn.equals( EOIS ) ) {
            names = indexNamesFor( findIndexedEOIs() );
        } else if ( indexedOn.equals( SEGMENTS ) ) {
            names = indexNamesFor( findIndexedSegments() );
        } else {
            throw new IllegalStateException( "Can't index on " + indexedOn );
        }
        return (List<String>) CollectionUtils.collect(
                CollectionUtils.select( names,
                        new Predicate() {
                            public boolean evaluate( Object obj ) {
                                return !isFilteredOutByName( (String) obj );
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
        names.addAll( indexNamesFor( findIndexedEOIs() ) );
        names.addAll( indexNamesFor( findIndexedParts() ) );
        names.addAll( indexNamesFor( findIndexedSegments() ) );
        return new ArrayList<String>( names );
    }


    private List<? extends Taggable> getAllTaggables() {
        List<Taggable> taggables = new ArrayList<Taggable>();
        taggables.addAll( findIndexedActors() );
        taggables.addAll( findIndexedEvents() );
        taggables.addAll( findIndexedOrganizations() );
        taggables.addAll( findIndexedPhases() );
        taggables.addAll( findIndexedMedia() );
        taggables.addAll( findIndexedPlaces() );
        taggables.addAll( findIndexedRoles() );
        taggables.addAll( findIndexedFlows() );
        taggables.addAll( findIndexedParts() );
        taggables.addAll( findIndexedSegments() );
        return taggables;
    }

    @SuppressWarnings( "unchecked" )
    private List<String> indexNamesFor( List<? extends Nameable> nameables ) {
        return (List<String>) CollectionUtils.collect(
                nameables,
                new Transformer() {
                    public Object transform( Object input ) {
                        if ( input instanceof Actor && ( (Actor) input ).isActual() ) {
                            return ( (Actor) input ).getLastName();
                        } else if ( input instanceof Part ) {
                            return ( (Part) input ).getTask().toLowerCase();
                        } else if ( input instanceof Flow ) {
                            return ( (ModelObject) input ).getName().toLowerCase();
                        } else {
                            return ( (Nameable) input ).getName();
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
        List<IndexEntry> allIndices = getFilteredIndices();
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
        List<IndexEntry> allIndices = getFilteredIndices();
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
        List<IndexEntry> allIndices = getFilteredIndices();
        int fromIndex = getRowCounts()[0] + getRowCounts()[1];
        int toIndex = allIndices.size();
        return ( toIndex > 0 )
                ? allIndices.subList( fromIndex, toIndex )
                : new ArrayList<IndexEntry>();
    }

    private int[] getRowCounts() {
        int count = getFilteredIndices().size();
        int split = count / 3;
        int[] rowCounts = new int[]{split, split, split};
        count = count % 3;
        if ( count-- > 0 ) rowCounts[0]++;
        if ( count > 0 ) rowCounts[1]++;
        return rowCounts;
    }

    private List<IndexEntry> getFilteredIndices() {
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
            } else if ( indexedOn.equals( MEDIA ) ) {
                indices = indicesFor( findIndexedMedia() );
            } else if ( indexedOn.equals( TASKS ) ) {
                indices = indicesFor( findIndexedParts() );
            } else if ( indexedOn.equals( FLOWS ) ) {
                indices = indicesFor( findIndexedFlows() );
            } else if ( indexedOn.equals( EOIS ) ) {
                indices = isFilteredByName()
                        ? indicesFor( findIndexedEOIs() )
                        : indicesFor( new ArrayList<Modelable>() );
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
        if ( isFilteredByName() ) indexEntries.addAll( indicesFor( findIndexedEOIs() ) );
        indexEntries.addAll( indicesFor( findIndexedSegments() ) );
        return indexEntries;
    }

    private String rangeName( Nameable nameable ) {
        if ( nameable instanceof Actor && ( (Actor) nameable ).isActual() ) {
            return ( (Actor) nameable ).getLastName();
        } else if ( nameable instanceof Part ) {
            return ( (Part) nameable ).getTask().toLowerCase();
        } else if ( nameable instanceof Flow ) {
            return nameable.getName().toLowerCase();
        } else {
            return nameable.getName();
        }
    }

    private String indexName( Nameable nameable ) {
        if ( nameable instanceof Actor && ( (Actor) nameable ).isActual() ) {
            return ( (Actor) nameable ).getNormalizedName();
        } else if ( nameable instanceof Part ) {
            return ( (Part) nameable ).getTask().toLowerCase();
        } else if ( nameable instanceof Flow ) {
            return nameable.getName().toLowerCase();
        } else {
            return nameable.getName();
        }

    }

    @SuppressWarnings( "unchecked" )
    private List<IndexEntry> indicesFor( List<? extends Modelable> modelables ) {
        Map<String, IndexEntry> entries = new HashMap<String, IndexEntry>();
        for ( Modelable modelable : modelables ) {
            boolean included = nameRange.contains( rangeName( modelable ) ) &&
                    !isFilteredOut( modelable );
            if ( included ) {
                String indexName = indexName( modelable );
                IndexEntry entry = entries.get( indexName );
                if ( entry == null ) {
                    entry = new IndexEntry( indexName, modelable.getModelObject() );
                    entries.put( indexName, entry );
                } else {
                    entry.addNameHolder( modelable.getModelObject() );
                }
            }
        }
        return new ArrayList<IndexEntry>( entries.values() );
    }

    private boolean isFilteredOut( Modelable modelable ) {
        if ( isFilteredByName() ) {
            return isFilteredOutByName( indexName( modelable ) );
        } else {
            assert isFilteredByTags();
            return isFilteredOutByTags( modelable.getModelObject() );
        }
    }

    private boolean isFilteredOutByName( String name ) {
        return !filter.isEmpty() && name.toLowerCase().indexOf( filter ) < 0;
    }

    private boolean isFilteredOutByTags( Taggable taggable ) {
        return !filter.isEmpty()
                && !isTaggedWith( Matcher.getInstance(), taggable, filter );
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

    protected String addToCss( ModelObject modelObject, String css ) {
        String added = "";
        if ( modelObject instanceof Part ) {
            QueryService queryService = getQueryService();
            boolean overridden = queryService.isOverridden( (Part)modelObject );
            boolean overriding = queryService.isOverriding( (Part)modelObject );
            added = overriding && overridden
                            ? "overridden-overriding"
                            : overriding
                            ? "overriding"
                            : overridden
                            ? "overridden"
                            : "";
        } else if ( modelObject instanceof Flow ) {
            QueryService queryService = getQueryService();
            boolean overridden = queryService.isOverridden( (Flow)modelObject );
            boolean overriding = queryService.isOverriding( (Flow)modelObject );
            added = overriding && overridden
                            ? "overridden-overriding"
                            : overriding
                            ? "overriding"
                            : overridden
                            ? "overridden"
                            : "";
        }
        return css + (added.isEmpty() ? "" : (" " + added));
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
        protected String getToolTip() {
            ModelObject mo = getIndexedModelObject();
            String kind = ( mo instanceof Part
                    ? "Task"
                    : ( mo instanceof Flow )
                    ? ( "Flow \"" + mo.getName() + "\"" )
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
            return kind + ( isNameAbbreviated() ? ": " + getFullName() : "" ) + " [" + mo.getId() + "]";
        }

        /**
         * Get the kind of model oebjct.
         *
         * @param mo model object
         * @return a string
         */
        protected String getToolTip( ModelObject mo ) {
            return mo.getTypeName() + " [" + mo.getId() + "]";
        }
    }

    /**
     * A panel showing a unique index entry.
     */
    public class SingleIndexEntryPanel extends IndexEntryPanel {

        private String css;


        public SingleIndexEntryPanel( String id, IModel<IndexEntry> model ) {
            this( id, model, null );
        }

        public SingleIndexEntryPanel( String id, IModel<IndexEntry> model, String css ) {
            super( id, model );
            this.css = css;
            initialize();
        }

        private void initialize() {
            ModelObjectLink moLink = new ModelObjectLink(
                    "moLink",
                    new Model<ModelObject>( getIndexedModelObject() ),
                    new Model<String>( getAbbreviatedName() ),
                    getToolTip(),
                    addToCss( getIndexedModelObject(), css  ) );
            italicizeIfEntityType( moLink, getIndexedModelObject() );
            add( moLink );
        }

    }


    /**
     * A planel showing a repeated index entry.
     */
    public class RepeatedIndexEntryPanel extends IndexEntryPanel {

        private String css;


        public RepeatedIndexEntryPanel( String id, IModel<IndexEntry> model ) {
            this( id, model, null );
        }

        public RepeatedIndexEntryPanel( String id, IModel<IndexEntry> model, String css ) {
            super( id, model );
            this.css = css;
            initialize();
        }

        private void initialize() {
            Label moLabel = new Label( "moLabel", new PropertyModel<String>( this, "abbreviatedName" ) );
            if ( isNameAbbreviated() ) {
                moLabel.add( new AttributeModifier(
                        "title",
                        true,
                        new Model<String>( getFullName()  ) ) );
            }
            add( moLabel );
            ListView refList = new ListView<ModelObject>(
                    "references",
                    new PropertyModel<List<ModelObject>>( this, "references" ) ) {
                protected void populateItem( ListItem<ModelObject> item ) {
                    ModelObjectLink moLink = new ModelObjectLink(
                            "moLink",
                            new Model<ModelObject>( item.getModelObject() ),
                            new Model<String>( getRank( item.getModelObject() ) ),
                            getToolTip( item.getModelObject() ),
                            addToCss( item.getModelObject(), css ) );
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
        if ( !findIndexedFlows().isEmpty() ) choices.add( FLOWS );
        if ( !findIndexedEOIs().isEmpty() ) choices.add( EOIS );
        if ( !findIndexedSegments().isEmpty() ) choices.add( SEGMENTS );
        return choices;
    }

    /**
     * Find all actors to index.
     *
     * @return a list of actors
     */
    protected List<Actor> findIndexedActors() {
        return new ArrayList<Actor>();
    }

    /**
     * Find all events to index.
     *
     * @return a list of events
     */
    protected List<Event> findIndexedEvents() {
        return new ArrayList<Event>();
    }

    /**
     * Find all organizations to index.
     *
     * @return a list of organizations
     */
    protected List<Organization> findIndexedOrganizations() {
        return new ArrayList<Organization>();
    }

    /**
     * Find all phases to index.
     *
     * @return a list of phases
     */
    protected List<Phase> findIndexedPhases() {
        return new ArrayList<Phase>();
    }

    /**
     * Find all phases to index.
     *
     * @return a list of transmission media
     */
    protected List<TransmissionMedium> findIndexedMedia() {
        return new ArrayList<TransmissionMedium>();
    }

    /**
     * Find all places to index.
     *
     * @return a list of places
     */
    protected List<Place> findIndexedPlaces() {
        return new ArrayList<Place>();
    }

    /**
     * Find all roles to index.
     *
     * @return a list of roles
     */
    protected List<Role> findIndexedRoles() {
        return new ArrayList<Role>();
    }

    /**
     * Find all flows to index.
     *
     * @return a list of flows
     */
    protected List<Flow> findIndexedFlows() {
        return new ArrayList<Flow>();
    }

    /**
     * Find all Subjects to index.
     *
     * @return a list of Elements of Information
     */
    protected List<ElementOfInformationInFlow> findIndexedEOIs() {
        return new ArrayList<ElementOfInformationInFlow>();
    }

    /**
     * Find all parts to index.
     *
     * @return a list of parts
     */
    protected List<Part> findIndexedParts() {
        return new ArrayList<Part>();
    }

    /**
     * Find all segments to index.
     *
     * @return a list of segments
     */
    protected List<Segment> findIndexedSegments() {
        return new ArrayList<Segment>();
    }

    /**
     * An element of information in a flow.
     */
    public class ElementOfInformationInFlow implements Nameable, Modelable {

        private Flow flow;
        private ElementOfInformation eoi;

        public ElementOfInformationInFlow( Flow flow, ElementOfInformation eoi ) {
            this.flow = flow;
            this.eoi = eoi;
        }

        public String getName() {
            return eoi.getContent().toLowerCase();
        }

        /**
         * {@inheritDoc}
         */
        public ModelObject getModelObject() {
            return flow;
        }
    }
}
