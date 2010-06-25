package com.mindalliance.channels.pages.components;

import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.ajax.calldecorator.AjaxCallDecorator;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.model.IModel;

/**
 * AjaxFallbackLink which execution is conditional to being confirmed via dialog.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Sep 2, 2009
 * Time: 12:55:46 PM
 */
abstract public class ConfirmedAjaxFallbackLink<T> extends AjaxFallbackLink<T> {
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

    /**
     * {@inheritDoc}
     */
    protected IAjaxCallDecorator getAjaxCallDecorator() {
        if ( message == null ) {
            return super.getAjaxCallDecorator();
        } else {
            return new AjaxCallDecorator() {
                public CharSequence decorateScript( CharSequence script ) {
                    String prompt = message.replaceAll( "'", "\\\\'" );
                    return "if (confirm('" + prompt + "')) {" + script + "} else {return false;};";
                }
            };
        }
    }
}
