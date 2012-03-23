package com.mindalliance.channels.social.model;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Command;
import com.mindalliance.channels.core.command.ModelObjectRef;
import com.mindalliance.channels.core.command.commands.UpdateObject;
import com.mindalliance.channels.core.orm.model.AbstractPersistentPlanObject;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/2/12
 * Time: 2:20 PM
 */
@Entity
public class ExecutedCommand extends AbstractPersistentPlanObject {

    private String commandName;
    private String commandUndoes;
    private String changeProperty;
    @Column(columnDefinition="LONGTEXT")
    private String commandTargetDescription;
    private boolean update;
    private Long changeId;

    public enum Type {
        Done,
        Undone,
        Redone
    }

    private String execution;
    private String executedOn;
    private Type type;

    public ExecutedCommand() {}

    public ExecutedCommand( Type type, Command command, Change change, String planUri, int planVersion ) {
        super( planUri, planVersion, command.getUserName() );
        this.type = type;
        commandName = command.getName();
        if ( type == Type.Undone )  commandUndoes = command.getUndoes();
        changeProperty = change.getProperty();
        commandTargetDescription = command.getTargetDescription();
        update = command instanceof UpdateObject;
        changeId = change.getId();
        executedOn = makeExecutedOn( change );
        execution = makeExecution( command, change );
    }

    public boolean isDone() {
        return type == Type.Done;
    }

    public boolean isUndone() {
        return type == Type.Undone;
    }

    public boolean isRedone() {
        return type == Type.Redone;
    }

    public String getCommandName() {
        return commandName;
    }

    public String getCommandUndoes() {
        return commandUndoes;
    }

    public String getChangeProperty() {
        return changeProperty;
    }

    public String getCommandTargetDescription() {
        return commandTargetDescription;
    }

    public boolean isUpdate() {
        return update;
    }

    public Long getChangeId() {
        return changeId;
    }

    public void setCommandName( String commandName ) {
        this.commandName = commandName;
    }

    public void setCommandUndoes( String commandUndoes ) {
        this.commandUndoes = commandUndoes;
    }

    public void setChangeProperty( String changeProperty ) {
        this.changeProperty = changeProperty;
    }

    public void setCommandTargetDescription( String commandTargetDescription ) {
        this.commandTargetDescription = commandTargetDescription;
    }

    public void setUpdate( boolean update ) {
        this.update = update;
    }

    public void setChangeId( Long changeId ) {
        this.changeId = changeId;
    }

    public void setType( Type type ) {
        this.type = type;
    }

    private String makeExecutedOn( Change change ) {
        ModelObjectRef moref = new ModelObjectRef(  );
        moref.setClassName( change.getClassName() );
        moref.setId( change.getId() );
        return moref.asString();
    }

    private String makeExecution( Command command, Change change ) {
        StringBuilder sb = new StringBuilder();
        sb.append( "Command " );
        sb.append( command.getName() );
        if ( isDone() ) {
            sb.append( " executed on ");
            sb.append( change.getClassName() );
            sb.append( '[');
            sb.append( change.getId() );
            sb.append( ']' );
        } else {
            sb.append(type == Type.Undone ? " undone" : " done");
        }
        sb.append( " by ");
        sb.append( command.getUserName() );
        sb.append( " " );
        sb.append( super.toString() );
        return sb.toString();
    }
    
    public String toString() {
        return execution;
    }

    public ModelObjectRef getCommandTarget() {
        return ModelObjectRef.fromString( executedOn );
    }
}
