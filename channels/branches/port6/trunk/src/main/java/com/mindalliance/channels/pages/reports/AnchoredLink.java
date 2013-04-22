// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.core.model.Identifiable;
import org.apache.wicket.Page;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/** A normal bookmarkable link with a specified anchor. */
public class AnchoredLink<T extends Page> extends BookmarkablePageLink<T> {

    private Object anchor;

    public AnchoredLink(
        String id, Class<T> pageClass, PageParameters parameters, Identifiable anchor ) {

        this( id, pageClass, parameters, anchor.getId() );
    }

    public AnchoredLink( String id, Class<T> pageClass, PageParameters parameters, Object anchor ) {
        super( id, pageClass, parameters );

        if ( anchor == null )
            throw new IllegalArgumentException();

        this.anchor = anchor;
    }

    @Override
    protected CharSequence appendAnchor( ComponentTag tag, CharSequence url ) {
        return url + "#" + anchor.toString();
    }
}
