package com.mindalliance.channels.playbook.pages.filters;

import com.mindalliance.channels.playbook.ifm.project.InOrganization;
import com.mindalliance.channels.playbook.ifm.project.resources.Organization;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ref.Referenceable;
import com.mindalliance.channels.playbook.support.models.Container;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

/**
 * ...
 */
public class OrganizationResourceFilters extends AbstractFilters {

    public OrganizationResourceFilters() {
    }

    @Override
    void addFilters( Container container, List<Filter> results ) {
        Collection<Organization> orgs = new TreeSet<Organization>(
            new Comparator<Organization>(){
                public int compare( Organization o1, Organization o2 ) {
                    return o1.getName().compareTo( o2.getName() );
                }
            } );
        for ( Ref ref: container ) {
            Referenceable object = ref.deref();
            if ( object != null && object instanceof InOrganization ) {
                InOrganization pe = (InOrganization) object;
                Ref orgRef = pe.getOrganization();
                Organization org = (Organization) orgRef.deref();
                if ( org != null )
                    orgs.add( org );
            }
        }

        if ( orgs.size() > 1 )
            for ( Organization org: orgs ) {
                if ( org.getParents().isEmpty() )
                    results.add( new OrganizationFilter( org.getReference() ) );
            }
    }
}
