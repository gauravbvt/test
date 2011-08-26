package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.core.attachments.AttachmentManager;
import com.mindalliance.channels.core.model.Attachment;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.ResourceSpec;
import org.apache.wicket.model.Model;

import java.io.Serializable;
import java.io.StringWriter;
import java.util.List;

/**
 * Basic task holder for reports.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/30/11
 * Time: 12:13 PM
 */
public class ReportTask  implements Serializable, Comparable<ReportTask> {

    private final int segmentSeq;
    private int taskSeq;
    private final Part part;

    public ReportTask( int segmentSeq, Part part ) {

        this.segmentSeq = segmentSeq;
        this.part = part;
    }

    // TODO: Move to ChannelsUtils
    public static String ensurePeriod( String sentence ) {
        return sentence == null || sentence.isEmpty() || sentence.endsWith( "." )
                || sentence.endsWith( ";" ) ?
                sentence :
                sentence + '.';
    }

    public String getDescription() {
        return ensurePeriod( part.getDescription() );
    }

    public String getTitle() {
        return part.getTask();
    }

    public Model<String> getLink() {
         return new Model<String>( "#t_" + part.getId() );
     }

     public String getLabel() {
         return "Task " + getSeqString();
     }

     public String getSeqString() {
         return Integer.toString( segmentSeq ) + '.' + taskSeq;
     }

     public int getTaskSeq() {
         return taskSeq;
     }

     public void setTaskSeq( int taskSeq ) {
         this.taskSeq = taskSeq;
     }

     public String getTask() {
         return part.getTask();
     }

    public Part getPart() {
        return part;
    }

    public int getSegmentSeq() {
        return segmentSeq;
    }

    public Model<String> getAnchor() {
        return new Model<String>( "t_" + getPart().getId() );
    }

    public String getTaskSummary() {

        StringWriter w = new StringWriter();
        w.append( String.valueOf( getPart().getSegment().getPhaseEventTitle() ) );
        Place location = part.getLocation();
        if ( location != null )
            w.append( " in " ).append( String.valueOf( location ) );
        return ensurePeriod( w.toString() );
    }

    public String getRoleString( ) {
        StringBuilder sb = new StringBuilder();
        ResourceSpec spec = part.resourceSpec();
        if ( !spec.isAnyRole() && !spec.isAnyOrganization() ) {
            sb.append( spec.isAnyRole() ? "Member " : spec.getRole().getName() );
            if ( !spec.isAnyOrganization() ) {
                Organization org = spec.getOrganization();
                sb.append( " at " );
                sb.append( org.getName() );
            }
        }
        return sb.toString();
    }

    public String getLocationString() {

        return part.getLocation() == null ?
                "" :
                ensurePeriod( "This task is located in " + part.getLocation() );
    }

    public String getCategoryString() {
        Part.Category category = getPart().getCategory();
        return category == null ? "" : category.getLabel().toLowerCase();
    }

    public boolean isProhibited() {
        return getPart().isProhibited();
    }

    public Place getLocation() {
        return getPart().getLocation();
    }

    public String getTeamSpec() {
        return new ResourceSpec( getPart() ).getReportTitle();
    }

    public boolean isAsTeam() {
        return getPart().isAsTeam();
    }

    public String getRepetition() {
        return isRepeating() ? "This is repeated every " + getPart().getRepeatsEvery() + '.' : "";
    }

    public boolean isRepeating() {
        return getPart().isRepeating();
    }



    public List<Attachment> getAttachmentsFrom( AttachmentManager attachmentManager ) {
        return attachmentManager.getMediaReferences( getPart() );
    }

    @Override
    public int compareTo( ReportTask o ) {
        return taskSeq - o.getTaskSeq();
    }



}
