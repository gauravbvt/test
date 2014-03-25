package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.api.AssetConnectionData;
import com.mindalliance.channels.api.directory.ContactData;
import com.mindalliance.channels.api.entities.MediumData;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.protocols.CommunityAssignment;
import com.mindalliance.channels.core.community.protocols.CommunityCommitment;
import com.mindalliance.channels.core.community.protocols.CommunityCommitments;
import com.mindalliance.channels.core.community.protocols.CommunityEmployment;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.InfoFormat;
import com.mindalliance.channels.core.model.InfoProduct;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.core.model.asset.AssetConnection;
import com.mindalliance.channels.core.query.ModelService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/9/11
 * Time: 1:37 PM
 */
public abstract class AbstractFlowData extends AbstractProcedureElementData {

    private String serverUrl;
    private boolean initiating;
    private Flow flow;
    private Level failureSeverity;
    private List<CommunityEmployment> allEmployments;
    private DocumentationData documentation;
    private List<MediumData> mediumDataList;
    private List<ChannelData> channelDataList;
    private List<AssetConnectionData> assetConnectionDataList;


    private AssignmentData assignmentData;

    public AbstractFlowData() {
        // required
    }

    public AbstractFlowData(
            String serverUrl,
            CommunityService communityService,
            boolean initiating,
            Flow flow,
            CommunityAssignment assignment,
            ChannelsUser user ) {
        super( communityService, assignment, user );
        this.serverUrl = serverUrl;
        this.initiating = initiating;
        this.flow = flow;
    }

    protected void initOtherData( CommunityService communityService ) {
         ModelService modelService = communityService.getModelService();
        initFailureSeverity( modelService );
        initMediumDataList( serverUrl, communityService );
        initChannelDataList( communityService );
        documentation = new DocumentationData( serverUrl, getSharing() );
    }

    protected void initAssignmentData( String serverUrl, CommunityService communityService, ChannelsUser user ) {
        CommunityCommitments commitments = communityService.findAllCommitments( getSharing(), false );
        assignmentData = new AssignmentData(
                serverUrl,
                getAssignment(),
                commitments.benefiting( getAssignment() ),
                commitments.committing( getAssignment() ),
                communityService,
                user
        );
    }

    protected void initAssetConnections() {
        assetConnectionDataList = new ArrayList<AssetConnectionData>(  );
        for ( AssetConnection assetConnection : getSharing().getAssetConnections().getAll() ) {
            assetConnectionDataList.add( new AssetConnectionData( assetConnection ));
        }
    }

    public List<AssetConnectionData> getAssetConnections() {
        return assetConnectionDataList;
    }


    public AssignmentData getAssignmentData() {
        return assignmentData;
    }


    private void initChannelDataList( CommunityService communityService ) {
        channelDataList = new ArrayList<ChannelData>();
        for ( Channel channel : getSharing().getChannels() ) {
            channelDataList.add( new ChannelData( channel, communityService ) );
        }
    }

    private void initMediumDataList( String serverUrl, CommunityService communityService ) {
        mediumDataList = new ArrayList<MediumData>(  );
        for ( TransmissionMedium medium : getSharing().transmissionMedia() ) {
            mediumDataList.add( new MediumData( serverUrl, medium, communityService ) );
        }
    }

    private void initFailureSeverity( ModelService modelService ) {
        failureSeverity = modelService.computeSharingPriority( getSharing() );
    }

    public boolean isInitiating() {
        return initiating;
    }

    public boolean getReceiptConfirmationRequested() {
        return getSharing().isReceiptConfirmationRequested();
    }

    public SharedInformationData getInformation() {
        return new SharedInformationData( getSharing() );
    }

    public String getIntent() {
        return getSharing().getIntent() == null
                ? null
                : getSharing().getIntent().getLabel();
    }

    public boolean isContextCommunicated() {
        return getSharing().isReferencesEventPhase();
    }

    public String getCommunicableContext() {
        return getSharing().getSegment().getPhaseEventTitle();
    }

    public String getCommunicatedContext() {
        return getSharing().isReferencesEventPhase()
                ? getSharing().getSegment().getPhaseEventTitle()
                : null;
    }

    private List<ContactData> findContactsFromEmployment(
            CommunityEmployment employment,
            CommunityCommitment commitment,
            CommunityService communityService ) {
        return ContactData.findContactsFromEmploymentAndCommitment(
                serverUrl,
                employment,
                commitment,
                communityService,
                getUser() == null ? null : getUser().getUserRecord()
        );

    }


