package com.mindalliance.zk.beanview;

import java.util.Locale;

import org.zkoss.zul.Box;

import com.beanview.BeanView;
import com.beanview.BeanViewConfiguration;
import com.beanview.PropertyComponent;

/**
 * Base class for ZkBeanViewPanel.
 * 
 */
public abstract class ZkBeanViewPanelBase<V> extends Box implements BeanView {
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	BeanViewConfiguration<V> helper = new BeanViewConfiguration<V>(this);

	private Locale locale = Locale.ENGLISH;

	/**
	 * Default constructor
	 * 
	 */
	public ZkBeanViewPanelBase() {
		super();
	}

	/**
	 * 
	 * @param name
	 */
	public ZkBeanViewPanelBase(String name) {
		super(name);
	}

	abstract public void configure();

	/**
	 * Retrieves the editor component for a particular property.  <code>field</code> should 
	 * be of the form {bean}.{property}.
	 */
	public PropertyComponent getPropertyComponent(String field) {
		return helper.getPropertyComponent(field);
	}

	/**
	 * Sets the error value to be displayed by a particular property.
	 */
	public void setError(String field, String error) {
		helper.setError(field, error);
	}

	public String getError(String field) {
		return helper.getError(field);
	}

	/**
	 * @see com.beanview.BeanViewConfiguration<V>#setContext(String, Object)
	 */
	public void setContext(String key, Object value) {
		helper.setContext(key, value);
	}

	public Object getContext(String key) {
		return helper.getContext(key);
	}

	/**
	 * Retrieves the current <V> instance being edited
	 */
	public V getDataObject() {
		return helper.getDataObject();
	}

	/**
	 * Sets the instance of <V> to be edited.
	 */
	@SuppressWarnings("unchecked")
	public void setDataObject(Object dataObject) {
		helper.setDataObject(dataObject);
	}

	/**
	 * Sets the list of properties of the bean that will be excluded from editing
	 */
	public void setExcludeProperties(String[] excludeProperties) {
		helper.setExcludeProperties(excludeProperties);
	}
	/**
	 * Returns the list of properties of the bean that will be excluded from editing
	 */
	public String[] getExcludeProperties() {
		return helper.getExcludeProperties();
	}

	/**
	 * Updates the ZK panel with the current values in the configured bean instance
	 */
	public void updatePanelFromObject() {
		helper.updatePanelFromObject();
	}

	/**
	 * Updates the bean instance with the values currently set in the ZK component
	 */
	public void updateObjectFromPanel() {
		helper.updateObjectFromPanel();
	}

	/**
	 * Returns true if any properties have an associated error
	 */
	public boolean hasErrors() {
		return helper.hasErrors();
	}
	
	/**
	 * Sets the list of properties that are editable by the component,
	 * 
	 * @see com.beanview.BeanViewConfiguration<V>#setSubView(String[], boolean, boolean)
	 */
	public void setSubView(String[] namedProperties, boolean includeAll,
			boolean sortAll) {
		helper.setSubView(namedProperties, includeAll, sortAll);
	}

	/**
	 * Retrieves the BeanView configuration for this panel.  This can be used to 
	 * configure property validation and to get a handle on the ZK editor components.
	 */
	public BeanViewConfiguration getBeanViewConfiguration() {
		return helper;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.beanview.BeanView#getLocale()
	 */
	public Locale getLocale() {
		return locale;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.beanview.BeanView#setLocale(java.util.Locale)
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	

	// abstract protected void setUpLabel(String key, int currentRow);
	//
	// abstract protected void setUpEntryField(String key, int currentRow);
	//
	// abstract protected void setUpError(String key, int currentRow);
}
