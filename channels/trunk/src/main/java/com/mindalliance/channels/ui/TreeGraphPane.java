// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.ui;

import java.beans.PropertyDescriptor;
import java.text.Collator;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;

import com.mindalliance.channels.DisplayAs;
import com.mindalliance.channels.User;
import com.mindalliance.channels.data.Caused;
import com.mindalliance.channels.data.Occurrence;
import com.mindalliance.channels.data.elements.project.Scenario;
import com.mindalliance.channels.data.elements.scenario.Product;
import com.mindalliance.channels.services.SystemService;
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
     * How deep to build the tree.
     */
    private static final int MAX_LEVEL = 2;

    /**
     * Height of the title, in pixels.
     */
    private static final int TITLE_HEIGHT = 35;
    private static final int BORDERS = 10;

    private SystemService system;
    private Scenario scenario;
    private User user;
    private MxGraph graph;
    private Tree tree;
    private Caused rootElement;
    private IconManager iconManager;

    private Map<Object,Set<Arc>> arcCache = new HashMap<Object,Set<Arc>>();

    /**
     * Default constructor.
     *
     * @param height the available height
     * @param scenario the current scenario
     * @param system the system
     * @param user the current user
     */
    public TreeGraphPane(
            int height, Scenario scenario, SystemService system, User user ) {

        this.system = system;
        this.scenario = scenario;
        this.user = user;

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

        // TODO repopulate graph
        rebuildTree( root );
    }

    /**
     * Rebuild and redisplay the tree given a selected node.
     * @param root the root element
     */
    @SuppressWarnings( "unchecked" )
    private void rebuildTree( Caused root ) {
        Treechildren trees = new Treechildren();
        trees.appendChild( createItem( root, getName( root ), MAX_LEVEL ) );

        tree.getChildren().clear();
        tree.getChildren().add( trees );
        tree.invalidate();
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
     * Create a tree item on an element and its first-level
     * children.
     * @param element the element underlying element
     * @param label how to label the root item
     * @param level how deep to dig
     */
    private Treeitem createItem( Object element, String label, int level ) {

        Treeitem result = new Treeitem( label, element );
        result.setImage( getIconManager().getSmallIcon( element ) );
        result.setTooltiptext( MessageFormat.format(
                "an instance of {0}",
                element.getClass().getSimpleName().toLowerCase() ) );

        if ( level > 0 ) {
            Treechildren kids = new Treechildren();
            Map<String, Set<Arc>> groups = separate( getArcs( element ) );
            for ( Entry<String,Set<Arc>> e : groups.entrySet() ) {
                Set<Arc> arcs = e.getValue();
                Arc firstArc = arcs.iterator().next();
                if ( arcs.size() == 1 ) {
                    Treeitem item = createItem(
                                        firstArc.getNode(),
                                        firstArc.getLabel( false ),
                                        level - 1 );
                    item.setOpen( false );
                    kids.appendChild( item );

                } else {
                    Treeitem group =
                        new Treeitem( firstArc.getLabel( true ) );
                    group.setOpen( false );
                    Treechildren grandKids = new Treechildren();
                    for ( Arc a : arcs )
                        grandKids.appendChild( createItem(
                            a.getNode(), a.getNode().toString(), level - 1 ) );

                    group.appendChild( grandKids );
                    kids.appendChild( group );
                }
            }
            result.appendChild( kids );
        }

        return result;
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
//        return type != null && ScenarioElement.class.isAssignableFrom( type );
        return type != null
            && type.getName().startsWith( "com.mindalliance.channels." )
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
     * Return the value of system.
     */
    public final SystemService getSystem() {
        return this.system;
    }

    /**
     * Return the value of user.
     */
    public final User getUser() {
        return this.user;
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
     * React to a selection from the timeline.
     * @param timeline the timeline
     * @param oldSelection the previous selected item
     * @param newSelection the new selected item
     */
    public void selectionChanged(
            ScenarioTimeline timeline,
            Caused oldSelection, Caused newSelection ) {

        setRootElement( newSelection );
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
