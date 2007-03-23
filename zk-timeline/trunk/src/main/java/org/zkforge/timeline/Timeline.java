/*	This program is distributed under Lesser GPL Version 2.1 in the hope that
 *	it will be useful, but WITHOUT ANY WARRANTY.
 */
package org.zkforge.timeline;

import org.json.simple.JSONArray;
import org.zkforge.timeline.impl.TimelineComponent;
import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;

/** The timeline component.
 *
 * <p>See also <a href="http://simile.mit.edu/timeline">MIT Timeline</a>
 *
 * @author WeiXing Gu, China
 */
public class Timeline extends TimelineComponent {

	private String _orient = "horizontal";// default

	private String _height = "150px";// default

	private String _width = "100%";// default

	/**
	 * Returns the orientation of {@link Timeline}.
	 */
	public String getOrient() {
		return _orient;
	}

	/**
	 * Sets the orientation of {@link Timeline}.
	 */
	public void setOrient(String orient) throws WrongValueException {
		if (!"horizontal".equals(orient) && !"vertical".equals(orient))
			throw new WrongValueException(orient);

		if (!Objects.equals(_orient, orient)) {
			_orient = orient;
			invalidate();
		}
	}

	public String getInnerAttrs() {
		return "z.orientation=" + getOrient();

	}

	// -- Component --//
	public boolean insertBefore(Component child, Component insertBefore) {
		if (!(child instanceof Bandinfo))
			throw new UiException("Unsupported child for timeline: " + child);
		return super.insertBefore(child, insertBefore);
	}

	/** Returns the height.
	 * @return the height
	 */
	public String getHeight() {
		return _height;
	}

	/** Sets the height.
	 * @param height
	 *            the height to set
	 */
	public void setHeight(String height) {
		if (!Objects.equals(_height, height)) {
			_height = height;
			smartUpdate("z.height", height);
			// invalidate();
		}
	}

	/** Returns the width
	 */
	public String getWidth() {
		return _width;
	}
	/** Sets the width.
	 */
	public void setWidth(String width) {
		if (!Objects.equals(_width, width)) {
			_width = width;
			smartUpdate("z.width", width);
			// invalidate();
		}

	}

	/*public void reLayout() {
		//
	}*/

	public void performFiltering(String filterText) {
		smartUpdate("z.filter", filterText);
	}
	public void clearFilter() {
		smartUpdate("z.clearFilter", "");
	}
	public void performHighlitht(String highlightText[]) {
		JSONArray matchers = new JSONArray();
		for (int i = 0; i < highlightText.length; i++) {
			matchers.add(highlightText[i]);
		}
		// System.out.println(matchers.toString());
		smartUpdate("z.highlight", matchers.toString());
	}

	public void clearHighlight() {
		smartUpdate("z.clearHighlight", "");
	}
}
