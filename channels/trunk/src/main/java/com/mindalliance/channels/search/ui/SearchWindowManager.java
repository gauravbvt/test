package com.mindalliance.channels.search.ui;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Window;
import org.zkoss.zul.Button;

import com.mindalliance.channels.ui.Prompter;

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
