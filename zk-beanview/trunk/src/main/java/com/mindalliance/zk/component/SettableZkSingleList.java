package com.mindalliance.zk.component;
import java.util.IllegalFormatConversionException;

import javax.swing.ComboBoxModel;

import org.zkoss.zul.ListModel;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;

import com.beanview.SingleSelectComponent;
import com.beanview.model.SelectedIndex;

/**
 * A single-selectable ZK list that can be read and set by BeanView.  Expects a model
 * that implements both the ZK ListModel interface and the swing ComboBoxModel.
 *
 */
public class SettableZkSingleList extends Listbox implements
		SingleSelectComponent {

	
	public SettableZkSingleList() {
		this.setRows(1);
		this.setMold("select");
	}
	
	/*
	 * @see com.beanview.BeanViewSingleSelect#setSingleSelectOptions(javax.swing.ComboBoxModel)
	 */
	public void setSingleSelectOptions(ComboBoxModel oneToManyValues)
	{
		if(!(oneToManyValues instanceof ListModel))
			throw new Error("Invalid model");
        if(!(oneToManyValues instanceof SelectedIndex))
            throw new Error("Invalid model");

        
		this.setModel((ListModel)oneToManyValues);
	}

	/*
	 * @see com.beanview.BeanViewSingleSelect#getSingleSelectOptions()
	 */
	public ComboBoxModel getSingleSelectOptions()
	{
		return (ComboBoxModel)this.getModel();
	}

	public void setValue(Object in) throws IllegalFormatConversionException
	{
		if(in == null)
		{
			this.clearSelection();
			return;
		}
		
		for(int i = 0; i < this.getModel().getSize(); i++)
		{
			if(in.equals(this.getModel().getElementAt(i)))
				this.setSelectedIndex(i);
			if(in == this.getModel().getElementAt(i))
				this.setSelectedIndex(i);
		}
	}
	

	public Object getValue()
	{
		Listitem it = this.getSelectedItem();
		if (it != null) {
			return it.getValue();
		}
        return null;
	}

}
