/*	This program is distributed under Lesser GPL Version 2.1 in the hope that
 *	it will be useful, but WITHOUT ANY WARRANTY.
 */
package org.zkforge.timeline;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import net.sf.json.JSONArray;

import org.zkforge.timeline.data.OccurEvent;
import org.zkforge.timeline.decorator.HighlightDecorator;
import org.zkforge.timeline.impl.TimelineComponent;
import org.zkforge.timeline.util.OccurEventComparator;
import org.zkforge.timeline.util.TimelineUtil;
import org.zkoss.lang.Objects;
import org.zkoss.xml.HTMLs;
import org.zkoss.zk.au.AuScript;
import org.zkoss.zk.au.Command;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;

/** The Bandinfo component.
 *
 * <p>See also <a href="http://simile.mit.edu/timeline">MIT Timeline</a>
 *
 * @author WeiXing Gu, China
 */
public class Bandinfo extends TimelineComponent {

	// private List decorators = new ArrayList();

	private OccurEventComparator oec = new OccurEventComparator();

	private String _local;

	private String _width = "70%";

	private String _intervalUnit = "month";

	private int _intervalPixels = 100;

	private boolean _highlight = true;

	private boolean _showEventText = true;

	private String _syncWith;

	private TimeZone _timeZone = TimeZone.getDefault();

	private float _trackHeight = (float) 1.5;

	private float _trackGap = (float) 0.5;

	private Date _date = new Date();

	private String _eventSourceUrl;

	// public String getOuterAttrs() {
	//
	// return "";
	// }

	// public String getContent() {
	//
	// return "";
	// }

