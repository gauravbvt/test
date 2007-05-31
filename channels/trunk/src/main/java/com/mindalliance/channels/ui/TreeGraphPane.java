// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.ui;

import java.beans.PropertyDescriptor;
import java.text.Collator;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Menuseparator;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;

import com.mindalliance.channels.DisplayAs;
import com.mindalliance.channels.JavaBean;
import com.mindalliance.channels.data.components.Cause;
import com.mindalliance.channels.data.components.Caused;
import com.mindalliance.channels.data.elements.Occurrence;
import com.mindalliance.channels.data.elements.project.Scenario;
import com.mindalliance.channels.data.elements.scenario.Event;
import com.mindalliance.channels.data.elements.scenario.Product;
import com.mindalliance.channels.data.elements.scenario.Task;
import com.mindalliance.channels.data.support.Duration;
import com.mindalliance.channels.ui.editor.EditorFactory;
import com.mindalliance.channels.util.GUID;
import com.mindalliance.zk.mxgraph.MxFastOrganicLayout;
import com.mindalliance.zk.mxgraph.MxGraph;
import com.mindalliance.zk.mxgraph.MxPanningHandler;

import static com.mindalliance.channels.ui.TreeGraphPane.Arc.Direction.to;

/**
 * A scenario element viewer.
 *
 * @author dfeeney
 * @version $Revision$
 */
public class TreeGraphPane extends Tabbox implements TimelineListener {

    /**
     * Default duration for new tasks (in minutes).
     */
    private static final int TASK_DURATION = 15;

    /**
     * Height of the title, in pixels.
     */
    private static final int TITLE_HEIGHT = 35;
    private static final int BORDERS = 10;

    private EditorFactory editorFactory;
    private Scenario scenario;
    private MxGraph graph;
    private Tree tree;
    private Caused rootElement;
    private IconManager iconManager;

    private Map<Object,Set<Arc>> arcCache;
    private Set<Object> expandedNodes;

    private Object treeSelection;

    /** One popup menu, reused for all items in the tree/graph. */
    private Menupopup menu;
    private ScenarioTimeline timeline;

    /**
     * Default constructor.
     *
     * @param height the available height
     * @param scenario the current scenario
     * @param editorFactory the editor factory
     */
    public TreeGraphPane(
            int height, Scenario scenario, EditorFactory editorFactory ) {

        this.scenario = scenario;
        this.editorFactory = editorFactory;

        int contentHeight = height - TITLE_HEIGHT - BORDERS;

        Tab treeTab = new Tab( "Tree" );

        Tabs tabs = new Tabs();
        tabs.appendChild( new Tab( "Net" ) );
        tabs.appendChild( treeTab );
        tabs.setWidth( "30px" );
        tabs.setHeight( "16px" );

        this.graph = generateGraph();
        graph.setSclass( "what-pane" );
        Tabpanel graphPanel = new Tabpanel();
        graphPanel.appendChild( graph );
        graphPanel.setHeight( contentHeight + "px" );

        this.tree = new Tree();
        tree.setSclass( "what-pane" );
        tree.addEventListener( "onSelect", new EventListener() {
            public boolean isAsap() {
                return true;
            }

            public void onEvent( org.zkoss.zk.ui.event.Event e ) {
                setTreeSelection( tree.getSelectedItem().getValue() );
            }
        } );

        Tabpanel treePanel = new Tabpanel();
        treePanel.setHeight( contentHeight + "px" );
        treePanel.appendChild( tree );

        Tabpanels tabPanels = new Tabpanels();
        tabPanels.appendChild( graphPanel );
        tabPanels.appendChild( treePanel );

        this.appendChild( tabs );
        this.appendChild( tabPanels );
        this.setOrient( "vertical" );
        this.setSelectedTab( treeTab );
        this.setWidth( "100%" );
    }

    private MxGraph generateGraph() {
        MxGraph graph = new MxGraph();
        graph.setLayout( new MxFastOrganicLayout() );
        graph.setWidth( "100%" );
        graph.setProperty( MxGraph.AUTO_SIZE, "true", true );
        graph.setStyle( "overflow:hidden; "
                + "background:url('images/grid.gif');" );

        graph.getPanningHandler().setProperty(
                MxPanningHandler.IS_SELECT_ON_POPUP, false, false );
        graph.getPanningHandler().setProperty(
                MxPanningHandler.IS_USE_SHIFT_KEY, true, false );
        graph.getPanningHandler().setProperty(
                MxPanningHandler.IS_PAN_ENABLED, true, false );

        return graph;
    }

