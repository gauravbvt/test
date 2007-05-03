/*
 * Created on Apr 26, 2007
 *
 */
package com.mindalliance.channels.data.beans;

import com.mindalliance.channels.data.Level;
import com.mindalliance.channels.data.elements.Channel;
import com.mindalliance.channels.util.AbstractJavaBean;

/**
 * How to contact someone or something: end point (address etc.), channel, privacy level and availability.
 * @author jf
 *
 */
public class ContactInfo extends AbstractJavaBean {

	private Channel channel;
	private String endPoint;
	private Level privacy;
	private Availability availability;
}
