package com.mindalliance.channels.playbook.pages.filters;

import com.mindalliance.channels.playbook.ifm.resources.Organization;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.models.Container;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Generate filters appropriate for a collection of organizations.
 */
public class OrganizationFilters extends AbstractFilters {

    public OrganizationFilters() {
        super();
    }

    public List<AbstractFilter> getFilters( Container data ) {
        List<AbstractFilter> result = new ArrayList<AbstractFilter>();

        addParentFilters( result, data );

        return result;
    }

    private void addParentFilters( List<AbstractFilter> result, Container data ) {
        if ( data.getColumnProvider().includes( "parent" ) ) {
            // We have some parent orgs. Check if we can differentiate

            boolean nullParents = false;
            Map<Organization,Set<Organization>> parents = new TreeMap<Organization,Set<Organization>>(
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
                    final Ref parentRef = org.getParent();
                    if ( parentRef == null )
                        nullParents = true ;
                    else {
                        Organization parent = (Organization) parentRef.deref();

                        if ( parents.containsKey( parent ) )
                            parents.get( parent ).add( org );
                        else {
                            Set<Organization> kids = new HashSet<Organization>();
                            kids.add( org );
                            parents.put( parent, kids );
                        }
                    }
                }
            }

            int size = parents.size() + (nullParents ? 1 : 0) ;
            if ( size > 1 ) {
                // We have a discriminating factor
                // TODO add filters
            }


        }
        // else no parent orgs in this list...
    }

}
