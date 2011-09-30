package com.mindalliance.channels.core.model;

import com.mindalliance.channels.core.Matcher;
import com.mindalliance.channels.core.query.Commitments;
import com.mindalliance.channels.core.query.QueryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An information sharing requirement on organizations within the scope of the plan.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 9/20/11
 * Time: 9:50 AM
 */
public class Requirement extends ModelObject {

    private static String UNNAMED = "UNNAMED";

    /**
     * Degree of satisfaction.
     */
    public enum Satisfaction {
        /**
         * Could not be satisfied however the plan is populated.
         */
        Impossible,
        /**
         * Not satisfied but could be if the plan is populated right.
         */
        Negative,
        /**
         * Weakly satisfied.
         */
        Weak,
        /**
         * Strongly satisfied.
         */
        Strong
    }

    /**
     * Information shared.
     */
    private String information = "";
    /**
     * Info tags.
     */
    private List<Tag> requiredTags = new ArrayList<Tag>();
    /**
     * Specification of committers.
     */
    private AssignmentSpec committerSpec = new AssignmentSpec();
    /**
     * Specification of beneficiaries.
     */
    private AssignmentSpec beneficiarySpec = new AssignmentSpec();
    /**
     * Number of committers per beneficiary.
     */
    private Cardinality cardinality = new Cardinality();

    public Requirement() {
        super( UNNAMED );
    }

    protected Requirement( String name ) {
        super();
        String s = name == null || name.trim().isEmpty() ? UNNAMED : name.trim();
        setName( s );
    }

    public String getInformation() {
        return information;
    }

    public void setInformation( String information ) {
        this.information = information;
    }

    public List<Tag> getRequiredTags() {
        return requiredTags;
    }

    public void setRequiredTags( List<Tag> requiredTags ) {
        this.requiredTags = requiredTags;
    }

    public void addRequiredTag( Tag tag ) {
        requiredTags.add( tag );
    }

    public void addRequiredTags( String s ) {
        for ( Tag tag : Tag.tagsFromString( s ) ) {
            addRequiredTag( tag );
        }
    }

    public AssignmentSpec getCommitterSpec() {
        return committerSpec;
    }

    public void setCommitterSpec( AssignmentSpec committerSpec ) {
        this.committerSpec = committerSpec;
    }

    public AssignmentSpec getBeneficiarySpec() {
        return beneficiarySpec;
    }

    public void setBeneficiarySpec( AssignmentSpec beneficiarySpec ) {
        this.beneficiarySpec = beneficiarySpec;
    }

    public Cardinality getCardinality() {
        return cardinality;
    }

    public void setCardinality( Cardinality cardinality ) {
        this.cardinality = cardinality == null ? new Cardinality() : cardinality;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( "Requirement" );
        sb.append( "[" );
        sb.append( getId() );
        sb.append( "] " );
        sb.append( getName() );
        return sb.toString();
    }

    /**
     * Whether two resource specs are compatible.
     *
     * @param spec       a resource spec
     * @param other      a resource spec
     * @param planLocale a place
     * @return a boolean
     */
    static protected boolean compatible( ResourceSpec spec, ResourceSpec other, Place planLocale ) {
        return ( ModelEntity.implies( spec.getActor(), other.getActor(), planLocale )
                || ModelEntity.implies( other.getActor(), spec.getActor(), planLocale ) )
                && ( ModelEntity.implies( spec.getRole(), other.getRole(), planLocale )
                || ModelEntity.implies( other.getRole(), spec.getRole(), planLocale ) )
                && ( ModelEntity.implies( spec.getJurisdiction(), other.getJurisdiction(), planLocale )
                || ModelEntity.implies( other.getJurisdiction(), spec.getJurisdiction(), planLocale ) )
                && ( ModelEntity.implies( spec.getOrganization(), other.getOrganization(), planLocale )
                || ModelEntity.implies( other.getOrganization(), spec.getOrganization(), planLocale ) );
    }


