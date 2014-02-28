package com.mindalliance.channels.core.model;

import com.mindalliance.channels.core.Matcher;
import com.mindalliance.channels.core.community.Agency;
import com.mindalliance.channels.core.community.Agent;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.protocols.CommunityAssignment;
import com.mindalliance.channels.core.community.protocols.CommunityCommitment;
import com.mindalliance.channels.core.community.protocols.CommunityCommitments;
import com.mindalliance.channels.core.query.ModelService;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.db.data.communities.RegisteredOrganization;
import com.mindalliance.channels.db.services.communities.RegisteredOrganizationService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    public String getCommitterAgencyName() {
        Agency agency = getCommitterSpec().getAgency();
        return agency != null ? agency.getName() : "?";
    }

    public String getBeneficiaryAgencyName() {
        Agency agency = getBeneficiarySpec().getAgency();
        return agency != null ? agency.getName() : "?";
    }

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
        Strong;

        public Satisfaction min( Satisfaction other ) {
            return this.compareTo( other ) <= 0
                    ? this
                    : other;
        }

        public boolean isFailed() {
            return this.compareTo( Weak ) < 0;
        }
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

    public void initialize( CommunityService communityService ) {
        committerSpec.initialize( communityService );
        beneficiarySpec.initialize( communityService );
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

    @Override
    public boolean isModifiableInProduction() {
        return true;
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


    public boolean equals( Object object ) {
        if ( object instanceof Requirement ) {
            Requirement other = (Requirement) object;
            return getName().equals( other.getName() )
                    && getDescription().equals( other.getDescription() )
                    && information.equals( other.getInformation() )
                    && cardinality.equals( other.getCardinality() )
                    && Matcher.matchesAll( infoTags, other.getInfoTags() )
                    && CollectionUtils.isEqualCollection( eois, other.getEois() )
                    && committerSpec.equals( other.getCommitterSpec() )
                    && beneficiarySpec.equals( other.getBeneficiarySpec() );
        } else {
            return false;
        }
    }

    public int hashCode() {
        int hash = 1;
        hash = hash + 31 * getName().hashCode();
        hash = hash + 31 * getDescription().hashCode();
        hash = hash + 31 * information.hashCode();
        hash = hash + 31 * cardinality.hashCode();
        for ( Tag tag : infoTags ) {
            hash = hash + 31 * tag.hashCode();
        }
        for ( String eoi : eois ) {
            hash = hash + 31 * eoi.hashCode();
        }
        hash = hash + 31 * committerSpec.hashCode();
        hash = hash + 31 * beneficiarySpec.hashCode();
        return hash;
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
     * Evaluate the satisfaction of the requirement in a given situation.
     *
     * @param timing           a phase timing
     * @param event            an event
     * @param communityService a plan community service
     * @return a satisfaction rating
     */
    public Satisfaction measureSatisfaction(
            Phase.Timing timing,
            Event event,
            CommunityService communityService ) {
        SatisfactionMeasure[] satisfactions = evaluateSatisfaction( timing, event, communityService );
        return satisfactions[0].getSatisfaction()
                .min( satisfactions[1].getSatisfaction() )
                .min( satisfactions[2].getSatisfaction() );
    }

    private SatisfactionMeasure[] evaluateSatisfaction( Phase.Timing timing,
                                                        Event event,
                                                        CommunityService communityService ) {
        SatisfactionMeasure[] satisfactions = new SatisfactionMeasure[3];
        CommunityCommitments commitments = communityService.getAllCommitments( false )
                .inSituation( timing, event, communityService.getPlanCommunity().getLocale( communityService ) )
                .satisfying( this, communityService )
                .canBeRealized( communityService );
        if ( commitments.isEmpty() ) {
            satisfactions[0] = new SatisfactionMeasure( Satisfaction.Impossible, getCommitterSpec().getCardinality(), 0 );
            satisfactions[1] = new SatisfactionMeasure( Satisfaction.Impossible, getBeneficiarySpec().getCardinality(), 0 );
            satisfactions[2] = new SatisfactionMeasure( Satisfaction.Impossible, getCardinality(), 0 );
        } else {
            Set<Agent> committers = new HashSet<Agent>();
            Set<Agent> beneficiaries = new HashSet<Agent>();
            Map<Agent, Set<Agent>> agentSources = new HashMap<Agent, Set<Agent>>();
            for ( CommunityCommitment commitment : commitments ) {
                Agent committer = commitment.getCommitter().getAgent();
                Agent beneficiary = commitment.getBeneficiary().getAgent();
                committers.add( committer );
                beneficiaries.add( beneficiary );
                Set<Agent> sourcesOfAgent = agentSources.get( beneficiary );
                if ( sourcesOfAgent == null ) {
                    sourcesOfAgent = new HashSet<Agent>();
                    agentSources.put( beneficiary, sourcesOfAgent );
                }
                sourcesOfAgent.add( committer );
            }
            int sourcesCount = committers.size();
            int receiversCount = beneficiaries.size();
            int minSourcesPerReceiverCount = Integer.MAX_VALUE;
            for ( Agent beneficiary : beneficiaries ) {
                int sourcesPerReceiverCount = agentSources.get( beneficiary ) == null
                        ? 0
                        : agentSources.get( beneficiary ).size();
                minSourcesPerReceiverCount = Math.min( minSourcesPerReceiverCount, sourcesPerReceiverCount );
            }
            Satisfaction sourceCountSatisfaction = getCommitterSpec().getCardinality().evaluate( sourcesCount );
            Satisfaction receiverCountSatisfaction = getBeneficiarySpec().getCardinality().evaluate( receiversCount );
            Satisfaction sourcesPerReceiverSatisfaction = getCardinality().evaluate( minSourcesPerReceiverCount );
            satisfactions[0] = new SatisfactionMeasure(
                    sourceCountSatisfaction,
                    getCommitterSpec().getCardinality(),
                    sourcesCount );
            satisfactions[1] = new SatisfactionMeasure(
                    receiverCountSatisfaction,
                    getBeneficiarySpec().getCardinality(),
                    receiversCount );
            satisfactions[2] = new SatisfactionMeasure(
                    sourcesPerReceiverSatisfaction,
                    getCardinality(),
                    minSourcesPerReceiverCount );
        }
        return satisfactions;
    }

    private boolean canBeRealized( CommunityCommitment communityCommitment, CommunityService communityService ) {
        return communityService.getAnalyst()
                .findRealizabilityProblems(
                        communityService.getPlan(),
                        communityCommitment.getCommitment(),
                        communityService ).isEmpty();
    }

    /**
     * Return the reason a requirement is not satisfied by an organization as committer or beneficiary.
     *
     * @param timing           a phase timing
     * @param event            an event
     * @param communityService a plan community service
     * @return a string
     */
    public String satisfactionSummary(
            Phase.Timing timing,
            Event event,
            CommunityService communityService ) {
        String dissatisfaction = "";
        SatisfactionMeasure[] satisfactions = evaluateSatisfaction(
                timing,
                event,
                communityService );
        SatisfactionMeasure sourceCountSatisfactionMeasure = satisfactions[0];
        SatisfactionMeasure receiverCountSatisfactionMeasure = satisfactions[1];
        SatisfactionMeasure sourcesPerReceiverSatisfactionMeasure = satisfactions[2];
        if ( sourceCountSatisfactionMeasure.getSatisfaction() == Satisfaction.Impossible ) {
            dissatisfaction = "There is no commitment that can fulfill the requirement";
        } else if ( sourceCountSatisfactionMeasure.getSatisfaction() == Satisfaction.Strong
                && receiverCountSatisfactionMeasure.getSatisfaction() == Satisfaction.Strong
                && sourcesPerReceiverSatisfactionMeasure.getSatisfaction() == Satisfaction.Strong ) {
            dissatisfaction = "Requirement is completely satisfied";
        } else {
            StringBuilder sb = new StringBuilder();
            if ( sourceCountSatisfactionMeasure.getSatisfaction() != Satisfaction.Strong ) {
                sb.append( sourceCountSatisfactionMeasure.getLabel( "valid source(s)" ) );
            }
            if ( receiverCountSatisfactionMeasure.getSatisfaction() != Satisfaction.Strong ) {
                if ( sb.length() != 0 )
                    sb.append( ", and " );
                String label = receiverCountSatisfactionMeasure.getLabel( "valid receiver(s)" );
                sb.append( sb.length() != 0
                        ? label.toLowerCase()
                        : label );
            }
            if ( sourcesPerReceiverSatisfactionMeasure.getSatisfaction() != Satisfaction.Strong ) {
                if ( sb.length() != 0 )
                    sb.append( ", and " );
                String label = sourcesPerReceiverSatisfactionMeasure.getLabel( "valid source(s) per receiver as worst case," );
                sb.append( sb.length() != 0
                        ? label.toLowerCase()
                        : label );
            }
            dissatisfaction = sb.toString();
        }
        return dissatisfaction;
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

/*

    public boolean appliesTo(
            Agency agency,
            boolean asBeneficiary,
            Place planLocale ) {
        return asBeneficiary
                ? beneficiarySpec.appliesTo( agency )
                : committerSpec.appliesTo( agency );
    }
*/

    private boolean matchesFlow( Flow flow, Place planLocale ) {
        return ( information.isEmpty()
                || Matcher.contains( flow.getName(), information ) )
                // Match tags if required
                && Matcher.matchesAll( getInfoTags(), flow.getTags() )
                && matchesEois( flow );
    }

    @SuppressWarnings("unchecked")
    private boolean matchesEois( Flow flow ) {
        if ( eois.isEmpty() ) return true;
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
     * @param commitment       a commitment
     * @param communityService a plan community service
     * @return a boolean
     */
    public boolean satisfiedBy( CommunityCommitment commitment, CommunityService communityService ) {
        if ( isEmpty() ) return false;
        Place locale = communityService.getPlanCommunity().getLocale( communityService );
        Flow flow = commitment.getSharing();
        return matchesFlow( flow, locale )
                && beneficiarySpec.satisfiesSituation( commitment, locale )
                && committerSpec.satisfiesTaskAndResources( commitment.getCommitter(), communityService )
                && beneficiarySpec.satisfiesTaskAndResources( commitment.getBeneficiary(), communityService );
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
    public void initFromMap( Map<String, Object> state, CommunityService communityService ) {
        super.initFromMap( state, communityService );
        setInformation( (String) state.get( "information" ) );
        setInfoTags( Tag.tagsFromString( (String) state.get( "requiredTags" ) ) );
        setEois( (List<String>) state.get( "eois" ) );
        Cardinality card = new Cardinality();
        card.initFromMap( (Map<String, Object>) state.get( "cardinality" ) );
        setCardinality( card );
        AssignmentSpec cSpec = new AssignmentSpec();
        cSpec.initFromMap( (Map<String, Object>) state.get( "committerSpec" ), communityService );
        setCommitterSpec( cSpec );
        AssignmentSpec bSpec = new AssignmentSpec();
        bSpec.initFromMap( (Map<String, Object>) state.get( "beneficiarySpec" ), communityService );
        setCommitterSpec( bSpec );
    }

    public Agency getCommitterAgency() {
        return committerSpec.getAgency();
    }

    public void setCommitterAgency( Agency agency ) {
        committerSpec.makeApplyToAgencyIfPossible( agency );
    }

    public Agency getBeneficiaryAgency() {
        return beneficiarySpec.getAgency();
    }

    public void setBeneficiaryAgency( Agency agency ) {
        beneficiarySpec.makeApplyToAgencyIfPossible( agency );
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

    public class SatisfactionMeasure implements Serializable {
        private Satisfaction satisfaction;
        private Cardinality cardinality;
        private int count;

        public SatisfactionMeasure( Satisfaction satisfaction, Cardinality cardinality, int count ) {
            this.satisfaction = satisfaction;
            this.cardinality = cardinality;
            this.count = count;
        }

        public Cardinality getCardinality() {
            return cardinality;
        }

        public int getCount() {
            return count;
        }

        public Satisfaction getSatisfaction() {
            return satisfaction;
        }

        public String getLabel( String about ) {
            return "Found " + count + " " + about + " (" + cardinality.toString() + " required)";
        }
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

        public Satisfaction evaluate( int count ) {
            if ( isRequiredCount( count ) ) {
                if ( isSafeCount( count ) ) {
                    return Satisfaction.Strong;
                } else {
                    return Satisfaction.Weak;
                }
            } else {
                return Satisfaction.Negative;
            }
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

        @SuppressWarnings( "unchecked" )
        public void initFromMap( Map<String, Object> state, CommunityService communityService ) {
            setTaskName( (String) state.get( "taskName" ) );
            setTaskTags( Tag.tagsFromString( (String) state.get( "requiredTags" ) ) );
            Cardinality card = new Cardinality();
            card.initFromMap( (Map<String, Object>) state.get( "cardinality" ) );
            setCardinality( card );
            AgentSpec agentSpec = new AgentSpec();
            agentSpec.initFromMap( (Map<String, Object>) state.get( "agentSpec" ), communityService );
        }

        public Agent getAgent() {
            return agentSpec.getAgent();
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

        public void makeApplyToAgencyIfPossible( Agency agency ) {
            agentSpec.makeApplyToAgencyIfPossible( agency );
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
                        && taskName.equals( other.getTaskName() )
                        && Matcher.matchesAll( taskTags, other.getTaskTags() )
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


        public boolean appliesToAgency( Agency agency, CommunityService communityService ) {
            return agentSpec.appliesToAgency( agency, communityService );
        }

        public boolean satisfiesTaskAndResources( CommunityAssignment assignment, CommunityService communityService ) {
            Part part = assignment.getPart();
            return ( taskName.isEmpty() || Matcher.same( taskName, part.getTask() ) )
                    && ( taskTags.isEmpty() || Matcher.matchesAll( taskTags, part.getTags() ) )
                    && agentSpec.appliesToAssignment( assignment, communityService );
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

        public void initialize( CommunityService communityService ) {
            agentSpec.initialize( communityService );
        }

    }

    public class AgentSpec implements Serializable {

        // If agent is specified as registered (by actor and orgParticipationId) then agency is implied.
        // If agency is specified and not implied (by fixedOrgId or orgParticipationId) then placeholder is implied.
        // Placeholder is either given or implied via specified/implied agency.

        private boolean initialized = false;
        private Actor actor; // an agent is to participate as
        private String registeredOrgId; // of agency if directly identified
        private Long fixedOrgId; // of agency, if agency of agent is to participate as fixed org
        private Place jurisdiction;
        private Organization placeholder; // if agency is to participate as placeholder
        private Agent agent; // computed and cached
        private Agency agency; // computed and cached

        public AgentSpec() {
        }

        public AgentSpec( AgentSpec agentSpec ) {
            actor = agentSpec.getActor();
            jurisdiction = agentSpec.getJurisdiction();
            registeredOrgId = agentSpec.getRegisteredOrgId();
            fixedOrgId = agentSpec.getFixedOrgId();
            placeholder = agentSpec.getPlaceholder();
            agent = agentSpec.getAgent();
            agency = agentSpec.getAgency();
        }

        public String getLabel() {
            StringBuilder sb = new StringBuilder();
            if ( getAgent() != null ) {
                sb.append( getAgent().isSingularParticipation() ? "The " : "All " );
                sb.append( getAgent().getName() );
            } else if ( getAgency() != null ) {
                sb.append( "Someone from " );
                sb.append( getAgency().getName() );
            } else if ( getPlaceholder() != null ) {
                sb.append( "Someone at every organization participating as " );
                sb.append( getPlaceholder().getName() );
            }
            return sb.toString();
        }

        // MUST BE RUN before accessing agent or agency whenever requirement is created or one of its agentSpecs is updated
        // Makes sure all agent specs are valid, else nulled.
        public void initialize( CommunityService communityService ) {
            if ( !initialized ) {
                agent = null;
                agency = null;
                // verify actor still valid
                if ( actor != null ) {
                    try {
                        actor = communityService.getModelService().find( Actor.class, actor.getId() );
                        assert actor.isActual();
                    } catch ( Exception e ) {
                        LOG.warn( "Failed to find actor " + actor.getId() );
                        actor = null;
                    }
                }
                // if agency directly identified
                if ( registeredOrgId != null ) {
                    assert fixedOrgId == null;
                    RegisteredOrganizationService registeredOrganizationService
                            = communityService.getRegisteredOrganizationService();
                    RegisteredOrganization registeredOrganization = registeredOrganizationService.load( registeredOrgId );
                    if ( registeredOrganization != null ) {
                        agency = new Agency( registeredOrganization, communityService );
                        placeholder = null;
                    } else {
                        LOG.warn( "Invalid organization registration " + registeredOrgId );
                        registeredOrgId = null;
                    }
                }
                if ( fixedOrgId != null ) {
                    assert registeredOrgId == null;
                    try {
                        Organization org = communityService.getModelService().find( Organization.class, fixedOrgId );
                        if ( org != null && !org.isUnknown() ) {
                            RegisteredOrganization registeredOrganization = communityService
                                    .getRegisteredOrganizationService().find( org.getName(), communityService );
                            if ( registeredOrganization != null )
                                agency = new Agency( registeredOrganization, communityService );
                        }
                        placeholder = null;
                    } catch ( NotFoundException e ) {
                        LOG.warn( "Failed to find organization " + fixedOrgId );
                        fixedOrgId = null;
                    }
                }
                if ( placeholder != null && !placeholder.isPlaceHolder() ) {
                    LOG.warn( placeholder.getName() + " no longer a placeholder" );
                    placeholder = null;
                }
                if ( actor != null && agency != null ) {
                    agent = (Agent) CollectionUtils.find(
                            agency.getAgents( communityService ),
                            new Predicate() {
                                @Override
                                public boolean evaluate( Object object ) {
                                    return ( (Agent) object ).getActor().equals( actor );
                                }
                            }
                    );
                }

            }
            initialized = true;
        }


        public Agency getAgency() {
            assert initialized;
            return agency;
        }

        public void makeApplyToAgencyIfPossible( Agency agency ) {
            RegisteredOrganization registeredOrganization = agency.getRegisteredOrganization();
            if ( registeredOrganization.isFixedOrganization()
                    && fixedOrgId == null // todo - really?
                    && registeredOrgId == null ) {
                setFixedOrgId( registeredOrganization.getFixedOrganizationId() );
            } else if ( registeredOrgId == null && !registeredOrganization.isFixedOrganization() ) {
                setRegisteredOrgId( registeredOrganization.getUid() );
            }
        }

        public Agent getAgent() {
            assert initialized;
            return agent;
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
                registeredOrgId = null;
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
                registeredOrgId = null;
                placeholder = null;
            }
            initialized = false;
        }

        public String getRegisteredOrgId() {
            return registeredOrgId;
        }

        public void setRegisteredOrgId( String registeredOrgId ) {
            this.registeredOrgId = registeredOrgId;
            if ( registeredOrgId != null ) {
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
                        && ChannelsUtils.areEqualOrNull( fixedOrgId, other.getFixedOrgId() )
                        && ChannelsUtils.areEqualOrNull( registeredOrgId, other.getRegisteredOrgId() )
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
            if ( fixedOrgId != null )
                hash = hash + 31 * fixedOrgId.hashCode();
            if ( registeredOrgId != null )
                hash = hash + 31 * registeredOrgId.hashCode();
            if ( placeholder != null )
                hash = hash + 31 * placeholder.hashCode();
            return hash;
        }

        public Map<String, Object> mapState() {
            Map<String, Object> state = new HashMap<String, Object>();
            if ( actor != null ) state.put( "actor", actor.getId() );
            if ( jurisdiction != null ) state.put( "jurisdiction", jurisdiction.getId() );
            if ( fixedOrgId != null ) state.put( "fixedOrgId", fixedOrgId );
            if ( registeredOrgId != null ) state.put( "registeredOrgId", registeredOrgId );
            if ( placeholder != null ) state.put( "placeholder", placeholder.getId() );
            return state;
        }

        public void initFromMap( Map<String, Object> state, CommunityService communityService ) {
            ModelService modelService = communityService.getModelService();
            if ( state.containsKey( "actor" ) ) {
                Long id = (Long) state.get( "actor" );
                try {
                    actor = modelService.find( Actor.class, id );
                } catch ( NotFoundException e ) {
                    LOG.warn( "Actor not found at " + id );
                }
            }
            if ( state.containsKey( "jurisdiction" ) ) {
                Long id = (Long) state.get( "jurisdiction" );
                try {
                    jurisdiction = modelService.find( Place.class, id );
                } catch ( NotFoundException e ) {
                    LOG.warn( "Place not found at " + id );
                }
            }
            if ( state.containsKey( "fixedOrgId" ) ) {
                fixedOrgId = (Long) state.get( "fixedOrgId" );
            }
            if ( state.containsKey( "registeredOrgId" ) ) {
                registeredOrgId = (String) state.get( "registeredOrgId" );
            }
            if ( state.containsKey( "placeholder" ) ) {
                Long id = (Long) state.get( "placeholder" );
                try {
                    placeholder = modelService.find( Organization.class, id );
                } catch ( NotFoundException e ) {
                    LOG.warn( "Organization not found at " + id );
                }
            }
            initialize( communityService );
        }

        public boolean references( ModelObject mo ) {
            return ModelObject.areIdentical( jurisdiction, mo )
                    || ModelObject.areIdentical( placeholder, mo )
                    || ModelObject.areIdentical( actor, mo );
        }

        public boolean isAnyone() {
            return actor == null
                    && jurisdiction == null
                    && fixedOrgId == null
                    && registeredOrgId == null
                    && placeholder == null;
        }

        public boolean narrowsOrEquals( AgentSpec agentSpec, CommunityService communityService ) {
            Place locale = communityService.getPlanCommunity().getLocale( communityService );
            return ( agentSpec.getAgent() == null
                    || ( getAgent() != null && getAgent().equals( agentSpec.getAgent() ) ) )
                    && ModelEntity.implies( jurisdiction, agentSpec.getJurisdiction(), locale )
                    && ( agentSpec.getAgency() == null
                    || ( getAgency() != null
                    && ( getAgency().equals( agentSpec.getAgency() )
                    || getAgency().hasAncestor( agentSpec.getAgency(), communityService ) ) ) )
                    && ModelEntity.implies( placeholder, agentSpec.getPlaceholder(), locale );
        }

        public boolean isAgencyImplied() {
            return getAgent() != null;
        }

        public boolean isPlaceholderImplied() {
            return getAgency() != null;
        }

        public boolean appliesToAssignment( CommunityAssignment assignment, CommunityService communityService ) {
            return isAnyone()
                    || ( appliesToAgent( assignment.getAgent() )      // todo - deal with jurisdiction matching when re-enabled
                    && appliesToAgency( assignment.getAgency(), communityService ) );
        }

        private boolean appliesToAgent( Agent anAgent ) {
            return getAgent() == null || getAgent().equals( anAgent );
        }

        public boolean appliesToAgency( final Agency anAgency, final CommunityService communityService ) {
            if ( getAgency() != null )
                return getAgency().equals( anAgency )
                        || anAgency.hasAncestor( getAgency(), communityService );
            else if ( getPlaceholder() != null ) {
                return CollectionUtils.exists(
                        anAgency.getPlaceholders( communityService ),
                        new Predicate() {
                            @Override
                            public boolean evaluate( Object object ) {
                                Organization otherPlaceHolder = (Organization) object;
                                return getPlaceholder().equals( otherPlaceHolder )
                                        || anAgency.hasAncestorWithPlaceholder( getPlaceholder(), communityService );
                            }
                        }
                );
            }
            return true;
        }
    }
}
