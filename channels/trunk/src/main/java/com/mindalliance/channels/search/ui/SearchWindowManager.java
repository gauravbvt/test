package com.mindalliance.channels.search.ui;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;

public class SearchWindowManager implements EventListener {

	public SearchWindowManager() {
		System.out.println("constructing SearchWindowManager");
	}
	
	public boolean isAsap() {
		return true;
	}

	public void onEvent(Event event) {
		System.out.println("search event %%^|||^%% in SearchWindowManager.onEvent");
		try {new SearchWindow();} catch (Exception trouble) {trouble.printStackTrace();}
	}
}
