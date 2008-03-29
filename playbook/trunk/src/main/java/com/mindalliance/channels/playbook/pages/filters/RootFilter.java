package com.mindalliance.channels.playbook.pages.filters;

/**
 * ...
 */
public class RootFilter extends Filter {

    public RootFilter( Filter... children ) {
        super( "Show everything", "Show...", children );
    }

    /**
     * Test an object for inclusion, assuming either selected, collapsed or no children.
     *
     * @param object the object
     * @return true if the object satisfies this filter.
     */
    protected boolean localMatch( Object object ) {
        return isSelected();
    }
}
