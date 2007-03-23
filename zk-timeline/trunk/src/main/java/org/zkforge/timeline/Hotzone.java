/*	This program is distributed under Lesser GPL Version 2.1 in the hope that
 *	it will be useful, but WITHOUT ANY WARRANTY.
 */
package org.zkforge.timeline;

import java.util.Date;

import org.zkforge.timeline.impl.TimelineComponent;
import org.zkforge.timeline.util.TimelineUtil;
import org.zkoss.lang.Objects;
import org.zkoss.xml.HTMLs;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;

/** The hotzone component.
 *
 * <p>See also <a href="http://simile.mit.edu/timeline">MIT Timeline</a>
 *
 * @author WeiXing Gu, China
 */
public class Hotzone extends TimelineComponent {
	private Date _start = new Date();

	private Date _end = new Date();

	private int _magnify = 7;// default value

	private String _unit = "week";// default value

	private int _multiple = 1;// default value

	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Bandinfo))
			throw new UiException("Unsupported parent for hotzone: " + parent);
		super.setParent(parent);
	}

	public String getInnerAttrs() {
		final String attrs = super.getInnerAttrs();
		final StringBuffer sb = new StringBuffer(128);
		if (attrs != null) {
			sb.append(attrs);
		}
		HTMLs.appendAttribute(sb, "z.pid", getParent().getUuid());
		if (getStart() != null)
			HTMLs.appendAttribute(sb, "z.start", TimelineUtil
					.formatDateTime(getStart()));
		if (getEnd() != null)
			HTMLs.appendAttribute(sb, "z.end", TimelineUtil
					.formatDateTime(getEnd()));
		HTMLs.appendAttribute(sb, "z.magnify", getMagnify());
		HTMLs.appendAttribute(sb, "z.unit", TimelineUtil
				.convertIntervalUnitFromName(getUnit()));
		HTMLs.appendAttribute(sb, "z.multiple", getMultiple());
		return sb.toString();
	}

	/** Returns the end date.
	 */
	public Date getEnd() {
		return _end;
	}
	/** Sets the end date.
	 */
	public void setEnd(Date end) {
		if (!Objects.equals(end, _end)) {
			_end = end;
			//smartUpdate("z.end", end.toString());
			invalidate();
		}
	}

	public int getMagnify() {
		return _magnify;
	}

	public void setMagnify(int magnify) {
		if (magnify != _magnify) {
			_magnify = magnify;
			//smartUpdate("z.magnify", magnify);
			invalidate();
		}
	}

	/** Returns the start date.
	 */
	public Date getStart() {
		return _start;
	}
	/** Sets the start date.
	 */
	public void setStart(Date start) {
		if (!Objects.equals(start, _start)) {
			_start = start;
			//smartUpdate("z.start", start.toString());
			invalidate();
		}
	}

	public String getUnit() {
		return _unit;
	}

	public void setUnit(String unit) {
		if (!Objects.equals(unit, _unit)) {
			_unit = unit;
			//smartUpdate("z.unit", unit);
			invalidate();
		}
	}

	public String getContent() {

		return "";
	}

	public int getMultiple() {
		return _multiple;
	}

	public void setMultiple(int multiple) {
		if (multiple !=  _multiple) {
			_multiple = multiple;
			//smartUpdate("z.multiple", multiple);
			invalidate();
		}
	}

//	@Override
	public void invalidate() {
		// TODO Auto-generated method stub
		super.invalidate();
		if (getParent() != null)
			getParent().invalidate();
	}
}
