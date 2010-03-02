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
 * A goal of an organization (risk mitigation of opportunity capture).
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 26, 2010
 * Time: 1:12:38 PM
 */
public class Goal implements Serializable, Mappable {
    /**
     * Category of goal.
     */
    private Category category;
    /**
     * Whether positive (capture gain) or negative (mitigate risk).
     */
    boolean positive = false;
    /**
     * Level of gain or risk.
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

    public Goal() {

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
     * Whether the goal is risk mitigation.
     *
     * @return a boolean
     */
    public boolean isRisk() {
        return !isPositive();
    }

    /**
     * Whether the goal is a opportunity capture.
     *
     * @return a boolean
     */
    public boolean isGain() {
        return isPositive();
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

        return ( level != null ? getLevelLabel().toLowerCase() : "" ) + " "
                + ( category != null ? category.getGroup().toLowerCase() : "" )
                + goalLabel()
                + ( getOrganization() != null ? getOrganization().getName() : "all" )
                + ( category != null ? " of " + category.getName( positive ) : "" ).toLowerCase();
    }

    public String getLevelLabel() {
        return isPositive() ? getLevel().name() : getLevel().negative();
    }

    private String goalLabel() {
        return isPositive() ? " gain by " : " risk to ";
    }


    /**
     * {inheritDoc}
     */
    public boolean equals( Object obj ) {
        if ( obj instanceof Goal ) {
            Goal other = (Goal) obj;
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
        label += getLevelLabel();
        label += goalLabel();
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
        String label = isRisk() ? "Mitigates " : "Gains ";
        label += category.getLabel( positive ) + sep + organization.getName();
        return label;
    }

    /**
     * Get full label for goal.
     *
     * @return a string
     */
    public String getFullLabel() {
        String label = isRisk() ? "Mitigates " : "Gains ";
        label += getFullTitle();
        return label;
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
         * Goal group.
         */
        private String group;
        /**
         * Negative goal.
         */
        private String loss;
        /**
         * Positive goal.
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
         * Get all goal types belonging to a given category.
         *
         * @param category a category
         * @param positive whether the goal is positive of not
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
