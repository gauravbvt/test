/*
 * Created on May 3, 2007
 *
 */
package com.mindalliance.channels.data.user;


/**
 * An opinion about an element's definition
 * @author jf
 *
 */
public class Opinion extends Statement {
	
	enum Intent {OK, CHANGE, REMOVE} // approval, change recommended, should be removed

	private Intent intent;

	public Opinion() {
		super();
	}

	/**
	 * @return the intent
	 */
	public Intent getIntent() {
		return intent;
	}

	/**
	 * @param intent the intent to set
	 */
	public void setIntent(Intent intent) {
		this.intent = intent;
	}
}
