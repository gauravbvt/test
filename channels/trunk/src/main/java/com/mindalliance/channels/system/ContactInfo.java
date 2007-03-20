// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.system;

import java.util.ArrayList;
import java.util.List;

import com.mindalliance.channels.util.Duration;

/**
 * How an agent, organization or information resource can be
 * reached, when and how quickly.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 *
 * @opt attributes
 * @composed - - * ContactInfoDetails
 * @navassoc * indirect * Agent
 */
public class ContactInfo {

    private List<ContactInfoDetails> direct =
            new ArrayList<ContactInfoDetails>();
    private List<Agent> indirect = new ArrayList<Agent>();

    /**
     * Default constructor.
     */
    ContactInfo() {
        super();
    }

    /**
     * Return the value of direct.
     */
    public List<ContactInfoDetails> getDirect() {
        return this.direct;
    }

    /**
     * Set the value of direct.
     * @param direct The new value of direct
     */
    public void setDirect( List<ContactInfoDetails> direct ) {
        this.direct = direct;
    }

    /**
     * Return the value of indirect.
     */
    public List<Agent> getIndirect() {
        return this.indirect;
    }

    /**
     * Set the value of indirect.
     * @param indirect The new value of indirect
     */
    public void setIndirect( List<Agent> indirect ) {
        this.indirect = indirect;
    }

    //====================================
    /**
     * A direct way of contacting someone.
     *
     * @opt attributes
     * @composed - - 1 Channel
     * @composed - - 1 Delay
     * @composed - - 1 Availability
     */
    public static class ContactInfoDetails {

        private Channel channel;
        private String endPoint;
        private boolean restricted;
        private Delay delay;
        private Availability availability;

        /**
         * Default constructor.
         */
        public ContactInfoDetails() {
            super();
        }

        /**
         * Return the value of availability.
         */
        public Availability getAvailability() {
            return this.availability;
        }

        /**
         * Set the value of availability.
         * @param availability The new value of availability
         */
        public void setAvailability( Availability availability ) {
            this.availability = availability;
        }

        /**
         * Return the value of channel.
         */
        public Channel getChannel() {
            return this.channel;
        }

        /**
         * Set the value of channel.
         * @param channel The new value of channel
         */
        public void setChannel( Channel channel ) {
            this.channel = channel;
        }

        /**
         * Return the value of delay.
         */
        public Delay getDelay() {
            return this.delay;
        }

        /**
         * Set the value of delay.
         * @param delay The new value of delay
         */
        public void setDelay( Delay delay ) {
            this.delay = delay;
        }

        /**
         * Return the value of endPoint.
         */
        public String getEndPoint() {
            return this.endPoint;
        }

        /**
         * Set the value of endPoint.
         * @param endPoint The new value of endPoint
         */
        public void setEndPoint( String endPoint ) {
            this.endPoint = endPoint;
        }

        /**
         * Return the value of restricted.
         */
        public boolean isRestricted() {
            return this.restricted;
        }

        /**
         * Set the value of restricted.
         * @param restricted The new value of restricted
         */
        public void setRestricted( boolean restricted ) {
            this.restricted = restricted;
        }
    }

    //====================================
    /**
     * Delays involved when contacting someone.
     *
     * @opt attributes
     */
    public static class Delay {

        private Duration best;
        private Duration usual;
        private Duration worst;

        /**
         * Default constructor.
         */
        public Delay() {
            super();
        }

        /**
         * Return the value of best.
         */
        public Duration getBest() {
            return this.best;
        }

        /**
         * Set the value of best.
         * @param best The new value of best
         */
        public void setBest( Duration best ) {
            this.best = best;
        }

        /**
         * Return the value of usual.
         */
        public Duration getUsual() {
            return this.usual;
        }

        /**
         * Set the value of usual.
         * @param usual The new value of usual
         */
        public void setUsual( Duration usual ) {
            this.usual = usual;
        }

        /**
         * Return the value of worst.
         */
        public Duration getWorst() {
            return this.worst;
        }

        /**
         * Set the value of worst.
         * @param worst The new value of worst
         */
        public void setWorst( Duration worst ) {
            this.worst = worst;
        }
    }
}
