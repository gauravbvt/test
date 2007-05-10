/*
 * Created on May 8, 2007
 *
 */
package com.mindalliance.channels.data.user;

import com.mindalliance.channels.User;
import com.mindalliance.channels.util.GUID;

/**
 * Identity certification
 * @author jf
 *
 */
public class IdentityCertification extends Certification {

	private User userCertified; // the user being certified
	private GUID personGUID; // the certified personification
	
	public IdentityCertification() {
		super();
	}
	/**
	 * @return the personGUID
	 */
	public GUID getPersonGUID() {
		return personGUID;
	}
	/**
	 * @param personGUID the personGUID to set
	 */
	public void setPersonGUID(GUID personGUID) {
		this.personGUID = personGUID;
	}
	/**
	 * @return the userCertified
	 */
	public User getUserCertified() {
		return userCertified;
	}
	/**
	 * @param userCertified the userCertified to set
	 */
	public void setUserCertified(User userCertified) {
		this.userCertified = userCertified;
	}
	
}
