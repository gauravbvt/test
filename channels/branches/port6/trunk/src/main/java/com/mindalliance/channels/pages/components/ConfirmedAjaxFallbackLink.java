package com.mindalliance.channels.pages.components;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.model.IModel;

/**
 * AjaxLink which execution is conditional to being confirmed via dialog.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Sep 2, 2009
 * Time: 12:55:46 PM
 */
abstract public class ConfirmedAjaxFallbackLink<T> extends AjaxLink<T> {
    /**
     * Confirmation message.
     */
    private String message;

    public ConfirmedAjaxFallbackLink( String s, String message ) {
        super( s );
        this.message = message;
    }

    public ConfirmedAjaxFallbackLink( String s, IModel<T> iModel, String message ) {
        super( s, iModel );
        this.message = message;
    }

    protected void updateAjaxAttributes( AjaxRequestAttributes attributes) {
        super.updateAjaxAttributes( attributes );
        AjaxCallListener myAjaxCallListener = new AjaxCallListener() {
            @Override
            public CharSequence getPrecondition( Component component ) {
                String prompt = message.replaceAll( "'", "\\\\'" );
                return "if (confirm('" + prompt + "')) {return true;} else {return false;};";
            }

        };
        attributes.getAjaxCallListeners().add(myAjaxCallListener);
    }

 /*   protected IAjaxCallDecorator getAjaxCallDecorator() {      // Wicket 1.5.*
        if ( message == null ) {
            return super.getAjaxCallDecorator();
        } else {
            return new AjaxCallDecorator() {
                public CharSequence decorateScript( Component component, CharSequence script ) {
                    String prompt = message.replaceAll( "'", "\\\\'" );
                    return "if (confirm('" + prompt + "')) {" + script + "} else {return false;};";
                }
            };
        }
    }*/
}
