// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.model.support;

import java.beans.PropertyDescriptor;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.acegisecurity.context.SecurityContextHolder;

import com.mindalliance.channels.User;
import com.mindalliance.channels.util.AbstractJavaBean;

import static com.mindalliance.channels.model.support.Suggestion.Vote.*;

/**
 * A suggested value for a property of a java bean.
 *
 * @param <T> the type of the suggested values.
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public class Suggestion<T extends Comparable> extends AbstractJavaBean
    implements Comparable<Suggestion> {

    /**
     * The rounder value when comparing ratings.
     */
    private static final double ROUNDER = 100.0;

    /**
     * The default percentage when there are no votes.
     */
    private static final double DEFAULT_PERCENT = 0.5;

    /**
     * Types of vote for a value:
     * <ul>
     *      <li><b>Yay</b>: a yes vote.</li>
     *      <li><b>Nay</b>: a no vote.</li>
     *      <li><b>Abstention</b>: no vote at all.</li>
     * </ul>
     */
    public enum Vote { Abstention, Nay, Yay };

    private final PropertyDescriptor property ;
    private final T value;
    private Map<User,Suggestion.Vote> votingDetails ;

    //-----------------------------
    /**
     * Create a new suggested value for a property.
     * @param property the property
     * @param value the value
     */
    public Suggestion( PropertyDescriptor property, T value ) {
        this.property = property;
        this.value = value;

        // Creating a suggestion implies a yes vote from the user
        setVote( Yay );
    }

    /**
     * Return the value of the property for this suggestion.
     */
    public PropertyDescriptor getProperty() {
        return this.property;
    }

    /**
     * Return the value of the suggestion.
     */
    public T getValue() {
        return this.value;
    }

    //-----------------------------
    private synchronized Map<User,Suggestion.Vote> getVotingDetails() {
        if ( this.votingDetails == null )
            this.votingDetails =
                Collections.synchronizedMap(
                        new HashMap<User,Suggestion.Vote>() );

        return this.votingDetails;
    }

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext()
                        .getAuthentication().getPrincipal();
    }

    //-----------------------------
    /**
     * Set the vote for the current user.
     * @param vote the vote
     */
    public void setVote( Vote vote ) {
        double oldRating ;
        double newRating ;
        int oldTotal ;
        int newTotal ;
        SortedSet<User> newSupporters ;
        SortedSet<User> newDetractors ;

        synchronized ( getVotingDetails() ) {
            oldRating = getRating();
            oldTotal = getTotalVotes();

            if ( vote == Abstention )
                this.votingDetails.remove( getCurrentUser() );
            else
                this.votingDetails.put( getCurrentUser(), vote );

            newTotal = getTotalVotes();
            newRating = getRating();
            newSupporters = getSupporters();
            newDetractors = getDetractors();
        }

        // Raise appropriate change events
        super.firePropertyChange( "totalVotes", oldTotal, newTotal );
        super.firePropertyChange( "rating", oldRating, newRating );
        super.firePropertyChange( "supporters", null, newSupporters );
        super.firePropertyChange( "detractors", null, newDetractors );
    }

    /**
     * Get the current user's vote for this suggestion.
     * @return the vote
     */
    public Vote getVote() {

        if ( this.votingDetails != null ) {
            Vote vote = getVotingDetails().get( getCurrentUser() );
            if ( vote != null )
                return vote;
        }

        return Abstention;
    }

    //-----------------------------
    /**
     * Return a list of users who support this value (yes vote).
     */
    public SortedSet<User> getSupporters() {

        Set<User> supporters = new HashSet<User>();
        if ( this.votingDetails != null ) synchronized ( this.votingDetails ) {
            for ( Entry<User,Suggestion.Vote> e
                    : this.votingDetails.entrySet() )

                if ( e.getValue() == Yay )
                    supporters.add( e.getKey() );
            }

        return Collections.unmodifiableSortedSet(
                new TreeSet<User>( supporters ) );
    }

    /**
     * Return a list of users who do not support this value (no vote).
     */
    public SortedSet<User> getDetractors() {

        Set<User> supporters = new HashSet<User>();
        if ( this.votingDetails != null ) synchronized ( this.votingDetails ) {
            for ( Entry<User,Suggestion.Vote> e
                    : this.votingDetails.entrySet() )

                if ( e.getValue() == Nay )
                    supporters.add( e.getKey() );
            }

        return Collections.unmodifiableSortedSet(
                new TreeSet<User>( supporters ) );
    }

    //-----------------------------
    /**
     * Return the rating of this suggestion.
     * @return yes votes / total votes,  or 50% if no votes.
     */
    public double getRating() {
        int yesVotes = 0 ;
        int noVotes  = 0 ;

        if ( this.votingDetails != null ) synchronized ( this.votingDetails ) {
            for ( Entry<User,Suggestion.Vote> e
                    : this.votingDetails.entrySet() )

                if ( e.getValue() == Nay )
                    noVotes += 1 ;
                else if ( e.getValue() == Yay )
                    yesVotes += 1 ;
            }

        return yesVotes == noVotes?
                DEFAULT_PERCENT
              : yesVotes / ( yesVotes + noVotes ) ;
    }

    /**
     * Return the total yes/no votes for this suggestion.
     */
    public int getTotalVotes() {
        int totalVotes = 0 ;

        if ( this.votingDetails != null ) synchronized ( this.votingDetails ) {
            for ( Entry<User,Suggestion.Vote> e
                    : this.votingDetails.entrySet() )

                if ( e.getValue() == Nay
                        || e.getValue() == Yay )
                    totalVotes += 1 ;
            }

        return totalVotes ;
    }

    //-----------------------------
    /**
     * Return a string representation of this suggestion.
     */
    public String toString() {
        return MessageFormat.format(
                "{0} ({1,percent})",
                getValue(), getRating() );
    }

    //-----------------------------
    /**
     * Compare this suggestion to another.
     * <p>Implements sorting by descending rating (rounded to nearest
     * percent), and for equivalent ratings, by ascending values.
     * Null values are considered smaller than anything else.</p>
     *
     * @param other the suggestion to compare to.
     */
    @SuppressWarnings( "unchecked" )
    public int compareTo( Suggestion other ) {
        long thisRating = Math.round( ROUNDER * this.getRating() );
        long otherRating = Math.round( ROUNDER * other.getRating() );

        return thisRating > otherRating ?  1
             : thisRating < otherRating
                || ( getValue() == null && other.getValue() != null ) ?  -1
             : getValue() == null && other.getValue() == null ? 0
             : ( (Comparable) getValue() ).compareTo( other.getValue() );
    }

    //-----------------------------
    /**
     * Compare this suggestion with another.
     * @param suggestion the other suggestion
     * @return the result of comparing respective values.
     */
    public boolean equals( Suggestion suggestion ) {
        return ( getValue() == suggestion.getValue() )
            || ( getValue() != null
                    && getValue().equals( suggestion.getValue() ) )
            ;
    }

    /**
     * Compare this object to another.
     * @param obj the other object
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( Object obj ) {
        return obj != null
            && obj.getClass() == this.getClass()
            && equals( (Suggestion) obj );
    }

    //-----------------------------
    /**
     * Return the hash value to use in sets, maps, etc.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return getValue() == null ? 0 : getValue().hashCode();
    }

}
