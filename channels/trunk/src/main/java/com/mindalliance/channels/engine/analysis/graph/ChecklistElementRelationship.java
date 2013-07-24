package com.mindalliance.channels.engine.analysis.graph;

import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.checklist.Checklist;
import com.mindalliance.channels.core.model.checklist.ChecklistElement;
import org.apache.commons.lang.StringUtils;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/13/13
 * Time: 9:06 PM
 */
public class ChecklistElementRelationship implements Identifiable {

    private ChecklistElement fromElement;
    private ChecklistElement toElement;
    private Checklist checklist;

    public ChecklistElementRelationship( ChecklistElement fromElement,
                                         ChecklistElement toElement,
                                         Checklist checklist ) {
        this.fromElement = fromElement;
        this.toElement = toElement;
        this.checklist = checklist;
    }

    public ChecklistElementRelationship( Checklist checklist ) {
        this.checklist = checklist;
        fromElement = null;
        toElement = null;
    }

    @Override
    public String getClassLabel() {
        return getClass().getSimpleName();
    }

    @Override
    public long getId() {
        String toId = Long.toString( toElement.getId() );
        toId = StringUtils.leftPad( toId, 9, '0' );
        String fromId = Long.toString( fromElement.getId() );
        return Long.valueOf( fromId + toId );
    }

    public Checklist getChecklist() {
        return checklist;
    }

    public ChecklistElement getFromElement() {
        return fromElement;
    }

    public ChecklistElement getToElement() {
        return toElement;
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getTypeName() {
        return "checklist element relationship";
    }

    @Override
    public boolean isModifiableInProduction() {
        return false;
    }

    @Override
    public String getName() {
        return "From " + fromElement.getId() + " to " + toElement.getId();
    }

    @Override
    public String toString() {
        return fromElement + " --> " + toElement;
    }


    public boolean isBetweenConditions() {
        return fromElement.isCondition() && toElement.isCondition();
    }

    public boolean isWithOutcome() {
        return toElement.isOutcome();
    }
}