    /**
     * Return the value of rootElement.
     */
    public final Caused getRootElement() {
        return this.rootElement;
    }

    /**
     * Set the root element to display in this pane.
     * @param root a scenario element
     */
    public void setRootElement( Caused root ) {
        this.rootElement = root;

        arcCache = new HashMap<Object,Set<Arc>>();
        expandedNodes = new HashSet<Object>();

        if ( menu == null ) {
            menu = new Menupopup();
            menu.setId( "tree-popup-" + hashCode() );
            menu.setPage( editorFactory.getPage() );
            resetTreePopup( root );
            tree.setContext( menu.getId() );
        }

        // TODO repopulate graph
        rebuildTree( root );
    }

    /**
     * Kludge...
     * @param root the node
     */
    private String getName( Caused root ) {
        // TODO Clean this up...
        return root instanceof Product ?
                ( (Product) root ).getName()
              : ( (Occurrence) root ).getName();
    }

    /**
     * Create a new scenario event.
     */
    private Event createEvent() {
        GUID guid = getEditorFactory().getSystem().getGuidFactory().newGuid();
        Event event = new Event( guid );
        event.setName( "Some new event" );
        event.setScenario( scenario );
        return event;
    }

    /**
     * Create a new product.
     */
    private Product createProduct() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Create a new scenario task.
     */
    private Task createTask() {
        GUID guid = getEditorFactory().getSystem().getGuidFactory().newGuid();
        Task task = new Task( guid );
        task.setName( "Some new task" );
        task.setDuration(
                new Duration( TASK_DURATION, Duration.Unit.minute ) );
        task.setScenario( scenario );
        return task;
    }

    private Menuitem newItem( String label, final Runnable action ) {
        Menuitem result = new Menuitem( label );
        if ( action != null )
            result.addEventListener( "onClick", new EventListener() {
                public boolean isAsap() {
                    return true;
                }

                public void onEvent( org.zkoss.zk.ui.event.Event arg0 ) {
                    action.run();
                }
            } );
        return result;
    }

    private Menu newMenu( String label, Component... children ) {
        Menu menu = new Menu( label );
        Menupopup popup = new Menupopup();
        for ( Component child : children )
            popup.appendChild( child );
        menu.appendChild( popup );
        return menu;
    }

    /**
     * Cause the tree and timeline to get repainted with new data.
     */
    private void refresh() {
        timeline.invalidate();
    }

    private void resetTreePopup( Object object ) {
        menu.getChildren().clear();
        if ( scenario.getOccurrences().size() == 0 )
            menu.appendChild( newMenu( "New",
                newItem( "Event...", new Runnable() {
                    public void run() {
                        Event event = (Event) getEditorFactory().popupEditor(
                                createEvent() );
                        if ( event != null ) {
                            scenario.addOccurrence( event );
                            refresh();
                        }
                    }
                } ),
                newItem( "Task...", new Runnable() {
                    public void run() {
                        Task task = (Task) getEditorFactory().popupEditor(
                                createTask() );
                        if ( task != null ) {
                            scenario.addOccurrence( task );
                            refresh();
                        }
                    }
                } ) ) );

        if ( getEditorFactory().supports( getTreeSelection() ) )
            menu.appendChild( newItem( "Edit...", new Runnable() {
                public void run() {
                    editSelection();
                }
            } ) );

        if ( object == rootElement ) {
            menu.appendChild( new Menuseparator() );

            if ( isCaused( object ) && ( (Caused) object ).getCause() == null )
                menu.appendChild( newMenu( "Set cause",
                    newItem( "Event...", new Runnable() {
                        public void run() {
                            setCause(
                                createEvent(),
                                (Caused) getTreeSelection() );
                        }
                    } ),
                    newItem( "Task...", new Runnable() {
                        public void run() {
                            setCause(
                                createTask(),
                                (Caused) getTreeSelection() );
                        }
                    } ) ) );

            if ( Occurrence.class.isAssignableFrom( object.getClass() ) )
                menu.appendChild( newMenu( "Add a consequence",
                    newItem( "Event...", new Runnable() {
                        public void run() {
                            addConsequence(
                                (Occurrence) getTreeSelection(),
                                createEvent() );
                        }
                    } ),
                    newItem( "Task...", new Runnable() {
                        public void run() {
                            addConsequence(
                                (Occurrence) getTreeSelection(),
                                createTask() );
                        }
                    } ),
                    newItem( "Product...", new Runnable() {
                        public void run() {
                            addConsequence(
                                (Occurrence) getTreeSelection(),
                                createProduct() );
                        }
                    } )
                ) );

            menu.appendChild( newItem( "Delete", new Runnable() {
                public void run() {
                    deleteObject( getTreeSelection() );
                }
            } ) );
        }
        menu.invalidate();
    }

