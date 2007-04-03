package com.mindalliance.zk.beanview;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;

import com.beanview.BeanView;
import com.beanview.PropertyComponent;
import com.beanview.util.Configuration;
import com.mindalliance.zk.component.ZkError;

/**
 * A generic ZK component for editing beans using BeanView.  This component will lay out the 
 * bean properties to be manipulated by the bean vertically in a grid.  Each property has a label, 
 * an editable field appropriate to the property type, and a column for indicating errors.
 * 
 * <p>In order to create an editor for a particular class, simply instantiate a new instance of ZkBeanViewPanel<ClassName>.
 * The properties that are edited by a particular panel instance can be constrained using the {@link #setSubView(String[], boolean, boolean)}
 * method. 
 */
public class ZkBeanViewPanel<V> extends ZkBeanViewPanelBase<V> implements BeanView {

	/**
	 * Default constructor
	 */
	public ZkBeanViewPanel() {
		super();
	}

	private Grid grid = new Grid();
	
	/**
	 * @param name
	 */
	public ZkBeanViewPanel(String name) {
		super(name);
	}

	/**
	 * Creates the edit components for each member of the bean and places them 
	 * in a grid.
	 * @see ZkBeanViewPanelBase#configure()
	 */
	@Override
	public void configure() {
		String[] keys = helper.keys();
		Rows rows = new Rows();
		
		for (String key : keys)
		{
			Row currentRow = new Row();
			setUpLabel(key, currentRow);
			setUpEntryField(key, currentRow);
			setUpError(key, currentRow);
		
			rows.appendChild(currentRow);
		}
		grid.appendChild(rows);
		this.appendChild(grid);
	}

	/**
	 * Retrieves the appropriate editable entry for a particular bean
	 * member and sticks it into the provided row.
	 * @see ZkBeanViewPanelBase#setUpEntryField(java.lang.String, int)
	 */
	protected void setUpEntryField(String key, Row currentRow) {
		// Set up entry field
		ZkComponentFactory factory = new ZkComponentFactory(key, helper
				.getPropertyType(key), this);

		PropertyComponent newComponent = factory.getSettable();
		Component zkComponent = (Component) newComponent;

		helper.getComponents().put(key, newComponent);
		currentRow.appendChild(zkComponent);
	}

	/**
	 * Inserts a ZkError component in the provided row.
	 * @see ZkBeanViewPanelBase#setUpError(java.lang.String, int)
	 */
	protected void setUpError(String key, Row currentRow) {
		ZkError errorLabel = new ZkError();
		helper.getErrorComponents().put(key, errorLabel);
		errorLabel.setStyle("color:red");
		currentRow.appendChild(errorLabel);
	}

	/**
	 * Creates a label for a particular bean member and adds it to the provided Row.
	 * @see ZkBeanViewPanelBase#setUpLabel(java.lang.String, int)
	 */
	protected void setUpLabel(String key, Row currentRow) {
		Label keyLabel = new Label();
		Configuration config = new Configuration(this);
		keyLabel.setValue(config.getLabel(key));
		keyLabel.setId(key + "_label");
		currentRow.appendChild(keyLabel);
	}



}
