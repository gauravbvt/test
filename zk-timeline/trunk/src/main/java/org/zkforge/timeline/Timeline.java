/*	This program is distributed under Lesser GPL Version 2.1 in the hope that
 *	it will be useful, but WITHOUT ANY WARRANTY.
 */
package org.zkforge.timeline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.zkforge.timeline.data.OccurEvent;
import org.zkforge.timeline.impl.TimelineComponent;
import org.zkoss.lang.Objects;
import org.zkoss.zk.au.Command;
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
	
	private static final String SELECT_EVENT_COMMAND = "onSelectEvent";
	
	static {
		new SelectCommand(SELECT_EVENT_COMMAND, Command.IGNORE_OLD_EQUIV);
	}
	
	private String _orient = "horizontal";// default

	private String _height = "150px";// default

	private String _width = "100%";// default
	
	private String[] selection = new String[0];

    private Map<String, OccurEvent> events  = new HashMap<String, OccurEvent>();
    
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
	public void performHighlight(String highlightText[]) {
		JSONArray matchers = new JSONArray();
		for (int i = 0; i < highlightText.length; i++) {
			matchers.put(highlightText[i]);
		}
		 System.out.println(matchers.toString());
		smartUpdate("z.highlight", matchers.toString());
	}

	public void clearHighlight() {
		smartUpdate("z.clearHighlight", "");
	}
	
	public String[] getSelection() {
		return selection;
	}
	
	public void setSelection(String[] selection) {
		JSONArray ids = new JSONArray();
		for (int i = 0; i < selection.length; i++) {
			ids.put(selection[i]);
		}
		smartUpdate("z.select", ids.toString());
		setLocalSelection(selection);
	}
	
	public void setLocalSelection(String[] selection) {
		this.selection = selection;
	}
	
	public void clearSelection() {
		setSelection(new String[0]);
	}
	
    public void addManyOccurEvents(List<OccurEvent> events) {
        for (OccurEvent event : events) {
            this.events.put(event.getId(), event);
        }
        for (Object child: this.getChildren()) {
            if (child instanceof Bandinfo) {
                Bandinfo band = (Bandinfo)child;
                if (band.getSyncWith() == null) {
                    band.addManyOccureEvents(events);
                }
            }
        }
    }
    
    public void addOccurEvent(OccurEvent event) {
        events.put(event.getId(), event);
        for (Object child: this.getChildren()) {
            if (child instanceof Bandinfo) {
                Bandinfo band = (Bandinfo)child;
                if (band.getSyncWith() == null) {
                    band.addOccurEvent(event);
                }
            }
        }
    }
    
    public void removeOccurEvent(OccurEvent event) {
        events.remove(event.getId());
        for (Object child: this.getChildren()) {
            Bandinfo band = (Bandinfo)child;
            if (band.getSyncWith() == null) {
                band.removeOccurEvent(event);
            }
//            if (child instanceof Bandinfo) {
//                ((Bandinfo)child).removeOccurEvent(event);
//            }
        }
    }
    
    public List<Object> getSelectedData() {
        ArrayList<Object> results = new ArrayList<Object>();
        for (int inx = 0 ; inx < selection.length ; inx++) {
            OccurEvent event = events.get(selection[inx]);
            if (event != null) {
                results.add(event.getData());
            }
        }
        
        return results;
    }
    
    public Map<String, OccurEvent> getOccurEvents() {
        return events;
    }
}
