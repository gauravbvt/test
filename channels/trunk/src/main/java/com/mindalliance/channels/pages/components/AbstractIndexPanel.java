package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.core.Matcher;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.ElementOfInformation;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Function;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.InfoFormat;
import com.mindalliance.channels.core.model.InfoProduct;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Modelable;
import com.mindalliance.channels.core.model.Nameable;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Requirement;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.model.Tag;
import com.mindalliance.channels.core.model.Taggable;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.core.model.asset.MaterialAsset;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.core.util.NameRange;
import com.mindalliance.channels.pages.ModelObjectLink;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
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
     * Indexing choice.
     */
    protected static final String REQUIREMENTS = "Requirements";
    /**
     * Indexing choice.
     */
    protected static final String INFO_PRODUCTS = "Info products";
    /**
     * Indexing choice.
     */
    protected static final String INFO_FORMATS = "Info formats";
    /**
     * Indexing choice.
     */
    protected static final String FUNCTIONS = "Functions";
    /**
     * Indexing choice.
     */
    protected static final String ASSETS = "Assets";
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
    /**
     * Whether index entries must be referenced to appear.
     */
    private boolean mustBeReferenced = true;

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
     * @param taggable the object
     * @param s        a string
     * @return true if the object is tagged with given string
     */
    private static boolean isTaggedWith( Taggable taggable, String s ) {
        List<Tag> otherTags = Tag.tagsFromString( s );

        for ( Tag otherTag : otherTags )
            if ( !isTaggedWith( taggable, otherTag ) )
                return false;

        return true;
    }

    public static boolean isTaggedWith( Taggable taggable, Tag tag ) {
        for ( Tag myTag : taggable.getTags() )
            if ( matches( myTag, tag ) )
                return true;

        return false;
    }

    /**
     * Whether two tags match.
     *
     * @param tag   a tag
     * @param other another tag
     * @return a boolean
     */
    private static boolean matches( Tag tag, Tag other ) {
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
            matching = Matcher.same( shorter.next(), longer.next() );
        return matching;
    }

    @Override
    public void redisplay( AjaxRequestTarget target ) {
        indices = null;
        init();
        super.redisplay( target );
    }

    protected void init() {
        addIndexChoice();
        addByNameOrTags();
        addFilterField();
        addMustBeReferenced();
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
                if ( indexedOn.equals( EOIS ) ) {
                    setFilteredByName( true );
                }
                target.add( byNameCheckBox );
                target.add( byTagsCheckBox );
                nameRange = new NameRange();
                addNameRangePanel();
                addIndices();
                target.add( nameRangePanel );
                target.add( indicesContainer );
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
                target.add( nameRangePanel );
                addIndices();
                target.add( indicesContainer );
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
                target.add( nameRangePanel );
                addIndices();
                target.add( indicesContainer );
                target.add( filterField );
                target.add( byTagsCheckBox );
            }
        } );
        addOrReplace( byNameCheckBox );
        byTagsCheckBox = new CheckBox( "tags", new PropertyModel<Boolean>( this, "filteredByTags" ) );
        byTagsCheckBox.setOutputMarkupId( true );
        byTagsCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addNameRangePanel();
                target.add( nameRangePanel );
                addIndices();
                target.add( indicesContainer );
                target.add( filterField );
                target.add( byNameCheckBox );
            }
        } );
        addOrReplace( byTagsCheckBox );
    }

    private void addMustBeReferenced() {
        AjaxCheckBox referencedCheckbox = new AjaxCheckBox(
                "referenced",
                new PropertyModel<Boolean>( this, "mustBeReferenced" )
        ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                indices = null;
                addIndices();
                target.add( indicesContainer );
            }
        };
        referencedCheckbox.setOutputMarkupId( true );
        addOrReplace( referencedCheckbox );
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

    @SuppressWarnings("unchecked")
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
        } else if ( indexedOn.equals( REQUIREMENTS ) ) {
            taggables = findIndexedRequirements();
        } else if ( indexedOn.equals( INFO_PRODUCTS ) ) {
            taggables = findIndexedInfoProducts();
        } else if ( indexedOn.equals( INFO_FORMATS ) ) {
            taggables = findIndexedInfoFormats();
        } else if ( indexedOn.equals( FUNCTIONS ) ) {
            taggables = findIndexedFunctions();
        } else if ( indexedOn.equals( ASSETS ) ) {
            taggables = findIndexedMaterialAssets();
        } else {
            throw new IllegalStateException( "Can't index on " + indexedOn );
        }
        return (List<String>) CollectionUtils.collect(
                CollectionUtils.select( taggables,
                        new Predicate() {
                            public boolean evaluate( Object obj ) {
                                return !isFilteredOutByTags( (Taggable) obj );
                            }
                        }
                ),
                new Transformer() {
                    public Object transform( Object obj ) {
                        return ( indexName( (Taggable) obj ) ).toLowerCase();
                    }
                }
        );

    }

    /**
     * Get all distinct, filtered names to be indexed, as lowercase.
     *
     * @return a list of strings
     */
    @SuppressWarnings("unchecked")
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
        } else if ( indexedOn.equals( REQUIREMENTS ) ) {
            names = indexNamesFor( findIndexedRequirements() );
        } else if ( indexedOn.equals( INFO_PRODUCTS ) ) {
            names = indexNamesFor( findIndexedInfoProducts() );
        } else if ( indexedOn.equals( INFO_FORMATS ) ) {
            names = indexNamesFor( findIndexedInfoFormats() );
        } else if ( indexedOn.equals( FUNCTIONS ) ) {
            names = indexNamesFor( findIndexedFunctions() );
        } else if ( indexedOn.equals( ASSETS ) ) {
            names = indexNamesFor( findIndexedMaterialAssets() );
        } else {
            throw new IllegalStateException( "Can't index on " + indexedOn );
        }
        return (List<String>) CollectionUtils.collect(
                CollectionUtils.select( names,
                        new Predicate() {
                            public boolean evaluate( Object obj ) {
                                return !isFilteredOutByName( (String) obj );
                            }
                        }
                ),
                new Transformer() {
                    public Object transform( Object obj ) {
                        return ( (String) obj ).toLowerCase();
                    }
                }
        );
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
        names.addAll( indexNamesFor( findIndexedRequirements() ) );
        names.addAll( indexNamesFor( findIndexedInfoProducts() ) );
        names.addAll( indexNamesFor( findIndexedInfoFormats() ) );
        names.addAll( indexNamesFor( findIndexedFunctions() ) );
        names.addAll( indexNamesFor( findIndexedMaterialAssets() ) );
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
        taggables.addAll( findIndexedRequirements() );
        taggables.addAll( findIndexedInfoProducts() );
        taggables.addAll( findIndexedInfoFormats() );
        taggables.addAll( findIndexedFunctions() );
        taggables.addAll( findIndexedMaterialAssets() );
        return taggables;
    }

    @SuppressWarnings("unchecked")
    private List<String> indexNamesFor( List<? extends Nameable> nameables ) {
        return (List<String>) CollectionUtils.collect(
                nameables,
                new Transformer() {
                    public Object transform( Object input ) {
                        if ( input instanceof Actor && ( (Actor) input ).isActual() ) {
                            return ( (Actor) input ).getName();
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
        target.add( indicesContainer );
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
            } else if ( indexedOn.equals( REQUIREMENTS ) ) {
                indices = indicesFor( findIndexedRequirements() );
            } else if ( indexedOn.equals( INFO_PRODUCTS ) ) {
                indices = indicesFor( findIndexedInfoProducts() );
            } else if ( indexedOn.equals( INFO_FORMATS ) ) {
                indices = indicesFor( findIndexedInfoFormats() );
            } else if ( indexedOn.equals( FUNCTIONS ) ) {
                indices = indicesFor( findIndexedFunctions() );
            } else if ( indexedOn.equals( ASSETS ) ) {
                indices = indicesFor( findIndexedMaterialAssets() );
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
        indexEntries.addAll( indicesFor( findIndexedRequirements() ) );
        indexEntries.addAll( indicesFor( findIndexedInfoProducts() ) );
        indexEntries.addAll( indicesFor( findIndexedInfoFormats() ) );
        indexEntries.addAll( indicesFor( findIndexedFunctions() ) );
        indexEntries.addAll( indicesFor( findIndexedMaterialAssets() ) );
        return indexEntries;
    }

    private String rangeName( Nameable nameable ) {
        if ( nameable instanceof Actor && ( (Actor) nameable ).isActual() ) {
            return ( (Actor) nameable ).getName();
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

    @SuppressWarnings("unchecked")
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
                && !isTaggedWith( taggable, filter );
    }

    private void styleEntityEntry( Component component, ModelObject mo ) {
        StringBuilder sb = new StringBuilder();
        if ( mo.isEntity() && ( (ModelEntity) mo ).isType() ) {
            sb.append( "font-style: oblique" );
        }
        if ( mo.isEntity() && !getQueryService().isReferenced( mo ) ) {
            if ( sb.length() > 0 ) sb.append( "; " );
            sb.append( "text-decoration:line-through" );
        }
        if ( sb.length() > 0 ) {
            component.add(
                    new AttributeModifier(
                            "style",
                            new Model<String>( sb.toString() ) )
            );
        }
    }

    protected String addToCss( ModelObject modelObject, String css ) {
        String added = "";
        if ( modelObject instanceof Part ) {
            QueryService queryService = getQueryService();
            boolean overridden = queryService.isOverridden( (Part) modelObject );
            boolean overriding = queryService.isOverriding( (Part) modelObject );
            added = overriding && overridden
                    ? "overridden-overriding"
                    : overriding
                    ? "overriding"
                    : overridden
                    ? "overridden"
                    : "";
        } else if ( modelObject instanceof Flow ) {
            QueryService queryService = getQueryService();
            boolean overridden = queryService.isOverridden( (Flow) modelObject );
            boolean overriding = queryService.isOverriding( (Flow) modelObject );
            added = overriding && overridden
                    ? "overridden-overriding"
                    : overriding
                    ? "overriding"
                    : overridden
                    ? "overridden"
                    : "";
        } else if ( modelObject instanceof Organization ) {
            if ( ( (Organization) modelObject ).isActual() && ( (Organization) modelObject ).isPlaceHolder() ) {
                added = "placeholder";
            }
        } else if ( modelObject instanceof Place ) {
            if ( ( (Place) modelObject ).isActual() && ( (Place) modelObject ).isPlaceholder() ) {
                added = "placeholder";
            }
        } else if ( modelObject instanceof MaterialAsset ) {
            if ( ( (MaterialAsset) modelObject ).isActual() && ( (MaterialAsset) modelObject ).isPlaceholder() ) {
                added = "placeholder";
            }
        }
        return css + ( added.isEmpty() ? "" : ( " " + added ) );
    }

    public boolean isMustBeReferenced() {
        return mustBeReferenced;
    }

    public void setMustBeReferenced( boolean val ) {
        mustBeReferenced = val;
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
            String name = getIndexEntry().getName();
            return name.trim().isEmpty() ? "UNNAMED" : name;
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
                    ? "Segment"
                    : ( !mo.isEntity() )
                    ? mo.getClass().getSimpleName()
                    : ( mo instanceof Actor && ( (Actor) mo ).isActual() )
                    ? ( ( (Actor) mo ).isPerson() ? "Person" : "System" )
                    : mo instanceof Organization && ( (Organization) mo ).isPlaceHolder()
                    ? "Placeholder " + mo.getClass().getSimpleName().toLowerCase()
                    : mo instanceof Place && ( (Place) mo ).isPlaceholder()
                    ? "Placeholder " + mo.getClass().getSimpleName().toLowerCase()
                    : mo instanceof MaterialAsset && ( (MaterialAsset) mo ).isPlaceholder()
                    ? "Placeholder " + mo.getClass().getSimpleName().toLowerCase()
                    : ( (ModelEntity) mo ).isActual()
                    ? "Actual " + mo.getClass().getSimpleName().toLowerCase()
                    : ( ModelEntity.canBeActual( ( (ModelEntity) mo ) )
                    ? ( "Type of " + mo.getClass().getSimpleName().toLowerCase() )
                    : mo.getClass().getSimpleName() ) );
            return kind
                    + ( isNameAbbreviated() ? ": " + getFullName() : "" )
                    + " [" + mo.getId() + "]"
                    + ( mo.getDescription().isEmpty()
                    ? ""
                    : ( " - " + StringUtils.abbreviate( mo.getDescription(), MAX_NAME_LENGTH * 3 ) ) );
        }

        /**
         * Get the kind of model object.
         *
         * @param mo model object
         * @return a string
         */
        protected String getToolTip( ModelObject mo ) {
            return StringUtils.capitalize( mo.getTypeName() )
                    + " [" + mo.getId() + "]"
                    + ( mo.getDescription().isEmpty()
                    ? ""
                    : ( " - " + StringUtils.abbreviate( mo.getDescription(), MAX_NAME_LENGTH * 3 ) ) );
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
                    addToCss( getIndexedModelObject(), css ) );
            styleEntityEntry( moLink, getIndexedModelObject() );
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
                addTipTitle( moLabel, new Model<String>( getFullName() ) );
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
            styleEntityEntry( moLabel, getIndexedModelObject() );
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
            return "" + ( getReferences().indexOf( mo ) + 1 );
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
        if ( !findIndexedRequirements().isEmpty() ) choices.add( REQUIREMENTS );
        if ( !findIndexedInfoProducts().isEmpty() ) choices.add( INFO_PRODUCTS );
        if ( !findIndexedInfoFormats().isEmpty() ) choices.add( INFO_FORMATS );
        if ( !findIndexedFunctions().isEmpty() ) choices.add( FUNCTIONS );
        if ( !findIndexedMaterialAssets().isEmpty() ) choices.add( ASSETS );
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
    protected List<Modelable> findIndexedEOIs() {
        return new ArrayList<Modelable>();
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
     * Find all requirements to index.
     *
     * @return a list of requirements
     */
    protected List<Requirement> findIndexedRequirements() {
        return new ArrayList<Requirement>();
    }

    /**
     * Find all info products to index.
     *
     * @return a list of info products
     */
    protected List<InfoProduct> findIndexedInfoProducts() {
        return new ArrayList<InfoProduct>();
    }

    /**
     * Find all info formats to index.
     *
     * @return a list of info formats
     */
    protected List<InfoFormat> findIndexedInfoFormats() {
        return new ArrayList<InfoFormat>();
    }

    /**
     * Find all functions to index.
     *
     * @return a list of functions
     */
    protected List<Function> findIndexedFunctions() {
        return new ArrayList<Function>();
    }

    /**
     * Find all material assets to index.
     *
     * @return a list of material assets
     */
    protected List<MaterialAsset> findIndexedMaterialAssets() {
        return new ArrayList<MaterialAsset>();
    }


    /**
     * An element of information in a flow.
     */
    public class ElementOfInformationInFlow implements Modelable {

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

    /**
     * An element of information in an info product.
     */
    public class ElementOfInformationInInfoProduct implements Modelable {

        private InfoProduct infoProduct;
        private ElementOfInformation eoi;

        public ElementOfInformationInInfoProduct( InfoProduct infoProduct, ElementOfInformation eoi ) {
            this.infoProduct = infoProduct;
            this.eoi = eoi;
        }

        public String getName() {
            return eoi.getContent().toLowerCase();
        }

        /**
         * {@inheritDoc}
         */
        public ModelObject getModelObject() {
            return infoProduct;
        }
    }

}
