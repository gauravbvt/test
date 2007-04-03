/**
 * 
 */
package com.mindalliance.zk.component;

import org.zkoss.zul.Label;

import com.beanview.ErrorComponent;

/**
 * A ZK label for displaying an error.  The Value is just '(!)' in red while the tooltip contains the 
 * actual error text.
 *
 */
public class ZkError extends Label implements ErrorComponent {


	private static final long serialVersionUID = 1L;
	
	/* (non-Javadoc)
	 * @see com.beanview.ErrorComponent#getError()
	 */
	public String getError() {
        if(this.getTooltiptext() == null)
            return null;
        if(this.getTooltiptext().length() == 0)
            return null;
		return this.getTooltiptext();
	}

	/* (non-Javadoc)
	 * @see com.beanview.ErrorComponent#isErrorSet()
	 */
	public boolean isErrorSet() {
		if (this.getTooltiptext() == null)
			return false;
		if (this.getTooltiptext().length() > 0)
			return true;
		return false;
	}

	/* (non-Javadoc)
	 * @see com.beanview.ErrorComponent#setError(java.lang.String)
	 */
	public void setError(String err) {
		if (err == null)
		{
			this.setTooltiptext(null);
			this.setValue("");
			return;
		}
		this.setValue("(!)");
		this.setTooltiptext(err);

	}

}
