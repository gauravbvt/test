package com.mindalliance.channels.playbook.pages.filters;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.extensions.markup.html.tree.Tree;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.PropertyModel;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

/**
 * ...
 */
public class FilterTree extends Tree {

    public FilterTree( String id, TreeModel model ) {
        super( id, model );
    }

    @Override
    protected String renderNode( TreeNode node ) {
        Filter f = (Filter) node;
        return f.getText();
    }

    @Override
    protected void populateTreeItem( final WebMarkupContainer item, int level ) {
        super.populateTreeItem( item, level );
        final AjaxCheckBox checkBox = new AjaxCheckBox( "filter-check",
            new PropertyModel( item.getModelObject(), "selected" ) ){

            protected void onSelectionChanged( Object newSelection ) {
                getModel().setObject( newSelection );
                Filter f = (Filter) item.getModelObject();
                // TODO fix the following
                if ( f.isExpanded() )
                    getTreeState().expandNode(  f );
                else
                    getTreeState().collapseNode( f );
            }

            protected boolean wantOnSelectionChangedNotifications() {
                return true;
            }

            protected void onUpdate( AjaxRequestTarget target ) {
                target.addComponent( FilterTree.this );
            }
        };
        checkBox.setOutputMarkupId( true );

        item.add( checkBox );
    }

    protected MarkupContainer newNodeLink( MarkupContainer parent, String id, TreeNode node ) {
        return new WebMarkupContainer( id );
    }

    protected void onJunctionLinkClicked( AjaxRequestTarget target, TreeNode node ) {
        super.onJunctionLinkClicked( target, node );
        Filter f = (Filter) node;
        f.setExpanded( getTreeState().isNodeExpanded( node ) );
    }
}
