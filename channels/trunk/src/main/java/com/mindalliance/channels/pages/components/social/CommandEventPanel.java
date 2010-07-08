package com.mindalliance.channels.pages.components.social;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.SegmentObject;
import com.mindalliance.channels.pages.ModelObjectLink;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.query.QueryService;
import com.mindalliance.channels.social.CommandEvent;
import com.mindalliance.channels.social.PlanningEventService;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Command event panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 7, 2010
 * Time: 10:36:10 AM
 */
public class CommandEventPanel extends AbstractSocialEventPanel {

    @SpringBean
    PlanningEventService planningEventService;

    @SpringBean
    QueryService queryService;

    private IModel<CommandEvent> commandEventModel;

    public CommandEventPanel( String id, IModel<CommandEvent> commandEventModel, Updatable updatable ) {
        super( id, commandEventModel.getObject().getUsername(), updatable );
        this.commandEventModel = commandEventModel;
        init();
    }

    protected void moreInit( WebMarkupContainer socialItemContainer ) {
        addCommandEvent( socialItemContainer );
        addSubject( socialItemContainer );
    }

    private void addCommandEvent( WebMarkupContainer socialItemContainer ) {
        Label eventLabel = new Label( "commandEvent", new Model<String>( getcommandEventString() ) );
        socialItemContainer.add( eventLabel );
    }

    private String getcommandEventString() {
        CommandEvent commandEvent = getCommandEvent();
        Command command = commandEvent.getCommand();
        String eventString = commandEvent.isDone()
                ? command.getName()
                : commandEvent.isUndone()
                ? ( "Undoing " + command.getUndoes() )
                : ( "Redoing " + command.getName() );
        return StringUtils.capitalize( eventString );
    }

    private void addSubject( WebMarkupContainer socialItemContainer ) {
        WebMarkupContainer subjectContainer = new WebMarkupContainer( "subject" );
        Identifiable identifiable;
        CommandEvent commandEvent = getCommandEvent();
        boolean linked = false;
        String subject = getTargetDescription( commandEvent );
        Change change = commandEvent.getChange();
        if ( change != null ) {
            identifiable = change.getSubject( getQueryService() );
            if ( identifiable != null && identifiable instanceof ModelObject ) {
                ModelObject mo = (ModelObject) identifiable;
                ModelObjectLink moLink = new ModelObjectLink(
                        "modelObject",
                        new Model<ModelObject>( mo ),
                        new Model<String>( subject ) );
                subjectContainer.add( moLink );
                linked = true;
            }
        }
        if ( !linked ) {
            subjectContainer.add( new Label( "modelObject", subject ) );
        }
        subjectContainer.setVisible( !subject.isEmpty() );
        socialItemContainer.add( subjectContainer );
    }

    private String getTargetDescription( CommandEvent commandEvent ) {
        String description = commandEvent.getCommand().getTargetDescription();
        if ( description.isEmpty() ) {
            // Try to compute it
            Identifiable subject = commandEvent.getChange().getSubject( queryService );
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


    protected String getCssClass() {
        return getCommandEventType();
    }

    private String getCommandEventType() {
        CommandEvent commandEvent = getCommandEvent();
        return commandEvent.isDone()
                ? "doing"
                : commandEvent.isUndone()
                ? "undoing"
                : "redoing";
    }

    public String getTime() {
        return getCommandEvent().getShortTimeElapsedString();
    }

    public String getLongTime() {
        return getCommandEvent().getLongTimeElapsedString();
    }

    private CommandEvent getCommandEvent() {
        return commandEventModel.getObject();
    }
}
