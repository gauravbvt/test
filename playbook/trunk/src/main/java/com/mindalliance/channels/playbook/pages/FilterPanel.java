package com.mindalliance.channels.playbook.pages;

import com.mindalliance.channels.playbook.ifm.Tab;
import com.mindalliance.channels.playbook.ifm.context.environment.Organization;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.pages.filters.FilterTree;
import com.mindalliance.channels.playbook.pages.filters.ParentFilter;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.models.ColumnProvider;
import com.mindalliance.channels.playbook.support.models.Container;
import org.apache.wicket.extensions.markup.html.tree.DefaultAbstractTree;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;

import javax.swing.tree.DefaultTreeModel;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * ...
 */
public class FilterPanel extends Panel {

    public FilterPanel( String id, IModel model ) {
        super( id, model );

        final FilterTree tree = new FilterTree( "filter-tree", new DefaultTreeModel( getRawData().getFilter() ) );
//        tree.getTreeState().setAllowSelectMultiple( true );
        tree.setLinkType( DefaultAbstractTree.LinkType.AJAX_FALLBACK );
        add( tree );
    }

    // TODO move somewhere else...
    protected void addOrgs( Filter parent, ColumnProvider cp, IDataProvider data ) {
        if ( !cp.includes( "parent" ) )
            parent.add( new ParentFilter() );
        else { // do it the hard way
            boolean nullParents = false;
            Set<Organization> parents = new TreeSet<Organization>(
                    new Comparator<Organization>(){
                        public int compare( Organization o1, Organization o2 ) {
                            return o1.getName().compareTo( o2.getName() );
                        }
                    } );
            for ( Iterator i=data.iterator(0,data.size()) ; i.hasNext() ; ) {
                Ref r = (Ref) i.next();
                Object obj = r.deref();
                if ( obj instanceof Organization ) {
                    Organization org = (Organization) obj;
                    if ( org.getParent() == null )
                        nullParents = true ;
                    else
                        parents.add( org );
                }
            }
        }
    }

    protected void addPersons( Filter parent, ColumnProvider cp, IDataProvider data ) {
    }

    public Container getFilteredData() {
        // TODO implement this
        return getRawData();
    }

    public Tab getRawData() {
        return (Tab) ((Ref) getModelObject() ).deref();
    }
}
