package com.mindalliance.channels.playbook.pages.filters;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.Mapper;
import com.mindalliance.channels.playbook.support.models.Container;
import com.mindalliance.channels.playbook.support.models.FilteredContainer;
import com.mindalliance.channels.playbook.support.persistence.Mappable;

import javax.swing.tree.TreeNode;
import java.io.IOException;
import java.io.Serializable;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Collection;

/** A node in the filter tree. */
public abstract class Filter
        implements Cloneable, TreeNode, Serializable, Mappable {

    private String collapsedText;
    private String expandedText;
    private List<Filter> children;
    private Filter parent;
    private transient Container container;
    private boolean expanded;
    private boolean selected;
    private boolean showingLeaves;
    private boolean invalid;
    private boolean inclusion;
    private boolean singleSelect;

    private static final String COLLAPSED_TEXT = "collapsedText";// NON-NLS
    private static final String EXPANDED_TEXT = "expandedText";// NON-NLS
    private static final String EXPANDED = "expanded";// NON-NLS
    private static final String SELECTED = "selected";// NON-NLS
    private static final String CHILDREN = "children";// NON-NLS
    private static final long serialVersionUID = -83012746444045817L;

    //-------------------------
    // Constructors
    protected Filter( String collapsedText, String expandedText ) {
        this.collapsedText = collapsedText;
        this.expandedText = expandedText;
        container = null;
        children = null;
        parent = null;
    }

    protected Filter( String text ) {
        this( text, text );
    }

    protected Filter() {
        this( "" );
    }

    @Override
    public final synchronized Filter clone() throws CloneNotSupportedException {
        Filter shallow = (Filter) super.clone();

        if ( children != null ) {
            List<Filter> cs = new ArrayList<Filter>();
            for ( Filter c : children )
                cs.add( c.clone() );
            shallow.setChildren( cs );
        }

        return shallow;
    }

    private void readObject( ObjectInputStream in )
            throws IOException, ClassNotFoundException {
        container = null;
        children = null;
        parent = null;
        in.defaultReadObject();
    }

    //-------------------------
    @Override
    public boolean equals( Object obj ) {
        if ( this == obj )
            return true;
        if ( obj == null || getClass() != obj.getClass() )
            return false;

        Filter filter = (Filter) obj;

        return getCollapsedText().equals( filter.getCollapsedText() )
               && getExpandedText().equals( filter.getExpandedText() );
    }

    @Override
    public int hashCode() {
        int result = getCollapsedText().hashCode();
        result = 31 * result + getExpandedText().hashCode();
        return result;
    }

    private Filter getRoot() {
        Filter p = getParent();
        return p == null ? this : p.getRoot();
    }

    public synchronized void invalidate() {
        if ( !invalid ) {
            invalid = true;
            if ( container != null && !( container instanceof UserScope ) )
                container.detach();

            if ( children != null )
                for ( Filter f : children )
                    f.invalidate();
        }
    }

    public synchronized Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put( Mappable.CLASS_NAME_KEY, getClass().getName() );
        map.put( COLLAPSED_TEXT, getCollapsedText() );
        map.put( EXPANDED_TEXT, getExpandedText() );
        map.put( EXPANDED, isExpanded() );
        map.put( SELECTED, isSelected() );
        if ( children != null )
            map.put( CHILDREN, (Object) Mapper.toPersistedValue( children ) );

        return map;
    }

    @SuppressWarnings( { "unchecked" } )
    public void initFromMap( Map<String, Object> map ) {
        setCollapsedText( (String) map.get( COLLAPSED_TEXT ) );
        setExpandedText( (String) map.get( EXPANDED_TEXT ) );
        setExpanded( (Boolean) map.get( EXPANDED ) );
        setSelected( (Boolean) map.get( SELECTED ) );
        if ( map.containsKey( CHILDREN ) )
            setChildren(
                    (List<Filter>) Mapper.valueFromPersisted(
                            map.get( CHILDREN ) ) );

        // Force a reevaluation on next display
        invalidate();
    }

    public final synchronized Container getContainer() {
        if ( container == null ) {
            Filter p = getParent();
            Container pc = p == null ? new UserScope() : p.getContainer();
            container = new FilteredContainer( pc, this, true );
        }

        return container;
    }

    public synchronized void setContainer( Container container ) {
        this.container = container;
    }

    @Override
    public String toString() {
        return getText();
    }

    //-------------------------
    String getCollapsedText() {
        return collapsedText;
    }

    String getExpandedText() {
        return expandedText;
    }

    void setCollapsedText( String collapsedText ) {
        this.collapsedText = collapsedText;
    }

    void setExpandedText( String expandedText ) {
        this.expandedText = expandedText;
    }

    public String getText() {
        return isExpanded() ? getExpandedText() : getCollapsedText();
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

    public synchronized void setForceSelected( boolean selected ) {
        setSelected( selected );

        if ( children != null )
            for ( Filter kid : getChildren() ) {
                assert equals( kid.getParent() );
                kid.setForceSelected( selected );
            }

        if ( parent != null ) {
            if ( !selected ) {
                assert parent.getChildren().contains( this );
                parent.childDeselected();
            }
            if ( isInclusion() ) {
                // Invalidate normal siblings
                for ( Filter sibling : parent.getChildren() )
                    if ( this != sibling && !sibling.isInclusion() )
                        sibling.invalidate();
            }
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

    /** Called by a child when deselected. */
    void childDeselected() {
        selected = false;
        if ( getParent() != null )
            getParent().childDeselected();
    }

    public final synchronized Filter getParent() {
        return parent;
    }

    private synchronized void setParent( Filter parent ) {
        this.parent = parent;
    }

    /**
     * Returns the index of <code>node</code> in the receivers children. If the
     * receiver does not contain <code>node</code>, -1 will be returned.
     */
    public final synchronized int getIndex( TreeNode node ) {
        return children == null ? -1 : children.indexOf( node );
    }

    /** Returns true if the receiver allows children. */
    public final boolean getAllowsChildren() {
        return true;
    }

    /** Returns true if the receiver is a leaf. */
    public final boolean isLeaf() {
        return getChildren().isEmpty();
    }

    /** Force recomputing of children on next access. */
    private synchronized void resetChildren() {
        if ( children != null )
            for ( Filter f : children ) {
                f.resetChildren();
            }
        children = null;
    }

    private synchronized List<Filter> getChildren() {
        if ( children == null )
            setChildren( createChildren( isSelected() ) );
        else if ( invalid ) {
            // Recompute while preserving order and selections
            List<Filter> newChildren = createChildren( isSelected() );

            for ( Filter oldKid : children ) {
                int i = newChildren.indexOf( oldKid );
                // Keep using old child to revalidate its selection and children
                if ( i >= 0 )
                    newChildren.set( i, oldKid );
            }

            for ( Filter newKid : newChildren ) {
                int i = children.indexOf( newKid );
                if ( i < 0 ) {
                    newKid.setForceSelected( isSelected() );
                    newKid.setExpanded( false );
                }
            }

            setChildren( newChildren );
        }

        invalid = false;
        return children;
    }

    private synchronized void setChildren( List<Filter> children ) {
        this.children = children;
        for ( Filter f : children ) {
            f.setParent( this );
        }
    }

    /**
     * Select the first leaf node matching a ref.
     *
     * @param ref the ref to match
     * @return true if a selection was done
     */
    public boolean hasSelected( Ref ref ) {
        if ( isMatching( ref ) ) {
            Collection<Filter> fs = getChildren();
            if ( fs.isEmpty() ) {
                setSelected( true );
                return true;
            } else
                for ( Filter kid : fs ) {
                    if ( kid.hasSelected( ref ) )
                        return true;
                }
        }
        return false;
    }

    /**
     * Simplify the tree. Enforces the following conditions:
     * <ol><li>If all simplified children are selected+collapsed,
     *      select+collapse this one and forget children.</li>
     * <li>If all simplified children are deselected+collapsed,
     *      deselect+collapse this one and forget children.</li>
     * <li>If any remaining child is selected,
     *      deselect+expand this one</li>
     * <li>Otherwise, deselect+collapse this one</li> </ol>
     */
    public synchronized void simplify() {
        if ( children == null || children.isEmpty() ) {
            // keep selection as is
            setExpanded( false );
        } else {
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
                if ( isSingleSelect() )
                    setExpanded( !allDeselected );
                else {
                    resetChildren();
                    setExpanded( false );
                }

                setSelected( allSelected );
            } else {
                setExpanded( true );
                setSelected( false );
            }
        }
    }

    /** Returns the children of the receiver as an <code>Enumeration</code>. */
    public final Enumeration<Filter> children() {
        return new EnumerationAdaptor<Filter>( getChildren().iterator() );
    }

    /** Returns the child <code>TreeNode</code> at index <code>childIndex</code>. */
    public final TreeNode getChildAt( int childIndex ) {
        return getChildren().get( childIndex );
    }

    /**
     * Returns the number of children <code>TreeNode</code>s the receiver
     * contains.
     */
    public final int getChildCount() {
        return getChildren().size();
    }

    /**
     * Test if this filter or subfilters applies to the given object.
     *
     * @param object the object.
     * @return true if the object should be removed from the results, false if
     *         object does not apply
     */
    public synchronized boolean isApplicableTo( Ref object ) {

        if ( isSelected() )
            return isMatching( object );

        // Do the equivalent of
        //  ( normal1 || ... || normalN )
        //   && ( inclusion1 || ... || inclusionM )

        if ( children != null ) {
            boolean hasNormal = false;
            for ( Filter f : getChildren() ) {
                if ( !f.isInclusion() && f.isApplicableTo( object ) ) {
                    hasNormal = true;
                    break;
                }
            }
            if ( hasNormal )
                return isIncluding( object );
        }

        return false;
    }

    /**
     * Test if an object satisfies at least one inclusion filter.
     *
     * @param object the object to test
     * @return true if a match or there are no inclusion filters
     */
    public synchronized boolean isIncluding( Ref object ) {
        boolean hasInclusion = false;
        if ( children != null )
            for ( Filter kid : children )
                if ( kid.isInclusion() ) {
                    hasInclusion = true;
                    if ( kid.isSelected() && kid.isMatching( object ) )
                        return true;
                }
        return !hasInclusion;
    }

    //-------------------------
    // Abstractness...

    /**
     * Create the children of this filter given a container.
     *
     * @param selectionState initial selection of children
     * @return filters to add to this one
     */
    protected abstract List<Filter> createChildren( boolean selectionState );

    /**
     * Test if given object is a direct match for this filter, without its
     * subfilters.
     *
     * @param object the object
     * @return true if the filter matches the object.
     */
    public abstract boolean isMatching( Ref object );

    /**
     * Test if this filter would allow creation of objects of the given class,
     * without consideration of selection and/or children.
     *
     * @param c the class
     * @return true if this filter allows this class
     */
    protected abstract boolean allowsClassLocally( Class<?> c );

    /**
     * Test if this filter would allow creation of objects of the given class.
     *
     * @param clazz the class
     * @return true if this filter (or children) allows this class
     */
    public boolean allowsClass( Class<?> clazz ) {
        if ( isSelected() )
            return allowsClassLocally( clazz );

        for ( Filter f : getChildren() )
            if ( f.allowsClass( clazz ) )
                return true;

        return false;
    }

    public boolean isInclusion() {
        return inclusion;
    }

    public void setInclusion( boolean inclusion ) {
        this.inclusion = inclusion;
    }

    public boolean isSingleSelect() {
        Filter p = getParent();
        return p == null ? singleSelect : p.isSingleSelect();
    }

    public void setSingleSelect( boolean singleSelect ) {
        this.singleSelect = singleSelect;
    }

    //===================================================
    static class EnumerationAdaptor<T extends Serializable>
            implements Enumeration<T> {

        private final Iterator<T> iterator;

        EnumerationAdaptor( Iterator<T> iterator ) {
            this.iterator = iterator;
        }

        /**
         * Tests if this enumeration contains more elements.
         *
         * @return <code>true</code> if and only if this enumeration object
         *         contains at least one more element to provide;
         *         <code>false</code> otherwise.
         */
        public boolean hasMoreElements() {
            return iterator.hasNext();
        }

        /**
         * Returns the next element of this enumeration if this enumeration
         * object has at least one more element to provide.
         *
         * @return the next element of this enumeration.
         *
         * @throws NoSuchElementException if no more elements exist.
         */
        public T nextElement() {
            return iterator.next();
        }
    }
}
