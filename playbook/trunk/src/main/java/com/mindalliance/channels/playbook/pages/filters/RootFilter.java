package com.mindalliance.channels.playbook.pages.filters;

import com.mindalliance.channels.playbook.support.models.Container;

/**
 * ...
 */
public class RootFilter extends ClassFilter {

    public RootFilter( Container container ) {
        super( "Show everything", "Show...", Object.class, container );
        setSelected( true );
    }
}
