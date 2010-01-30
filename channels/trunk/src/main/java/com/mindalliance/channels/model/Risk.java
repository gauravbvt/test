package com.mindalliance.channels.model;

import com.mindalliance.channels.command.MappedObject;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.io.Serializable;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A risk to be mitigated in a segment.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 4, 2009
 * Time: 2:11:12 PM
 */
@Entity
public class Risk implements Serializable, Mappable {
    /**
     * Risk type.
     */
    private Type type;
    /**
     * Description.
     */
    private String description = "";
    /**
     * Organization at risk.
     */
    private Organization organization;
    /**
     * Severity of risk.
     */
    private Issue.Level severity;
    /**
     * Whether a risk ends with the segment that causes it.
     */
    private boolean endsWithSegment;

    public Risk() {
    }

    public Type getType() {
        return type;
    }

    public void setType( Type type ) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription( String description ) {
        this.description = description;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization( Organization organization ) {
        this.organization = organization;
    }

    public Issue.Level getSeverity() {
        return severity;
    }

    public void setSeverity( Issue.Level severity ) {
        this.severity = severity;
    }

    public boolean isEndsWithSegment() {
        return endsWithSegment;
    }

    public void setEndsWithSegment( boolean endsWithSegment ) {
        this.endsWithSegment = endsWithSegment;
    }

    /**
     * Get label.
     *
     * @return a string
     */
    @Transient
    public String getLabel() {
        return toString();
    }

    /**
     * {inheritDoc}
     */
    @Override
    public String toString() {
        return ( severity != null ? severity.toString().toLowerCase() : "" ) + " "
                + ( type != null ? type.getCategory().toLowerCase() : "" )
                + " risk to " + ( organization != null ? organization.getName() : "all" )
                + ( type != null ? " of " + type.getLabel() : "" ).toLowerCase();
    }

    /**
     * {inheritDoc}
     */
    public boolean equals( Object obj ) {
        if ( obj instanceof Risk ) {
            Risk other = (Risk) obj;
            return ( type != null && type == other.getType() )
                    && ( organization != null && organization == other.getOrganization() );

        } else {
            return false;
        }
    }

    /**
     * {inheritDoc}
     */
    public int hashCode() {
        int hash = 1;
        if ( type != null ) hash = hash * 31 + type.hashCode();
        if ( organization != null ) hash = hash * 31 + organization.hashCode();
        return hash;
    }

    /**
     * {inheritDoc}
     */
    public MappedObject map() {
        MappedObject mappedObject = new MappedObject( Risk.class );
        mappedObject.set( "severity", severity );
        mappedObject.set( "type", type );
        mappedObject.set( "organization", organization );
        mappedObject.set( "description", description );
        return mappedObject;
    }

    /**
     * Whether this references a model object.
     *
     * @param mo a model object
     * @return a boolean
     */
    public boolean references( ModelObject mo ) {
        return ModelObject.areIdentical( organization, mo );
    }

    /**
     * Return a full title label for the risk.
     *
     * @return a string
     */
    @Transient
    public String getFullTitle( ) {
        String label = "";
        label += severity.getLabel();
        label += " risk of ";
        label += type.getLabel().toLowerCase();
        label += " to ";
        label += organization.getName();
        return label;
    }

    /**
     * Return a title label for the risk.
     *
     * @param sep the separator string
     * @return a string
     */
    @Transient
    public String getTitle( String sep ) {
        return type.getLabel() + sep + organization.getName();
    }

    /**
     * Type of risk.
     */
    public enum Type {

        PropertyDamage( "Hazard", "Property damage" ),
        PropertyTheft( "Hazard", "Property theft" ),
        BodilyHarm( "Hazard", "Injury, disablement or death" ),
        EnvironmentalDegradation( "Hazard", "Environmental degradation" ),
        // OtherHazard( "Hazard", "Other" ),
        CostIncrease( "Financial", "Cost increase" ),
        LostRevenue( "Financial", "Lost revenue" ),
        AssetDevaluation( "Financial", "Asset devaluation" ),
        ReducedLiquidity( "Financial", "Reduced liquidity" ),
        ReducedCredit( "Financial", "Reduced credit" ),
        IncreasedLiability( "Financial", "Increased liability" ),
        FinesPenalties( "Financial", "Fines or penalties" ),
        // OtherFinancial( "Financial", "Other" ),
        DelayedDelivery( "Operational", "Delayed delivery" ),
        ImpairedQuality( "Operational", "Impaired quality" ),
        ProcessInefficiency( "Operational", "Process inefficiency" ),
        SupplyChainDisruption( "Operational", "Supply chain disruption" ),
        // OtherOperational( "Operational", "Other" ),
        OpportunityLoss( "Strategic", "Opportunity loss" ),
        ReputationDamage( "Strategic", "Damage to reputation" ),
        CompetitivenessLoss( "Strategic", "Loss of competitiveness" ),
        ProductObsolescence( "Strategic", "Product obsolescence" );
        // OtherStrategic( "Strategic", "Other" );


        public static final String[] Categories = {"Hazard", "Financial", "Operational", "Strategic"};
        private static Collator collator = Collator.getInstance();

        private String category;
        private String label;

        Type( String category, String label ) {
            this.category = category;
            this.label = label;
        }

        public String getCategory() {
            return category;
        }

        public String getLabel() {
            return label;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return category + " - " + label;
        }

        /**
         * List all risk types.
         *
         * @return a list of medium's
         */
        public static List<Type> types() {
            return Arrays.asList( Type.values() );
        }

        /**
         * List of all risk categories.
         *
         * @return a list of strings
         */
        public static List<String> categories() {
            List<String> categories = Arrays.asList( Categories );
            Collections.sort( categories );
            return categories;
        }

        /**
         * Get all risk types belonging to a given category.
         *
         * @param category a category
         * @return a list of types
         */
        @SuppressWarnings( "unchecked" )
        public static List<Type> typesInCategory( String category ) {
            List<Type> types = new ArrayList<Type>();
            for ( Type type : types() ) {
                if ( type.getCategory().equals( category ) ) types.add( type );
            }
            Collections.sort( types, new Comparator() {
                public int compare( Object type, Object other ) {
                    return collator.compare( ( (Type) type ).getLabel(), ( (Type) other ).getLabel() );
                }
            } );
            return types;
        }

    }


}