	public String getInnerAttrs() {
		final String attrs = super.getInnerAttrs();
		final StringBuffer sb = new StringBuffer(64);
		if (attrs != null) {
			sb.append(attrs);
		}

		HTMLs.appendAttribute(sb, "z.pid", getParent().getUuid());
		HTMLs.appendAttribute(sb, "z.highlight", isHighlight());
		HTMLs.appendAttribute(sb, "z.width", getWidth());
		HTMLs.appendAttribute(sb, "z.intervalUnit", TimelineUtil
				.convertIntervalUnitFromName(_intervalUnit));
		HTMLs.appendAttribute(sb, "z.intervalPixels", getIntervalPixels());
		HTMLs.appendAttribute(sb, "z.showEventText", isShowEventText());
		HTMLs.appendAttribute(sb, "z.timeZone", _timeZone.getRawOffset()
				/ (1000 * 60 * 60));
		HTMLs.appendAttribute(sb, "z.trackHeight", String
				.valueOf(getTrackHeight()));
		HTMLs.appendAttribute(sb, "z.syncWith", findSyncWithIndex(_syncWith));
		HTMLs.appendAttribute(sb, "z.trackGap", String.valueOf(getTrackGap()));

		HTMLs.appendAttribute(sb, "z.date", TimelineUtil
				.formatDateTime(getDate()));
		HTMLs.appendAttribute(sb, "z.eventSourceUrl", _eventSourceUrl);
		return sb.toString();
	}

	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Timeline))
			throw new UiException("Unsupported parent for bandinfo: " + parent);
		super.setParent(parent);
	}

	/**
	 * @return the width
	 */
	public String getWidth() {
		return _width;
	}

	/**
	 * @param width
	 *            the width to set
	 */
	public void setWidth(String width) {
		if (!Objects.equals(_width, width)) {
			_width = width;
			// smartUpdate("z.width", _width);
			invalidate();
		}
	}

	/**
	 * @return the intervalPixels
	 */
	public int getIntervalPixels() {
		return _intervalPixels;
	}

	/**
	 * @param intervalPixels
	 *            the intervalPixels to set
	 */
	public void setIntervalPixels(int intervalPixels) {
		if (intervalPixels != _intervalPixels) {
			_intervalPixels = intervalPixels;
			// smartUpdate("z.intervalPixels", intervalPixels);
			invalidate();
		}
	}

	/**
	 * @return the intervalUnit
	 */
	public String getIntervalUnit() {
		return _intervalUnit;
	}

	/**
	 * @param intervalUnit
	 *            the intervalUnit to set
	 */
	public void setIntervalUnit(String intervalUnit) {
		if (!Objects.equals(intervalUnit, _intervalUnit)) {
			_intervalUnit = intervalUnit;
			// smartUpdate("z.intervalUnit", intervalUnit);
			invalidate();
		}

	}

	/**
	 * @return the showEventText
	 */
	public boolean isShowEventText() {
		return _showEventText;
	}

	/**
	 * @param showEventText
	 *            the showEventText to set
	 */
	public void setShowEventText(boolean showEventText) {
		if (showEventText != _showEventText) {
			_showEventText = showEventText;
			// smartUpdate("z.showEventText", showEventText);
			invalidate();
		}
	}

	private String findSyncWithIndex(String id) {
		Timeline parent = (Timeline) getParent();
		List l = parent.getChildren();
		for (int i = 0; i < l.size(); i++) {
			Bandinfo b = (Bandinfo) l.get(i);

			if (b.getId().equals(id)) {
				return String.valueOf(i);
			}
		}
		return "";
	}

	/**
	 * @return the _syncWith
	 */
	public String getSyncWith() {
		return _syncWith;
	}

	/**
	 * @param syncWith
	 *            the _syncWith to set
	 */
	public void setSyncWith(String syncWith) {
		if (!Objects.equals(syncWith, _syncWith)) {
			_syncWith = syncWith;
			// smartUpdate("z.syncWith", syncIndex);
			invalidate();
		}

	}

	/**
	 * @return the highlight
	 */
	public boolean isHighlight() {
		return _highlight;
	}

	/**
	 * @param highlight
	 *            the _highlight to set
	 */
	public void setHighlight(boolean highlight) {
		if (highlight != _highlight) {
			_highlight = highlight;
			// smartUpdate("z.highlight", highlight);
			invalidate();
		}
	}

	public TimeZone getTimeZone() {
		return _timeZone;
	}

	public void setTimeZone(TimeZone timeZone) {
		if (!Objects.equals(timeZone, _timeZone)) {
			_timeZone = timeZone;

			// smartUpdate("z.timeZone", timeZone.toString());
			invalidate();
		}
	}

	public float getTrackGap() {
		return _trackGap;
	}

	public void setTrackGap(float trackGap) {
		if (trackGap != _trackGap) {
			_trackGap = trackGap;
			// smartUpdate("z.trackGap", String.valueOf(trackGap));
			invalidate();
		}
	}

	public float getTrackHeight() {
		return _trackHeight;
	}

	public void setTrackHeight(float trackHeight) {
		if (trackHeight != _trackHeight) {
			_trackHeight = trackHeight;
			// smartUpdate("z.trackHeight", String.valueOf(trackHeight));
			invalidate();
		}
	}

	public Date getDate() {
		return _date;
	}

	public void setDate(Date date) {
		if (!Objects.equals(date, _date)) {
			_date = date;
			// smartUpdate("z.date", date.toString());
			invalidate();
		}
	}

	// -- Component --//
	public boolean insertBefore(Component child, Component insertBefore) {
		if (!(child instanceof Hotzone))
			throw new UiException("Unsupported child for timeline: " + child);
		return super.insertBefore(child, insertBefore);
	}

	public void addOccurEvent(OccurEvent event) {
		// if (!Objects.equals(event, _event)) {
		// _event = event;
		if (event == null)
			return;

		response("addOccurEvent" + event.getId(), new AuScript(this,
				"zkBandInfo.addOccurEvent(\"" + getUuid() + "\"" + ","
						+ event.toString() + ")"));

		// }
	}

	public void removeOccurEvent(OccurEvent event) {
		if (event == null)
			return;
		response("removeOccurEvent" + event.getId(), new AuScript(this,
				"zkBandInfo.removeOccurEvent(\"" + getUuid() + "\"" + ","
						+ "\"" + event.getId() + "\")"));
	}

	// -- Component --//

	public String getEventSourceUrl() {
		return _eventSourceUrl;
	}

	public void setEventSourceUrl(String eventSourceUrl) {
		if (!Objects.equals(eventSourceUrl, _eventSourceUrl)) {
			_eventSourceUrl = eventSourceUrl;
			smartUpdate("z.eventSourceUrl", eventSourceUrl);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zkoss.zk.ui.AbstractComponent#invalidate()
	 */
//	@Override
	public void invalidate() {
		// TODO Auto-generated method stub
		super.invalidate();
		if (getParent() != null)
			getParent().invalidate();
	}

	public void addManyOccureEvents(ArrayList events) {
		if (events == null)
			return;
		Iterator iter = events.iterator();
		JSONArray list = new JSONArray();
		while (iter.hasNext()) {
			OccurEvent e = (OccurEvent) iter.next();
			list.put(e);
		}

		response("addManyOccurEvent" + events.hashCode(), new AuScript(this,
				"zkBandInfo.addManyOccurEvent(\"" + getUuid() + "\"" + ","
						+ list.toString() + ")"));

	}

	public void addHighlightDecorator(HighlightDecorator hd) {
		// decorators.add(hd);
		if (hd == null)
			return;
		response("addHighlightDecorator" + hd.getId(), new AuScript(this,
				"zkBandInfo.addHighlightDecorator(\"" + getUuid() + "\"" + ","
						+ hd.toString() + ")"));
	}

	public void removeHighlightDecorator(HighlightDecorator hd) {
		// decorators.remove(hd);
		if (hd == null)
			return;
		response("removeHighlightDecorator" + hd.getId(), new AuScript(this,
				"zkBandInfo.removeHighlightDecorator(\"" + getUuid() + "\""
						+ "," + hd.getId() + ")"));

	}

	public void showLoadingMessage(boolean show) {
		if (show) {
			response("showLoadingMessage", new AuScript(this,
					"zkTimeline.showLoadingMessage(\"" + getParent().getUuid()
							+ "\"" + ")"));
		} else {
			response("hideLoadingMessage", new AuScript(this,
					"zkTimeline.hideLoadingMessage(\"" + getParent().getUuid()
							+ "\"" + ")"));
		}

	}

	public void scrollToCenter(Date date) {
		if (date == null)
			return;
		response("scrollToCenter", new AuScript(this,
				"zkBandInfo.scrollToCenter(\"" + getUuid() + "\"" + ",\""
						+ date.toString() + "\")"));
	}

	static {
		new BandScrollCommand("onBandScroll", Command.IGNORE_OLD_EQUIV);
	}
}
