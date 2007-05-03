/*
 * Created on Apr 26, 2007
 *
 */
package com.mindalliance.channels.data.support;

import java.io.Serializable;

/**
 * A simplistic way of defining availability. Availabilities can be compared.
 * @author jf
 *
 */
public class Availability implements Serializable {
	
	 enum Hours {ALL, BUSINESS_HOURS};
	 enum Days {ALL, WORK_DAYS};
	 
	 private Days days = Days.ALL;
	 private Hours hours = Hours.ALL;
	 
	 public boolean comprise(Availability availability) {
		 return false;
	 }
	 
	 public boolean overlap(Availability availability) {
		 return false;
	 }

}
