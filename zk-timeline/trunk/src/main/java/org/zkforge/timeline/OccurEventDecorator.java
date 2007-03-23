package org.zkforge.timeline;

import org.zkforge.timeline.data.OccurEvent;

/** The decorator.
 *
 * @author WeiXing Gu, China
 */
class OccurEventDecorator {
	private OccurEvent event;

	private boolean show;

	public OccurEvent getEvent() {
		return event;
	}

	public void setEvent(OccurEvent event) {
		this.event = event;
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}

	public OccurEventDecorator(OccurEvent event) {
		super();
		this.event = event;
	}
}
