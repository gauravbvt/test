package com.mindalliance.channels.model;

import com.mindalliance.channels.command.MappedObject;

import java.io.Serializable;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A positive or negative outcome to an organization.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 26, 2010
 * Time: 1:12:38 PM
 */
public class Outcome implements Serializable, Mappable {
    /**
     * Category of outcome.
     */
    private Category category;
    /**
     * WHether positive outcome (gain) or negative outcome (risk).
     */
    boolean positive = false;
    /**
     * Level of outcome.
     */
    private Level level;
    /**
     * Description.
     */
    private String description = "";
    /**
     * Organization at risk.
     */
    private Organization organization;
    /**
     * Whether the goal ends with the segment that causes it.
     */
    private boolean endsWithSegment;

    public Outcome( ) {

    }

    public Category getCategory() {
        return category;
    }

    public void setCategory( Category category ) {
        this.category = category;
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

    public boolean isEndsWithSegment() {
        return endsWithSegment;
    }

    public void setEndsWithSegment( boolean endsWithSegment ) {
        this.endsWithSegment = endsWithSegment;
    }

    public boolean isPositive() {
        return positive;
    }

    public void setPositive( boolean positive ) {
        this.positive = positive;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel( Level level ) {
        this.level = level;
    }

    /**
     * Get label.
     *
     * @return a string
     */
    public String getLabel() {
        return toString();
    }

    /**
     * {inheritDoc}
     */
    @Override
    public String toString() {

        return ( level != null ? levelLabel().toLowerCase() : "" ) + " "
                + ( category != null ? category.getGroup().toLowerCase() : "" )
                + outcomeLabel()
                + ( getOrganization() != null ? getOrganization().getName() : "all" )
                + ( category != null ? " of " + category.getName( positive ) : "" ).toLowerCase();
    }

    private String levelLabel() {
        return isPositive() ? getLevel().name() : getLevel().negative();
    }

    private String outcomeLabel() {
        return isPositive() ? " gain by " : " risk to ";
    }


    /**
     * {inheritDoc}
     */
    public boolean equals( Object obj ) {
        if ( obj instanceof Outcome ) {
            Outcome other = (Outcome) obj;
            return organization != null && organization == other.getOrganization();

        } else {
            return false;
        }
    }

    /**
     * {inheritDoc}
     */
    public int hashCode() {
        int hash = 1;
        if ( organization != null ) hash = hash * 31 + organization.hashCode();
        return hash;
    }

    /**
     * {inheritDoc}
     */
    public MappedObject map() {
        MappedObject mappedObject = new MappedObject( this.getClass() );
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
    public String getFullTitle() {
        String label = "";
        label += levelLabel();
        label += outcomeLabel();
        label += category.getLabel( positive ).toLowerCase();
        label += positive ? " for " : " to ";
        label += organization.getName();
        return label;
    }

    /**
     * Return a title label for the risk.
     *
     * @param sep the separator string
     * @return a string
     */
    public String getTitle( String sep ) {
        return category.getLabel( positive ) + sep + organization.getName();
    }

    /**
     * Type of risk.
     */
    public enum Category {
        // Material
        PropertyState( "Material", "Property damage", "Property improvement" ),
        PropertyOnwership( "Material", "Property loss", "Property acquisition" ),
        BodilyStatus( "Material", "Injury, disablement or death", "Better health" ),
        EnvironmentalStatus( "Material", "Environmental degradation", "Environmental improvement" ),
        //Financial
        Cost( "Financial", "Cost increase", "Cost decrease" ),
        Revenue( "Financial", "Lost revenue", "Increased revenue" ),
        Assets( "Financial", "Asset devaluation", "Asset value increase" ),
        Liquidity( "Financial", "Reduced liquidity", "Increased liquidity" ),
        Credit( "Financial", "Reduced credit", "Increased credit" ),
        Liability( "Financial", "Increased liability", "Recover damages" ),
        Penalties( "Financial", "Fines or penalties", "Reward" ),
        // Operational
        Delivery( "Operational", "Delayed delivery", "Faster delivery" ),
        Quality( "Operational", "Impaired quality", "Better quality" ),
        ProcessEfficiency( "Operational", "Process inefficiency", "Improved process efficiency" ),
        SupplyChain( "Operational", "Supply chain disruption", "Improved supply chain" ),
        // Strategic
        Opportunity( "Strategic", "Opportunity loss", "Opportunity gain" ),
        Reputation( "Strategic", "Damage to reputation", "Improved reputation" ),
        Competitiveness( "Strategic", "Loss of competitiveness", "Greater of competitiveness" ),
        ProductLife( "Strategic", "Product obsolescence", "Longer product life" );


        public static final String[] Categories = {"Material", "Financial", "Operational", "Strategic"};
        private static Collator collator = Collator.getInstance();

        /**
         * Outcome group.
         */
        private String group;
        /**
         * Negative outcome.
         */
        private String loss;
        /**
         * Positive outcome.
         */
        private String gain;

        Category( String group, String loss, String gain ) {
            this.group = group;
            this.loss = loss;
            this.gain = gain;
        }

        public String getGroup() {
            return group;
        }

        public String getLoss() {
            return loss;
        }

        public String getGain() {
            return gain;
        }

        public void setGain( String gain ) {
            this.gain = gain;
        }

        public String getName( boolean positive ) {
            return positive ? getGain() : getLoss();
        }

        /**
         * {@inheritDoc}
         */
        public String getLabel( boolean positive ) {
            return group + " - " + getName( positive );
        }

        // todo- remove
        public String toString() {
            throw new RuntimeException( "Don't call" );
        }

        /**
         * List all risk types.
         *
         * @return a list of medium's
         */
        public static List<Category> types() {
            return Arrays.asList( Category.values() );
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
         * Get all outcome types belonging to a given category.
         *
         * @param category a category
         * @param positive whether the outcome is positive of not
         * @return a list of types
         */
        @SuppressWarnings( "unchecked" )
        public static List<Category> typesInCategory( String category, final boolean positive ) {
            List<Category> types = new ArrayList<Category>();
            for ( Category type : types() ) {
                if ( type.getGroup().equals( category ) ) types.add( type );
            }
            Collections.sort( types, new Comparator() {
                public int compare( Object type, Object other ) {
                    return collator.compare(
                            ( (Category) type ).getName( positive ),
                            ( (Category) other ).getName( positive ) );
                }
            } );
            return types;
        }

    }

}
