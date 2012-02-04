package com.mindalliance.channels.playbook.pages.filters;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.tree.Tree;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.PropertyModel;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

/** ... */
public class FilterTree extends Tree {

    private boolean singleSelect;
    private static final String FILTER_SELECTOR = "filter-selector"; // NON-NLS
    private static final long serialVersionUID = 8869116108143655099L;

    //--------------------------
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

    //--------------------------
    public void onFilterSelect( AjaxRequestTarget target, Filter filter ) {
    }

    public void onExpandCollapse( AjaxRequestTarget target, Filter filter ) {
    }

    public Filter getFilter() {
        return (Filter) getTreeModel().getRoot();
    }

    public void setFilter( Filter filter ) {
        getTreeModel().setRoot( filter );
        filter.setSingleSelect( isSingleSelect() );
    }

    @Override
    protected void populateTreeItem( WebMarkupContainer item, int level ) {
        super.populateTreeItem( item, level );
        final Filter f = (Filter) item.getDefaultModelObject();
        assert f.getChildCount() == 0 || f.getChildAt( 0 ).getParent() == f;
        String selector = isSingleSelect() ?
                          "uniqueSelection" : "forceSelected";
        if ( !isSingleSelect() || f.getChildCount() == 0 ) {
            item.add(
                    new FilterCheck(
                            FILTER_SELECTOR,
                            new PropertyModel<Boolean>( f, selector ) ) {
                        private static final long serialVersionUID = 1940582010196766765L;

                        @Override
                        public void onFilterSelect( AjaxRequestTarget target ) {
                            getTreeModel().reload();
                            updateTree( target );
                            FilterTree.this.onFilterSelect( target, f );
                        }
                    } );
        } else
            item.add( new WebMarkupContainer( FILTER_SELECTOR ) );

        if ( f.isExpanded() )
            getTreeState().expandNode( f );
    }

    @Override
    protected MarkupContainer newNodeLink(
            MarkupContainer parent, String id, TreeNode node ) {

        MarkupContainer markupContainer = new WebMarkupContainer( id );
        markupContainer.setRenderBodyOnly( true );
        return markupContainer;
    }

    @Override
    protected void onJunctionLinkClicked(
            AjaxRequestTarget target, TreeNode node ) {

        super.onJunctionLinkClicked( target, node );
        Filter f = (Filter) node;
        f.setExpanded( getTreeState().isNodeExpanded( node ) );
        getTreeModel().nodeStructureChanged( node );
        invalidateAll();
        updateTree( target );
        onExpandCollapse( target, f );
    }

    private DefaultTreeModel getTreeModel() {
        return (DefaultTreeModel) getDefaultModelObject();
    }

    public boolean isSingleSelect() {
        return singleSelect;
    }

    public void setSingleSelect( boolean singleSelect ) {
        this.singleSelect = singleSelect;
    }
}