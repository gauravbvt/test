package com.mindalliance.channels.playbook.pages.filters;

/**
 * Accept anything of the given type.
 */
public class ClassFilter extends Filter {

    private Class<?> type;

    public ClassFilter( Class<?> type, String collapsed, String expanded ) {
        super( collapsed, expanded );
        this.type = type;
    }

    protected boolean localMatch( Object object ) {
        return type.isAssignableFrom( object.getClass() );
    }

    public Class<?> getType() {
        return type;
    }
}
