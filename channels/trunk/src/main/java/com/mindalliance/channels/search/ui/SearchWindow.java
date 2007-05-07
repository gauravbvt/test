// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.search.ui;

import com.mindalliance.channels.search.SearchResult;

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

import java.util.*;

/**
 * A window for a user to perform a search and view the results.
 *
 * @author <a href="mailto:bax@mind-alliance.com">bax</a>
 * @version $Revision:$
 */
public final class SearchWindow extends Window {
    private Textbox queryText;
    private Vbox content;
    private Vbox resultBox;
    private int k = 0; // For testing only.
    private static int maxResults = 15; // Maximum number of search results to display.

    /**
     * Default constructor.
     *
     * @param title the title of the prompt dialog
     * @param prompt the text before the entry field.
     * @param defaultValue the default value in the entry field.
     * @throws InterruptedException if the dialog thread was interrupted
     */
    public SearchWindow()
        throws InterruptedException {
  
        super( "Search", "normal", false );
        
        queryText = new Textbox( "" );
        
        Button searchButton = new Button( "Search" );
        searchButton.addEventListener( "onClick", new EventListener() {
            public boolean isAsap() {
                return true;
            }

            public void onEvent( Event event ) {
            	System.out.println("search -- " + queryText.getValue());
            	search(queryText.getValue());
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
        query.appendChild( queryText );
        query.appendChild( searchButton );
        query.setWidth( "100%" );

        queryText = new Textbox( "" );

        content = new Vbox();
        content.appendChild( query );
        content.appendChild( dismissButton );

        appendChild( content );
        setWidth( "50%" );
        setPage( (Page) Executions.getCurrent().getDesktop()
                          .getPages().iterator().next() );
        doOverlapped();
    }

    /**
     * Perform a search and display the results.
     */
    private void search(String query) {
    	List<SearchResult> results = SearchResult.test(k++); // Searcher.search(query, maxResults);
    	
    	if ( resultBox != null ) content.removeChild(resultBox);
    	
    	resultBox = new Vbox();
    	
    	if (results.size()>0) {
    		for (SearchResult r: results) {
    			Button goButton = new Button( "Go" );
    			goButton.addEventListener( "onClick", new SearchResultListener(r) );
    	        /* goButton.addEventListener( "onClick", new EventListener() {
    	            public boolean isAsap() {
    	                return true;
    	            }

    	            public void onEvent( Event event ) {
    	            	System.out.println("go");
    	            }
    	        } ); */
    			
    			
    			Hbox line = new Hbox();
    			line.appendChild(new Text(r.getKind() + ": " + r.getName() + " in: " + r.getProject()));
    			line.appendChild(goButton);
    			resultBox.appendChild(line);
    		}
    	}
    	else {
    		resultBox.appendChild(new Text( "No results. Please try a different query." ));
    	}
    	
		content.appendChild(resultBox);
    }
}
