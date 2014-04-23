package com.mindalliance.channels.core.model;

import com.mindalliance.channels.core.util.ChannelsUtils;
import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/23/14
 * Time: 12:04 PM
 */
public class Cycle implements Serializable {

    public enum TimeUnit {
        Year,
        Month,
        Week,
        Day,
        Hour,
        Minute,
        Second;

        public static TimeUnit getSubUnitOf( TimeUnit timeUnit ) {
            switch (timeUnit) {
                case Year: return TimeUnit.Month;
                case Month: return TimeUnit.Week;
                case Week: return TimeUnit.Day;
                case Day: return TimeUnit.Hour;
                case Hour: return TimeUnit.Minute;
                case Minute: return TimeUnit.Second;
                default: return null;
            }
        }

        public static int getMaxIndex( TimeUnit timeUnit ) {
            switch (timeUnit) {
                case Year: return 12;
                case Month: return 4;
                case Week: return 7;
                case Day: return 24;
                case Hour: return 60;
                case Minute: return 60;
                default: return 0;
            }
        }

    }

    public class Tranche {

        private TimeUnit timeUnit = TimeUnit.Day;
        private int index = 1;

        public Tranche( TimeUnit timeUnit, int index ) {
            this.timeUnit = timeUnit;
            setIndex( index );
        }

        public TimeUnit getTimeUnit() {
            return timeUnit;
        }

        public void setTimeUnit( TimeUnit tu ) {
            if ( timeUnit != tu ) {
                timeUnit = tu;
                resetTranches();
            }
        }

        public int getIndex() {
            return index;
        }

        public void setIndex( int index ) {
            this.index = Math.min( index, TimeUnit.getMaxIndex( timeUnit ) );
        }

        public String getLabel() {
            switch ( timeUnit ) {
                case Year:
                    return getMonthLabel( index );
                case Month:
                    return getWeekLabel( index );
                case Week:
                    return getDayLabel( index );
                case Day:
                    return getHourLabel( index );
                default:
                    return Integer.toString( index );
            }
        }

        private String getMonthLabel( int index ) {
            switch (index ) {
                case 1 : return "January";
                case 2 : return "February";
                case 3 : return "March";
                case 4 : return "April";
                case 5 : return "May";
                case 6 : return "June";
                case 7 : return "July";
                case 8 : return "August";
                case 9 : return "September";
                case 10 : return "October";
                case 11 : return "November";
                case 12 : return "December";
                default: return null;
            }
        }

        private String getWeekLabel( int index ) {
            return "Week " + index;
        }

        private String getDayLabel( int index ) {
            switch (index ) {
                case 1 : return "Monday";
                case 2 : return "Tuesday";
                case 3 : return "Wednesday";
                case 4 : return "Thursday";
                case 5 : return "Friday";
                case 6 : return "Saturday";
                case 7 : return "Sunday";
                default: return null;
            }
        }

        private String getHourLabel( int index ) {
            return Integer.toString( index ) + ":00";
        }

        @Override
        public String toString() {
            return getLabel();
        }

    }

    private TimeUnit timeUnit = TimeUnit.Day;
    private int skip = 1;
    private List<Integer> trancheIndices = new ArrayList<Integer>(  );

    public Cycle( ) {
    }

    public Cycle( Cycle cycle ) {
        timeUnit = cycle.getTimeUnit();
        skip = cycle.getSkip();
        setTrancheIndices( cycle.getTrancheIndices() );
    }

    public Cycle( TimeUnit timeUnit ) {
        this.timeUnit = timeUnit;
    }

    public void setSkip( int skip ) {
        this.skip = skip;
    }

    public void setTrancheIndices( List<Integer> trancheIndices ) {
        resetTranches();
        for ( int index : trancheIndices ) {
            addTrancheIndex( index );
        }
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit( TimeUnit timeUnit ) {
        this.timeUnit = timeUnit;
        resetTranches();
    }

    public int getSkip() {
        return skip;
    }

    public List<Integer> getTrancheIndices() {
        return trancheIndices;
    }

    public void addTrancheIndex( int index ) {
        if ( canAddTrancheIndex( index ) ) {
            trancheIndices.add( index );
        }
    }

    public void resetTranches() {
        trancheIndices = new ArrayList<Integer>(  );
    }

    private boolean canAddTrancheIndex( int index ) {
        TimeUnit subUnit = TimeUnit.getSubUnitOf( timeUnit );
        return index <= TimeUnit.getMaxIndex( subUnit )
            && !trancheIndices.contains( index );
    }

    public List<Tranche> getTranches() {
        List<Tranche> allTranches = getAllTranches();
        List<Tranche> tranches = new ArrayList<Tranche>();
        for ( Integer i : trancheIndices ) {
            tranches.add( allTranches.get( i ) );
        }
        return tranches;
    }

    private List<Tranche> getAllTranches() {
        List<Tranche> tranches = new ArrayList<Tranche>();
        TimeUnit subUnit = TimeUnit.getSubUnitOf( timeUnit );
        for ( int i = 1; i <= TimeUnit.getMaxIndex( subUnit ); i++ ) {
            tranches.add( new Tranche( subUnit, i ) );
        }
        return tranches;
    }

    public String getLabel() {
        StringBuilder sb = new StringBuilder(  );
        sb.append( "Every ");
        if ( hasTranches() ) {
            sb.append( ChannelsUtils.listToString( getTranches(), " and " ) );
            sb.append( " of every " );
        }
        if ( skip > 1 ) {
            sb.append( skip == 2 ? "other " : skip );
            sb.append( timeUnit.name().toLowerCase() )
                    .append( "s" );
        } else {
            sb.append( timeUnit.name().toLowerCase() );
        }
        return sb.toString();
    }

    private boolean hasTranches() {
        return !trancheIndices.isEmpty();
    }

    @Override
    public String toString() {
        return getLabel();
    }

    @Override
    public boolean equals( Object object ) {
        if ( object instanceof Cycle ) {
            Cycle other = (Cycle)object;
            return timeUnit == other.getTimeUnit()
                    && skip == other.getSkip()
                    && CollectionUtils.isEqualCollection( trancheIndices, other.getTrancheIndices() );
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash = hash + 31 * timeUnit.hashCode();
        hash = hash + 31 * skip;
        for ( Integer index : trancheIndices ) {
            hash = hash + 31 * index.hashCode();
        }
        return hash;
    }



}
