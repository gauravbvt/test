/**
 * 
 */
package com.mindalliance.zk.beanview.model;

import java.util.HashSet;

import org.zkoss.zul.ListModel;
import org.zkoss.zul.event.ListDataListener;

import com.beanview.model.ArrayModel;

/**
 * A wrapper around the BeanView Swing ArrayModel that can be used as a ZK ListModel.
 *
 */
public class ZkArrayModel extends ArrayModel implements ListModel {
	
	public ZkArrayModel(Object[] in, Object selected, boolean nullable)
	{
		super(in, selected, nullable);
	}
	
	HashSet<ListDataListener> listeners = new HashSet<ListDataListener>();
	/* (non-Javadoc)
	 * @see org.zkoss.zul.ListModel#addListDataListener(org.zkoss.zul.event.ListDataListener)
	 */
	public void addListDataListener(ListDataListener arg0) {

		listeners.add(arg0);
	}

	/* (non-Javadoc)
	 * @see org.zkoss.zul.ListModel#removeListDataListener(org.zkoss.zul.event.ListDataListener)
	 */
	public void removeListDataListener(ListDataListener arg0) {

		listeners.remove(arg0);
	}
	
	public Object get(int arg0)
	{
		return this.getElementAt(arg0);
	}

	public int size()
	{
		return this.getSize();
	}


}
