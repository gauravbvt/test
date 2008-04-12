package com.mindalliance.channels.playbook.pages;

import com.mindalliance.channels.playbook.ifm.context.environment.Organization;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.pages.filters.FilterTree;
import com.mindalliance.channels.playbook.pages.filters.ParentFilter;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.models.ColumnProvider;
import com.mindalliance.channels.playbook.support.models.ContainerModel;
import org.apache.wicket.extensions.markup.html.tree.DefaultAbstractTree;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.data.IDataProvider;

import javax.swing.tree.DefaultTreeModel;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * ...
 */
public class FilterPanel extends Panel {

    private ContainerModel rawData;

    public FilterPanel( String id, ContainerModel rawData ) {
        super( id );
        this.rawData = rawData;

        final FilterTree tree = new FilterTree( "filter-tree", new DefaultTreeModel( rawData.getFilter() ) );
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

    public ContainerModel getFilteredData() {
        // TODO implement this
        return getRawData();
    }

    public ContainerModel getRawData() {
        return rawData;
    }
}
