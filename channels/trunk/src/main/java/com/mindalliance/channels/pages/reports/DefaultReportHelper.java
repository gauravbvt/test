package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.attachments.AttachmentManager;
import com.mindalliance.channels.imaging.ImagingService;
import com.mindalliance.channels.model.Assignment;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.Specable;
import com.mindalliance.channels.query.PlanService;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.CompoundPropertyModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/15/11
 * Time: 10:47 PM
 */
public class DefaultReportHelper implements ReportHelper, Serializable {

    private final AssignmentsSelector selector;

    public DefaultReportHelper( AssignmentsSelector selector ) {
        this.selector = selector;
    }

    @Override
   public void setAsDefaultModel( Component component ) {
        component.setDefaultModel( new CompoundPropertyModel<Object>( this ) {
            @Override
            public void detach() {
                super.detach();
             //   service = null;
             //   assignment = null;
            }
        } );
    }
    @Override
    public Part getPart() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public AttributeModifier newCssClass( String css ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public AttachmentManager getAttachmentManager() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public PlanService getService() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Assignment getAssignment() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Component newFlowLink( Flow flow ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Specable getActor() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    // todo - replace by Ajax link
    public Component newFlowLink( Part part, Specable actor ) {
          Plan plan = selector.getPlan();

          PageParameters parms = new PageParameters();
          parms.put( SelectorPanel.ACTOR_PARM, Long.toString( ( (Identifiable) actor ).getId() ) );
          parms.put( SelectorPanel.PLAN_PARM, plan.getUri() );
          parms.put( SelectorPanel.VERSION_PARM, Long.toString( plan.getVersion() ) );
          parms.put( "task", Long.toString( part.getId() ) );

          return new BookmarkablePageLink<AssignmentReportPage>(
                  "task", AssignmentReportPage.class, parms )
                  .add( new Label( "name", getFlowString( part ) ) );
      }

    @Override
    public String getFlowString( Part part ) {
        StringBuilder result = new StringBuilder();
        Set<String> flowNames = new HashSet<String>();

        Iterator<Flow> iterator = part.flows();
        while ( iterator.hasNext() ) {
            Flow flow = iterator.next();
            if ( part.equals( flow.getSource() ) && flow.isTriggeringToSource()
                    || part.equals( flow.getTarget() ) && flow.isTriggeringToTarget() )
                flowNames.add( flow.getName() );
        }

        List<String> sortedNames = new ArrayList<String>( flowNames );
        if ( sortedNames.size() > 1 )
            Collections.sort( sortedNames );
        for ( int i = 0; i < sortedNames.size(); i++ ) {
            if ( i != 0 )
                result.append( i == sortedNames.size() - 1 ? " or " : ", " );

            result.append( sortedNames.get( i ) );
        }

        return result.toString();
    }

    @Override
    // todo - replace with AjaxLink
    public MarkupContainer newTaskLink( Part part, Specable actor ) {
        Plan plan = selector.getPlan();

        PageParameters parms = new PageParameters();
        parms.put( SelectorPanel.ACTOR_PARM, Long.toString( ( (Identifiable) actor ).getId() ) );
        parms.put( SelectorPanel.PLAN_PARM, plan.getUri() );
        parms.put( SelectorPanel.VERSION_PARM, Long.toString( plan.getVersion() ) );
        parms.put( AbstractReportPage.TASK_PARM, Long.toString( part.getId() ) );

        return new BookmarkablePageLink<AssignmentReportPage>(
                "task", AssignmentReportPage.class, parms )
                .add( new Label( "name", part.getTask() ) );
    }

    @Override
    public Flow getFlow() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ImagingService getImagingService() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
