package com.mindalliance.channels.playbook.pages.filters;

import com.mindalliance.channels.playbook.ifm.IfmElement;
import com.mindalliance.channels.playbook.ifm.Participation;
import com.mindalliance.channels.playbook.ifm.User;
import com.mindalliance.channels.playbook.ifm.context.environment.Organization;
import com.mindalliance.channels.playbook.ifm.context.environment.Person;
import com.mindalliance.channels.playbook.ifm.context.environment.Position;
import com.mindalliance.channels.playbook.ifm.project.Project;
import com.mindalliance.channels.playbook.ifm.project.scenario.Event;
import com.mindalliance.channels.playbook.ifm.project.scenario.act.Activity;
import com.mindalliance.channels.playbook.support.models.ColumnProvider;
import com.mindalliance.channels.playbook.support.models.ContainerModel;

import javax.swing.tree.TreeNode;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

/**
 * A node in the filter tree.
 */
abstract public class Filter implements TreeNode, Serializable {

    private String collapsedText;
    private String expandedText;
    private boolean expanded;
    private boolean selected;
    private Filter parent;
    private Class rootClass = IfmElement.class;

    List<Filter> children = new ArrayList<Filter>();

    protected Filter( String collapsedText, String expandedText, Filter... children ) {
        setCollapsedText( collapsedText );
        setExpandedText( expandedText );

        if ( children.length > 0 ) {
            this.children.addAll( Arrays.asList( children ) );
            for ( Filter child : this.children )
                child.setParent( this );
        }
    }

    public Filter( String text, Filter... children ) {
        this( text, text, children );
    }

    /**
     * Specify what kind of objects are allowed by this filter.
     * @return A superclass
     */
    public Class getRootClass() {
        return rootClass;
    }

    public String getCollapsedText() {
        return collapsedText;
    }

    public void setCollapsedText( String collapsedText ) {
        this.collapsedText = collapsedText;
    }

    public String getExpandedText() {
        return expandedText;
    }

    public String getText() {
        return isExpanded()? getExpandedText() : getCollapsedText();
    }

    public void setExpandedText( String expandedText ) {
        this.expandedText = expandedText;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded( boolean expanded ) {
        this.expanded = expanded;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected( boolean selected ) {
        this.selected = selected;
        for ( Filter kid : children )
            kid.setSelected( selected );

        if ( !selected && parent != null )
            parent.childDeselected();
    }

    /**
     * Called by a child when deselected.
     */
    protected void childDeselected() {
        this.selected = false;
    }

    public final Filter getParent() {
        return parent;
    }

    private void setParent( Filter parent ) {
        this.parent = parent;
    }

    /**
     * Returns the index of <code>node</code> in the receivers children. If the receiver does not contain
     * <code>node</code>, -1 will be returned.
     */
    public final int getIndex( TreeNode node ) {
        return children.indexOf( (Filter) node );
    }

    /**
     * Returns true if the receiver allows children.
     */
    public final boolean getAllowsChildren() {
        return true;
    }

    /**
     * Returns true if the receiver is a leaf.
     */
    public final boolean isLeaf() {
        return children.isEmpty();
    }

    /**
     * Returns the children of the receiver as an <code>Enumeration</code>.
     */
    public final Enumeration<Filter> children() {
        return new EnumerationAdaptor<Filter>( children.iterator() );
    }
    /**
     * Returns the child <code>TreeNode</code> at index <code>childIndex</code>.
     */
    public final TreeNode getChildAt( int childIndex ) {
        return children.get( childIndex );
    }

    /**
     * Returns the number of children <code>TreeNode</code>s the receiver contains.
     */
    public final int getChildCount() {
        return children.size();
    }

    public final void add( Filter child ) {
        if ( children == null )
            children = new ArrayList<Filter>();
        children.add( child );
        child.setParent( this );
    }

    /**
     * This is this filter applies to the given object.
     * @param object the object.
     * @return  true if the object should be included in the results.
     */
    public boolean match( Object object ) {
        if ( !isSelected() && getChildCount() > 0 && isExpanded() ) {
            // OR the match of all children
            for ( Filter f : children )
                if ( f.match( object ) )
                    return true;

            return false;

        } else {
            // selected || no kids || collapsed
            return localMatch( object );
        }
    }

    //-------------------------
    // Abstractness...

    /**
     * Test an object for inclusion, assuming either selected, collapsed or no children.
     * @param object the object
     * @return true if the object satisfies this filter.
     */
    abstract protected boolean localMatch( Object object );

    public static Filter[] Resources( ContainerModel data ) {
        List<Filter> result = new ArrayList<Filter>();
        ColumnProvider cp = data.getColumnProvider();

        if ( cp.getClasses().contains( Organization.class ) ) {
            Filter filter = new ClassFilter( Organization.class, "all organizations", "organizations..." );
            result.add( filter );
//            addOrgs( filter, cp, data );
        }

        if ( cp.getClasses().contains( Person.class ) ) {
            Filter filter = new ClassFilter( Person.class, "all persons", "persons..." );
            result.add( filter );
            //addPersons( filter, cp, data );
        }

        if ( cp.getClasses().contains( Position.class ) ) {
            Filter filter = new ClassFilter( Position.class, "all positions", "positions..." );
            result.add( filter );
         }

        if ( cp.getClasses().contains( System.class ) ) {
            Filter filter = new ClassFilter( System.class, "all systems", "systems..." );
            result.add( filter );
         }

        return result.toArray( new Filter[ result.size() ] );
    }

    public static Filter[] ScenarioItems( ContainerModel data ) {
        List<Filter> result = new ArrayList<Filter>();
        ColumnProvider cp = data.getColumnProvider();

        if ( cp.getClasses().contains( Event.class ) ) {
            Filter filter = new ClassFilter( Event.class, "all events", "events..." );
            result.add( filter );
        }

        if ( cp.getClasses().contains( Activity.class ) ) {
            Filter filter = new ClassFilter( Activity.class, "all activities", "activities..." );
            result.add( filter );
        }

        return result.toArray( new Filter[ result.size() ] );
    }

    public static Filter[] SystemItems( ContainerModel data ) {
        List<Filter> result = new ArrayList<Filter>();
        ColumnProvider cp = data.getColumnProvider();

        if ( cp.getClasses().contains( User.class ) ) {
            Filter filter = new ClassFilter( User.class, "all users", "users..." );
            result.add( filter );
        }

        if ( cp.getClasses().contains( Project.class ) ) {
            Filter filter = new ClassFilter( Project.class, "all projects", "projects..." );
            result.add( filter );
        }

        if ( cp.getClasses().contains( Participation.class ) ) {
            Filter filter = new ClassFilter( Participation.class, "all participations", "participations..." );
            result.add( filter );
        }

        return result.toArray( new Filter[ result.size() ] );
    }

    //===================================================
    static class EnumerationAdaptor<T> implements Serializable, Enumeration<T> {

        private Iterator<T> iterator;

        public EnumerationAdaptor( Iterator<T> iterator ) {
            this.iterator = iterator;
        }

        /**
         * Tests if this enumeration contains more elements.
         *
         * @return <code>true</code> if and only if this enumeration object contains at least one more element to provide;
         *         <code>false</code> otherwise.
         */
        public boolean hasMoreElements() {
            return iterator.hasNext();
        }

        /**
         * Returns the next element of this enumeration if this enumeration object has at least one more element to
         * provide.
         *
         * @return the next element of this enumeration.
         *
         * @throws java.util.NoSuchElementException
         *          if no more elements exist.
         */
        public T nextElement() {
            return iterator.next();
        }
    }

}
