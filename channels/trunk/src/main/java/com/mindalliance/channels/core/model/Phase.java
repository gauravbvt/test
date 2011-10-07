package com.mindalliance.channels.core.model;

import java.text.Collator;

/**
 * A phase in a plan.
 * A phase is an entity.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Sep 18, 2009
 * Time: 10:29:06 AM
 */
public class Phase extends ModelEntity {
    /**
     * Unknown phase.
     */
    public static Phase UNKNOWN;
    
    /**
     * Name of unknown phase.
     */
    public static String UnknownName = "(unknown)";

    /**
     * The timing of the phase relative to an event.
     */
    private Timing timing = Timing.Concurrent;

    public Phase() {
    }

    public Phase( String name ) {
        super( name );
    }

    public Timing getTiming() {
        return timing;
    }

    public void setTiming( Timing timing ) {
        this.timing = timing;
    }

    /**
     * Whether this phase precedes another.
     *
     * @param other a phase
     * @return a boolean
     */
    public boolean precedes( Phase other ) {
        return timing.compareTo( other.getTiming() ) < 0;
    }

    /**
     * Whether this phase follows another.
     *
     * @param other a phase
     * @return a boolean
     */
    public boolean follows( Phase other ) {
        return other.precedes( this );
    }

    /**
     * Whether this phase co-occurs with another.
     *
     * @param other a phase
     * @return a boolean
     */
    public boolean cooccursWith( Phase other ) {
        return timing.compareTo( other.getTiming() ) == 0;
    }

    /**
     * {@inheritDoc}
     */
    public int compareTo( Phase other ) {
        if ( precedes( other ) ) return -1;
        if ( follows( other ) ) return 1;
        return Collator.getInstance().compare( getName(), other.getName() );
    }

    /**
     * Get preposition "before", "during" or "after" from timing.
     *
     * @return a string
     */
    public String getPreposition() {
        switch ( timing ) {
            case PreEvent:
                return "before";
            case Concurrent:
                return "while";
            case PostEvent:
                return "after";
            default:
                throw new RuntimeException( "Unsupported timing" );
        }
    }

    /**
     * Is phase timing concurrent with events?
     *
     * @return a boolean
     */
    public boolean isConcurrent() {
        return timing == Timing.Concurrent;
    }

    /**
     * Is phase timing post-events?
     *
     * @return a boolean
     */
    public boolean isPostEvent() {
        return timing == Timing.PostEvent;
    }

    /**
     * Is phase timing pre-events?
     *
     * @return a boolean
     */
    public boolean isPreEvent() {
        return timing == Timing.PreEvent;
    }

    

    /**
     * The timing of a phase.
     */
    public enum Timing {
        /**
         * The phase is about what precedes the event.
         */
        PreEvent( "Pre-event" ),
        /**
         * The phase is about what happens during the event.
         */
        Concurrent( "Co-event" ),
        /**
         * The phase is about what happens after the event.
         */
        PostEvent( "Post-event" );
        /**
         * A label for the phase timing.
         */


        public static final String ANY_TIME = "During, before or after";
        public static final String BEFORE = "Before";
        public static final String DURING = "During";
        public static final String AFTER = "After";

        private String label;

        Timing( String label ) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        /**
         * Whether this timing follows another.
         *
         * @param timing a timing
         * @return a boolean
         */
        public boolean immediatelyFollows( Timing timing ) {
            switch ( this ) {
                case PreEvent:
                    return false;
                case Concurrent:
                    return timing.equals( PreEvent );
                case PostEvent:
                    return timing.equals( Concurrent );
                default:
                    throw new IllegalArgumentException( "Can't compare with " + timing.name() );
            }
        }

        public static String translateTiming( Phase.Timing timing ) {
            if ( timing == null )
                return ANY_TIME;
            else
                return timing == Phase.Timing.PreEvent
                        ? BEFORE
                        : timing == Phase.Timing.PostEvent
                        ? AFTER
                        : DURING;
        }

        public static Phase.Timing translateTiming( String name ) {
            if ( name.equals( ANY_TIME ) )
                return null;
            else
                return name.equals( BEFORE )
                        ? Phase.Timing.PreEvent
                        : name.equals( AFTER )
                        ? Phase.Timing.PostEvent
                        : Phase.Timing.Concurrent;
        }


        public static boolean areEqualOrNull( Timing timing, Timing otherTiming ) {
            return timing == null && otherTiming == null
                    || (timing != null && otherTiming != null && timing == otherTiming );
        }
    }
}