    public List<Long> getMediumIds() {
        List<Long> media = new ArrayList<Long>();
        for ( TransmissionMedium medium : getSharing().transmissionMedia() ) {
            media.add( medium.getId() );
        }
        return media;
    }

    public List<Long> getInfoProductIds() {
        List<Long> ids = new ArrayList<Long>(  );
        InfoProduct infoProduct = getSharing().getInfoProduct();
        if ( infoProduct != null ) {
            ids.add( infoProduct.getId() );
        }
        return ids;
    }

    public List<Long> getInfoFormatIds() {
        Set<Long> ids = new HashSet<Long>(  );
        for ( Channel channel : getSharing().getChannels() ) {
            InfoFormat format = channel.getFormat();
            if ( format != null ) {
                ids.add( format.getId() );
            }
        }
        return new ArrayList<Long>( ids );
    }

    public Set<Long> getAssetIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( AssetConnectionData assetConnectionData : assetConnectionDataList ) {
            ids.add( assetConnectionData.getAssetId() );
        }
        return ids;
    }

        public List<MediumData> mediumDataList() {
        return mediumDataList;
    }

    public List<ChannelData> getChannelDataList() {
        return channelDataList;
    }


    public boolean getTaskFailed() {
        return getSharing().isIfTaskFails();
    }

    public boolean getContactAll() {
        return getSharing().isAll();
    }

    public TimeDelayData getMaxDelay() {
        return new TimeDelayData( getSharing().getMaxDelay() );
    }

    public String getInstructions() {
        String instructions = getSharing().getDescription();
        return instructions == null
                ? null
                : instructions;
    }

    public String getFailureImpact() {
        return getFailureSeverity().getNegativeLabel();
    }

    public Level getFailureSeverity() {
        return failureSeverity;
    }

    public Set<Long> allOrganizationIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( CommunityEmployment employment : findAllEmployments() ) {
            for ( Organization organization :employment.getEmployer().getPlanOrganizations()) {
                ids.add( organization.getId() );
            }
        }
        return ids;
    }

    public Set<Long> allActorIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( CommunityEmployment employment : findAllEmployments() ) {
            ids.add( employment.getAgent().getActorId() );
            if ( employment.getSupervisor() != null )
                ids.add( employment.getSupervisor().getId() );
        }
        for ( TransmissionMedium medium : getSharing().transmissionMedia() ) {
            if ( medium.getQualification() != null )
                ids.add( medium.getQualification().getId() );
        }
        return ids;
    }

    public Set<Long> allRoleIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( CommunityEmployment employment : findAllEmployments() ) {
            ids.add( employment.getRole().getId() );
        }
        return ids;
    }

    public Set<Long> allPlaceIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( CommunityEmployment employment : findAllEmployments() ) {
            if ( employment.getJurisdiction() != null )
                ids.add( employment.getJurisdiction().getId() );
        }
        return ids;
    }

    private List<CommunityEmployment> findAllEmployments() {
        if ( allEmployments == null ) {
            Set<CommunityEmployment> employmentSet = new HashSet<CommunityEmployment>();
            for ( ContactData contactData : getContacts() ) {
                employmentSet.add( contactData.employment() );
                employmentSet.addAll( contactData.bypassEmployments() );
            }
            allEmployments = new ArrayList<CommunityEmployment>( employmentSet );
        }
        return allEmployments;
    }

    public abstract List<ContactData> getContacts();


/*    public List<AgreementData> getAgreements() {
        List<AgreementData> agreements = new ArrayList<AgreementData>(  );
        for ( Agreement agreement : getPlanService().findAllConfirmedAgreementsCovering( sharing ) ) {
            agreements.add( new AgreementData( agreement ) );
        }
        return agreements;
    }*/


    public DocumentationData getDocumentation() {
        return documentation;
    }


    public boolean isNotification() {
        return flow.isNotification();
    }


    public Flow getSharing() {
        return flow;
    }

    public abstract List<CommunityEmployment> findContactEmployments();

    public Flow flow() {
        return flow;
    }

    public String getIntentText() {
        String intent = getIntent();
        if ( intent == null ) {
            return "";
        } else {
            return intent.equalsIgnoreCase( "command" )
                    ? intent
                    : intent + " about";
        }
    }

    public String getId() {
        return Long.toString( flow.getId() );
    }

    public boolean isRequest() {
        return flow.isAskedFor();
    }
}
