package com.mindalliance.channels.core.model;

import com.mindalliance.channels.core.Matcher;
import com.mindalliance.channels.core.query.Commitments;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.pages.Channels;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
public class Requirement extends ModelObject implements Countable {

    private static String UNNAMED = "UNNAMED";
    //     public static final RFISurvey UNKNOWN = new RFISurvey( Channels.UNKNOWN_RFI_SURVEY_ID );

    public static final Requirement UNKNOWN = new Requirement( Channels.UNKNOWN_REQUIREMENT_ID );


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
    private List<Tag> infoTags = new ArrayList<Tag>();
    /**
     * Required elements of information.
     * Meaningful only if information is named.
     * Empty list means required eois unspecified.
     */
    private List<String> eois = new ArrayList<String>();
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

    public Requirement( long id ) {
        super( UNNAMED );
        setId( id );
    }

    protected Requirement( String name ) {
        super();
        String s = name == null || name.trim().isEmpty() ? UNNAMED : name.trim();
        setName( s );
    }

    public static String classLabel() {
        return "sharing requirements";
    }

    @Override
    public String getClassLabel() {
        return classLabel();
    }

    @Override
    public boolean isSegmentObject() {
        return false;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation( String information ) {
        this.information = information;
    }

    public List<Tag> getInfoTags() {
        return infoTags;
    }

    public String getInfoTagsAsString() {
        return Tag.tagsToString( getInfoTags() );
    }

    public void setInfoTags( List<Tag> infoTags ) {
        this.infoTags = infoTags;
    }

    public void addInfoTags( Tag tag ) {
        infoTags.add( tag );
    }

    public void addInfoTags( String s ) {
        for ( Tag tag : Tag.tagsFromString( s ) ) {
            addInfoTags( tag );
        }
    }

    public List<String> getEois() {
        return eois;
    }

    public void setEois( List<String> eois ) {
        this.eois = eois;
    }

    public void addEoi( String eoi ) {
        eois.add( eoi );
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

    public Requirement transientCopy() {
        Requirement copy = new Requirement( getName() );
        copy.setDescription( getDescription() );
        copy.setId( getId() ); // requirement must never be persisted!
        copy.setInformation( information );
        copy.setInfoTags( Tag.copy( infoTags ) );
        copy.setEois( new ArrayList<String>( eois ) );
        copy.setCardinality( cardinality.copy() );
        copy.setCommitterSpec( committerSpec.copy() );
        copy.setBeneficiarySpec( beneficiarySpec.copy() );
        return copy;
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
     * Evaluate the satisfaction of the requirement by an organization as committer or beneficiary in any situation.
     *
     * @param organization  an organization
     * @param asBeneficiary a boolean
     * @param queryService  a query service
     * @param analyst       an analyst
     * @return a satisfaction rating
     */
    public Satisfaction satisfaction(
            Organization organization,
            boolean asBeneficiary,
            QueryService queryService,
            Analyst analyst ) {
        return satisfaction( organization, asBeneficiary, null, null, queryService, analyst );
    }

    /**
     * Evaluate the satisfaction of the requirement by an organization as committer or beneficiary
     * in a given situation (if timing and/or event are not null).
     *
     * @param organization  an organization
     * @param asBeneficiary a boolean
     * @param timing        a phase timing
     * @param event         an event
     * @param queryService  a query service
     * @param analyst       an analyst
     * @return a satisfaction rating
     */
    public Satisfaction satisfaction(
            Organization organization,
            boolean asBeneficiary,
            Phase.Timing timing,
            Event event,
            QueryService queryService,
            Analyst analyst ) {
        Satisfaction[] satisfactions = getSatisfactions(
                organization,
                asBeneficiary,
                timing,
                event,
                queryService,
                analyst );
        Satisfaction agentCountSatisfaction = satisfactions[0];
        Satisfaction sourcesPerReceiverSatisfaction = asBeneficiary ? satisfactions[1] : null;
        assert agentCountSatisfaction != null;
        if ( asBeneficiary ) {
            if ( agentCountSatisfaction == Satisfaction.Impossible )
                return agentCountSatisfaction;
            else {
                assert sourcesPerReceiverSatisfaction != null;
                return agentCountSatisfaction.compareTo( sourcesPerReceiverSatisfaction ) <= 0
                        ? agentCountSatisfaction
                        : sourcesPerReceiverSatisfaction;
            }
        } else {
            return agentCountSatisfaction;
        }
    }

    @SuppressWarnings( "unchecked" )
    private Satisfaction[] getSatisfactions(
            Organization organization,
            boolean asBeneficiary,
            final Phase.Timing timing,
            final Event event,
            final QueryService queryService,
            final Analyst analyst ) {
        Satisfaction[] satisfactions = new Satisfaction[2];
        Satisfaction agentCountSatisfaction = null;
        Satisfaction sourcesPerReceiverSatisfaction = null;
        List<Flow> candidateFlows = findCandidateFlows( organization, asBeneficiary, queryService );
        if ( candidateFlows.isEmpty() ) {
            satisfactions[0] = Satisfaction.Impossible;
            if ( asBeneficiary ) {
                satisfactions[1] = Satisfaction.Impossible;
            }
        } else {
            Commitments commitments = new Commitments( queryService, candidateFlows );
            final Plan plan = queryService.getPlan();
            final Place planLocale = plan.getLocale();
            Iterator<Commitment> commitmentIterator = (Iterator<Commitment>) IteratorUtils.filteredIterator(
                    commitments.iterator(),
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            Commitment commitment = (Commitment) object;
                            return commitment.isInSituation( timing, event, plan.getLocale() )
                                    && satisfiedBy( commitment, planLocale )
                                    && analyst.canBeRealized( commitment, plan, queryService );
                        }
                    }
            );
            Map<Actor, List<Commitment>> groupedCommitments = new HashMap<Actor, List<Commitment>>();
            while ( commitmentIterator.hasNext()
                    && ( agentCountSatisfaction == null
                    || ( asBeneficiary && sourcesPerReceiverSatisfaction == null ) ) ) {
                Commitment commitment = commitmentIterator.next();
                groupByActor( groupedCommitments, commitment, asBeneficiary );
                if ( asBeneficiary ) {
                    agentCountSatisfaction = getAgentCountSatisfaction(
                            groupedCommitments,
                            getBeneficiarySpec().getCardinality(),
                            commitmentIterator.hasNext() );
                    sourcesPerReceiverSatisfaction =
                            getSourcesPerReceiverCountSatisfaction(
                                    groupedCommitments,
                                    commitmentIterator.hasNext() );
                } else {
                    agentCountSatisfaction = getAgentCountSatisfaction(
                            groupedCommitments,
                            getCommitterSpec().getCardinality(),
                            commitmentIterator.hasNext() );
                }
            }
        }
        if ( agentCountSatisfaction == null )
            agentCountSatisfaction = Satisfaction.Negative;
        if ( asBeneficiary && sourcesPerReceiverSatisfaction == null )
            sourcesPerReceiverSatisfaction = Satisfaction.Negative;
        satisfactions[0] = agentCountSatisfaction;
        satisfactions[1] = sourcesPerReceiverSatisfaction;
        return satisfactions;
    }

    /**
     * Return the reason a requirement is not satisfied by an organization as committer or beneficiary.
     *
     * @param organization  an organization
     * @param asBeneficiary a boolean
     * @param queryService  a query service
     * @param analyst       an analyst
     * @return a string
     */
    public String dissatisfactionSummary(
            Organization organization,
            boolean asBeneficiary,
            QueryService queryService,
            Analyst analyst ) {
        String dissatisfaction = "";
        Satisfaction[] satisfactions = getSatisfactions(
                organization,
                asBeneficiary,
                null,
                null,
                queryService,
                analyst );
        Satisfaction agentCountSatisfaction = satisfactions[0];
        Satisfaction sourcesPerReceiverSatisfaction = asBeneficiary ? satisfactions[1] : null;
        if ( agentCountSatisfaction == Satisfaction.Impossible ) {
            dissatisfaction = "No flow in the plan could possibly have "
                    + organization.getName()
                    + " satisfy the requirement";
        } else {
            if ( agentCountSatisfaction != Satisfaction.Negative
                    && ( !asBeneficiary || sourcesPerReceiverSatisfaction != Satisfaction.Negative ) )
                return dissatisfaction;
            else if ( asBeneficiary ) {
                if ( agentCountSatisfaction == Satisfaction.Negative ) {
                    dissatisfaction = "There is not "
                            + cardinality.toString()
                            + " agent(s) in "
                            + organization.getName()
                            + " receiving "
                            + "the specified info";
                } else {
                    dissatisfaction = "Not all receiving agents in "
                            + organization.getName()
                            + " have "
                            + cardinality.toString()
                            + " alternate source(s) as required";
                }

            } else {
                dissatisfaction = "There is not "
                        + cardinality.toString()
                        + " agent(s) in "
                        + organization.getName()
                        + " sharing "
                        + "the specified info";
            }

        }
        return dissatisfaction;
    }

    private void groupByActor( Map<Actor,
            List<Commitment>> groupedCommitments,
                               Commitment commitment,
                               boolean asBeneficiary ) {
        Actor actor = asBeneficiary
                ? commitment.getBeneficiary().getActor()
                : commitment.getCommitter().getActor();
        List<Commitment> list = groupedCommitments.get( actor );
        if ( list == null ) {
            list = new ArrayList<Commitment>();
            groupedCommitments.put( actor, list );
        }
        list.add( commitment );
    }

    private Satisfaction getAgentCountSatisfaction( Map<Actor, List<Commitment>> groupedCommitments,
                                                    Cardinality cardinality,
                                                    boolean more ) {
        int agentCount = groupedCommitments.keySet().size();
        if ( cardinality.isRequiredCount( agentCount ) ) {
            if ( cardinality.isSafeCount( agentCount ) ) {
                return Satisfaction.Strong;
            } else {
                if ( more ) {
                    return null;
                } else {
                    return Satisfaction.Weak;
                }
            }
        } else {
            if ( more ) {
                return null;
            } else {
                return Satisfaction.Negative;
            }

        }
        /*  return cardinality.isRequiredCount( agentCount )
   ? cardinality.isSafeCount( agentCount )
           ? Satisfaction.Strong
           : more
               ? null
               : Satisfaction.Weak
   : more
       ? null
       : Satisfaction.Negative;*/
    }

    private Satisfaction getSourcesPerReceiverCountSatisfaction(
            Map<Actor, List<Commitment>> groupedCommitments,
            boolean more ) {
        int minSourceCount = getMinSourceCount( groupedCommitments );
        int maxSourceCount = getMaxSourceCount( groupedCommitments );
        Cardinality cardinality = getCardinality();
        if ( cardinality.isRequiredCount( minSourceCount ) && cardinality.isRequiredCount( maxSourceCount ) ) {
            if ( cardinality.isSafeCount( minSourceCount ) && cardinality.isSafeCount( maxSourceCount ) ) {
                return Satisfaction.Strong;
            } else {
                if ( more ) {
                    return null;
                } else {
                    return Satisfaction.Weak;
                }
            }
        } else {
            if ( more ) {
                return null;
            } else {
                return Satisfaction.Negative;
            }
        }

/*
        return cardinality.isRequiredCount( minSourceCount ) && cardinality.isRequiredCount( maxSourceCount )
                ? cardinality.isSafeCount( minSourceCount ) && cardinality.isSafeCount( maxSourceCount )
                    ? Satisfaction.Strong
                    : more
                        ? null
                        : Satisfaction.Weak
                : more
                    ? null
                    : Satisfaction.Negative;
*/
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
                                && appliesTo( organization, asBeneficiary, planLocale );
                    }
                }
        );
    }

    public boolean appliesTo( Phase.Timing timing ) {
        return timing == null
                || beneficiarySpec.getTiming() == null
                || beneficiarySpec.getTiming() == timing;
    }

    public boolean appliesTo( Event event, Place planLocale ) {
        return event == null
                || beneficiarySpec.getEvent() == null
                || beneficiarySpec.getEvent().narrowsOrEquals( event, planLocale )
                || event.narrowsOrEquals( beneficiarySpec.getEvent(), planLocale );
    }


    public boolean appliesTo(
            Organization organization,
            boolean asBeneficiary,
            Place planLocale ) {
        return asBeneficiary
                ? beneficiarySpec.appliesTo( organization, planLocale )
                : committerSpec.appliesTo( organization, planLocale );
    }

    private boolean matchesFlow( Flow flow, Place planLocale ) {
        return ( information.isEmpty()
                || Matcher.matches( flow.getName(), information ) )
                // Match tags if required
                && Matcher.matchesAll( getInfoTags(), flow.getTags() )
                && matchesEois( flow );
    }

    @SuppressWarnings( "unchecked" )
    private boolean matchesEois( Flow flow ) {
        if ( information.isEmpty() || eois.isEmpty() ) return true;
        final List<String> flowEoiNames = (List<String>) CollectionUtils.collect(
                flow.getEois(),
                new Transformer() {
                    @Override
                    public Object transform( Object input ) {
                        return ( (ElementOfInformation) input ).getContent();
                    }
                }
        );
        // none of the required eois is not to be found in the flow's eois
        return !CollectionUtils.exists(
                eois,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        // todo - deal with transformations?
                        return !Matcher.contains( flowEoiNames, (String) object );
                    }
                }
        );
    }

    /**
     * Whether a commitment satisfies this requirement.
     *
     * @param commitment a commitment
     * @param planLocale the plan's locale
     * @return a boolean
     */
    public boolean satisfiedBy( Commitment commitment, Place planLocale ) {
        ResourceSpec committer = commitment.getCommitter().getResourceSpec();
        ResourceSpec beneficiary = commitment.getBeneficiary().getResourceSpec();
        Flow flow = commitment.getSharing();
        return matchesFlow( flow, planLocale )
                && beneficiarySpec.appliesToSituation( commitment, planLocale )
                && committerSpec.appliesTo( committer, planLocale )
                && beneficiarySpec.appliesTo( beneficiary, planLocale );
    }

    public Map<String, Object> mapState() {
        Map<String, Object> state = super.mapState();
        state.put( "information", getInformation() );
        state.put( "eois", new ArrayList<String>( getEois() ) );
        state.put( "requiredTags", Tag.tagsToString( getInfoTags() ) );
        state.put( "cardinality", getCardinality().mapState() );
        state.put( "committerSpec", getCommitterSpec().mapState() );
        state.put( "beneficiarySpec", getBeneficiarySpec().mapState() );
        return state;
    }

    @SuppressWarnings( "unchecked" )
    public void initFromMap( Map<String, Object> state, QueryService queryService ) {
        super.initFromMap( state, queryService );
        setInformation( (String) state.get( "information" ) );
        setInfoTags( Tag.tagsFromString( (String) state.get( "requiredTags" ) ) );
        setEois( (List<String>) state.get( "eois" ) );
        Cardinality card = new Cardinality();
        card.initFromMap( (Map<String, Object>) state.get( "cardinality" ) );
        setCardinality( card );
        AssignmentSpec cSpec = new AssignmentSpec();
        cSpec.initFromMap( (Map<String, Object>) state.get( "committerSpec" ), queryService );
        setCommitterSpec( cSpec );
        AssignmentSpec bSpec = new AssignmentSpec();
        bSpec.initFromMap( (Map<String, Object>) state.get( "beneficiarySpec" ), queryService );
        setCommitterSpec( bSpec );
    }

    public Organization getCommitterOrganization() {
        return committerSpec.getOrganization();
    }

    public void setCommitterOrganization( Organization organization ) {
        committerSpec.setOrganization( organization );
    }

    public Organization getBeneficiaryOrganization() {
        return beneficiarySpec.getOrganization();
    }

    public void setBeneficiaryOrganization( Organization organization ) {
        beneficiarySpec.setOrganization( organization );
    }

    public void setSituationIfAppropriate( Phase.Timing timing, Event event, Place planLocale ) {
        beneficiarySpec.setSituationIfAppropriate( timing, event, planLocale );
    }

    public String getInformationAndEois() {
        StringBuilder sb = new StringBuilder();
        sb.append( getInformation() );
        if ( !getEois().isEmpty() ) {
            sb.append( " [" );
            sb.append( ChannelsUtils.listToString( getEois(), ", ", ", and " ) );
            sb.append( ']' );
        }
        return sb.toString();
    }

    @Override
    public boolean references( ModelObject mo ) {
        return super.references( mo )
                || committerSpec.references( mo )
                || beneficiarySpec.references( mo );
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

        public int getMinCount() {
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

        public void setMaxCount( Integer maxCount ) {
            if ( maxCount != null ) {
                this.maxCount = Math.max( minCount, Math.max( 0, maxCount ) );
                safeCount = Math.min( this.maxCount, safeCount );
            } else {
                this.maxCount = null;
            }
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
            setMinCount( (Integer) state.get( "minCount" ) );
            setMaxCount( (Integer) state.get( "maxCount" ) );
            setSafeCount( (Integer) state.get( "safeCount" ) );
        }

        @Override
        public boolean equals( Object object ) {
            if ( object instanceof Cardinality ) {
                Cardinality other = (Cardinality) object;
                if ( minCount != other.getMinCount() ) return false;
                if ( safeCount != other.getSafeCount() ) return false;
                return ( maxCount == null ? 0 : maxCount ) == ( other.getMaxCount() == null ? 0 : other.getMaxCount() );
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            int result = minCount;
            result = 31 * result + safeCount;
            result = 31 * result + ( maxCount == null ? 0 : maxCount );
            return result;
        }

        public Cardinality copy() {
            Cardinality copy = new Cardinality();
            copy.setMinCount( minCount );
            copy.setSafeCount( safeCount );
            if ( maxCount != null ) copy.setMaxCount( maxCount );
            return copy;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append( "at least " );
            sb.append( minCount );
            if ( safeCount != minCount ) {
                sb.append( " and preferably more than " );
                sb.append( safeCount - 1 );
            }
            if ( maxCount != null ) {
                sb.append( " but at most " );
                sb.append( maxCount );
            }
            return sb.toString();
        }
    }

    /**
     * Assignment specification.
     */
    public static class AssignmentSpec implements Countable {
        /**
         * Task name.
         */
        private String taskName = "";
        /**
         * Task tags.
         */
        private List<Tag> taskTags = new ArrayList<Tag>();

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

        public List<Tag> getTaskTags() {
            return taskTags;
        }

        public void setTaskTags( List<Tag> taskTags ) {
            this.taskTags = taskTags;
        }

        public void addRequiredTag( Tag tag ) {
            taskTags.add( tag );
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
                    && Matcher.matchesAll( getTaskTags(), part.getTags() )
                    && inRequiredContext( part, planLocale );
        }

        public boolean appliesTo( Organization organization, Place planLocale ) {
            Organization orgSpec = getResourceSpec().getOrganization();
            return orgSpec == null
                    // when the required organization is actual, use equality only, to avoid "within" match
                    || ( orgSpec.isActual() && orgSpec.equals( organization ) )
                    || ( orgSpec.isType() && organization.narrowsOrEquals( orgSpec, planLocale ) );
        }

        public Map<String, Object> mapState() {
            Map<String, Object> state = new HashMap<String, Object>();
            state.put( "taskName", getTaskName() );
            state.put( "requiredTags", Tag.tagsToString( getTaskTags() ) );
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
            setTaskName( (String) state.get( "taskName" ) );
            setTaskTags( Tag.tagsFromString( (String) state.get( "requiredTags" ) ) );
            Cardinality card = new Cardinality();
            card.initFromMap( (Map<String, Object>) state.get( "cardinality" ) );
            setCardinality( card );
            ResourceSpec spec = new ResourceSpec();
            spec.initFromMap( (Map<String, Object>) state.get( "resourceSpec" ), queryService );
        }

        public Actor getActor() {
            return resourceSpec.getActor();
        }

        public void setActor( Actor actor ) {
            resourceSpec.setActor( actor );
        }

        public Role getRole() {
            return resourceSpec.getRole();
        }

        public void setRole( Role role ) {
            resourceSpec.setRole( role );
        }

        public Place getJurisdiction() {
            return resourceSpec.getJurisdiction();
        }

        public void setJurisdiction( Place place ) {
            resourceSpec.setJurisdiction( place );
        }

        public Organization getOrganization() {
            return resourceSpec.getOrganization();
        }

        public void setOrganization( Organization organization ) {
            resourceSpec.setOrganization( organization );
        }

        @Override
        public boolean equals( Object object ) {
            if ( object instanceof AssignmentSpec ) {
                AssignmentSpec other = (AssignmentSpec) object;
                return Phase.Timing.areEqualOrNull( timing, other.getTiming() )
                        && ModelObject.areEqualOrNull( event, other.getEvent() )
                        && Matcher.same( taskName, other.getTaskName() )
                        && Matcher.same( Tag.tagsToString( taskTags ), Tag.tagsToString( other.getTaskTags() ) )
                        && resourceSpec.equals( other.getResourceSpec() )
                        && cardinality.equals( other.getCardinality() );
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            int result = taskName.hashCode();
            result = result * 31 + Tag.tagsToString( taskTags ).hashCode();
            if ( event != null ) result = result * 31 + event.hashCode();
            if ( timing != null ) result = result * 31 + timing.hashCode();
            result = result * 31 + cardinality.hashCode();
            result = result * 31 + resourceSpec.hashCode();
            return result;
        }

        public AssignmentSpec copy() {
            AssignmentSpec copy = new AssignmentSpec();
            copy.setTaskName( taskName );
            copy.setTaskTags( Tag.copy( taskTags ) );
            copy.setCardinality( cardinality.copy() );
            copy.setResourceSpec( new ResourceSpec( resourceSpec ) );
            copy.setTiming( timing );
            copy.setEvent( event );
            return copy;
        }


        public boolean appliesTo( ResourceSpec spec, Place planLocale ) {
            return resourceSpec.isAnyone() || spec.narrowsOrEquals( resourceSpec, planLocale );
        }

        public void setSituationIfAppropriate( Phase.Timing t, Event e, Place planLocale ) {
            if ( t != null && timing == null )
                timing = t;
            if ( e != null && ( event == null || e.narrowsOrEquals( event, planLocale ) ) )
                event = e;
        }

        public boolean appliesToSituation( Commitment commitment, Place planLocale ) {
            return commitment.isInSituation( timing, event, planLocale );
        }

        private boolean references( ModelObject mo ) {
            return ModelObject.areIdentical( mo, event ) || resourceSpec.references( mo );
        }
    }
}
