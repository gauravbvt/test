// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.elements.scenario;

import com.mindalliance.channels.data.components.Contactable;
import com.mindalliance.channels.data.elements.Occurrence;
import com.mindalliance.channels.data.elements.resources.Channel;
import com.mindalliance.channels.data.reference.Information;
import com.mindalliance.channels.util.GUID;

/**
 * The movement of information from a source to a recipient over one
 * or more interoperable system. A communication can be caused by
 * another communication, such as when a notification or request is
 * passed along.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 * @param <T> the type of the cause
 */
public abstract class Communication<T extends Occurrence>
    extends AbstractOccurrence<T> {

    private Contactable source;
    private Contactable recipient;
    private Information information;
    private Channel sourceChannel;
    private Channel recipientChannel;

    /**
     * Default constructor.
     */
    public Communication() {
        super();
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public Communication( GUID guid ) {
        super( guid );
    }

    /**
     * Return the information.
     */
    public Information getInformation() {
        return information;
    }

    /**
     * Set the information.
     * @param information the information to set
     */
    public void setInformation( Information information ) {
        this.information = information;
    }

    /**
     * Return the recipient.
     */
    public Contactable getRecipient() {
        return recipient;
    }

    /**
     * Set the recipient.
     * @param recipient the recipient to set
     */
    public void setRecipient( Contactable recipient ) {
        this.recipient = recipient;
    }

    /**
     * Return the recipient channel.
     */
    public Channel getRecipientChannel() {
        return recipientChannel;
    }

    /**
     * Set the recipient channel.
     * @param recipientChannel the recipientChannel to set
     */
    public void setRecipientChannel( Channel recipientChannel ) {
        this.recipientChannel = recipientChannel;
    }

    /**
     * Return the source.
     */
    public Contactable getSource() {
        return source;
    }

    /**
     * Set the source.
     * @param source the source to set
     */
    public void setSource( Contactable source ) {
        this.source = source;
    }

    /**
     * Return the source channel.
     */
    public Channel getSourceChannel() {
        return sourceChannel;
    }

    /**
     * Set the source channel.
     * @param sourceChannel the sourceChannel to set
     */
    public void setSourceChannel( Channel sourceChannel ) {
        this.sourceChannel = sourceChannel;
    }
}
