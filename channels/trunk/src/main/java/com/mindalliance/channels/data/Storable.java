/*
 * Created on May 1, 2007
 *
 */
package com.mindalliance.channels.data;

import java.util.List;

import com.mindalliance.channels.data.beans.StoredIn;

/**
 * Something that can be stored in a repository for later retrieval.
 * @author jf
 *
 */
public interface Storable extends Assertable {

	List<StoredIn> getStoredInAssertions();
	
}
