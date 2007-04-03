/**
 * 
 */
package com.mindalliance.zk.beanview.model;

import java.util.Collection;
import java.util.HashSet;

import org.zkoss.zul.ListModel;
import org.zkoss.zul.event.ListDataListener;

import com.beanview.model.CollectionModel;

/**
 * A wrapper around the BeanView Swing CollectionModel that can be used as a ZK ListModel.
 *
 */
public class ZkCollectionModel extends CollectionModel implements ListModel {

	private static final long serialVersionUID = 1L;
	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public ZkCollectionModel(Collection arg0, Class arg1, Object arg2,
			boolean arg3) {
		super(arg0, arg1, arg2, arg3);
		// TODO Auto-generated constructor stub
	}

	public Object get(int arg0)
	{
		return this.getElementAt(arg0);
	}

	public int size()
	{
		return this.getSize();
	}

	HashSet<ListDataListener> listeners = new HashSet<ListDataListener>();

	public void addListDataListener(ListDataListener arg0)
	{
		listeners.add(arg0);
	}

	public void removeListDataListener(ListDataListener arg0)
	{
		listeners.remove(arg0);
	}

}