    private void deleteObject( Object treeSelection ) {
        // TODO Auto-generated method stub
    }

    @SuppressWarnings( "unchecked" )
    private void setCause( Occurrence cause, Caused caused ) {
        Event event =
            (Event) getEditorFactory().popupEditor( cause );
        if ( event != null ) {
            caused.setCause( new Cause( cause ) );
            scenario.addOccurrence( event );
            refresh();
        }
    }

    private void addConsequence(
            Occurrence causeObject, JavaBean consequence ) {

        JavaBean editedObject = getEditorFactory().popupEditor( consequence );
        if ( editedObject != null ) {
            Caused<Occurrence> c = (Caused<Occurrence>) consequence;
            c.setCause( new Cause<Occurrence>( causeObject ) );
            if ( Product.class.isAssignableFrom( consequence.getClass() ) )
                scenario.addProduct( (Product) consequence );
            else
                scenario.addOccurrence( (Occurrence) consequence );
            refresh();
        }
    }

    /**
     * Test if an object can have a cause.
     * @param object the object
     */
    private boolean isCaused( Object object ) {
        return object != null
            && Caused.class.isAssignableFrom( object.getClass() );
    }

    /**
     * Rebuild and redisplay the tree given a selected node.
     * @param root the root element
     */
    @SuppressWarnings( "unchecked" )
    private void rebuildTree( Caused root ) {
        Treechildren trees = new Treechildren();
        Treeitem rootItem = createItem( root, getName( root ), true );
        trees.appendChild( rootItem );

        List<Object> children = (List<Object>) tree.getChildren();
        children.clear();
        children.add( trees );
        tree.invalidate();
    }

    /**
     * Create a tree item on an element and its first-level
     * children.
     * @param element the element underlying element
     * @param label how to label the root item
     * @param open true if children are visible. If false, user
     * expansion will create childrens
     */
    private Treeitem createItem(
            final Object element, String label, boolean open ) {

        final Treeitem result = new Treeitem( label, element );
        final Treechildren children = new Treechildren();
        result.setImage( getIconManager().getSmallIcon( element ) );
        result.setOpen( open );
        result.appendChild( children );
        result.setTooltiptext( MessageFormat.format(
                "an instance of {0}",
                element.getClass().getSimpleName().toLowerCase() ) );

        if ( open ) {
            expandedNodes.add( element );
            appendChildren( children, element );

        } else {
            result.addEventListener( "onOpen", new EventListener() {
                    public boolean isAsap() {
                        return true;
                    }

                    public void onEvent( org.zkoss.zk.ui.event.Event arg0 ) {
                        if ( !expandedNodes.contains( element ) ) {
                            expandedNodes.add( element );
                            appendChildren( children, element );
                        }
                    }
                } );
        }
        result.addEventListener( Events.ON_DOUBLE_CLICK, new EventListener() {
            public boolean isAsap() {
                return true;
            }

            public void onEvent( org.zkoss.zk.ui.event.Event arg0 ) {
                if ( getEditorFactory().supports( getTreeSelection() ) )
                    editSelection();
            }
        } );

        return result;
    }

    /**
     * Create all children tree item of a given element.
     * @param kids the children to populate
     * @param element the element
     */
    private Treechildren appendChildren(
            Treechildren kids, Object element ) {

        Map<String, Set<Arc>> groups = separate( getArcs( element ) );
        for ( Entry<String,Set<Arc>> e : groups.entrySet() ) {
            Set<Arc> arcs = e.getValue();
            Arc firstArc = arcs.iterator().next();
            if ( arcs.size() == 1 ) {
                kids.appendChild(
                    createItem(
                        firstArc.getNode(),
                        firstArc.getLabel( false ),
                        false ) );

            } else {
                Treechildren grandKids = new Treechildren();
                for ( Arc a : arcs )
                    grandKids.appendChild( createItem(
                        a.getNode(), a.getNode().toString(), false ) );

                Treeitem group = new Treeitem( firstArc.getLabel( true ) );
                group.setOpen( false );
                group.appendChild( grandKids );
                kids.appendChild( group );
            }
        }
        return kids;
    }

