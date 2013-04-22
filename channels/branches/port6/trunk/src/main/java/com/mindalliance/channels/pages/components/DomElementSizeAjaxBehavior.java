package com.mindalliance.channels.pages.components;

import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.attributes.CallbackParameter;
import org.apache.wicket.markup.ComponentTag;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/22/13
 * Time: 12:48 PM
 */
public abstract class DomElementSizeAjaxBehavior extends AbstractDefaultAjaxBehavior {

    private final String domIdentifier;
    private final Boolean reducedToFit;

    public DomElementSizeAjaxBehavior( String domIdentifier, Boolean reducedToFit ) {
        this.domIdentifier = domIdentifier;
        this.reducedToFit = reducedToFit;
    }

    protected void onComponentTag( ComponentTag tag ) {
        super.onComponentTag( tag );
        // String script; // wicket 1.5.*
        CharSequence script;
        if ( reducedToFit == null || !reducedToFit ) {
                    /*script = "wicketAjaxGet('"
                            + getCallbackUrl(  )
                            + "&width='+$('" + domIdentifier + "').width()+'"
                            + "&height='+$('" + domIdentifier + "').height()";*/ // wicket 1.5.*
            CallbackParameter param1 = CallbackParameter.resolved("width", "$('" + domIdentifier + "').width()" );
            CallbackParameter param2 = CallbackParameter.resolved("height", "$('" + domIdentifier + "').height()" );
            script = getCallbackFunction( param1, param2 );
        } else {
                    /*script = "wicketAjaxGet('"
                            + getCallbackUrl(  )
                            + "'";*/ // wicket 1.5.*
            script = getCallbackFunction();
        }
                /*String onclick = ( "{" + generateCallbackScript( script ) + " return false;}" )  // wicket 1.5.*
                        .replaceAll( "&amp;", "&" );*/
        tag.put( "onclick", script );
        // tag.put( "onclick", onclick ); wicket 1.5.*
    }


}
