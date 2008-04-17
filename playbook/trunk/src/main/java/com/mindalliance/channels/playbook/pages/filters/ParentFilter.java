package com.mindalliance.channels.playbook.pages.filters;

import com.mindalliance.channels.playbook.ifm.resources.Organization;
import com.mindalliance.channels.playbook.ref.Ref;

import java.text.MessageFormat;

/**
 * ...
 */
public class ParentFilter extends Filter {

    private Organization parentOrganization;
    private boolean direct;


    private ParentFilter( Organization org, boolean direct, String collapsed, String expanded ) {
        super( collapsed, expanded );
        this.parentOrganization = org;
        this.direct = direct;
    }

    private ParentFilter( Organization org, boolean direct, String text ) {
        this( org, direct, text, text );
    }

    public ParentFilter( Organization org, boolean direct ) {
        this( org, direct, getCollapsedText( org, direct ), getExpandedText( org, direct ) );
    }

    public ParentFilter() {
        this( null, true, "without parent" );
    }

    /**
     * Test an object for inclusion, assuming either selected, collapsed or no children.
     *
     * @param object the object
     * @return true if the object satisfies this filter.
     */
    protected boolean localMatch( Object object ) {
        if ( ! ( object instanceof Organization ) )
            return false ;

        Organization org = (Organization) object;
        Ref parentRef = org.getParent();
        if ( parentRef == null && parentOrganization == null )
            return true;
        // parentRef != null || parentOrg != null
        if ( parentOrganization != null ) {
            Ref po = parentOrganization.getReference();
            if ( po.equals( parentRef ) )
                return true;
            if ( !direct ) {
                while ( parentRef != null ) {
                    Organization p = (Organization) parentRef.deref();
                    parentRef = p.getParent();
                    if ( po.equals( parentRef ) )
                        return true;
                }
            }
        }

        return false;
    }

    static private String getCollapsedText( Organization org, boolean direct ) {
        return direct ? "directly"
                      : MessageFormat.format( "within {0}", org.getName() );
    }

    static private String getExpandedText( Organization org, boolean direct ) {
        return MessageFormat.format( "within {0}...", org.getName() );
    }

}
