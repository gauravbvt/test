/*	This program is distributed under Lesser GPL Version 2.1 in the hope that
 *	it will be useful, but WITHOUT ANY WARRANTY.
 */
package org.zkforge.timeline.event;

import java.util.Date;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;

public class BandScrollEvent extends Event {
	private String _name;

	private Date _min;

	private Date _max;

	private Component _band;

	public BandScrollEvent(String name, Component target, Date min, Date max) {
		super(name, target);
		// TODO Auto-generated constructor stub
		_min = min;
		_max = max;
		_band = target;
		_name = name;
	}

	public Component getBand() {
		return _band;
	}

	public void setBand(Component band) {
		this._band = band;
	}

	public Date getMax() {
		return _max;
	}

	public void setMax(Date max) {
		this._max = max;
	}

	public Date getMin() {
		return _min;
	}

	public void setMin(Date min) {
		this._min = min;
	}

}
