package org.zkforge.timeline.util;

import java.util.Comparator;

import org.zkforge.timeline.data.OccurEvent;

public class OccurEventComparator implements Comparator {

	public int compare(Object o1, Object o2) {
		// TODO Auto-generated method stub
		OccurEvent evt1 = (OccurEvent) o1;
		OccurEvent evt2 = (OccurEvent) o2;
		if (evt1.getStart().compareTo(evt2.getStart()) <= 0)
			return 0;
		else if (evt1.getEnd().compareTo(evt2.getEnd()) <= 0)
			return 0;
		return 1;
	}

}
