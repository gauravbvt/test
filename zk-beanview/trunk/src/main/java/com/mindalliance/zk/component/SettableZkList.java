/**
 * 
 */
package com.mindalliance.zk.component;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.IllegalFormatConversionException;

import org.zkoss.zul.ListModel;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;

import com.beanview.MultipleSelectComponent;
import com.beanview.model.ConvertingSelectionModel;

/**
 * A multi-selectable ZK list that can be read and set by BeanView.  Expects a model that
 * implements both the BeanView MultipleSelectComponent interface and the ZK ListModel. 
 *
 */
public class SettableZkList extends Listbox implements MultipleSelectComponent {

	private static final long serialVersionUID = -3921775129440642578L;

	public SettableZkList() {
		this.setRows(4);
	}
	
	/*
	 * @see com.beanview.BeanViewMultipleSelect#setMultipleSelectOptions(javax.swing.ListModel)
	 */
	public void setMultipleSelectOptions(
			ConvertingSelectionModel manyToManyValues)
	{
		this.setModel((ListModel) manyToManyValues);
	}

	/*
	 * @see com.beanview.BeanViewMultipleSelect#getMultipleSelectOptions()
	 */
	public ConvertingSelectionModel getMultipleSelectOptions()
	{
		return (ConvertingSelectionModel) this.getModel();
	}

	/* (non-Javadoc)
	 * @see com.beanview.PropertyComponent#getValue()
	 */
	public Object getValue() {
		if (this.getSelectedItems() == null)
			return null;
		if (this.getSelectedItems().size() == 0)
			return null;

		ConvertingSelectionModel converter = (ConvertingSelectionModel) this.getModel();

		return converter.returnSelection(this.getSelectedItems().toArray());
	}

	/* (non-Javadoc)
	 * @see com.beanview.PropertyComponent#setValue(java.lang.Object)
	 */
	public void setValue(Object in) throws IllegalFormatConversionException {

		if (in == null)
		{
			this.clearSelection();
			return;
		}
		
		if (in.getClass().isArray())
		{
			Object objectArray = Array.newInstance(in.getClass()
					.getComponentType(), Array.getLength(in));
			for (int i = 0; i < Array.getLength(objectArray); i++)
			{
				Array.set(objectArray, i, Array.get(in, i));
			}
			ListModel available = this.getModel();
		
			this.clearSelection();
		
			for (int i = 0; i < available.getSize() ; i++)
			{
				for (int ii = 0; ii < Array.getLength(objectArray); ii++)
				{
					Object currentSetValue = Array.get(objectArray, ii);
					//if (available.getElementAt(i).equals(Array.get(objectArray, ii)))
					//	this.setSelectedIndex(i);
					if (currentSetValue instanceof Listitem) {
						Listitem li = (Listitem)currentSetValue;
						if (li.getValue().equals(available.getElementAt(i)))
							this.addItemToSelection(li);
					} else if (currentSetValue.equals(available.getElementAt(i))) {
						this.setSelectedIndex(i);
					}
				}
			}
		}
		
		if (in instanceof Collection)
		{
			Collection valuesToSet = (Collection) in;
		
			this.clearSelection();
			ListModel available = this.getModel();
			for (int i = 0; i < available.getSize(); i++)
			{
				for (Object currentSetValue : valuesToSet)
				{
					if (currentSetValue instanceof Listitem) {
						Listitem li = (Listitem)currentSetValue;
						if (li.getValue().equals(available.getElementAt(i)))
							this.addItemToSelection(li);
					} else if (currentSetValue.equals(available.getElementAt(i))) {
						this.setSelectedIndex(i);
					}
				}
		
			}
		}
	}

}
