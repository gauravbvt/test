package com.mindalliance.channels.playbook.pages.filters;

import com.mindalliance.channels.playbook.ifm.project.resources.Organization;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.models.Container;
import com.mindalliance.channels.playbook.support.models.ContainerSummary;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Collection;

/**
 * Generate filters appropriate for a collection of organizations.
 */
public class OrganizationFilters extends AbstractFilters {

    public OrganizationFilters() {
    }

    @Override
    public void addFilters( Container container, List<Filter> results ) {
        addParentFilters( results, container );
    }

    private static void addParentFilters(
            Collection<Filter> result, Container data ) {

        ContainerSummary summary = data.getSummary();
        if ( summary.contains( "Parent" ) ) {
            // We have some parent orgs. Check if we can differentiate

            boolean nullParents = false;
            Set<Organization> parents = new TreeSet<Organization>(
                    new Comparator<Organization>(){
                        public int compare( Organization o1, Organization o2 ) {
                            return o1.getName().compareTo( o2.getName() );
                        }
                    } );
            for ( Ref r : data ) {
                Object obj = r.deref();
                if ( obj instanceof Organization ) {
                    Organization org = (Organization) obj;
                    Ref parentRef = org.getParent();
                    if ( parentRef == null )
                        nullParents = true ;
                    else {
                        Organization parent = (Organization) parentRef.deref();
                        if ( parent.getParent() == null )
                            parents.add( parent );
                    }
                }
            }
            int size = parents.size() + (nullParents ? 1 : 0) ;
            if ( size > 1 ) {
                if ( nullParents )
                    result.add( new OrganizationParentFilter() );
                for ( Organization parent: parents ) {
                    // We have a discriminating factor
                    result.add( new OrganizationParentFilter( parent ) );
                }
            }
        }
        // else no parent orgs in this list...
    }
}