    /**
     * Evaluate the satisfaction of the requirement by an organization as committer or beneficiary.
     *
     * @param organization  an organization
     * @param asBeneficiary a boolean
     * @param queryService  a query service
     * @return a satisfaction rating
     */
    public Satisfaction satisfaction(
            Organization organization,
            boolean asBeneficiary,
            QueryService queryService ) {
        List<Flow> candidateFlows = findCandidateFlows( organization, asBeneficiary, queryService );
        if ( candidateFlows.isEmpty() ) {
            return Satisfaction.Impossible;
        } else {
            Commitments commitments = new Commitments( queryService, candidateFlows )
                    .satisfying( this );
            Map<Actor, List<Commitment>> groupedCommitments = groupByActor( commitments, asBeneficiary );
            if ( asBeneficiary ) {
                Satisfaction agentCountSatisfaction = getAgentCountSatisfaction(
                        groupedCommitments, getBeneficiarySpec().getCardinality() );
                Satisfaction sourcesCountSatisfaction = getSourcesCountSatisfaction( groupedCommitments );
                // return minimum satisfaction
                return agentCountSatisfaction.compareTo( sourcesCountSatisfaction ) <= 0 ?
                           agentCountSatisfaction
                        : sourcesCountSatisfaction;

            } else {
                return getAgentCountSatisfaction(
                        groupedCommitments, getCommitterSpec().getCardinality() );
            }
        }
    }

    private Map<Actor, List<Commitment>> groupByActor( Commitments commitments, boolean asBeneficiary ) {
        Map<Actor,List<Commitment>> groups = new HashMap<Actor, List<Commitment>>(  );
         for ( Commitment commitment : commitments ) {
             Actor actor = asBeneficiary
                     ? commitment.getBeneficiary().getActor()
                     : commitment.getCommitter().getActor();
             List<Commitment> list = groups.get( actor );
             if ( list == null ) {
                 list = new ArrayList<Commitment>();
                 groups.put( actor, list );
             }
             list.add( commitment );
         }
         return groups;
    }

    private Satisfaction getSourcesCountSatisfaction( Map<Actor, List<Commitment>> groupedCommitments ) {
        int minSourceCount = getMinSourceCount( groupedCommitments );
        int maxSourceCount = getMaxSourceCount( groupedCommitments );
        Cardinality cardinality = getCardinality();
        return cardinality.isRequiredCount( minSourceCount ) && cardinality.isRequiredCount( maxSourceCount ) ?
                cardinality.isSafeCount( minSourceCount ) && cardinality.isSafeCount( maxSourceCount )
                        ? Satisfaction.Strong
                        : Satisfaction.Weak
                : Satisfaction.Negative;
    }

    private Satisfaction getAgentCountSatisfaction(
            Map<Actor, List<Commitment>> groupedCommitments,
            Cardinality cardinality ) {
        int agentCount = groupedCommitments.keySet().size();
        return cardinality.isRequiredCount( agentCount ) ?
                cardinality.isSafeCount( agentCount )
                        ? Satisfaction.Strong
                        : Satisfaction.Weak
                : Satisfaction.Negative;
    }

    private int getMinSourceCount( Map<Actor, List<Commitment>> groupedCommitments ) {
        if ( groupedCommitments.isEmpty() ) return 0;
        int minCount = Integer.MAX_VALUE;
        for ( List<Commitment> list : groupedCommitments.values() ) {
            minCount = Math.min( minCount, list.size() );
        }
        return minCount;
     }

    private int getMaxSourceCount( Map<Actor, List<Commitment>> groupedCommitments ) {
        int maxCount = 0;
        for ( List<Commitment> list : groupedCommitments.values() ) {
            maxCount = Math.max( maxCount, list.size() );
        }
        return maxCount;
    }

