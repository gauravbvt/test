// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.search.ui;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zkoss.zhtml.Text;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

import com.mindalliance.channels.search.SearchResult;

/**
 * A window for a user to perform a search and view the results.
 * @author <a href="mailto:bax@mind-alliance.com">bax</a>
 * @version $Revision:$
 */
public final class SearchWindow extends Window {

    private static Log logger = LogFactory.getLog( SearchWindow.class );

    private Textbox queryText;
    private Vbox content;
    private Vbox resultBox;

    /** For testing only. */
    private int k;

    /**
     * Default constructor.
     * @throws InterruptedException if the dialog thread was
     *             interrupted
     */
    public SearchWindow() throws InterruptedException {

        super( "Search", "normal", false );

        this.queryText = new Textbox( "" );

        Button searchButton = new Button( "Search" );
        searchButton.addEventListener( "onClick", new EventListener() {

            public boolean isAsap() {
                return true;
            }

            public void onEvent( Event event ) {
                logger.debug(
                    "search -- " + SearchWindow.this.queryText.getValue() );
                search( SearchWindow.this.queryText.getValue() );
                // setVisible( false );
            }
        } );

        Button dismissButton = new Button( "Dismiss" );
        dismissButton.addEventListener( "onClick", new EventListener() {

            public boolean isAsap() {
                return true;
            }

            public void onEvent( Event event ) {
                setVisible( false );
            }
        } );

        Hbox query = new Hbox();
        query.appendChild( this.queryText );
        query.appendChild( searchButton );
        query.setWidth( "100%" );

        this.queryText = new Textbox( "" );

        this.content = new Vbox();
        this.content.appendChild( query );
        this.content.appendChild( dismissButton );

        appendChild( this.content );
        setWidth( "50%" );
        setPage( (Page) Executions.getCurrent().getDesktop().getPages()
                .iterator().next() );
        doOverlapped();
    }

    /**
     * Perform a search and display the results.
     * @param query the query.
     */
    private void search( String query ) {
//      Searcher.search(query,  maxResults);
        List<SearchResult> results = SearchResult.test( this.k++ );

        if ( this.resultBox != null )
            this.content.removeChild( this.resultBox );

        this.resultBox = new Vbox();

        if ( results.size() > 0 )
            for ( SearchResult r : results ) {
                Button goButton = new Button( "Go" );
                goButton.addEventListener( "onClick",
                        new SearchResultListener( r ) );
                /*
                 * goButton.addEventListener( "onClick", new
                 * EventListener() { public boolean isAsap() { return
                 * true; } public void onEvent( Event event ) {
                 * System.out.println("go"); } } );
                 */

                Hbox line = new Hbox();
                line.appendChild( new Text( r.getKind() + ": " + r.getName()
                        + " in: " + r.getProject() ) );
                line.appendChild( goButton );
                this.resultBox.appendChild( line );
            }
        else
            this.resultBox.appendChild(
                new Text( "No results. Please try a different query." ) );

        this.content.appendChild( this.resultBox );
    }
}
