package com.mindalliance.channels.playbook.pages.filters;

import com.mindalliance.channels.playbook.ifm.project.InOrganization;
import com.mindalliance.channels.playbook.ifm.project.resources.Organization;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ref.Referenceable;
import com.mindalliance.channels.playbook.support.Mapper;
import com.mindalliance.channels.playbook.support.models.FilteredContainer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * Filter organization resources for a given organization
 */
public class OrganizationFilter extends Filter {

    private static final long serialVersionUID = 294900729443889466L;
    private Ref organization;
    private boolean exact;

    public OrganizationFilter() {
    }

    public OrganizationFilter( Ref organization ) {
        super( "... within " + getOrganization( organization ) );
        setInclusion( true );
        this.organization = organization;
    }

    public OrganizationFilter( Ref organization, boolean exact ) {
        this( organization );
        this.exact = exact ;
        if ( exact )
            setCollapsedText( "directly" );
    }

    static Organization getOrganization( Ref ref ) {
        Organization result = (Organization) ref.deref();
        ref.detach();
        return result;
    }

    @Override
    protected List<Filter> createChildren( boolean selectionState ) {
        List<Filter> result = new ArrayList<Filter>();
        if ( !isExact() ) {
            Collection<Organization> directSubs = new TreeSet<Organization>(
                    new Comparator<Organization>(){
                        public int compare( Organization o1, Organization o2 ) {
                            return o1.getName().compareTo( o2.getName() );
                        }
                    } );
            boolean hasDirect = false;
            for ( Ref ref: new FilteredContainer(
                        getContainer(), this, true ) ) {

                InOrganization object = (InOrganization) ref.deref();
                if ( object != null ) {
                    Ref orgRef = object.getOrganization();
                    Organization org = getOrganization( orgRef );
                    if ( org != null ) {
                        if ( organization.equals( orgRef ) )
                            hasDirect = true;
                        else if ( org.getParents().contains( organization ) )
                            directSubs.add( org );
                    }
                }
            }

            int count = directSubs.size() + ( hasDirect? 1 : 0 );
            if ( count > 1 ) {
                if ( hasDirect ) {
                    OrganizationFilter direct =
                            new OrganizationFilter( organization, true );
                    direct.setSelected( selectionState );
                    direct.setInclusion( false );
                    result.add( direct );
                }
                for ( Organization sub: directSubs ) {
                    OrganizationFilter subParent =
                            new OrganizationFilter( sub.getReference() );
                    subParent.setSelected( selectionState );
                    subParent.setInclusion( false );
                    result.add( subParent );
                }
            }
        }

        return result;
    }

    @Override
    public boolean isMatching( Ref object ) {
        Referenceable o = object.deref();
        if ( o != null && o instanceof InOrganization ) {
            InOrganization io = (InOrganization) o;
            Ref orgRef = io.getOrganization();
            Organization org = (Organization) orgRef.deref();
            if ( exact )
                return organization.equals( orgRef );
            else
                return organization.equals( orgRef )
                    || ( org != null && org.isPartOf( organization ) );
        }
        return false;
    }

    @Override
    protected boolean allowsClassLocally( Class<?> c ) {
        return InOrganization.class.isAssignableFrom( c );
    }

    public Ref getOrganization() {
        return organization;
    }

    public void setOrganization( Ref organization ) {
        this.organization = organization;
    }

    @Override
    public Map<String,Object> toMap() {
        Map<String,Object> map = super.toMap();
        map.put("organization", (Object) Mapper.toPersistedValue( organization ));
        map.put("exact", Boolean.valueOf( exact ) );
        return map;
    }

    @Override
    public void initFromMap(Map<String,Object> map) {
        organization = (Ref)Mapper.valueFromPersisted( map.get( "organization" ));
        exact = (Boolean) map.get( "exact" );
        super.initFromMap(map);
    }

    public boolean isExact() {
        return exact;
    }
}
