/*
 * Created on Jan 31, 2007
 *
 */
package com.mindalliance.channels.ui;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.zkforge.timeline.Bandinfo;
import org.zkforge.timeline.Timeline;
import org.zkforge.timeline.data.OccurEvent;
import org.zkforge.timeline.event.SelectEvent;
import org.zkoss.zk.ui.GenericRichlet;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Window;

import com.mindalliance.zk.mxgraph.MxCompactTreeLayout;
import com.mindalliance.zk.mxgraph.MxGraph;
import com.mindalliance.zk.mxgraph.MxPanningHandler;

public class TimelineRichlet extends GenericRichlet {

    public void service(Page page) {
        page.setTitle("Timeline test");

        final ScenarioTimeline timeline = new ScenarioTimeline(300,page,null);
        Window w = new Window("Timeline", "normal", false);
//        TimeZone timeZone = TimeZone.getTimeZone("EDT");
//        Calendar date = Calendar.getInstance();
//        date.setTimeZone(timeZone);
//        date.set(2007, 5, 5, 13, 0);
//        final Bandinfo b1 = new Bandinfo();
//        b1.setIntervalUnit("minute");
//        b1.setWidth("50%");
//        final Bandinfo b2 = new Bandinfo();
//        b2.setIntervalUnit("hour");
//        b2.setSyncWith(b1.getId());
//        b2.setShowEventText(false);
//        b2.setTrackHeight(0.5f);
//        b2.setWidth("50%");
//        timeline.appendChild(b1);
//        timeline.appendChild(b2);
        //timeline.setHeight("300px");
        timeline.addEventListener( "onSelectEvent", new EventListener() {
            public boolean isAsap() {
                return false;
            }

            public void onEvent( Event event ) {
                SelectEvent se = (SelectEvent) event;
                List<Object> data = (List<Object>)se.getData();
                for (Object obj : data) {
                    System.out.println(obj);
                }
            }
        } );
        timeline.setParent(w);
        w.setPage(page);

        Calendar date = Calendar.getInstance();
        timeline.populateTimeline( date.getTime() );
        //date.setTimeZone( timeZone );
        //date.set( 2007, 5, 8, 13, 0 );
        final OccurEvent e = new OccurEvent();
        e.setText("BlahBlah");
        e.setDescription("<a href='http://www.yahoo.com'>Yahoo</a>");
        e.setStart(date.getTime());
        e.setData("Event 1");
        timeline.addOccurEvent(e);
        final OccurEvent e2 = new OccurEvent();
        e2.setText("BlahBlah2");
        e2.setDescription("<a href='http://www.google.com'>Google</a>");
        e2.setStart(date.getTime());
        e2.setData("Event 2");
        timeline.addOccurEvent(e2);
        Button button = new Button();
        button.setLabel("Add Event");

        button.addEventListener("onClick", new EventListener() {
            public boolean isAsap() {
                // TODO Auto-generated method stub
                return false;
            }
            public void onEvent(Event arg0) {

                OccurEvent event = new OccurEvent();
                event.setText("New Event");
                event.setData("New Event");
                event.setDescription("<a href='/channels/zk/test'>Mx Graph test</a>");
                event.setStart(new Date());
                event.setIconUrl("/channels/images/16x16/add2.png");
                event.setColor("#00FF00");
                timeline.addOccurEvent(event);
            }

        });
        button.setParent(w);

        button = new Button();
        button.setLabel("Delete Selected");
        button.addEventListener("onClick", new EventListener() {
            public boolean isAsap() {
                return false;
            }
            public void onEvent(Event arg0) {
                String[] selected = timeline.getSelection();
                for (int inx = 0; inx < selected.length; inx++) {
                    timeline.removeOccurEvent(timeline.getOccurEvents().get(selected[inx]));
                }
                timeline.setSelection(new String[0]);
            }

        });
        button.setParent(w);

        MxGraph graph = new MxGraph();
        graph.setLayout(new MxCompactTreeLayout());
        graph.setWidth("800px");
        graph.setHeight("800px");
        graph.setProperty(MxGraph.AUTO_SIZE, "true", true);
        graph.setStyle("overflow:hidden; background:url('/channels/images/grid.gif');"); 
        // graph.setProperty(MxGraph.BACKGROUND_IMAGE,
        // "/channels/images/grid.gif", false);
        // graph.setProperty(MxGraph.AUTO_SIZE, true, false);
        graph.getPanningHandler().setProperty(
                MxPanningHandler.IS_SELECT_ON_POPUP, false, false);
        graph.getPanningHandler().setProperty(
                MxPanningHandler.IS_USE_SHIFT_KEY, true, false);
        graph.getPanningHandler().setProperty(MxPanningHandler.IS_PAN_ENABLED,
                true, false);
        graph.setParent(w);
        
//        ScenarioTimeline tl2 = new ScenarioTimeline(300, null);
//        tl2.setParent(w);
    }

}
