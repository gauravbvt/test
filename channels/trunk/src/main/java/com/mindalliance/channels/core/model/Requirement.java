package com.mindalliance.channels.core.model;

import com.mindalliance.channels.core.Matcher;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.community.participation.Agency;
import com.mindalliance.channels.core.community.participation.Agent;
import com.mindalliance.channels.core.community.participation.OrganizationParticipation;
import com.mindalliance.channels.core.community.protocols.CommunityAssignment;
import com.mindalliance.channels.core.community.protocols.CommunityCommitment;
import com.mindalliance.channels.core.community.protocols.CommunityCommitments;
import com.mindalliance.channels.core.query.PlanService;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.core.util.ChannelsUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    /**
     * Class logger.
     */
    public static final Logger LOG = LoggerFactory.getLogger( Requirement.class );


    private static String UNNAMED = "UNNAMED";

    /**
     * Name of unknown info format.
     */
    public static String UnknownName = "(unknown)";

    public static Requirement UNKNOWN;


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

    public void initialize( PlanCommunity planCommunity ) {
        committerSpec.initialize( planCommunity );
        beneficiarySpec.initialize( planCommunity );
    }

    public boolean isEmpty() {
        return information.isEmpty()
                && infoTags.isEmpty()
                && eois.isEmpty()
                && committerSpec.isEmpty()
                && beneficiarySpec.isEmpty();
    }

    public AssignmentSpec makeNewAssignmentSpec() {
        return new AssignmentSpec();
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
        copy.makeTransient();
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
     * Whether two agent specs are compatible.
     *
     * @param spec       an agent spec
     * @param other      an agent spec
     * @param planLocale a place
     * @return a boolean
     */
    static protected boolean compatible( AgentSpec spec, AgentSpec other, Place planLocale ) {
        return ( spec.getAgent() == null || other.getAgent() == null || spec.getAgent().equals( other.getAgent() ) )
                && ( spec.getAgency() == null || other.getAgency() == null || spec.getAgency().equals( other.getAgency() ) )
                && ( ModelEntity.implies( spec.getJurisdiction(), other.getJurisdiction(), planLocale )
                || ModelEntity.implies( other.getJurisdiction(), spec.getJurisdiction(), planLocale ) )
                && ( ModelEntity.implies( spec.getPlaceholder(), other.getPlaceholder(), planLocale )
                || ModelEntity.implies( other.getPlaceholder(), spec.getPlaceholder(), planLocale ) );
    }

    /**
     * Evaluate the satisfaction of the requirement by an organization as committer or beneficiary in any situation.
     *
     * @param agency        an agency
     * @param asBeneficiary a boolean
     * @param planCommunity a plan community
     * @return a satisfaction rating
     */
    public Satisfaction satisfaction(
            Agency agency,
            boolean asBeneficiary,
            PlanCommunity planCommunity ) {
        return satisfaction( agency, asBeneficiary, null, null, planCommunity );
    }

    /**
     * Evaluate the satisfaction of the requirement by an organization as committer or beneficiary
     * in a given situation (if timing and/or event are not null).
     *
     * @param agency        an agency
     * @param asBeneficiary a boolean
     * @param timing        a phase timing
     * @param event         an event
     * @param planCommunity a plan community
     * @return a satisfaction rating
     */
    public Satisfaction satisfaction(
            Agency agency,
            boolean asBeneficiary,
            Phase.Timing timing,
            Event event,
            PlanCommunity planCommunity ) {
        Satisfaction[] satisfactions = getSatisfactions(
                agency,
                asBeneficiary,
                timing,
                event,
                planCommunity );
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

    @SuppressWarnings("unchecked")
    private Satisfaction[] getSatisfactions(
            Agency agency,
            boolean asBeneficiary,
            final Phase.Timing timing,
            final Event event,
            final PlanCommunity planCommunity ) {
        Satisfaction[] satisfactions = new Satisfaction[2];
        Satisfaction agentCountSatisfaction = null;
        Satisfaction sourcesPerReceiverSatisfaction = null;
        List<Flow> candidateFlows = findCandidateFlows( agency, asBeneficiary, planCommunity.getPlanService() );
        if ( candidateFlows.isEmpty() ) {
            satisfactions[0] = Satisfaction.Impossible;
            if ( asBeneficiary ) {
                satisfactions[1] = Satisfaction.Impossible;
            }
        } else {
            final Place communityLocale = planCommunity.getCommunityLocale();
            CommunityCommitments commitments = planCommunity.getAllCommitments( false )
                    .withFlows( candidateFlows );
            Iterator<CommunityCommitment> commitmentIterator = (Iterator<CommunityCommitment>) IteratorUtils.filteredIterator(
                    commitments.iterator(),
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            CommunityCommitment commitment = (CommunityCommitment) object;
                            return commitment.isInSituation( timing, event, communityLocale )
                                    && satisfiedBy( commitment, planCommunity )
                                    && canBeRealized( commitment, planCommunity );
                        }
                    }
            );
            Map<Agent, List<CommunityCommitment>> groupedCommitments = new HashMap<Agent, List<CommunityCommitment>>();
            while ( commitmentIterator.hasNext()
                    && ( agentCountSatisfaction == null
                    || ( asBeneficiary && sourcesPerReceiverSatisfaction == null ) ) ) {
                CommunityCommitment commitment = commitmentIterator.next();
                groupByAgent( groupedCommitments, commitment, asBeneficiary );
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

    private boolean canBeRealized( CommunityCommitment communityCommitment, PlanCommunity planCommunity ) {
        return planCommunity.getAnalyst()
                .findRealizabilityProblems(
                        planCommunity.getPlan(),
                        communityCommitment.getCommitment(),
                        planCommunity.getPlanService() ).isEmpty();
    }

    /**
     * Return the reason a requirement is not satisfied by an organization as committer or beneficiary.
     *
     * @param agency        an agency
     * @param asBeneficiary a boolean
     * @param planCommunity a plan community
     * @return a string
     */
    public String dissatisfactionSummary(
            Agency agency,
            boolean asBeneficiary,
            PlanCommunity planCommunity ) {
        String dissatisfaction = "";
        Satisfaction[] satisfactions = getSatisfactions(
                agency,
                asBeneficiary,
                null,
                null,
                planCommunity );
        Satisfaction agentCountSatisfaction = satisfactions[0];
        Satisfaction sourcesPerReceiverSatisfaction = asBeneficiary ? satisfactions[1] : null;
        if ( agentCountSatisfaction == Satisfaction.Impossible ) {
            dissatisfaction = "No sharing in the plan could possibly have "
                    + agency.getName()
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
                            + agency.getName()
                            + " receiving "
                            + "the specified info";
                } else {
                    dissatisfaction = "Not all receiving agents in "
                            + agency.getName()
                            + " have "
                            + cardinality.toString()
                            + " alternate source(s) as required";
                }

            } else {
                dissatisfaction = "There is not "
                        + cardinality.toString()
                        + " agent(s) in "
                        + agency.getName()
                        + " sharing "
                        + "the specified info";
            }

        }
        return dissatisfaction;
    }

    private void groupByAgent( Map<Agent,
            List<CommunityCommitment>> groupedCommitments,
                               CommunityCommitment commitment,
                               boolean asBeneficiary ) {
        Agent agent = asBeneficiary
                ? commitment.getBeneficiary().getAgent()
                : commitment.getCommitter().getAgent();
        List<CommunityCommitment> list = groupedCommitments.get( agent );
        if ( list == null ) {
            list = new ArrayList<CommunityCommitment>();
            groupedCommitments.put( agent, list );
        }
        list.add( commitment );
    }

    private Satisfaction getAgentCountSatisfaction( Map<Agent, List<CommunityCommitment>> groupedCommitments,
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
            Map<Agent, List<CommunityCommitment>> groupedCommitments,
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

    private int getMinSourceCount( Map<Agent, List<CommunityCommitment>> groupedCommitments ) {
        if ( groupedCommitments.isEmpty() ) return 0;
        int minCount = Integer.MAX_VALUE;
        for ( List<CommunityCommitment> list : groupedCommitments.values() ) {
            minCount = Math.min( minCount, list.size() );
        }
        return minCount;
    }

    private int getMaxSourceCount( Map<Agent, List<CommunityCommitment>> groupedCommitments ) {
        int maxCount = 0;
        for ( List<CommunityCommitment> list : groupedCommitments.values() ) {
            maxCount = Math.max( maxCount, list.size() );
        }
        return maxCount;
    }

    // Find all flows that match the requirement and where the organization could be the beneficiary or committer.
    @SuppressWarnings("unchecked")
    private List<Flow> findCandidateFlows(
            final Agency agency,
            final boolean asBeneficiary,
            QueryService queryService ) {
        final Place planLocale = queryService.getPlanLocale();
        return (List<Flow>) CollectionUtils.select(
                queryService.findAllFlows(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        Flow flow = (Flow) object;
                        return flow.isSharing()
                                && matchesFlow( (Flow) object, planLocale )
                                && appliesTo( agency, asBeneficiary, planLocale );
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
            Agency agency,
            boolean asBeneficiary,
            Place planLocale ) {
        return asBeneficiary
                ? beneficiarySpec.appliesTo( agency )
                : committerSpec.appliesTo( agency );
    }

    private boolean matchesFlow( Flow flow, Place planLocale ) {
        return ( information.isEmpty()
                || Matcher.matches( flow.getName(), information ) )
                // Match tags if required
                && Matcher.matchesAll( getInfoTags(), flow.getTags() )
                && matchesEois( flow );
    }

    @SuppressWarnings("unchecked")
    private boolean matchesEois( Flow flow ) {
        if ( information.isEmpty() || eois.isEmpty() ) return true;
        final List<String> flowEoiNames = (List<String>) CollectionUtils.collect(
                flow.getEffectiveEois(),
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
     * @param commitment    a commitment
     * @param planCommunity a plan community
     * @return a boolean
     */
    public boolean satisfiedBy( CommunityCommitment commitment, PlanCommunity planCommunity ) {
        if ( isEmpty() ) return false;
        Place locale = planCommunity.getCommunityLocale();
        Flow flow = commitment.getSharing();
        return matchesFlow( flow, locale )
                && beneficiarySpec.satisfiesSituation( commitment, locale )
                && committerSpec.satisfiesTaskAndResources( commitment.getCommitter(), planCommunity )
                && beneficiarySpec.satisfiesTaskAndResources( commitment.getBeneficiary(), planCommunity );
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

    @SuppressWarnings("unchecked")
    public void initFromMap( Map<String, Object> state, PlanCommunity planCommunity ) {
        super.initFromMap( state, planCommunity.getPlanService() );
        setInformation( (String) state.get( "information" ) );
        setInfoTags( Tag.tagsFromString( (String) state.get( "requiredTags" ) ) );
        setEois( (List<String>) state.get( "eois" ) );
        Cardinality card = new Cardinality();
        card.initFromMap( (Map<String, Object>) state.get( "cardinality" ) );
        setCardinality( card );
        AssignmentSpec cSpec = new AssignmentSpec();
        cSpec.initFromMap( (Map<String, Object>) state.get( "committerSpec" ), planCommunity );
        setCommitterSpec( cSpec );
        AssignmentSpec bSpec = new AssignmentSpec();
        bSpec.initFromMap( (Map<String, Object>) state.get( "beneficiarySpec" ), planCommunity );
        setCommitterSpec( bSpec );
    }

    public Agency getCommitterAgency() {
        return committerSpec.getAgency();
    }

    public void setCommitterAgency( Agency agency ) {
        committerSpec.makeApplyTo( agency );
    }

    public Agency getBeneficiaryAgency() {
        return beneficiarySpec.getAgency();
    }

    public void setBeneficiaryAgency( Agency agency ) {
        beneficiarySpec.makeApplyTo( agency );
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
    public class AssignmentSpec implements Countable {
        /**
         * Task name.
         */
        private String taskName = "";
        /**
         * Task tags.
         */
        private List<Tag> taskTags = new ArrayList<Tag>();

        /**
         * Task agent spec.
         */
        private AgentSpec agentSpec = new AgentSpec();

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

        public AgentSpec getAgentSpec() {
            return agentSpec;
        }

        public void setAgentSpec( AgentSpec agentSpec ) {
            this.agentSpec = agentSpec;
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

        public boolean appliesTo( Agency agency ) {
            Agency agencySpec = getAgentSpec().getAgency();
            return agencySpec == null || agency.equals( agencySpec );
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
            state.put( "agentSpec", getAgentSpec().mapState() );
            return state;
        }

        @SuppressWarnings("unchecked")
        public void initFromMap( Map<String, Object> state, PlanCommunity planCommunity ) {
            setTaskName( (String) state.get( "taskName" ) );
            setTaskTags( Tag.tagsFromString( (String) state.get( "requiredTags" ) ) );
            Cardinality card = new Cardinality();
            card.initFromMap( (Map<String, Object>) state.get( "cardinality" ) );
            setCardinality( card );
            AgentSpec agentSpec = new AgentSpec();
            agentSpec.initFromMap( (Map<String, Object>) state.get( "agentSpec" ), planCommunity );
        }

        public Agent getAgent() {
            return agentSpec.getAgent();
        }

        public void makeApplyTo( Agent agent ) {
            agentSpec.makeApplyTo( agent );
        }

        public Place getJurisdiction() {
            return agentSpec.getJurisdiction();
        }

        public void setJurisdiction( Place place ) {
            agentSpec.setJurisdiction( place );
        }

        public Agency getAgency() {
            return agentSpec.getAgency();
        }

        public void makeApplyTo( Agency agency ) {
            agentSpec.makeApplyTo( agency );
        }


        public Organization getPlaceholder() {
            return agentSpec.getPlaceholder();
        }

        public void setPlaceholder( Organization organization ) {
            agentSpec.setPlaceholder( organization );
        }

        @Override
        public boolean equals( Object object ) {
            if ( object instanceof AssignmentSpec ) {
                AssignmentSpec other = (AssignmentSpec) object;
                return Phase.Timing.areEqualOrNull( timing, other.getTiming() )
                        && ModelObject.areEqualOrNull( event, other.getEvent() )
                        && Matcher.same( taskName, other.getTaskName() )
                        && Matcher.same( Tag.tagsToString( taskTags ), Tag.tagsToString( other.getTaskTags() ) )
                        && agentSpec.equals( other.getAgentSpec() )
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
            result = result * 31 + agentSpec.hashCode();
            return result;
        }

        public AssignmentSpec copy() {
            AssignmentSpec copy = new AssignmentSpec();
            copy.setTaskName( taskName );
            copy.setTaskTags( Tag.copy( taskTags ) );
            copy.setCardinality( cardinality.copy() );
            copy.setAgentSpec( new AgentSpec( agentSpec ) );
            copy.setTiming( timing );
            copy.setEvent( event );
            return copy;
        }

        public boolean isEmpty() {
            return taskName.isEmpty()
                    && taskTags.isEmpty()
                    && agentSpec.isAnyone()
                    && timing == null
                    && event == null;
        }


        public boolean appliesToAgency( Agency agency, PlanCommunity planCommunity ) {
            return agentSpec.appliesToAgency( agency, planCommunity );
        }

        public boolean satisfiesTaskAndResources( CommunityAssignment assignment, PlanCommunity planCommunity ) {
            Part part = assignment.getPart();
            return ( taskName.isEmpty() || Matcher.same( taskName, part.getTask() ) )
                    && ( taskTags.isEmpty() || Matcher.matchesAll( taskTags, part.getTags() ) )
                    && agentSpec.appliesToAssignment( assignment, planCommunity );
        }

        public void setSituationIfAppropriate( Phase.Timing t, Event e, Place locale ) {
            if ( t != null && timing == null )
                timing = t;
            if ( e != null && ( event == null || e.narrowsOrEquals( event, locale ) ) )
                event = e;
        }

        public boolean satisfiesSituation( CommunityCommitment commitment, Place locale ) {
            return commitment.isInSituation( timing, event, locale );
        }

        private boolean references( ModelObject mo ) {
            return ModelObject.areIdentical( mo, event ) || agentSpec.references( mo );
        }

        public void initialize( PlanCommunity planCommunity ) {
            agentSpec.initialize( planCommunity );
        }

    }

    public class AgentSpec implements Serializable {

        // If agent is specified as registered (by actor and orgParticipationId) then agency is implied.
        // If agency is specified and not implied (by fixedOrgId or orgParticipationId) then placeholder is implied.
        // Placeholder is either given or implied via specified/implied agency.

        private boolean initialized = false;
        private Actor actor;
        private Long orgParticipationId;  // of agent or if, no actor, of agency
        private Long fixedOrgId; // of agency, if agency not already specified by agent
        private Place jurisdiction;
        private Organization placeholder; // meaningful only if fixedOrgId or orgParticipationId not set
        private Agent agent; // computed and cached
        private Agency agency; // computed and cached

        public AgentSpec() {
        }

        public AgentSpec( AgentSpec agentSpec ) {
            actor = agentSpec.getActor();
            jurisdiction = agentSpec.getJurisdiction();
            orgParticipationId = agentSpec.getOrgParticipationId();
            fixedOrgId = agentSpec.getFixedOrgId();
            placeholder = agentSpec.getPlaceholder();
            agent = agentSpec.getAgent();
            agency = agentSpec.getAgency();
        }

        public AgentSpec( CommunityAssignment assignment, PlanCommunity planCommunity ) {
            processAgent( assignment.getAgent() );
            jurisdiction = assignment.getJurisdiction();
            processAgency( assignment.getAgency() );
            placeholder = assignment.getEmployment().getEmployer().getPlaceholder( planCommunity );
            initialize( planCommunity );
        }

        private void processAgent( Agent agent ) {
            actor = agent.getActor();
            if ( agent.isFromOrganizationParticipation() ) {
                orgParticipationId = agent.getOrganizationParticipation().getId();
            }
        }

        private void processAgency( Agency agency ) {
            if ( agency.isFixedOrganization() ) {
                fixedOrgId = agency.getFixedOrganization().getId();
            } else if ( orgParticipationId == null && agency.isParticipatingAsPlaceholder() ) {
                orgParticipationId = agency.getOrganizationParticipation().getId();
            }
        }

        // MUST BE RUN before accessing agent or agency whenever requirement is created or one of its agentSpecs is updated
        public void initialize( PlanCommunity planCommunity ) {
            if ( !initialized ) {
                agent = null;
                agency = null;
                if ( actor != null ) {
                    if ( orgParticipationId != null ) {
                        OrganizationParticipation orgParticipation = planCommunity
                                .getOrganizationParticipationService().load( orgParticipationId );
                        if ( orgParticipation != null ) {
                            agent = new Agent( actor, orgParticipation, planCommunity );
                        }
                    } else {
                        agent = new Agent( actor );
                    }
                }
                if ( fixedOrgId != null ) {
                    assert orgParticipationId == null;
                    try {
                        Organization org = planCommunity.getPlanService().find( Organization.class, fixedOrgId );
                        agency = new Agency( org );
                        placeholder = null;
                    } catch ( NotFoundException e ) {
                        LOG.warn( "Failed to find organization " + fixedOrgId );
                    }
                }
                if ( orgParticipationId != null ) {
                    assert fixedOrgId == null;
                    OrganizationParticipation orgParticipation = planCommunity
                            .getOrganizationParticipationService().load( orgParticipationId );
                    if ( orgParticipation != null ) {
                        agency = new Agency( orgParticipation, planCommunity );
                        placeholder = agency.getPlaceholder( planCommunity );
                    }
                }
            }
            initialized = true;
        }


        public Agency getAgency() {
            assert initialized;
            return agency;
        }

        public void makeApplyTo( Agency agency ) {
            if ( agency.isFixedOrganization() ) {
                setFixedOrgId( agency.getFixedOrganization().getId() );
            } else {
                setOrgParticipationId( agency.getOrganizationParticipation().getId() );
            }
        }

        public Agent getAgent() {
            assert initialized;
            return agent;
        }

        public void makeApplyTo( Agent agent ) {
            actor = agent.getActor();
            if ( agent.isFromOrganizationParticipation() ) {
                setOrgParticipationId( agent.getOrganizationParticipation().getId() );
            }
        }

        public Place getJurisdiction() {
            return jurisdiction;
        }

        public void setJurisdiction( Place jurisdiction ) {
            this.jurisdiction = jurisdiction;
        }

        public Organization getPlaceholder() {
            return placeholder;
        }

        public void setPlaceholder( Organization placeholder ) {
            this.placeholder = placeholder;
            if ( placeholder != null ) {
                fixedOrgId = null;
            }
        }

        public Actor getActor() {
            return actor;
        }

        public void setActor( Actor actor ) {
            this.actor = actor;
            initialized = false;
        }

        public Long getFixedOrgId() {
            return fixedOrgId;
        }

        public void setFixedOrgId( Long fixedOrgId ) {
            this.fixedOrgId = fixedOrgId;
            if ( fixedOrgId != null ) {
                orgParticipationId = null;
                placeholder = null;
            }
            initialized = false;
        }

        public Long getOrgParticipationId() {
            return orgParticipationId;
        }

        public void setOrgParticipationId( Long orgParticipationId ) {
            this.orgParticipationId = orgParticipationId;
            if ( orgParticipationId != null ) {
                fixedOrgId = null;
                placeholder = null;
            }
            initialized = false;
        }

        ///////
        @Override
        public boolean equals( Object object ) {
            if ( object instanceof AgentSpec ) {
                AgentSpec other = (AgentSpec) object;
                return ChannelsUtils.areEqualOrNull( actor, other.getActor() )
                        && ChannelsUtils.areEqualOrNull( jurisdiction, other.getJurisdiction() )
                        && ChannelsUtils.areEqualOrNull( orgParticipationId, other.getOrgParticipationId() )
                        && ChannelsUtils.areEqualOrNull( fixedOrgId, other.getFixedOrgId() )
                        && ChannelsUtils.areEqualOrNull( placeholder, other.getPlaceholder() );

            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            int hash = 1;
            if ( actor != null )
                hash = hash + 31 * actor.hashCode();
            if ( jurisdiction != null )
                hash = hash + 31 * jurisdiction.hashCode();
            if ( orgParticipationId != null )
                hash = hash + 31 * orgParticipationId.hashCode();
            if ( fixedOrgId != null )
                hash = hash + 31 * fixedOrgId.hashCode();
            if ( placeholder != null )
                hash = hash + 31 * placeholder.hashCode();
            return hash;
        }

        public Map<String, Object> mapState() {
            Map<String, Object> state = new HashMap<String, Object>();
            if ( actor != null ) state.put( "actor", actor.getId() );
            if ( jurisdiction != null ) state.put( "jurisdiction", jurisdiction.getId() );
            if ( orgParticipationId != null ) state.put( "orgParticipationId", orgParticipationId );
            if ( fixedOrgId != null ) state.put( "fixedOrgId", fixedOrgId );
            if ( placeholder != null ) state.put( "placeholder", placeholder.getId() );
            return state;
        }

        public void initFromMap( Map<String, Object> state, PlanCommunity planCommunity ) {
            PlanService planService = planCommunity.getPlanService();
            if ( state.containsKey( "actor" ) ) {
                Long id = (Long) state.get( "actor" );
                try {
                    actor = planService.find( Actor.class, id );
                } catch ( NotFoundException e ) {
                    LOG.warn( "Actor not found at " + id );
                }
            }
            if ( state.containsKey( "orgParticipationId" ) ) {
                orgParticipationId = (Long) state.get( "orgParticipationId" );
            }
            if ( state.containsKey( "jurisdiction" ) ) {
                Long id = (Long) state.get( "jurisdiction" );
                try {
                    jurisdiction = planService.find( Place.class, id );
                } catch ( NotFoundException e ) {
                    LOG.warn( "Place not found at " + id );
                }
            }
            if ( state.containsKey( "fixedOrgId" ) ) {
                fixedOrgId = (Long) state.get( "fixedOrgId" );
            }
            if ( state.containsKey( "placeholder" ) ) {
                Long id = (Long) state.get( "placeholder" );
                try {
                    placeholder = planService.find( Organization.class, id );
                } catch ( NotFoundException e ) {
                    LOG.warn( "Organization not found at " + id );
                }
            }
            initialize( planCommunity );
        }

        public boolean references( ModelObject mo ) {
            return ModelObject.areIdentical( jurisdiction, mo )
                    || ModelObject.areIdentical( placeholder, mo );
        }

        public boolean isAnyone() {
            return actor == null
                    && jurisdiction == null
                    && orgParticipationId == null
                    && fixedOrgId == null
                    && placeholder == null;
        }

        public boolean narrowsOrEquals( AgentSpec agentSpec, Place locale ) {
            return ( agentSpec.getAgent() == null
                    || ( agent != null && agent.equals( agentSpec.getAgent() ) ) )
                    && ModelEntity.implies( jurisdiction, agentSpec.getJurisdiction(), locale )
                    && ( agentSpec.getAgency() == null
                    || ( agency != null && agency.equals( agentSpec.getAgency() ) ) )
                    && ModelEntity.implies( placeholder, agentSpec.getPlaceholder(), locale );
        }

        public boolean isAgencyImplied() {
            return getAgent() != null && getAgent().isFromOrganizationParticipation();
        }

        public boolean isPlaceholderImplied() {
            return getAgency() != null;
        }

        public boolean appliesToAssignment( CommunityAssignment assignment, PlanCommunity planCommunity ) {
            return isAnyone()
                    || ( appliesToAgent( assignment.getAgent() )
                    && appliesToAgency( assignment.getAgency(), planCommunity ) );
        }

        private boolean appliesToAgent( Agent anAgent ) {
            if ( getAgent() != null )
                return getAgent().equals( anAgent );
            else if ( anAgent.isFromOrganizationParticipation() ) {
                return getOrgParticipationId() != null
                        && getOrgParticipationId() == anAgent.getOrganizationParticipation().getId();
            }
            return true;
        }

        public boolean appliesToAgency( Agency anAgency, PlanCommunity planCommunity ) {
            if ( getAgency() != null )
                return getAgency().equals( anAgency );
            else if ( getPlaceholder() != null ) {
                Organization otherPlaceHolder = anAgency.getPlaceholder( planCommunity );
                return otherPlaceHolder != null && getPlaceholder().equals( otherPlaceHolder );
            }
            return true;
        }
    }
}
