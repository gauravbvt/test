package com.mindalliance.channels.playbook.support.renderers

import org.apache.wicket.markup.html.form.IChoiceRenderer
import com.mindalliance.channels.playbook.support.RefUtils

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 28, 2008
 * Time: 12:35:14 PM
 */
class RefChoiceRenderer implements IChoiceRenderer
{
	private static final long serialVersionUID = 1L;

	/** expression for getting the display value. */
	private final String displayExpression;

	/** expression for getting the id. */
	private final String idExpression;

	/**
	 * Construct. When you use this constructor, the display value will be determined by calling
	 * toString() on the list object, and the id will be based on the list index. the id value will
	 * be the index
	 */
	public RefChoiceRenderer()
	{
		super();
		this.displayExpression = null;
		this.idExpression = null;
	}

	/**
	 * Construct. When you use this constructor, the display value will be determined by executing
	 * the given property expression on the list object, and the id will be based on the list index.
	 * The display value will be calculated by the given property expression
	 *
	 * @param displayExpression
	 *            A property expression to get the display value
	 */
	public RefChoiceRenderer(String displayExpression)
	{
		super();
		this.displayExpression = displayExpression;
		this.idExpression = null;
	}

	/**
	 * Construct. When you use this constructor, both the id and the display value will be
	 * determined by executing the given property expressions on the list object.
	 *
	 * @param displayExpression
	 *            A property expression to get the display value
	 * @param idExpression
	 *            A property expression to get the id value
	 */
	public RefChoiceRenderer(String displayExpression, String idExpression)
	{
		super();
		this.displayExpression = displayExpression;
		this.idExpression = idExpression;
	}

	/**
	 * @see org.apache.wicket.markup.html.form.IChoiceRenderer#getDisplayValue(java.lang.Object)
	 */
	public Object getDisplayValue(Object object)
	{
		Object returnValue = object;
		if ((displayExpression != null) && (object != null))
		{
            returnValue = RefUtils.get(object, displayExpression);
		}

		if (returnValue == null)
		{
			return "";
		}

		return returnValue;
	}

	/**
	 * @see org.apache.wicket.markup.html.form.IChoiceRenderer#getIdValue(java.lang.Object, int)
	 */
	public String getIdValue(Object object, int index)
	{
		if (idExpression == null)
		{
			return Integer.toString(index);
		}

		if (object == null)
		{
			return "";
		}

		Object returnValue = RefUtils.get(object, idExpression);
		if (returnValue == null)
		{
			return "";
		}

		return returnValue.toString();
	}

}