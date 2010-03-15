// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.
package com.mindalliance.mindpeer.pages;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;

/**
 * A page displaying the result of a query.
 * Also runs said query prior to displaying...
 */
public class SearchResultPage extends AbstractUserPage {

    /** The text that originated this query
     * (distinct from the value of the "query" textField in the top bar).
     */
    private String textQuery;

    /**
     * Create a new MindPeerPage instance.
     *
     * @param parameters the given parameters
     */
    public SearchResultPage( PageParameters parameters ) {
        super( parameters );
        setStatelessHint( true );

        textQuery = parameters.getString( "q" );
        init( textQuery );

    }

    /**
     * ...
     */
    public void init( String query ) {
        add( new Label( "query", query ) );
    }

    /**
     * Return the page's title.
     * @return the value of title
     */
    @Override
    public String getTitle() {
        return "Search Results";
    }

    /**
     * Return the page's selectedTopItem.
     * @return the value of selectedTopItem
     */
    @Override
    protected int getSelectedTopItem() {
        return -1;
    }
}
