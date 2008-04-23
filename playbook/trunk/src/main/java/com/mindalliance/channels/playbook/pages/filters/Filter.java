package com.mindalliance.channels.playbook.pages.filters;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.Mapper;
import com.mindalliance.channels.playbook.support.models.Container;
import com.mindalliance.channels.playbook.support.models.FilteredContainer;
import com.mindalliance.channels.playbook.support.persistence.Mappable;

import javax.swing.tree.TreeNode;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A node in the filter tree.
 */
abstract public class Filter implements TreeNode, Serializable, Mappable {

    private String collapsedText;
    private String expandedText;
    private transient Container container;

    private boolean expanded;
    private boolean selected;
    private Filter parent;

    private List<Filter> children;

    //-------------------------
    public Filter() {}

    protected Filter( String collapsedText, String expandedText ) {
        this.collapsedText = collapsedText;
        this.expandedText = expandedText;
    }

    public Filter( String text ) {
        this( text, text );
    }

    public Filter getRoot() {
        if ( getParent() == null )
            return this;
        else
            return getParent().getRoot();
    }

    // Mappable

    public Map toMap() {
        Map map = new HashMap();
        map.put( Mappable.CLASS_NAME_KEY, getClass().getName() );
        map.put("collapsedText", collapsedText);
        map.put("expandedText", expandedText);
        map.put("expanded", expanded);
        map.put("selected", selected);
        if (children != null) map.put("children", (Object) Mapper.toPersistedValue( children ));
        return map;
    }

    public void initFromMap(Map map) {
        collapsedText = (String)map.get("collapsedText");
        expandedText = (String)map.get("expandedText");
        expanded = (Boolean)map.get("expanded");
        selected = (Boolean)map.get("selected");
        if (map.containsKey("children")) {
            List<Filter> list = (List<Filter>) Mapper.valueFromPersisted( map.get( "children" ) );
            setChildren( list );
        }
    }

    // end Mappable

    public final synchronized Container getContainer() {
        if ( container == null ) {
            Filter p = getParent();
            container = p == null ? new UserScope()
                                  : new FilteredContainer( p.getContainer(), p, true );
        }

        return container;
    }

    public synchronized void setContainer( Container container ) {
        this.container = container;
    }

    public String toString() {
        return getText();
    }

    public Map beanProperties() {
        Map<String,Object> result = new HashMap<String,Object>();
        result.put( "collapsedText", getCollapsedText() );
        result.put( "expandedText", getExpandedText() );
        result.put( "expanded", isExpanded() );
        result.put( "selected", isSelected() );
        if ( childrenRequired() )
            result.put( "children", getChildren() );
        return result;
    }

    /**
     * @return true if children should be saved/persisted.
     */
    public boolean childrenRequired() {
        if ( children == null )
            return false;
        else if ( !isSelected() ) {
            for ( Filter f : getChildren() )
                if ( f.isSelected() )
                    return true;
            return false;
        } else
            return true;
    }

    //-------------------------
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
    }

    public boolean isForceSelected() {
        return isSelected();
    }

    public void setForceSelected( boolean selected ) {
        setSelected( selected );
        if ( children != null )
            for ( Filter kid : getChildren() )
                kid.setForceSelected( selected );

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
        return getChildren().isEmpty();
    }

    /**
     * Force recomputing of children on next access.
     */
    public synchronized void resetChildren() {
        if ( children != null )
            for ( Filter f : children ) {
                f.resetChildren();
            }
        children = null;
    }

    public final synchronized List<Filter> getChildren() {
        if ( children == null ) {
            List<Filter> list = createChildren();
            setChildren( list );
            for ( Filter f : list ) {
                f.setSelected( this.isSelected() );
            }
        }

        return children;
    }

    public final synchronized void setChildren( List<Filter> children ){
        this.children = children;
        for ( Filter f : children ) {
            f.setParent( this );
        }
    }

    /**
     * Returns the children of the receiver as an <code>Enumeration</code>.
     */
    public final Enumeration<Filter> children() {
        return new EnumerationAdaptor<Filter>( getChildren().iterator() );
    }
    /**
     * Returns the child <code>TreeNode</code> at index <code>childIndex</code>.
     */
    public final TreeNode getChildAt( int childIndex ) {
        return getChildren().get( childIndex );
    }

    /**
     * Returns the number of children <code>TreeNode</code>s the receiver contains.
     */
    public final int getChildCount() {
        return getChildren().size();
    }

    /**
     * Test if this filter applies to the given object.
     * @param object the object.
     * @return  true if the object should be removed from the results,
     * false if object does not apply
     */
    public boolean filter( Ref object ) {

        if ( !isSelected() && ( !isExpanded() || getChildCount() == 0 ) )
            return match( object );

        else if ( !isSelected() )
            for ( Filter f : getChildren() )
                if ( f.filter( object ) )
                    return true;

        return false;
    }

    //-------------------------
    // Abstractness...

    /**
     * Create the children of this filter given a container.
     * @return filters to add to this one
     */
    abstract protected List<Filter> createChildren();

    /**
     * Test if given object is a match for this filter.
     * @param object the object
     * @return true if the filter matches the object.
     */
    abstract public boolean match( Ref object );

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
