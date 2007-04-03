/**
 * 
 */
package com.mindalliance.zk.beanview.model;

import java.util.HashSet;

import org.zkoss.zul.ListModel;
import org.zkoss.zul.event.ListDataListener;

import com.beanview.model.EnumModel;

/**
 * A wrapper around the BeanView Swing EnumModel that can be used as a ZK ListModel.
 *
 */
public class ZkEnumModel extends EnumModel implements ListModel {

	public ZkEnumModel(Class selected, boolean nullable) {
		super(selected, nullable);
		// TODO Auto-generated constructor stub
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

}
