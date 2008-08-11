package com.mindalliance.channels.playbook.pages.filters;

import com.mindalliance.channels.playbook.ref.impl.ReferenceableImpl;
import com.mindalliance.channels.playbook.support.models.Container;

/**
 * ...
 */
public class RootFilter extends ClassFilter {

    private static final long serialVersionUID = 2376421333113630571L;

    public RootFilter() {
        this( null );
    }

    public RootFilter( Container container ) {
        this( container, true );
    }

    public RootFilter( Container container, boolean selected ) {
        super( "Show everything", "Show...", ReferenceableImpl.class );
        setSelected( selected );
        setContainer( container );
    }
}