    /**
     * Group together arcs of similar labels.
     * @param arcs the arcs to organize
     * @return subset of arcs, indexed by new label
     */
    private Map<String, Set<Arc>> separate( Set<Arc> arcs ) {

        Map<String, Set<Arc>> p1 = new TreeMap<String, Set<Arc>>();
        for ( Arc a : arcs ) {
            String label = a.getPropertyName();
            Set<Arc> set = p1.get( label );
            if ( set == null ) {
                set = new HashSet<Arc>();
                p1.put( label, set );
            }
            set.add( a );
        }

        return p1;
    }

    /**
     * Find incoming and outgoing arcs from a node.
     * <p>(Package access for testing purposes)</p>
     * @param occurence the initial node
     */
    Set<Arc> getArcs( Object occurence ) {
        Set<Arc> result = arcCache.get( occurence );
        if ( result == null ) {
            result = new TreeSet<Arc>();
            arcCache.put( occurence, result );
            addOutgoingArcs( occurence, result );
            addIncomingArcs( occurence, result );
        }

        return result;
    }

    /**
     * Add indirect connections to an occurence.
     * @param occurence the occurence
     * @param arcs the set to contribute to
     */
    private void addIncomingArcs( Object occurence, Set<Arc> arcs ) {
//        JXPathContext context = JXPathContext.newContext( getSystem() );
    }

    /**
     * Add direct connections from an occurence.
     * @param occurence the occurence
     * @param arcs the set to contribute to
     */
    private void addOutgoingArcs( Object occurence, Set<Arc> arcs ) {
        BeanWrapper bw = new BeanWrapperImpl( occurence );
        for ( PropertyDescriptor pd : bw.getPropertyDescriptors() ) {
            // Only process readable properties
            String name = pd.getName();
            if ( pd.getReadMethod() != null && pd.getWriteMethod() != null ) {
                Class<?> type = pd.getPropertyType();
                Object value = bw.getPropertyValue( name );
                if ( value != null ) {

                    // Direct ScenarioElement values
                    if ( isArcNode( type ) )
                        arcs.add( new Arc( value, pd, to ) );

                    // Arrays of ScenarioElements
                    else if ( isArcNode( type.getComponentType() ) )
                        for ( Object v : (Object[]) value ) {
                            if ( v != null )
                                arcs.add( new Arc( v, pd, to ) );
                        }

                    // Collections containing ScenarioElements
                    else if ( Collection.class.isAssignableFrom(
                                                    value.getClass() ) ) {

                        for ( Object v : (Collection) value )
                            if ( v != null && isArcNode( v.getClass() ) )
                                arcs.add( new Arc( v, pd, to ) );
                    }
                }
            }
        }
    }

    /**
     * Test if given class is a suitable arc destination.
     * @param type the given class
     */
    private boolean isArcNode( Class<?> type ) {
        return type != null
            && type.getName().startsWith( "com.mindalliance.channels." )
            && !type.getSimpleName().equals( "TypeSet" )
            && !type.getName().startsWith(
                    "com.mindalliance.channels.data.support." );
    }

    /**
     * Return the value of scenario.
     */
    public final Scenario getScenario() {
        return this.scenario;
    }

    /**
     * Return the value of editorFactory.
     */
    public EditorFactory getEditorFactory() {
        return this.editorFactory;
    }

    /**
     * Set the value of iconManager.
     * @param iconManager The new value of iconManager
     */
    public void setIconManager( IconManager iconManager ) {
        this.iconManager = iconManager;
    }

    /**
     * Return the value of iconManager.
     */
    public IconManager getIconManager() {
        return this.iconManager;
    }

    /**
     * Set the tree selection.
     * @param treeSelection The new value of treeSelection
     */
    public void setTreeSelection( Object treeSelection ) {
        this.treeSelection = treeSelection;
        resetTreePopup( treeSelection );

        // TODO set the tree selection if not already set.
    }

    /**
     * Return the value of treeSelection.
     */
    public Object getTreeSelection() {
        return this.treeSelection == null ?
                getRootElement() : this.treeSelection;
    }

    /**
     * React to a selection from the *timeline*.
     * @param timeline the timeline
     * @param oldSelection the previous selected item
     * @param newSelection the new selected item
     */
    public void selectionChanged(
            ScenarioTimeline timeline,
            Caused oldSelection, Caused newSelection ) {

        setRootElement( newSelection );
    }

