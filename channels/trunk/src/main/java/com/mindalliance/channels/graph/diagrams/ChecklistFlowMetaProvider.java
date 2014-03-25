package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.EventTiming;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Goal;
import com.mindalliance.channels.core.model.Information;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.asset.AssetConnection;
import com.mindalliance.channels.core.model.checklist.ActionStep;
import com.mindalliance.channels.core.model.checklist.AssetProducedOutcome;
import com.mindalliance.channels.core.model.checklist.CapabilityCreatedOutcome;
import com.mindalliance.channels.core.model.checklist.Checklist;
import com.mindalliance.channels.core.model.checklist.ChecklistElement;
import com.mindalliance.channels.core.model.checklist.CommunicationStep;
import com.mindalliance.channels.core.model.checklist.Condition;
import com.mindalliance.channels.core.model.checklist.EventTimingCondition;
import com.mindalliance.channels.core.model.checklist.EventTimingOutcome;
import com.mindalliance.channels.core.model.checklist.GoalAchievedOutcome;
import com.mindalliance.channels.core.model.checklist.GoalCondition;
import com.mindalliance.channels.core.model.checklist.LocalCondition;
import com.mindalliance.channels.core.model.checklist.NeedSatisfiedCondition;
import com.mindalliance.channels.core.model.checklist.Outcome;
import com.mindalliance.channels.core.model.checklist.ReceiptConfirmationStep;
import com.mindalliance.channels.core.model.checklist.Step;
import com.mindalliance.channels.core.model.checklist.SubTaskStep;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.engine.analysis.graph.ChecklistElementRelationship;
import com.mindalliance.channels.engine.imaging.ImagingService;
import com.mindalliance.channels.graph.AbstractMetaProvider;
import com.mindalliance.channels.graph.DOTAttribute;
import com.mindalliance.channels.graph.DOTAttributeProvider;
import com.mindalliance.channels.graph.URLProvider;
import org.jgrapht.ext.EdgeNameProvider;
import org.jgrapht.ext.VertexNameProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.io.File;
import java.text.MessageFormat;
import java.util.List;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/13/13
 * Time: 9:37 PM
 */
public class ChecklistFlowMetaProvider extends AbstractMetaProvider<ChecklistElement, ChecklistElementRelationship> {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( ChecklistFlowMetaProvider.class );
    /**
     * Font for node labels
     */
    public static final String NODE_FONT = "DejaVuSans";

    /**
     * Font size for node labels.
     */
    public static final String NODE_FONT_SIZE = "10";

    private static final String CONDITION_ICON_IF = "step_condition_if";
    private static final String CONDITION_ICON_UNLESS = "step_condition_unless";
    private static final String ACTION_ICON = "step_action";
    private static final String NOTIFICATION_ICON = "step_notification";
    private static final String REQUEST_ICON = "step_request";
    private static final String ANSWER_ICON = "step_answer";
    private static final String RECEIPT_CONFIRMATION_ICON = "step_receipt_confirmation";
    private static final String RESEARCH_ICON = "step_research";
    private static final String FOLLOW_UP_ICON = "step_follow_up";
    private static final String EVENT_OUTCOME_ICON = "event";
    private static final String CAPABILITY_CREATED_OUTCOME_ICON = "info_product";
    private static final String ASSET_PRODUCED_OUTCOME_ICON = "asset";
    private static final String ASSET_PROVISIONED_ICON = "asset_provisioned";


    private final Part part;
    private final boolean interactive;

    public ChecklistFlowMetaProvider( Part part,
                                      String outputFormat,
                                      Resource imageDirectory,
                                      Analyst analyst,
                                      CommunityService communityService,
                                      boolean interactive ) {
        super( outputFormat, imageDirectory, analyst, communityService );
        this.part = part;
        this.interactive = interactive;
    }

    @Override
    public Object getContext() {
        return part.getEffectiveChecklist();
    }

