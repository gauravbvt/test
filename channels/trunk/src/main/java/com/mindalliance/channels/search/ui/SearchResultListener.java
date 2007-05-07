package com.mindalliance.channels.search.ui;

import com.mindalliance.channels.search.SearchResult;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;

/**
 * A SearchResultListener focuses the desktop UI on a result when a user selects the result.
 * 
 * @author ebax
 *
 */

public class SearchResultListener implements EventListener {
	SearchResult result; // The search result.
	
	/**
	 * @constructor
	 * @param result the search result.
	 */
	public SearchResultListener(SearchResult result) {
		this.result = result;
	}
	
	public boolean isAsap() {
		return true;
	}

	/**
	 * Focus the desktop UI on the search result.
	 * 
	 * @param event button click to "Go" to this search result.
	 */
	public void onEvent(Event event) {
		// TODO -- Make the desktop GUI focus on the search result.
		System.out.println("Now focus the desktop UI on the search result:" + 
				"  guid -- " + result.getGuid() + 
				"  name -- " + result.getName() + 
				"  project -- " + result.getProject() + 
				"  project guid -- " + result.getProjectGuid());
	}
}
