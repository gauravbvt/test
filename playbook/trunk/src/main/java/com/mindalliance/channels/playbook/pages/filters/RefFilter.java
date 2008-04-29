package com.mindalliance.channels.playbook.pages.filters;

import com.mindalliance.channels.playbook.ref.Ref;

import java.util.Collections;
import java.util.List;

/**
 * Bogus filter on a single ref.
 * Used for showing leaves.
 */
public class RefFilter extends Filter {

    private Ref ref;

    public RefFilter() {
    }

    public RefFilter( Ref ref ) {
        super( ref.deref().toString() );
        this.ref = ref;
    }

    protected List<Filter> createChildren() {
        return Collections.emptyList();
    }

    public boolean match( Ref object ) {
        return getRef().equals( object );
    }

    protected boolean strictlyAllowsClass( Class<?> c ) {
        return false;
    }

    public Ref getRef() {
        return ref;
    }

    public void setRef( Ref ref ) {
        this.ref = ref;
    }
}
