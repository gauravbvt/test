package com.mindalliance.channels.playbook.pages.filters;

import com.mindalliance.channels.playbook.ifm.project.InOrganization;
import com.mindalliance.channels.playbook.ifm.project.resources.Organization;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ref.Referenceable;
import com.mindalliance.channels.playbook.support.Mapper;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Filter organization resources for a given organization
 */
public class OrganizationFilter extends Filter {

    private static final long serialVersionUID = 294900729443889466L;
    private Ref organization;

    public OrganizationFilter() {
    }

    public OrganizationFilter( Ref organization ) {
        super( "... in organization " + getOrganization( organization ) );
        setInclusion( true );
        this.organization = organization;
    }

    static Organization getOrganization( Ref ref ) {
        Organization result = (Organization) ref.deref();
        ref.detach();
        return result;
    }

    @Override
    protected List<Filter> createChildren( boolean selectionState ) {
        return Collections.emptyList();
    }

    @Override
    public boolean isMatching( Ref object ) {
        Referenceable o = object.deref();
        if ( o != null && o instanceof InOrganization ) {
            InOrganization io = (InOrganization) o;
            return organization.equals( io.getOrganization() );
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
    public Map<String, Object> toMap() {
        Map<String, Object> map = super.toMap();
        map.put("organization", (Object) Mapper.toPersistedValue( organization ));
        return map;
    }

    @Override
    public void initFromMap(Map<String,Object> map) {
        organization = (Ref)Mapper.valueFromPersisted( map.get( "organization" ));
        super.initFromMap(map);
    }
}
