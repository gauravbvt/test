// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.models;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import com.beanview.annotation.PropertyOptions;
import com.mindalliance.channels.data.definitions.TypedObject;
import com.mindalliance.channels.data.support.GUID;
import com.mindalliance.channels.util.CollectionType;

/**
 * A hypothetical scenario caused by one or more incidents (events
 * with causes external to the scenario) that drives responses with
 * their attendant information needs and productions, and generated
 * events that drive further responses etc. The analysis of a scenario
 * uncovers circumstances, activities, sharing needs and flows, as
 * well as issues (attached to elements).
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public class Storyline extends TypedObject {

    /** What happens. */
    private List<Occurrence> occurrences = new ArrayList<Occurrence>();

    /** What gets produced. */
    private List<Product> products = new ArrayList<Product>();

    /** Information sharing needs. */
    private List<SharingNeed> sharingNeeds = new ArrayList<SharingNeed>();

    /** Realized sharing needs. */
    private List<Flow> flows = new ArrayList<Flow>();

    /**
     * Default constructor.
     */
    public Storyline() {
        super();
        addDefaultEvent();
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public Storyline( GUID guid ) {
        super( guid );
        addDefaultEvent();
    }

    /**
     * Initialize new scenario with a default starting event.
     */
    private void addDefaultEvent() {
        Event event = new Event();
        event.setName( "Something happened" );
        addOccurrence( event );
    }

    /**
     * Return the list of all incidents (occurrences without stated
     * causes).
     */
    @PropertyOptions( ignore = true )
    public List<Occurrence> getIncidents() {
        List<Occurrence> incidents = new ArrayList<Occurrence>();
        CollectionUtils.select( occurrences, new Predicate() {

            public boolean evaluate( Object obj ) {
                Occurrence occurrence = (Occurrence) obj;
                return occurrence.isIncident();
            }
        } );
        return incidents;
    }

    /**
     * Get the events associated with this scenario.
     */
    @PropertyOptions( ignore = true )
    public List<Event> getEvents() {
        List<Event> events = new ArrayList<Event>();
        for ( Occurrence occ : getOccurrences() ) {
            if ( occ instanceof Event ) {
                events.add( (Event) occ );
            }
        }
        return events;
    }

    /**
     * Get the tasks associated with this scenario.
     */
    @PropertyOptions( ignore = true )
    public List<Task> getTasks() {
        List<Task> tasks = new ArrayList<Task>();
        for ( Occurrence occ : getOccurrences() ) {
            if ( occ instanceof Task ) {
                tasks.add( (Task) occ );
            }
        }
        return tasks;
    }

    /**
     * Get the activities associated with this scenario.
     */
    @PropertyOptions( ignore = true )
    @CollectionType( type = Activity.class )
    public List<Activity> getActivities() {
        // TODO
        return new ArrayList<Activity>();
    }

    /**
     * Get the communications associated with this scenario.
     */
    @PropertyOptions( ignore = true )
    @CollectionType( type = Communication.class )
    public List<Communication> getCommunications() {
        // TODO
        return new ArrayList<Communication>();
    }

    /**
     * Get the circumstances associated with this scenario.
     */
    @PropertyOptions( ignore = true )
    @CollectionType( type = Circumstance.class )
    public List<Circumstance> getCircumstances() {
        // TODO
        return new ArrayList<Circumstance>();
    }

    /**
     * Return the flows.
     */
    @CollectionType( type = Flow.class )
    public List<Flow> getFlows() {
        return flows;
    }

    /**
     * Set the flows.
     * @param flows the flows to set
     */
    public void setFlows( List<Flow> flows ) {
        this.flows = flows;
    }

    /**
     * Add a flow.
     * @param flow the flow
     */
    public void addFlows( Flow flow ) {
        flows.add( flow );
    }

    /**
     * Remove a flow.
     * @param flow the flow
     */
    public void removeFlows( Flow flow ) {
        flows.remove( flow );
    }

    /**
     * Return the occurrences.
     */
    @CollectionType( type = Occurrence.class )
    public List<Occurrence> getOccurrences() {
        return occurrences;
    }

    /**
     * Set the occurrences.
     * @param occurrences the occurrences to set
     */
    public void setOccurrences( List<Occurrence> occurrences ) {
        for ( Occurrence o : this.occurrences )
            o.setStoryline( null );

        this.occurrences = new ArrayList<Occurrence>( occurrences );

        for ( Occurrence o : occurrences )
            o.setStoryline( this );
    }

    /**
     * Add an occurrence.
     * @param occurrence the occurrence
     */
    public void addOccurrence( Occurrence occurrence ) {
        occurrences.add( occurrence );
        occurrence.setStoryline( this );
    }

    /**
     * Remove an occurrence.
     * @param occurrence the occurrence
     */
    public void removeOccurrence( Occurrence occurrence ) {
        occurrences.remove( occurrence );
        occurrence.setStoryline( null );
    }

    /**
     * Return the products.
     */
    @CollectionType( type = Product.class )
    public List<Product> getProducts() {
        return products;
    }

    /**
     * Set the products.
     * @param products the products to set
     */
    public void setProducts( List<Product> products ) {
        this.products = products;
    }

    /**
     * Add a product.
     * @param product the product
     */
    public void addProduct( Product product ) {
        products.add( product );
    }

    /**
     * Remove a product.
     * @param product the product
     */
    public void removeProduct( Product product ) {
        products.remove( product );
    }

    /**
     * Return the sharingNeeds.
     */
    @CollectionType( type = SharingNeed.class )
    public List<SharingNeed> getSharingNeeds() {
        return sharingNeeds;
    }

    /**
     * Set the sharing needs.
     * @param sharingNeeds the sharingNeeds to set
     */
    public void setSharingNeeds( List<SharingNeed> sharingNeeds ) {
        this.sharingNeeds = sharingNeeds;
    }

    /**
     * Add a sharing need.
     * @param sharingNeed the sharing need
     */
    public void addSharingNeed( SharingNeed sharingNeed ) {
        sharingNeeds.add( sharingNeed );
    }

    /**
     * Remove a sharing need.
     * @param sharingNeed the sharing need
     */
    public void removeSharingNeed( SharingNeed sharingNeed ) {
        sharingNeeds.remove( sharingNeed );
    }
}