    @Override
    public URLProvider<ChecklistElement, ChecklistElementRelationship> getURLProvider() {
        return new URLProvider<ChecklistElement, ChecklistElementRelationship>() {
            @Override
            public String getGraphURL( ChecklistElement vertex ) {
                return null;
            }

            @Override
            public String getVertexURL( ChecklistElement vertex ) {
                if ( interactive ) {
                    Checklist checklist = (Checklist) getContext();
                    List<Step> steps = checklist.listEffectiveSteps();
                    int index = Integer.parseInt( "" + vertex.getId() );
                    if ( index >= 0 && index < steps.size() ) {
                        Step step = steps.get( index );
                        if ( !step.isActionStep() ) {
                            Object[] args = {checklist.getPart().getId(), vertex.getId()};
                            return MessageFormat.format( VERTEX_URL_FORMAT, args );
                        }
                    }
                }
                return null;
            }

            @Override
            public String getEdgeURL( ChecklistElementRelationship edge ) {
                return null;
            }
        };
    }

    @Override
    public DOTAttributeProvider<ChecklistElement, ChecklistElementRelationship> getDOTAttributeProvider() {
        return new ChecklistDOTAttributeProvider();
    }

    @Override
    public EdgeNameProvider<ChecklistElementRelationship> getEdgeLabelProvider() {
        return new EdgeNameProvider<ChecklistElementRelationship>() {
            @Override
            public String getEdgeName( ChecklistElementRelationship cleRel ) {
                if ( cleRel.isBetweenConditions() )
                    return "and";
                else
                    return "";
            }
        };
    }

    @Override
    public VertexNameProvider<ChecklistElement> getVertexLabelProvider() {
        return new VertexNameProvider<ChecklistElement>() {
            @Override
            public String getVertexName( ChecklistElement cle ) {
                return getIconLabelSeparated( cle ).replaceAll( "\\|", "\\\\n" );
            }
        };
    }

    private String getIconLabelSeparated( ChecklistElement cle ) {
        String prefix = getLabelPrefix( cle );
        String postfix = ChannelsUtils.split( getLabelPostfix( cle ), "|", 4, LINE_WRAP_SIZE );
        return prefix + (prefix.isEmpty() ? "" : "|" ) + postfix;
    }

    private String getLabelPrefix( ChecklistElement cle ) {
        StringBuilder sb = new StringBuilder();
        if ( cle.getStep() != null ) {
            Step step = cle.getStep();
            if ( step.isCommunicationStep() ) {
                CommunicationStep commStep = (CommunicationStep) step;
                sb.append( commStep.isNotification()
                        ? "SEND"
                        : commStep.isAnswer()
                        ? "ANSWER WITH"
                        : "ASK FOR");
                Flow.Intent intent = commStep.getSharing().getIntent();
                       sb.append( intent == null ? " INFO" : (" " + intent.getLabel().toUpperCase() ) );
            } else if ( step.isReceiptConfirmation() ) {
                sb.append( "CONFIRM RECEIPT OF" );
            } else if ( step.isSubTaskStep() ) {
                SubTaskStep subTaskStep = (SubTaskStep) step;
                sb.append( subTaskStep.isResearch() ? "RESEARCH " : "FOLLOW UP ON " );
            }
        }
        return sanitize( sb.toString() );
    }

