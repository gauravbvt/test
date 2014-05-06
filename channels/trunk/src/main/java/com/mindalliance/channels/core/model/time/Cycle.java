package com.mindalliance.channels.core.model.time;

import com.mindalliance.channels.core.util.ChannelsUtils;
import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/23/14
 * Time: 12:04 PM
 */
public class Cycle implements Serializable {

    private TimeUnit timeUnit = TimeUnit.Day;
    private int skip = 1;
    private List<Integer> trancheIndices = new ArrayList<Integer>();

    public Cycle() {
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
        Collections.sort( trancheIndices );
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

    private void addTrancheIndex( int index ) {
        if ( canAddTrancheIndex( index ) ) {
            trancheIndices.add( index );
        }
    }

    public void addTrancheIndexAndSort( int index ) {
        addTrancheIndex( index );
        Collections.sort( trancheIndices );
    }

    public void resetTranches() {
        trancheIndices = new ArrayList<Integer>();
    }

    private boolean canAddTrancheIndex( int index ) {
        return index <= TimeUnit.getMaxTrancheIndex( timeUnit )
                && !trancheIndices.contains( index );
    }

    public List<Tranche> getTranches() {
        List<Tranche> allTranches = getAllPossibleTranches();
        List<Tranche> tranches = new ArrayList<Tranche>();
        for ( Integer i : trancheIndices ) {
            tranches.add( allTranches.get( i ) );
        }
        return tranches;
    }

    public void setTranches( List<Tranche> tranches ) {
        resetTranches();
        List<Tranche> allTranches = getAllPossibleTranches();
        for ( Tranche tranche : tranches ) {
            int index = allTranches.indexOf( tranche );
            if ( index >= 0 )
                addTrancheIndex( index );
        }
        Collections.sort( trancheIndices );
    }

    public Tranche trancheFromLabel( String label ) {
        for ( Tranche tranche : getAllPossibleTranches() ) {
            if ( tranche.getLabel().equals( label ) ) {
                return tranche;
            }
        }
        return null;
    }

    public List<Tranche> getAllPossibleTranches() {
        List<Tranche> tranches = new ArrayList<Tranche>();
        TimeUnit subUnit = TimeUnit.getSubUnitOf( timeUnit );
        for ( int i = 1; i <= TimeUnit.getMaxTrancheIndex( timeUnit ); i++ ) {
            tranches.add( new Tranche( this, subUnit, i ) );
        }
        return tranches;
    }

    public String getLabel() {
        StringBuilder sb = new StringBuilder();
        sb.append( "every " );
        if ( hasTranches() ) {
            sb.append( ChannelsUtils.listToString( getTranches(), " and " ) );
            sb.append( " of every " );
        }
        if ( skip > 1 ) {
            sb.append( skip == 2 ? "other" : skip )
                    .append( " " )
                    .append( timeUnit.name().toLowerCase() )
                    .append( skip == 2 ? "" : "s" );
        } else {
            sb.append( timeUnit.name().toLowerCase() );
        }
        return sb.toString();
    }

    private boolean hasTranches() {
        return !trancheIndices.isEmpty();
    }

    public Delay findSmallestRepeatInterval() {
        if ( !hasTranches() ) {
            return new Delay( skip, timeUnit );
        } else {
            int i = findSmallestTrancheInterval();
            return new Delay( i, TimeUnit.getSubUnitOf( timeUnit ) );
        }
    }

    private int findSmallestTrancheInterval() {
        int maxIndex = TimeUnit.getMaxTrancheIndex( timeUnit );
        int min = maxIndex;
        if ( trancheIndices.size() > 1 ) {
            for ( int i = 1; i < trancheIndices.size(); i++ ) {
                min = Math.min( min, trancheIndices.get( i ) - trancheIndices.get( i - 1 ) );
            }
            min = Math.min( min, maxIndex - trancheIndices.get( trancheIndices.size() - 1 ) + trancheIndices.get( 0 ) );
        }
        return min;
    }

    public Delay findLargestRepeatInterval() {
        if ( !hasTranches() ) {
            return new Delay( skip, timeUnit );
        } else {
            int i = findLargestTrancheInterval();
            return new Delay( i, TimeUnit.getSubUnitOf( timeUnit ) );
        }
    }

    private int findLargestTrancheInterval() {
        int maxIndex = TimeUnit.getMaxTrancheIndex( timeUnit );
        int max = 0;
        if ( trancheIndices.size() > 1 ) {
            for ( int i = 1; i < trancheIndices.size(); i++ ) {
                max = Math.max( max, trancheIndices.get( i ) - trancheIndices.get( i - 1 ) );
            }
            max = Math.max( max, maxIndex - trancheIndices.get( trancheIndices.size() - 1 ) + trancheIndices.get( 0 ) );
        } else
            max = maxIndex;
        return max;
    }


    @Override
    public String toString() {
        return getLabel();
    }

    @Override
    public boolean equals( Object object ) {
        if ( object instanceof Cycle ) {
            Cycle other = (Cycle) object;
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
