package com.mindalliance.channels.playbook.pages.filters;

import com.mindalliance.channels.playbook.ifm.project.InOrganization;
import com.mindalliance.channels.playbook.ifm.project.resources.Resource;
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
        Collection<Ref> orgRefs = new TreeSet<Ref>( new Comparator<Ref>(){
            public int compare( Ref o1, Ref o2 ) {
                Resource org1 = (Resource) o1.deref();
                Resource org2 = (Resource) o2.deref();
                return org1.getName().compareTo( org2.getName() );
            }
        } );
        for ( Ref ref: container ) {
            Referenceable object = ref.deref();
            if ( object != null && object instanceof InOrganization ) {
                InOrganization pe = (InOrganization) object;
                orgRefs.add( pe.getOrganization() );
            }
        }

        if ( orgRefs.size() > 1 )
            for ( Ref ref: orgRefs ) {
                results.add( new OrganizationFilter( ref  ) );
            }
    }
}