    private String getLabelPostfix( ChecklistElement cle ) {
        StringBuilder sb = new StringBuilder();
        if ( cle.getStep() != null ) {
            Step step = cle.getStep();
            if ( step.isActionStep() ) {
                sb.append( ( (ActionStep) step ).getAction() );
            } else if ( step.isCommunicationStep() ) {
                CommunicationStep commStep = (CommunicationStep) step;
                sb.append( commStep.getSharing().getName() );

            } else if ( step.isReceiptConfirmation() ) {
                ReceiptConfirmationStep confStep = (ReceiptConfirmationStep) step;
                Flow sharing = confStep.getSharingToConfirm();
                sb.append( sharing.isNotification()
                                ? sharing.getIntent() != null
                                ? ( sharing.getIntent().getLabel().toLowerCase() + " " )
                                : "notification of "
                                : "request for "
                        )
                        .append( sharing.getName() );
            } else if ( step.isSubTaskStep() ) {
                SubTaskStep subTaskStep = (SubTaskStep) step;
                sb.append( subTaskStep.getSharing().getName() );
            }
        } else if ( cle.isCondition() ) { // condition
            Condition condition = cle.getCondition();
            if ( condition.isLocalCondition() ) {
                sb.append( ( (LocalCondition) condition ).getState() );
            } else if ( condition.isGoalCondition() ) {
                Goal goal = ( (GoalCondition) condition ).getGoal();
                sb.append( goal.getLabel() )
                        .append( goal.isGain() ? " is realized" : " is mitigated" );

            } else if ( condition.isNeedSatisfiedCondition() ) {
                Information need = ( (NeedSatisfiedCondition) condition ).getNeededInfo();
                sb.append( "The need for " )
                        .append( need.getName() )
                        .append( " is satisfied" );

            } else if ( condition.isTaskFailedCondition() ) {
                sb.append( "the task failed" );
            } else { //EventTiming condition
                sb.append( ( (EventTimingCondition) condition ).getEventTiming().getLabel() );
            }
        } else if ( cle.isOutcome() ) {
            Outcome outcome = cle.getOutcome();
            if ( outcome.isEventTimingOutcome() ) {
                EventTiming eventTiming = ( (EventTimingOutcome) outcome ).getEventTiming();
                Event event = eventTiming.getEvent();
                sb.append( event.getLabel() );
                sb.append( eventTiming.isConcurrent() ? " is caused" : " is terminated");
            } else if ( outcome.isGoalAchievedOutcome() ) {
                Goal goal = ( (GoalAchievedOutcome) outcome ).getGoal();
                sb.append( goal.getLabel() )
                        .append( goal.isGain() ? " is realized" : " is mitigated" );
            } else if ( outcome.isCapabilityCreatedOutcome() ) {
                Information capability = ( (CapabilityCreatedOutcome) outcome ).getShareableInfo();
                sb.append( "INFO \"" )
                        .append( capability.getName() )
                        .append( "\" can now be shared" );
            } else if ( outcome.isAssetProducedOutcome() ) {
                AssetConnection assetConnection = ( (AssetProducedOutcome)outcome).getAssetConnection();
                sb.append( assetConnection.getStepOutcomeLabel() );
            }
        } else if ( cle.isAssetProvisioning() ) {
            sb.append( cle.getAssetProvisioning().getShortLabel( ) );
        }
        return sanitize( sb.toString() );
    }


    @Override
    public VertexNameProvider<ChecklistElement> getVertexIDProvider() {
        return new VertexNameProvider<ChecklistElement>() {
            @Override
            public String getVertexName( ChecklistElement vertex ) {
                return vertex.getTypeName() + "_" + String.valueOf( vertex.getId() );
            }
        };
    }