    // Find all flows that match the requirement and where the organization could be the beneficiary or committer.
    @SuppressWarnings( "unchecked" )
    private List<Flow> findCandidateFlows(
            final Organization organization,
            final boolean asBeneficiary,
            QueryService queryService ) {
        final Place planLocale = queryService.getPlan().getLocale();
        return (List<Flow>) CollectionUtils.select(
                queryService.findAllFlows(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        Flow flow = (Flow) object;
                        return flow.isSharing()
                                && matchesFlow( (Flow) object, planLocale )
                                && matchesOrganization( organization, asBeneficiary, planLocale );
                    }
                }
        );
    }

    private boolean matchesOrganization(
            Organization organization,
            boolean asBeneficiary,
            Place planLocale ) {
        return asBeneficiary
                ? beneficiarySpec.appliesTo( organization, planLocale )
                : committerSpec.appliesTo( organization, planLocale );
    }

    private boolean matchesFlow( Flow flow, Place planLocale ) {
        return ( information.isEmpty()
                || !Matcher.matches( flow.getName(), information ) )
                // Match tags if required
                && Matcher.matchesAll( getRequiredTags(), flow.getTags() );
    }

    /**
      * Whether a commitment satisfies this requirement.
      *
      * @param commitment a commitment
      * @param planLocale the plan's locale
      * @return a boolean
      */
     public boolean satisfiedBy( Commitment commitment, Place planLocale ) {
         ResourceSpec cSpec = commitment.getCommitter().getResourceSpec();
         ResourceSpec bSpec = commitment.getBeneficiary().getResourceSpec();
         Flow flow = commitment.getSharing();
         return matchesFlow( flow, planLocale )
                 && cSpec.narrowsOrEquals( committerSpec.getResourceSpec(), planLocale )
                 && bSpec.narrowsOrEquals( beneficiarySpec.getResourceSpec(), planLocale );
     }

    public Map<String, Object> mapState() {
        Map<String, Object> state = super.mapState();
        state.put( "information", getInformation() );
        state.put( "requiredTags", Tag.tagsToString( getRequiredTags() ) );
        state.put( "cardinality", getCardinality().mapState() );
        state.put( "committerSpec", getCommitterSpec().mapState() );
        state.put( "beneficiarySpec", getBeneficiarySpec().mapState() );
        return state;
    }

    public void initFromMap( Map<String, Object> state, QueryService queryService ) {
        super.initFromMap( state, queryService );
        setInformation( (String) state.get( "information" ) );
        setRequiredTags( Tag.tagsFromString( ( String )state.get( "requiredTags" ) ) );
        Cardinality card = new Cardinality();
        card.initFromMap( (Map<String, Object>) state.get( "cardinality" ) );
        setCardinality( card );
        AssignmentSpec cSpec = new AssignmentSpec();
        cSpec.initFromMap( (Map<String, Object> )state.get( "committerSpec" ), queryService );
        setCommitterSpec( cSpec );
        AssignmentSpec bSpec = new AssignmentSpec();
        bSpec.initFromMap( (Map<String, Object> )state.get( "beneficiarySpec" ), queryService );
        setCommitterSpec( bSpec );
    }


    /**
     * Required count.
     */
    public static class Cardinality implements Serializable {

        /**
         * Minimum numbers of satisfactions.
         */
        private int minCount = 1;
        /**
         * Maximum numbers of satisfactions.
         */
        private Integer maxCount;

        /**
         * Safe number of satisfactions.
         */
        private int safeCount = 1;

        // The default is "at least one and one is enough".
        public Cardinality() {
        }

        public Integer getMinCount() {
            return minCount;
        }

        public void setMinCount( int minCount ) {
            int val = Math.max( 0, minCount );
            if ( maxCount != null ) val = Math.min( val, maxCount );
            this.minCount = val;
            safeCount = Math.max( this.minCount, safeCount );
        }

        public Integer getMaxCount() {
            return maxCount;
        }

        public void setMaxCount( int maxCount ) {
            this.maxCount = Math.max( minCount, Math.max( 0, maxCount ) );
            safeCount = Math.min( this.maxCount, safeCount );
        }

        public void unsetMaxCount() {
            maxCount = null;
        }

        public int getSafeCount() {
            return safeCount;
        }

        public void setSafeCount( int safeCount ) {
            int val = safeCount;
            if ( maxCount != null ) val = Math.min( safeCount, maxCount );
            this.safeCount = Math.max( val, minCount );
        }

        public boolean isRequiredCount( int count ) {
            return count >= minCount
                    && !( maxCount != null && count > maxCount );
        }

        public boolean isSafeCount( int count ) {
            return count >= safeCount;
        }


        public Map<String, Object> mapState() {
            Map<String, Object> state = new HashMap<String, Object>();
            state.put( "minCount", getMinCount() );
            state.put( "maxCount", getMaxCount() );
            state.put( "safeCount", getSafeCount() );
            return state;
        }

        public void initFromMap( Map<String, Object> state ) {
            setMinCount( (Integer)state.get( "minCount" ) );
            setMaxCount( (Integer) state.get( "maxCount" ) );
            setSafeCount( (Integer) state.get( "safeCount" ) );
        }
    }

    /**
     * Assignment specification.
     */
    public static class AssignmentSpec implements Serializable {
        /**
         * Task name.
         */
        private String taskName = "";
        /**
         * Task tags.
         */
        private List<Tag> requiredTags = new ArrayList<Tag>();

        /**
         * Task resource spec.
         */
        private ResourceSpec resourceSpec = new ResourceSpec();

        /**
         * Event.
         */
        private Event event;
        /**
         * Timing.
         */
        private Phase.Timing timing;
        /**
         * Number of committers/beneficiaries per specified organization.
         */
        private Cardinality cardinality = new Cardinality();

        public AssignmentSpec() {
        }

        public String getTaskName() {
            return taskName;
        }

        public void setTaskName( String taskName ) {
            this.taskName = taskName == null ? "" : taskName.trim();
        }

        public ResourceSpec getResourceSpec() {
            return resourceSpec;
        }

        public void setResourceSpec( ResourceSpec resourceSpec ) {
            this.resourceSpec = resourceSpec;
        }

        public List<Tag> getRequiredTags() {
            return requiredTags;
        }

        public void setRequiredTags( List<Tag> requiredTags ) {
            this.requiredTags = requiredTags;
        }

        public void addRequiredTag( Tag tag ) {
            requiredTags.add( tag );
        }

        public void addRequiredTags( String s ) {
            for ( Tag tag : Tag.tagsFromString( s ) ) {
                addRequiredTag( tag );
            }
        }

        public Event getEvent() {
            return event;
        }

        public void setEvent( Event event ) {
            this.event = event;
        }

        public Phase.Timing getTiming() {
            return timing;
        }

        public void setTiming( Phase.Timing timing ) {
            this.timing = timing;
        }

        public Cardinality getCardinality() {
            return cardinality;
        }

        public void setCardinality( Cardinality cardinality ) {
            this.cardinality = cardinality == null ? new Cardinality() : cardinality;
        }

        public Organization getOrganization() {
            return getResourceSpec().getOrganization();
        }

        private boolean inRequiredContext( Part part, Place planLocale ) {
            EventPhase partEventPhase = part.getSegment().getEventPhase();
            return ( getEvent() == null ||
                    partEventPhase.getEvent().narrowsOrEquals(
                            getEvent(),
                            planLocale )
                            && ( getTiming() == null ||
                            partEventPhase.getPhase().getTiming().equals( getTiming() ) ) );
        }

        private boolean matchesTask( Part part, Place planLocale ) {
            // Match task names if required
            return ( taskName.isEmpty()
                    || !Matcher.matches( taskName, part.getTask() ) )
                    // Match tags if required
                    && Matcher.matchesAll( getRequiredTags(), part.getTags() )
                    && inRequiredContext( part, planLocale );
        }

        public boolean appliesTo( Organization organization, Place planLocale ) {
            Organization orgSpec = getResourceSpec().getOrganization();
            return orgSpec == null || organization.narrowsOrEquals( orgSpec, planLocale );
        }

        public Map<String, Object> mapState() {
            Map<String,Object>state = new HashMap<String, Object>(  );
            state.put( "taskName", getTaskName() );
            state.put( "requiredTags", Tag.tagsToString( getRequiredTags() ) );
            state.put( "cardinality", getCardinality().mapState() );
            if ( event != null )
                state.put( "event", event.getName() );
            if ( timing != null )
                state.put( "timing", timing.name() );
            state.put( "resourceSpec", getResourceSpec().mapState() );
            return state;
        }

        @SuppressWarnings( "unchecked" )
        public void initFromMap( Map<String, Object> state, QueryService queryService ) {
           setTaskName( (String)state.get( "taskName" ) );
            setRequiredTags( Tag.tagsFromString( (String)state.get( "requiredTags" ) ) );
            Cardinality card = new Cardinality();
            card.initFromMap( (Map<String,Object>)state.get( "cardinality" ));
            setCardinality( card );
            ResourceSpec spec = new ResourceSpec();
            spec.initFromMap( (Map<String,Object>)state.get( "resourceSpec" ), queryService );
        }
    }
}
