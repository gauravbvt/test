package com.mindalliance.channels.playbook.pages.filters;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ref.impl.BeanImpl;
import com.mindalliance.channels.playbook.support.models.Container;

import javax.swing.tree.TreeNode;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

/**
 * A node in the filter tree.
 */
abstract public class Filter extends BeanImpl implements TreeNode, Serializable {

    private String collapsedText;
    private String expandedText;
    private Container container;

    private boolean expanded;
    private boolean selected;
    private boolean invalid;
    private Filter parent;

    private List<Filter> children;

    //-------------------------
    protected Filter( String collapsedText, String expandedText, Container container ) {
        this.collapsedText = collapsedText;
        this.expandedText = expandedText;
        this.container = container;
    }

    public Filter( String text, Container container ) {
        this( text, text, container );
    }

    public final Container getContainer() {
        return container;
    }

    public final void getContainer( Container container ) {
        this.container = container;
    }

    public String toString() {
        return getText();
    }

    public List transientProperties() {
        List result = super.transientProperties();
        if ( !isExpanded() && isSelected() )
            result.add( "children" );

        return result;
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
        if ( children != null )
            for ( Filter kid : getChildren() )
                kid.setSelected( selected );

        if ( !selected && parent != null )
            parent.childDeselected();
    }

    public boolean isInvalid() {
        return invalid;
    }

    public void setInvalid( boolean invalid ) {
        this.invalid = invalid;
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

    private synchronized List<Filter> getChildren() {
        if ( children == null ) {
            children = createChildren();
            for ( Filter f : children ) {
                f.setParent( this );
                f.setSelected( this.isSelected() );
            }
        }
        return children;
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
     * Test an object for inclusion, assuming either selected, collapsed or no children.
     * @param object the object
     * @return true if the object satisfies this filter.
     */
    abstract public boolean match( Ref object );

    /**
     * Create the children of this filter given a container.
     * @return filters to add to this one
     */
    abstract protected List<Filter> createChildren();

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
