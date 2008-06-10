package com.mindalliance.channels.playbook.pages.filters;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.tree.Tree;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.PropertyModel;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

/**
 * ...
 */
public class FilterTree extends Tree {

    private boolean singleSelect;
    private Component oldTarget;

    protected FilterTree( String id ) {
        super( id );
        setOutputMarkupId( true );
    }

    public FilterTree( String id, Filter filter ) {
        this( id, filter, false );
    }

    public FilterTree( String id, Filter filter, boolean singleSelect ) {
        super( id, new DefaultTreeModel( filter ) );
        this.singleSelect = singleSelect;
        setOutputMarkupId( true );
    }

    public void onFilterSelect( AjaxRequestTarget target, Filter filter ){}
    public void onExpandCollapse( AjaxRequestTarget target, Filter filter ){}

    public Filter getFilter() {
        return (Filter) getTreeModel().getRoot();
    }

    public void setFilter( Filter filter ) {
        getTreeModel().setRoot( filter );
    }

    @Override
    protected String renderNode( TreeNode node ) {
        Filter f = (Filter) node;
        return f.getText();
    }

    @Override
    protected void populateTreeItem( final WebMarkupContainer item, int level ) {
        super.populateTreeItem( item, level );
        final Filter f = (Filter) item.getModelObject();
        assert( f.getChildCount() == 0 || f.getChildAt( 0 ).getParent() == f );
        String selector = isSingleSelect()? "uniqueSelection" : "forceSelected" ;
        if ( !isSingleSelect() || f.getChildCount() == 0 ) {
            item.add( new FilterCheck( "filter-selector", new PropertyModel( f, selector ) ) {
                synchronized public void onFilterSelect( AjaxRequestTarget target ) {
                    DefaultTreeModel tm = getTreeModel();
                    tm.nodeStructureChanged( f );

                    Filter parent = f.getParent();
                    while ( parent != null ) {
                        tm.nodeChanged( parent );
                        parent = parent.getParent();
                    }

                    // Make sure old selection (now unchecked) is refreshed in browser
                    if ( isSingleSelect() && oldTarget != null ) {
                        target.addComponent( oldTarget );
                    }
                    oldTarget = this.getCheckBox();

                    updateTree( target );

                    FilterTree.this.onFilterSelect( target, f );
                }
            } );
        }
        else
            item.add( new WebMarkupContainer( "filter-selector" ) );
        if ( f.isExpanded() )
            getTreeState().expandNode( f );
//        else
//            getTreeState().collapseNode( f );
    }

    protected MarkupContainer newNodeLink( MarkupContainer parent, String id, TreeNode node ) {
        final WebMarkupContainer markupContainer = new WebMarkupContainer( id );
        markupContainer.setRenderBodyOnly( true );
        return markupContainer;
    }

    protected void onJunctionLinkClicked( AjaxRequestTarget target, TreeNode node ) {
        super.onJunctionLinkClicked( target, node );
        Filter f = (Filter) node;
        f.setExpanded( getTreeState().isNodeExpanded( node ) );
        getTreeModel().nodeStructureChanged( node );
        updateTree( target );
        onExpandCollapse( target, f );
    }

    private DefaultTreeModel getTreeModel() {
        return (DefaultTreeModel) getModelObject();
    }

    public boolean isSingleSelect() {
        return singleSelect;
    }

    public void setSingleSelect( boolean singleSelect ) {
        this.singleSelect = singleSelect;
    }
}