    /**
     * Return the value of timeline.
     */
    public ScenarioTimeline getTimeline() {
        return this.timeline;
    }

    /**
     * Set the value of timeline.
     * @param timeline The new value of timeline
     */
    public void setTimeline( ScenarioTimeline timeline ) {
        this.timeline = timeline;
    }

    /**
     * Popup an editor on the current tree selection.
     */
    private void editSelection() {
        // TODO make Caused extend JavaBean
        JavaBean result = getEditorFactory().popupEditor(
            (JavaBean) getTreeSelection() );

        if ( result != null )
            refresh();
    }

    //=================================================
    /**
     * A connector to a node.
     */
    public static class Arc implements Comparable<Arc> {

        /**
         * The direction of an arc.
         */
        public enum Direction { to, from }

        private Object node;
        private PropertyDescriptor property;
        private Direction direction;

        /**
         * Default constructor.
         */
        public Arc() {
        }

        /**
         * Default constructor.
         *
         * @param node the target node
         * @param prop the property descriptor where node was found
         * @param direction the direction of the arc
         */
        public Arc(
                Object node, PropertyDescriptor prop, Direction direction ) {

            this();

            if ( node == null || prop == null )
                throw new NullPointerException();

            this.node = node;
            this.direction = direction;
            this.property = prop;
        }

        /**
         * Concoct a decent label out of a property descriptor.
         * Uses the value of the \@DisplayAs annotation if it exists
         * of the name of the property otherwise.
         * @see com.mindalliance.channels.DisplayAs
         * @param many true if this will return the label for more than
         * one value.
         */
        private String getLabel( boolean many ) {
            DisplayAs annotation =
                getProperty().getReadMethod().getAnnotation( DisplayAs.class );

            return annotation == null ?
                       getDefaultLabel( many )

                     : MessageFormat.format(
                         many ? ( getDirection() == to ?
                                      annotation.directMany()
                                    : annotation.reverseMany() )
                              : ( getDirection() == to ?
                                      annotation.direct()
                                    : annotation.reverse() ),
                         getPropertyName(),
                         getNode() );
        }

        /**
         * Return a default label value.
         * @param many true if this will return the label for more than
         * one value.
         */
        private String getDefaultLabel( boolean many ) {
            return MessageFormat.format(
                many ? ( getDirection() == to ? "{0}:" : "{0} of:" )
                     : ( getDirection() == to ? "{0} is {1}" : "{0} of {1}" ),
                getPropertyName(),
                getNode() );
        }

        /**
         * Return the name of the underlying property.
         */
        public String getPropertyName() {
            return getProperty().getName();
        }

        /**
         * Return the value of direction.
         */
        public final Direction getDirection() {
            return this.direction;
        }

        /**
         * Return the value of node.
         */
        public final Object getNode() {
            return this.node;
        }

        /**
         * Compare with another object.
         * @param obj the other object
         */
        @Override
        public boolean equals( Object obj ) {
            return this == obj
                || ( obj != null
                        && obj instanceof Arc
                        && equals( (Arc) obj ) );
        }

        /**
         * Compare with another arc.
         * @param other the other arc
         */
        public boolean equals( Arc other ) {
            return other != null
                && this.getPropertyName().equals( other.getPropertyName() )
                && this.getDirection().equals( other.getDirection() )
                && this.getNode() == other.getNode();
        }

        /** Provide a good-enough hash for maps and sets.
         */
        @Override
        public int hashCode() {
            return getPropertyName().hashCode()
                 + getNode().hashCode()
                 + getDirection().hashCode();
        }

        /**
         * Provide a printed string form for debugging.
         */
        @Override
        public String toString() {
            return getLabel( false );
        }

        /**
         * Compare with another arc for ordering purposes.
         * @param o the other arc
         */
        public int compareTo( Arc o ) {
            Collator collator = Collator.getInstance();

            int result = collator.compare(
                    this.getPropertyName(), o.getPropertyName() );
            if ( result == 0 ) {
                result = this.getDirection().compareTo( o.getDirection() );
                if ( result == 0 )
                    result = collator.compare(
                                this.getNode().toString(),
                                o.getNode().toString() );
            }

            return result;
        }

        /**
         * Return the value of property.
         */
        public final PropertyDescriptor getProperty() {
            return this.property;
        }
    }
}
