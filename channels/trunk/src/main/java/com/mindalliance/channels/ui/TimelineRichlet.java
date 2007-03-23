/*
 * Created on Jan 31, 2007
 *
 */
package com.mindalliance.channels.ui;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.zkforge.timeline.Bandinfo;
import org.zkforge.timeline.Timeline;
import org.zkforge.timeline.data.OccurEvent;
import org.zkoss.zk.ui.GenericRichlet;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Window;

public class TimelineRichlet extends GenericRichlet {

	public void service(Page page) {
		page.setTitle("Timeline test");
		Window w = new Window("Timeline", "normal", false);
		final Timeline timeline = new Timeline();
		final Bandinfo b1 = new Bandinfo();
		b1.setIntervalUnit("minute");
		final Bandinfo b2 = new Bandinfo();
		b2.setIntervalUnit("hour");
		b2.setSyncWith(b1.getId());
		b2.setShowEventText(false);
		b2.setTrackHeight(0.5f);
		timeline.appendChild(b1);
		timeline.appendChild(b2);
		timeline.setHeight("300px");
		
		timeline.setParent(w);
		w.setPage(page);

		final Map<String, OccurEvent> events = new HashMap<String, OccurEvent>();
//		final OccurEvent e = new OccurEvent();
//		e.setText("BlahBlah");
//		e.setDescription("<a href='http://www.yahoo.com'>Yahoo</a>");
//		e.setStart(new Date());
//		b1.addOccurEvent(e);
//		b2.addOccurEvent(e);
//		events.put(e.getId(), e);
//		final OccurEvent e2 = new OccurEvent();
//		e2.setText("BlahBlah2");
//		e2.setDescription("<a href='http://www.google.com'>Google</a>");
//		e2.setStart(new Date());
//		events.put(e2.getId(), e2);
//		b1.addOccurEvent(e2);
//		b2.addOccurEvent(e2);
		Button button = new Button();
		button.setLabel("Add Event");

		button.addEventListener("onClick", new EventListener() {
			
			/* (non-Javadoc)
			 * @see org.zkoss.zk.ui.event.EventListener#isAsap()
			 */
			public boolean isAsap() {
				// TODO Auto-generated method stub
				return false;
			}

			/* (non-Javadoc)
			 * @see org.zkoss.zk.ui.event.EventListener#onEvent(org.zkoss.zk.ui.event.Event)
			 */
			public void onEvent(Event arg0) {

				OccurEvent event = new OccurEvent();
				event.setText("New Event");
				event.setDescription("<a href='/channels/zk/test'>Mx Graph test</a>");
				event.setStart(new Date());
				event.setIconUrl("/channels/images/16x16/add2.png");
				event.setColor("#00FF00");
				b1.addOccurEvent(event);
				b2.addOccurEvent(event);
				events.put(event.getId(), event);
			}
			
		});
		button.setParent(w);
		
		button = new Button();
		button.setLabel("Delete Selected");
		button.addEventListener("onClick", new EventListener() {
			
			/* (non-Javadoc)
			 * @see org.zkoss.zk.ui.event.EventListener#isAsap()
			 */
			public boolean isAsap() {
				// TODO Auto-generated method stub
				return false;
			}

			/* (non-Javadoc)
			 * @see org.zkoss.zk.ui.event.EventListener#onEvent(org.zkoss.zk.ui.event.Event)
			 */
			public void onEvent(Event arg0) {
				String[] selected = timeline.getSelection();
				for (int inx = 0 ; inx < selected.length ; inx++) {
					b1.removeOccurEvent(events.get(selected[inx]));
					b2.removeOccurEvent(events.get(selected[inx]));
					events.remove(selected[inx]);
				}
				timeline.setSelection(new String[0]);
			}
			
		});
		button.setParent(w);
	}
	
}
