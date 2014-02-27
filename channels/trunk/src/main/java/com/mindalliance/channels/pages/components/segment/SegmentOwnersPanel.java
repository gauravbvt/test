package com.mindalliance.channels.pages.components.segment;

import com.mindalliance.channels.core.command.commands.UpdateObject;
import com.mindalliance.channels.core.command.commands.UpdatePlanObject;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.util.SortableBeanProvider;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.AbstractTablePanel;
import com.mindalliance.channels.pages.components.guide.Guidable;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/26/14
 * Time: 1:54 PM
 */
public class SegmentOwnersPanel extends AbstractCommandablePanel implements Guidable {

    private final IModel<Segment> segmentModel;
    private Label note;
    private SegmentOwnersTable segmentOwnersTable;

    public SegmentOwnersPanel( String id, PropertyModel<Segment> segmentModel, Set<Long> expansions ) {
        super( id, segmentModel, expansions );
        this.segmentModel = segmentModel;
        init();
    }

    @Override
    public String getHelpSectionId() {
        return "scoping";
    }

    @Override
    public String getHelpTopicId() {
        return "segment-owners"; // todo
    }


    private void init() {
        addNote();
        addOwnersTable();
    }

    private void addNote() {
        note = new Label( "note", getNote() );
        note.setOutputMarkupId( true );
        addOrReplace( note );
    }

    private String getNote() {
        ChannelsUser user = getUser();
        boolean canModify = getSegment().isModifiabledBy( getUsername(), getCommunityService() );
        boolean isPlanner = user.isPlanner( getPlan().getUri() );
        boolean isAdmin = user.isAdmin();
        boolean isExplicitOwner = getSegment().isOwnedBy( getUsername() );
        StringBuilder sb = new StringBuilder();
        if ( canModify ) {
            sb.append( "You can modify this segment because " );
            if ( isAdmin ) {
                sb.append( "you are an administrator." );
            } else if ( !isExplicitOwner ) {
                sb.append( "no one explicitly owns it and you are a developer." );
            } else {
                sb.append( "you are a developer and you own it." );
            }
        } else {
            sb.append( "You can not modify this segment because " );
            if ( isPlanner ) {
                sb.append( " even though you are a developer others own it." );
            } else {
                sb.append( " you are not a developer." );
            }
        }
        if ( isLockedByOtherUser( getSegment() ) ) {
            ChannelsUser otherUser = getCommunityService()
                    .getUserRecordService()
                    .getUserWithIdentity( getLockOwner( getSegment() ) );
            if ( otherUser != null ) {
                sb.append( " (" )
                        .append( otherUser.getFullName() )
                        .append( " is currently editing the definition of this segment.) " );
            }
        }
        return sb.toString();
    }

    private Segment getSegment() {
        return segmentModel.getObject();
    }

    private void addOwnersTable() {
        segmentOwnersTable = new SegmentOwnersTable(
                "ownersTable",
                new PropertyModel<List<SegmentOwner>>( this, "segmentOwners" ) );
        addOrReplace( segmentOwnersTable );
    }

    public List<SegmentOwner> getSegmentOwners() {
        List<SegmentOwner> segmentOwners = new ArrayList<SegmentOwner>();
        String uri = getPlan().getUri();
        for ( ChannelsUser user : getCommunityService().getUserRecordService().getAllEnabledUsers() ) {
            if ( user.isPlannerOrAdmin( uri ) ) {
                segmentOwners.add( new SegmentOwner( user ) );
            }
        }
        return segmentOwners;
    }

    public void update( AjaxRequestTarget target, Object object, String action ) {
        if ( object instanceof SegmentOwner ) {
            if ( action.equals( "owner" ) ) {
                addNote();
                target.add( note );
                addOwnersTable();
                target.add( segmentOwnersTable );
            }
        }
    }

    public class SegmentOwner implements Serializable {

        private ChannelsUser owner;

        public SegmentOwner( ChannelsUser user ) {
            this.owner = user;
        }

        public boolean isOwner() {
            return getSegment().isOwnedBy( getOwnerUsername() );
        }

        public void setOwner( boolean val ) {
            doCommand(
                    new UpdatePlanObject(
                            getUsername(),
                            getSegment(),
                            "owners",
                            owner.getUsername(),
                            val ? UpdateObject.Action.Add : UpdateObject.Action.Remove )
            );
        }

        public String getOwnerUsername() {
            return owner.getUsername();
        }

        public String getNormalizedFullName() {
            return owner.getNormalizedFullName();
        }

        public String getPrivileges() {
            StringBuilder sb = new StringBuilder(  );
            if ( owner.isAdmin() ) {
                sb.append("Administrator");
            }
            if ( owner.isPlanner( getPlan().getUri() ) ) {
                if ( sb.length() > 0 ) sb.append(", ");
                sb.append( "Developer");
            }
            if ( isOwner() ) {
                if ( sb.length() > 0 ) sb.append(", ");
                sb.append( "Owner");
            }
            return sb.toString();
        }

        public boolean isCanBeChanged() {
            return isLockedByUser( getSegment() )
                    && owner.isPlanner( getPlan().getUri() );
        }

        public String getCanModifyYesNo() {
            return getSegment().isModifiableBy( owner, getCommunityService() )
                    ? "Yes"
                    : "No";
        }
    }

    private class SegmentOwnersTable extends AbstractTablePanel<SegmentOwner> {

        private IModel<List<SegmentOwner>> segmentOwners;

        private SegmentOwnersTable( String s, IModel<List<SegmentOwner>> segmentOwners ) {
            super( s );
            this.segmentOwners = segmentOwners;
            initTable();
        }

        @SuppressWarnings( "unchecked" )
        private void initTable() {
            final List<IColumn<SegmentOwner>> columns = new ArrayList<IColumn<SegmentOwner>>();
            columns.add( makeColumn( "Name", "normalizedFullName", EMPTY ) );
            columns.add( makeColumn( "User id", "ownerUsername", EMPTY ) );
            columns.add( makeColumn( "Privileges", "privileges", EMPTY ) );
            columns.add( makeCheckBoxColumn( "Is owner", "owner", "canBeChanged", SegmentOwnersPanel.this ) );
            columns.add( makeColumn( "Can modify", "canModifyYesNo", EMPTY ) );
            addOrReplace( new AjaxFallbackDefaultDataTable(
                    "segmentOwners",
                    columns,
                    new SortableBeanProvider<SegmentOwner>( segmentOwners.getObject(), "normalizedFullName" ),
                    getPageSize()
            ) );
        }
    }
}
