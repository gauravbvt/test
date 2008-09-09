package com.mindalliance.channels.playbook.pages.filters;

import com.mindalliance.channels.playbook.ifm.project.resources.Organization;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ref.Referenceable;
import com.mindalliance.channels.playbook.support.Mapper;
import com.mindalliance.channels.playbook.support.models.FilteredContainer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * ...
 */
public class OrganizationParentFilter extends Filter {

    private static final long serialVersionUID = 6854385115240171389L;
    private Ref parent;
    private boolean exact;

    public OrganizationParentFilter() {
        super( "... without parent" );
        parent = null;
    }

    public OrganizationParentFilter( Organization parent ) {
        super( "... within " + parent );
        this.parent = parent.getReference();
        assert( this.parent != null );
    }

    public OrganizationParentFilter( Organization parent, boolean exact ) {
        this( parent );
        setExact( exact );
    }

    @Override
    protected List<Filter> createChildren( boolean selectionState ) {
        List<Filter> result = new ArrayList<Filter>();
        if ( !isExact() ) {
            Set<Organization> directSubs = new TreeSet<Organization>(
                    new Comparator<Organization>(){
                        public int compare( Organization o1, Organization o2 ) {
                            return o1.getName().compareTo( o2.getName() );
                        }
                    } );

            for ( Ref ref: new FilteredContainer(
                        getContainer(), this, true ) ) {

                Organization org = (Organization) ref.deref();
                if ( org != null &&  parent != null
                     && org.getParents().contains( parent )
                     && !org.getSubOrganizations().isEmpty() ) {
                        directSubs.add( org );
                }
            }

            if ( !directSubs.isEmpty() ) {
                OrganizationParentFilter direct = new OrganizationParentFilter(
                        (Organization) parent.deref(), true );
                direct.setSelected( selectionState );
                result.add( direct );
                for ( Organization sub: directSubs ) {
                    OrganizationParentFilter subParent =
                            new OrganizationParentFilter( sub );
                    subParent.setSelected( selectionState );
                    result.add( subParent );
                }
            }
        }

        return result;
    }

    @Override
    public boolean isMatching( Ref object ) {
        Referenceable org = object.deref();
        if ( org != null && org instanceof Organization ) {
            Organization organization = (Organization) org;
            if ( isExact() )
                return parent != null
                       && organization.getParents().contains( parent );
            else
                return organization.isPartOf( parent );
        }
        return false;
    }

    @Override
    protected boolean allowsClassLocally( Class<?> c ) {
        return Organization.class.isAssignableFrom( c );
    }

    @Override
    public Map<String,Object> toMap() {
        Map<String,Object> map = super.toMap();
        map.put("parent", (Object) Mapper.toPersistedValue( parent ));
        map.put("exact", Boolean.valueOf( exact ) );
        return map;
    }

    @Override
    public void initFromMap(Map<String,Object> map) {
        parent = (Ref) Mapper.valueFromPersisted( map.get( "parent" ));
        exact = (Boolean) map.get( "exact" );
        super.initFromMap(map);
    }

    public boolean isExact() {
        return exact;
    }

    public void setExact( boolean exact ) {
        this.exact = exact;
        if ( exact ) {
            setCollapsedText( "directly" );
            setExpandedText( "directly" );
        }
    }
}
