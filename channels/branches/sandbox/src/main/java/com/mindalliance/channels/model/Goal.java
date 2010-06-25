package com.mindalliance.channels.model;

import com.mindalliance.channels.command.MappedObject;
import com.mindalliance.channels.query.QueryService;
import com.mindalliance.channels.util.ChannelsUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.io.Serializable;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private Level level = Level.Low;
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
    public boolean isRiskMitigation() {
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

    public String getShortLabel() {
        return ( isGain() ? "Make " : "Mitigate " )
                + ( level != null ? getLevelLabel().toLowerCase() : "" ) + " "
                + ( category != null ? category.getGroup().toLowerCase() : "" )
                + ( isPositive() ? " gain" : " risk" )
                + ( category != null ? " of " + category.getName( positive ) : "" ).toLowerCase();
    }

    /**
     * {inheritDoc}
     */
    @Override
    public String toString() {
        return ( isGain() ? "Make " : "Mitigate " )
                + ( level != null ? getLevelLabel().toLowerCase() : "" ) + " "
                + ( category != null ? category.getGroup().toLowerCase() : "" )
                + intentLabel()
                + ( getOrganization() != null ? getOrganization().getName() : "all" )
                + ( category != null ? " of " + category.getName( positive ) : "" ).toLowerCase();
    }

    public String getLevelLabel() {
        return isPositive() ? getLevel().name() : getLevel().getNegativeLabel();
    }

    public String getSeverityLabel() {
        return getLevel().getNegativeLabel();
    }


    public String getCategoryLabel() {
        return getCategory().getLabel( isPositive() );
    }

    private String intentLabel() {
        return isPositive() ? " gain by " : " risk to ";
    }


    /**
     * {inheritDoc}
     */
    public boolean equals( Object obj ) {
        if ( obj instanceof Goal ) {
            Goal other = (Goal) obj;
            return
                    category != null && category == other.getCategory()
                            && positive == other.isPositive()
                            && level != null && level == other.getLevel()
                            && organization != null && organization.equals( other.getOrganization() )
                            && description != null && description.equals( other.getDescription() );

        } else {
            return false;
        }
    }

    /**
     * {inheritDoc}
     */
    public int hashCode() {
        int hash = 1;
        if ( category != null ) hash = hash * 31 + category.hashCode();
        if ( positive ) hash = hash * 31;
        if ( level != null ) hash = hash * 31 + level.hashCode();
        if ( organization != null ) hash = hash * 31 + organization.hashCode();
        if ( description != null ) hash = hash * 31 + description.hashCode();
        return hash;
    }

    /**
     * {inheritDoc}
     */
    public MappedObject map() {
        MappedObject mappedObject = new MappedObject( this.getClass() );
        mappedObject.set( "category", category );
        mappedObject.set( "positive", positive );
        mappedObject.set( "level", level );
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
        label += intentLabel();
        label += category.getLabel( positive ).toLowerCase();
        label += positive ? " for " : " to ";
        label += organization.getName();
        return label;
    }

    /**
     * Return a failure label for the goals.
     *
     * @param sep the separator string
     * @return a string
     */
    public String getFailureLabel( String sep ) {
        String label = category.getName( positive );
        label += isRiskMitigation() ? " not mitigated by " : " not achieved by ";
        label += sep + organization.getName();
        return label;
    }

    /**
     * Return a success label for the goals.
     *
     * @param sep the separator string
     * @return a string
     */
    public String getSuccessLabel( String sep ) {
        String label = category.getName( positive );
        label += isRiskMitigation() ? " mitigated by " : " achieved by ";
        label += sep + organization.getName();
        return label;
    }

    /**
     * Get full label for goal.
     *
     * @return a string
     */
    public String getFullLabel() {
        String label = isRiskMitigation() ? "Mitigates risk of " : "Achieves ";
        label += getFullTitle();
        return label;
    }

    /**
     * Whether this goal is broadly implied by a list of goals.
     *
     * @param goals a list of goals
     * @return a boolean
     */
    public boolean isImpliedIn( List<Goal> goals ) {
        return CollectionUtils.exists(
                goals,
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        Goal other = (Goal) object;
                        return Goal.this.getCategory().equals( other.getCategory() )
                                && Goal.this.getLevel().compareTo( other.getLevel() ) <= 0;
                    }
                }
        );
    }

    /**
     * Serialize goal to a map.
     *
     * @return a map
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put( "category", getCategory().name() );
        map.put( "description", getDescription() );
        map.put( "level", getLevel().name() );
        map.put( "positive", isPositive() );
        map.put( "ends", isEndsWithSegment() );
        map.put( "organization", Arrays.asList( getOrganization().getName(), getOrganization().isType() ) );
        return map;
    }

    /**
     * Instantiate a gaol from a serialization map.
     *
     * @param map          a map
     * @param queryService a query service
     * @return a goal
     */
    public static Goal fromMap( Map<String, Object> map, QueryService queryService ) {
        Goal goal = new Goal();
        goal.setCategory( Category.valueOf( (String) map.get( "category" ) ) );
        goal.setDescription( (String) map.get( "description" ) );
        goal.setLevel( Level.valueOf( (String) map.get( "level" ) ) );
        goal.setPositive( (Boolean) map.get( "positive" ) );
        goal.setEndsWithSegment( (Boolean) map.get( "ends" ) );
        goal.setOrganization( ChannelsUtils.retrieveEntity(
                Organization.class,
                map,
                "organization",
                queryService ) );
        return goal;
    }


    /**
     * Type of risk.
     */
    public enum Category {
        // Material
        Property( "Material", "Property damage", "Property improvement" ),
        Onwership( "Material", "Property loss", "Property acquisition" ),
        Health( "Material", "Injury, disablement or death", "Better health" ),
        Environment( "Material", "Environmental degradation", "Environmental improvement" ),
        //Financial
        Cost( "Financial", "Cost increase", "Cost decrease" ),
        Revenue( "Financial", "Lost revenue", "Increased revenue" ),
        Assets( "Financial", "Asset devaluation", "Asset value increase" ),
        Liquidity( "Financial", "Reduced liquidity", "Increased liquidity" ),
        Credit( "Financial", "Reduced credit", "Increased credit" ),
        Liability( "Financial", "Increased liability", "Recover damages" ),
        Award( "Financial", "Fines or penalties", "Reward" ),
        // Operational
        Delivery( "Operational", "Delayed delivery", "Faster delivery" ),
        Quality( "Operational", "Impaired quality", "Better quality" ),
        ProcessEfficiency( "Operational", "Process inefficiency", "Improved process efficiency" ),
        SupplyChain( "Operational", "Supply chain disruption", "Improved supply chain" ),
        // Strategic
        Opportunity( "Strategic", "Opportunity loss", "Opportunity gain" ),
        Reputation( "Strategic", "Damage to reputation", "Improved reputation" ),
        Competitiveness( "Strategic", "Loss of competitiveness", "Greater competitiveness" ),
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

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return name();
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
