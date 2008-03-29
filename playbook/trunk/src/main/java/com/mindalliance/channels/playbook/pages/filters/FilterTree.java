package com.mindalliance.channels.playbook.pages.filters;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

import java.util.List;

/**
 * ...
 */
public class FilterTree extends Panel {

    public FilterTree( String id, List list ) {
        super( id );

        add( new ListView( "treeOrLeaf", list ){
            protected void populateItem( ListItem item ) {
                Object o = item.getModelObject();
                if ( o instanceof List ) {
                    WebMarkupContainer leaf = new WebMarkupContainer( "leaf" );
                        leaf.add( new WebMarkupContainer( "tree-label" ) );
                        leaf.setVisible( false );
                        item.add( leaf );
                    item.add( new FilterTree( "tree", (List) o ) );
                } else {
                    WebMarkupContainer leaf = new WebMarkupContainer( "leaf" );
                        leaf.add( new Label( "tree-label", o.toString() ));
                        item.add( leaf );
                    final FilterTree tree = new FilterTree( "tree", null );
                    tree.setVisible( false );
                    item.add( tree );
                }
            }
        } );
    }
}
