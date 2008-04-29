package com.mindalliance.channels.playbook.pages.filters;

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

    protected FilterTree( String id ) {
        super( id );
    }

    public FilterTree( String id, Filter filter ) {
        this( id, filter, false );
    }

    public FilterTree( String id, Filter filter, boolean singleSelect ) {
        super( id, new DefaultTreeModel( filter ) );
        this.singleSelect = singleSelect;

        if ( singleSelect )
            filter.setUniqueSelection( filter.isSelected() );
    }

    public void onFilterSelect( AjaxRequestTarget target, Filter filter ){}
    public void onExpandCollapse( AjaxRequestTarget target, Filter filter ){}

    public Filter getFilter() {
        DefaultTreeModel tm = (DefaultTreeModel) getModelObject();
        return (Filter) tm.getRoot();
    }

    public void setFilter( Filter filter ) {
        DefaultTreeModel tm = (DefaultTreeModel) getModelObject();
        tm.setRoot( filter );
    }

    @Override
    protected String renderNode( TreeNode node ) {
        Filter f = (Filter) node;
        return f.getText();
    }

    @Override
    protected void populateTreeItem( final WebMarkupContainer item, int level ) {
        super.populateTreeItem( item, level );
        Filter f = (Filter) item.getModelObject();
        String selector = isSingleSelect()? "uniqueSelection" : "forceSelected" ;
        if ( !isSingleSelect() || f.getChildCount() == 0 )
            item.add( new FilterCheck( "filter-selector", new PropertyModel( f, selector ) ){
                public void onFilterSelect( AjaxRequestTarget target, Filter filter ) {
                    FilterTree.this.onFilterSelect( target, filter );
                }
            } );
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
        DefaultTreeModel model = (DefaultTreeModel) getModelObject();
        model.nodeStructureChanged( node );
        updateTree( target );
        onExpandCollapse( target, f );
    }

    public boolean isSingleSelect() {
        return singleSelect;
    }

    public void setSingleSelect( boolean singleSelect ) {
        this.singleSelect = singleSelect;
    }
}
