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
    private boolean orable = true;

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

    public Filter clone() throws CloneNotSupportedException {
        final Filter shallow = (Filter) super.clone();

        if ( children != null ) {
            List<Filter> cs = new ArrayList<Filter>();
            for ( Filter c : children )
                cs.add( c.clone() );
            shallow.setChildren( cs );
        }

        return shallow;
    }

    public boolean equals( Object o ) {
        if ( this == o )
            return true;
        if ( o == null || getClass() != o.getClass() )
            return false;

        Filter filter = (Filter) o;

        if ( !collapsedText.equals( filter.collapsedText ) )
            return false;
        if ( !expandedText.equals( filter.expandedText ) )
            return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = collapsedText.hashCode();
        result = 31 * result + expandedText.hashCode();
        return result;
    }

    public Filter getRoot() {
        if ( getParent() == null )
            return this;
        else
            return getParent().getRoot();
    }

    public void invalidate() {
        if ( !invalid ) {
            setInvalid( true );
            if ( ! ( getContainer() instanceof UserScope ) )
                getContainer().detach();
            if ( children != null )
                for( Filter f : children )
                    f.invalidate();
        }
    }

    public boolean isInvalid() {
        return invalid;
    }

    public void setInvalid( boolean invalid ) {
        this.invalid = invalid;
    }

    public Map<String,Object> toMap() {
        Map<String,Object> map = new HashMap<String,Object>();
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
            for ( Filter k : children )
                assert( equals( k.getParent() ) );
        }

        // Force a reevaluation on next display
        setInvalid( true );
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
        Filter o = (Filter) node;
        return children.indexOf( o );
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
            for ( Filter f : list ) {
                f.setSelected( this.isSelected() );
            }
            setChildren( list );
            setInvalid( false );
        } else if ( isInvalid() ) {
            // Recompute while preserving order and selections
            List<Filter> newChildren = createChildren();

            for ( Filter old : children ) {
                int i = newChildren.indexOf( old );
                if ( i >= 0 ) {
                    // set expansion and selection
                    Filter nk = newChildren.get( i );
                    nk.setSelected( old.isSelected() );
                    nk.setExpanded( old.isExpanded() );
                }
            }

            for ( Filter nk : newChildren ) {
                int i = children.indexOf( nk );
                if ( i < 0 ) {
                    nk.setForceSelected( isSelected() );
                    nk.setExpanded( false );
                }
            }

            setChildren( newChildren );
            setInvalid( false );
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
            if ( fs.size() == 0 ) {
                setSelected( true );
                return true;
            }
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
                if ( kid.isSelected() || kid.isExpanded() )
                    allDeselected = false;
                if ( !kid.isSelected() )
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

        if ( isSelected() )
            return match( object );

        // Do the equivalent of
        //  ( orable1 || ... || orableN )
        //   && ( andable1 || ... || andableM )

        if ( children != null ) {
            boolean orClauses = false;
            for ( Filter f : getChildren() )
                if ( f.isOrable() && f.filter( object ) ) {
                    orClauses = true;
                    break;
                }
            if ( orClauses ) {
                boolean andClauses = true;
                for ( Filter f : getChildren() )
                    if ( !f.isOrable() ) {
                        if ( f.filter( object ) )
                            return true;
                        andClauses = false;
                    }
                return andClauses;
            }
        }

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

    public boolean isOrable() {
        return orable;
    }

    public void setOrable( boolean orable ) {
        this.orable = orable;
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
