package com.mindalliance.channels.playbook.pages.filters;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.Mapper;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Bogus filter on a single ref.
 * Used for showing leaves.
 */
public class RefFilter extends Filter {

    private Ref ref;
    private static final long serialVersionUID = 5435272200404756227L;

    public RefFilter() {
        ref = null;
    }

    public RefFilter( Ref ref ) {
        super( ref.deref().toString() );
        this.ref = ref;
    }

    @Override
    protected List<Filter> createChildren( boolean selectionState ) {
        return Collections.emptyList();
    }

    @Override
    public boolean isMatching( Ref object ) {
        return getRef().equals( object );
    }

    @Override
    protected boolean allowsClassLocally( Class<?> c ) {
        return false;
    }

    public synchronized Ref getRef() {
        return ref;
    }

    public synchronized void setRef( Ref ref ) {
        this.ref = ref;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> result = super.toMap();
        result.put( "ref", (Object) Mapper.toPersistedValue( getRef() ) );
        return result;
    }

    @Override
    public void initFromMap( Map<String, Object> map ) {
        super.initFromMap( map );
        setRef( (Ref) Mapper.valueFromPersisted( map.get( "ref" ) ) );
    }
}
