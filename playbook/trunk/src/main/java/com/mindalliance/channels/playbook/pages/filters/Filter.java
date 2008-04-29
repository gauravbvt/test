package com.mindalliance.channels.playbook.pages.filters;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.Mapper;
import com.mindalliance.channels.playbook.support.models.Container;
import com.mindalliance.channels.playbook.support.models.FilteredContainer;
import com.mindalliance.channels.playbook.support.persistence.Mappable;

import javax.swing.tree.TreeNode;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A node in the filter tree.
 */
abstract public class Filter implements Cloneable, TreeNode, Serializable, Mappable {

    private String collapsedText;
    private String expandedText;
    private transient Container container;

    private boolean expanded;
    private boolean selected;
    private boolean showingLeaves;
    private Filter parent;
    private boolean invalid;

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

    public Filter copy() {
        try {
            final Filter shallow = (Filter) super.clone();

            if ( children != null ) {
                List<Filter> cs = new ArrayList<Filter>();
                for ( Filter c : children )
                    cs.add( c.copy() );
                shallow.setChildren( cs );
            }

            return shallow;
        } catch ( CloneNotSupportedException e ) {
           throw new RuntimeException( e );
        }
    }

    public Filter getRoot() {
        if ( getParent() == null )
            return this;
        else
            return getParent().getRoot();
    }

    public void invalidate() {
        setInvalid( true );
        if ( children != null )
            for( Filter f : children )
                f.invalidate();

    }

    public boolean isInvalid() {
        return invalid;
    }

    public void setInvalid( boolean invalid ) {
        this.invalid = invalid;
    }

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

    public final synchronized Container getContainer() {
        if ( container == null ) {
            Filter p = getParent();
            Container pc = p == null ? new UserScope()
                                     : p.getContainer();
            container = new FilteredContainer( pc, this, true );
        }

        return container;
    }

    public synchronized void setContainer( Container container ) {
        this.container = container;
    }

    public String toString() {
        return getText();
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

    public boolean isShowingLeaves() {
        return showingLeaves;
    }

    public void setShowingLeaves( boolean showingLeaves ) {
        this.showingLeaves = showingLeaves;
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
            for ( Filter kid : getChildren() ) {
                assert( equals( kid.getParent() ) );
                kid.setForceSelected( selected );
            }

        if ( !selected && parent != null )
        {
            assert( parent.getChildren().contains( this ) );
            parent.childDeselected();
        }
    }

    public boolean isUniqueSelection() {
        return getChildCount() == 0 && isSelected();
    }

    public void setUniqueSelection( boolean selection ) {
        getRoot().setForceSelected( false );
        if ( getChildCount() == 0 )
            setSelected( selection );
        else {
            Filter first = (Filter) getChildAt( 0 );
            setExpanded( selection );
            first.setUniqueSelection( selection );
        }

    }

    /**
     * Called by a child when deselected.
     */
    protected void childDeselected() {
        this.selected = false;
        if ( getParent() != null )
            getParent().childDeselected();
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
        // TODO recompute children when invalide, while preserving selections

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
     * Select the first leaf node matching a ref.
     * @param ref the ref to match
     * @return true if a selection was done
     */
    public boolean selectFirstMatch( Ref ref ) {
        if ( match( ref ) ) {
            List<Filter> fs = getChildren();
            if ( fs.size() == 0 )
                return match( ref );
            else for( Filter kid : fs ) {
                if ( kid.selectFirstMatch( ref ) )
                    return true;
            }
        }
        return false;
    }

    /**
     * Simplify the tree. Enforces the following conditions:
     * <ol><li>If all simplified children are selected+collapsed, select+collapse this one and
     *         forget children.</li>
     *     <li>If all simplified children are deselected+collapsed, deselect+collapse this one and
     *         forget children.</li>
     *     <li>If any remaining child is selected, deselect+expand this one</li>
     *     <li>Otherwise, deselect+collapse this one</li>
     * </ol>
     */
    public synchronized void simplify() {
        if ( children != null && children.size() > 0 ) {
            boolean allDeselected = true;
            boolean allSelected = true;

            for ( Filter kid : children ) {
                kid.simplify();
                if ( kid.isSelected() )
                    allDeselected = false;
                else
                    allSelected = false;
            }

            if ( allDeselected || allSelected ) {
                resetChildren();
                setExpanded( false );
                setSelected( allSelected );

            } else {
                setExpanded( true );
                setSelected( false );
            }
        } else {
            setExpanded( false );
            // keep selection as is
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

    /**
     * Test if this filter would allow creation of objects
     * of the given class, without consideration of selection
     * and/or children.
     * @param c the class
     * @return  true if this filter allows this class
     */
    abstract protected boolean strictlyAllowsClass( Class<?> c );

    /**
     * Test if this filter would allow creation of objects
     * of the given class.
     * @param clazz the class
     * @return  true if this filter (or children) allows
     * this class
     */
    public boolean allowsClass( Class<?> clazz ) {
        if ( isSelected() )
            return strictlyAllowsClass( clazz );
        else for ( Filter f : getChildren() )
            if ( f.allowsClass( clazz ) )
                return true;

        return false;
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
