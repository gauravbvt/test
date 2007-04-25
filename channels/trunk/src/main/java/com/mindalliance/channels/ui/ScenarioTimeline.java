/**
 * 
 */
package com.mindalliance.channels.ui;

import org.zkforge.timeline.Bandinfo;
import org.zkforge.timeline.Timeline;

import com.mindalliance.channels.model.Scenario;

/**
 * A timeline view of a scenario.  Displays when events/tasks/etc... in a particular scenario occur.  
 * Selecting elements in this view will trigger a selection event. 
 *
 */
public class ScenarioTimeline extends Timeline {
	private Scenario scenario;
	
	public ScenarioTimeline(Scenario scenario) {
		setScenario(scenario);
		Bandinfo b1 = new Bandinfo();
		b1.setIntervalUnit("minute");
		Bandinfo b2 = new Bandinfo();
		b2.setIntervalUnit("hour");
		b2.setSyncWith(b1.getId());
		b2.setShowEventText(false);
		b2.setTrackHeight(0.5f);
		appendChild(b1);
		appendChild(b2);
		//setHeight("300px");
	}
	
	public void setScenario(Scenario scenario) {
		this.scenario = scenario;
	}
}
