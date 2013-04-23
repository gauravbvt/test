package com.mindalliance.channels.pages.components.diagrams;

import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.markup.ComponentTag;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/22/13
 * Time: 2:30 PM
 */
public abstract class AbstractDiagramAjaxBehavior extends AbstractDefaultAjaxBehavior {

    private final String domIdentifier;
    private final boolean reducedToFit;

    public AbstractDiagramAjaxBehavior( String domIdentifier, boolean reducedToFit ) {
        this.domIdentifier = domIdentifier;
        this.reducedToFit = reducedToFit;
    }

    protected void onComponentTag( ComponentTag tag ) {
        super.onComponentTag( tag );
        String script;
        if ( !reducedToFit ) {
            script = "wicketAjaxGet('"
                    + getCallbackUrl(  )
                    + "&width='+$('" + domIdentifier + "').width()+'"
                    + "&height='+$('" + domIdentifier + "').height()";
        } else {
            script = "wicketAjaxGet('"
                    + getCallbackUrl(  )
                    + "'";
        }
        String onclick = ( "{" + generateCallbackScript( script ) + " return false;}" )
                .replaceAll( "&amp;", "&" );
        tag.put( "onclick", onclick );
    }

}