    private class ChecklistDOTAttributeProvider
            implements DOTAttributeProvider<ChecklistElement, ChecklistElementRelationship> {

        @Override
        public List<DOTAttribute> getVertexAttributes( CommunityService communityService,
                                                       ChecklistElement vertex,
                                                       boolean highlighted ) {
            List<DOTAttribute> list = DOTAttribute.emptyList();
            list.add( new DOTAttribute( "image", getIcon( getAnalyst().getImagingService(), vertex ) ) );
            list.add( new DOTAttribute( "labelloc", "b" ) );
            list.add( new DOTAttribute( "shape", "none" ) );
            list.add( new DOTAttribute( "fontname", NODE_FONT ) );
            list.add( new DOTAttribute( "fontcolor", FONTCOLOR ) );
            list.add( new DOTAttribute( "fontsize", NODE_FONT_SIZE ) );
            list.add( new DOTAttribute( "tooltip", sanitize( getTooltip( vertex ) ) ) );
            return list;
        }

        @Override
        public List<DOTAttribute> getEdgeAttributes( CommunityService communityService,
                                                     ChecklistElementRelationship edge,
                                                     boolean highlighted ) {
            List<DOTAttribute> list = DOTAttribute.emptyList();
            if ( edge.isWithOutcome() || edge.isWithAssetProvisioning() ) {
                list.add( new DOTAttribute( "color", "gray" ) );
                list.add( new DOTAttribute( "arrowhead", "none" ) );
                list.add( new DOTAttribute( "len", "1.5" ) );
                list.add( new DOTAttribute( "weight", "2.0" ) );
            }  else {
                list.add( new DOTAttribute( "color", "#666666" ) );
                list.add( new DOTAttribute( "len", "1.5" ) );
                list.add( new DOTAttribute( "weight", "2.0" ) );
                list.add( new DOTAttribute( "arrowsize", "0.7" ) );
                list.add( new DOTAttribute( "style", "normal" ) );
                list.add( new DOTAttribute( "arrowhead", "normal" ) );
                list.add( new DOTAttribute( "fontname", EDGE_FONT_BOLD ) );
                list.add( new DOTAttribute( "fontsize", EDGE_FONT_SIZE ) );
                list.add( new DOTAttribute( "fontcolor", "darkslategray" ) );
            }
            return list;
        }

        @Override
        public List<DOTAttribute> getGraphAttributes() {
            List<DOTAttribute> list = DOTAttribute.emptyList();
            list.add( new DOTAttribute( "fontcolor", FONTCOLOR ) );
            list.add( new DOTAttribute( "rankdir", getGraphOrientation() ) );
            if ( getGraphSize() != null ) {
                list.add( new DOTAttribute( "size", getGraphSizeString() ) );
                //list.add( new DOTAttribute( "ratio", "compress" ) );
            }
            return list;
        }

        @Override
        public List<DOTAttribute> getSubgraphAttributes( boolean highlighted ) {
            return DOTAttribute.emptyList();
        }

        private String getIcon( ImagingService imagingService,
                                ChecklistElement vertex ) {
            int numLines = 0;
            String iconName;
            String[] lines = getIconLabelSeparated( vertex ).split( "\\|" );
            numLines = Math.min( lines.length, 5 );
            if ( vertex.isStep() ) {
                Step step = vertex.getStep();
                if ( step.isActionStep() ) {
                    iconName = ACTION_ICON;
                } else if ( step.isCommunicationStep() ) {
                    CommunicationStep communicationStep = (CommunicationStep)step;
                    iconName = communicationStep.isNotification()
                            ? NOTIFICATION_ICON
                            : communicationStep.isAnswer()
                                ? ANSWER_ICON
                                : REQUEST_ICON;
                } else if ( step.isReceiptConfirmation() ) {
                    iconName = RECEIPT_CONFIRMATION_ICON;
                } else {
                    iconName = ( (SubTaskStep) step ).isResearch()
                            ? RESEARCH_ICON
                            : FOLLOW_UP_ICON;
                }
            } else if ( vertex.isCondition() ) {
                iconName = vertex.getContext().equals( Condition.IF )
                        ? CONDITION_ICON_IF
                        : CONDITION_ICON_UNLESS;
            } else if ( vertex.isOutcome() ) {
                Outcome outcome = vertex.getOutcome();
                iconName = outcome.isEventTimingOutcome()
                        ? EVENT_OUTCOME_ICON
                        : outcome.isCapabilityCreatedOutcome()
                        ? CAPABILITY_CREATED_OUTCOME_ICON
                        : outcome.isAssetProducedOutcome()
                        ? ASSET_PRODUCED_OUTCOME_ICON
                        : outcome.isGoalAchievedOutcome()
                        ? getGoalIcon( ( (GoalAchievedOutcome) outcome ).getGoal(), part )
                        : null;
            } else if ( vertex.isAssetProvisioning() ) {
                iconName = ASSET_PROVISIONED_ICON;

            } else {
                iconName = null;
            }

            String name = imagingService.getImageDirPath() + '/' + iconName + ( numLines > 0 ? numLines : "" ) + ".png";
            if ( !new File( name ).canRead() ) {
                LOG.warn( "Icon file not found " + name );
            }
            return name;
        }


    }

    private String getTooltip( ChecklistElement vertex ) {
        if ( vertex.isStep() )
            return vertex.getStep().isRequired()
                    ? "Required"
                    : "Optional";
        else
            return "";
    }

    private String getGoalIcon( Goal goal, Part part ) {
        if ( goal.isRiskMitigation() ) {
            switch ( goal.getLevel() ) {
                case Low:
                    return "risk_minor";
                case Medium:
                    return "risk_major";
                case High:
                    return "risk_severe";
                case Highest:
                    return "risk_extreme";
                default:
                    throw new RuntimeException( "Unknown risk level" );
            }
        } else {
            switch ( goal.getLevel() ) {
                case Low:
                    return "gain_low";
                case Medium:
                    return "gain_medium";
                case High:
                    return "gain_high";
                case Highest:
                    return "gain_highest";
                default:
                    throw new RuntimeException( "Unknown gain level" );
            }
        }
    }

}
