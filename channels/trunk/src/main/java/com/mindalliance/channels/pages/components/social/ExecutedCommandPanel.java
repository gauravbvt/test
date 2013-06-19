package com.mindalliance.channels.pages.components.social;

import com.mindalliance.channels.core.command.ModelObjectRef;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.SegmentObject;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.db.data.activities.ExecutedCommand;
import com.mindalliance.channels.db.services.activities.ExecutedCommandService;
import com.mindalliance.channels.pages.ModelObjectLink;
import com.mindalliance.channels.pages.Updatable;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Date;

/**
 * Command event panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 7, 2010
 * Time: 10:36:10 AM
 */
public class ExecutedCommandPanel extends AbstractSocialEventPanel {

    @SpringBean
    ExecutedCommandService executedCommandService;

    @SpringBean
    QueryService queryService;

    private IModel<ExecutedCommand> executedCommandModel;

    public ExecutedCommandPanel(
            String id,
            IModel<ExecutedCommand> executedCommandModel,
            int index,
            boolean showProfile,
            Updatable updatable ) {
        super( id, index, executedCommandModel, showProfile, updatable );
        this.executedCommandModel = executedCommandModel;
        init();
    }

    @Override
    protected String getPersistentPlanObjectUsername() {
        return executedCommandModel.getObject().getUsername();
    }

    protected void moreInit( WebMarkupContainer socialItemContainer ) {
        addExecutedCommand( socialItemContainer );
        addCommandTarget( socialItemContainer );
        addTime( socialItemContainer );
    }

    private void addExecutedCommand( WebMarkupContainer socialItemContainer ) {
        Label eventLabel = new Label( "executedCommand", new Model<String>( getExecutedCommandString() ) );
        socialItemContainer.add( eventLabel );
    }

    private String getExecutedCommandString() {
        ExecutedCommand executedCommand = getExecutedCommand();
        String eventString = executedCommand.isDone()
                ? "Doing " + executedCommand.getCommandName()
                : executedCommand.isUndone()
                ? ( "Undoing " + executedCommand.getCommandUndoes() )
                : ( "Redoing " + executedCommand.getCommandName() );
        return StringUtils.capitalize( eventString );
    }

    private void addCommandTarget( WebMarkupContainer socialItemContainer ) {
        Identifiable identifiable;
        ExecutedCommand executedCommand = getExecutedCommand();
        String subject = getTargetDescription( executedCommand );
        ModelObjectRef modelObjectRef = executedCommand.getCommandTarget();
        Component modelObjectComponent = null;
        if ( modelObjectRef != null ) {
            identifiable = modelObjectRef.resolve( getCommunityService() );
            if ( identifiable != null && identifiable instanceof ModelObject ) {
                ModelObject mo = (ModelObject) identifiable;
                modelObjectComponent = new ModelObjectLink(
                        "modelObject",
                        new Model<ModelObject>( mo ),
                        new Model<String>( subject ) );
            }
            String property = executedCommand.getChangeProperty();
            if ( property != null ) {
                subject += " (" + property + ")";
            }
        }
        if ( modelObjectComponent == null ) {
            modelObjectComponent = new Label( "modelObject", subject );
        }
        socialItemContainer.add( modelObjectComponent );
        socialItemContainer.setVisible( !subject.isEmpty() );
    }

    private String getTargetDescription( ExecutedCommand executedCommand ) {
        String description = executedCommand.getCommandTargetDescription();
        if ( description.isEmpty() ) {
            // Try to compute it
            ModelObjectRef modelObjectRef = executedCommand.getCommandTarget();
            Identifiable subject = null;
            if ( modelObjectRef != null ) subject = modelObjectRef.resolve( getCommunityService() );
            if ( subject != null && subject instanceof ModelObject ) {
                ModelObject mo = (ModelObject) subject;
                description = "\"" + mo.getLabel() + "\"";
                if ( mo instanceof SegmentObject ) {
                    description += " in segment \"" + ( (SegmentObject) mo ).getSegment().getLabel() + "\"";
                }
            }
        }
        return description;
    }

    private void addTime( WebMarkupContainer socialItemContainer ) {
        String time = getTime();
        String timeLabelString = "";
        if ( !time.isEmpty() ) {
            timeLabelString = "(" + time + ")";
        }
        Label timeLabel = new Label( "time", new Model<String>( timeLabelString ) );
        if ( !timeLabelString.isEmpty() ) {
            addTipTitle( timeLabel, new PropertyModel<String>( this, "longTime" ) );
        }
        socialItemContainer.add( timeLabel );
    }


    protected String getCssClasses() {
        return getCommandEventType() + super.getCssClasses();
    }

    private String getCommandEventType() {
        ExecutedCommand commandEvent = getExecutedCommand();
        return commandEvent.isDone()
                ? "doing"
                : commandEvent.isUndone()
                ? "undoing"
                : "redoing";
    }

    @Override
    public Date getDate() {
        return getExecutedCommand().getCreated();
    }

    private ExecutedCommand getExecutedCommand() {
        return executedCommandModel.getObject();
    }
}
