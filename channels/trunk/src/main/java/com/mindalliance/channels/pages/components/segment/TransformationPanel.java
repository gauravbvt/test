package com.mindalliance.channels.pages.components.segment;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.commands.UpdateObject;
import com.mindalliance.channels.model.ElementOfInformation;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Subject;
import com.mindalliance.channels.model.Transformation;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * EOI transformation panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Oct 10, 2010
 * Time: 9:27:54 AM
 */
public class TransformationPanel extends AbstractCommandablePanel {

    private WebMarkupContainer subjectsContainer;
    private DropDownChoice<String> typeChoice;
    private IModel<Flow> flowModel;
    private int eoiIndex;
    private static final int MAX_INFO_LENGTH = 20;

    public TransformationPanel( String id,
                                IModel<Flow> flowModel,
                                int eoiIndex ) {
        super( id );
        this.flowModel = flowModel;
        this.eoiIndex = eoiIndex;
        init();
    }

    private void init() {
        subjectsContainer = new WebMarkupContainer( "subjectsContainer" );
        subjectsContainer.setOutputMarkupId( true );
        add( subjectsContainer );
        addTypeChoice();
        addSubjectsList();
        adjustFields();
    }

    private void addTypeChoice() {
        typeChoice = new DropDownChoice<String>(
                "type",
                new PropertyModel<String>( this, "typeLabel" ),
                Transformation.Type.getAllLabels() );
        typeChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                addSubjectsList();
                adjustFields();
                target.addComponent( subjectsContainer );
                update( target, new Change( Change.Type.Updated, getFlow(), "eois" ) );
            }
        } );
        add( typeChoice );
    }

    private void addSubjectsList() {
        final List<SubjectWrapper> wrappers = getSubjectWrappers();
        ListView<SubjectWrapper> subjectsListView = new ListView<SubjectWrapper>(
                "subjects",
                wrappers ) {
            protected void populateItem( ListItem<SubjectWrapper> item ) {
                item.setOutputMarkupId( true );
                addSubject( item );
                addRemove( item );
                item.add( new AttributeModifier(
                        "class",
                        true,
                        new Model<String>( cssClasses( item, wrappers.size() ) ) ) );
            }
        };
        subjectsContainer.addOrReplace( subjectsListView );
    }

    private void addSubject( ListItem<SubjectWrapper> item ) {
        final SubjectWrapper wrapper = item.getModelObject();
        Part source =  (Part)getFlow().getSource();
        final List<Subject> inputSubjects = source.getAllSubjects( false );
        List<Subject> subjectChoices = new ArrayList<Subject>( inputSubjects );
        if ( getTransformation().getType() == Transformation.Type.Renaming ) {
            subjectChoices.remove( new Subject(
                    getFlow().getName(),
                    getElementOfInformation().getContent() ) );
        } else if ( getTransformation().getType() == Transformation.Type.Aggregation ) {
            subjectChoices.removeAll( getTransformation().getSubjects() );
        }
        DropDownChoice<Subject> subjectText = new DropDownChoice<Subject>(
                "newSubject",
                new PropertyModel<Subject>( wrapper, "subject" ),
                subjectChoices,
                new IChoiceRenderer<Subject>() {
                    public Object getDisplayValue( Subject subject ) {
                        return subject.getLabel( MAX_INFO_LENGTH );
                    }

                    public String getIdValue( Subject object, int index ) {
                        return "" + index;
                    }
                }
        );
        subjectText.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                if ( wrapper.isMarkedForCreation() ) {
                    addSubjectsList();
                    target.addComponent( subjectsContainer );
                }
                update( target, new Change( Change.Type.Updated, getFlow(), "eois" ) );
            }
        } );
        subjectText.setVisible( !isReadOnly() && wrapper.isMarkedForCreation() );
        item.addOrReplace( subjectText );
        Label subjectLabel = new Label(
                "subject",
                new PropertyModel<String>( wrapper, "subjectLabel" ) );
        subjectLabel.setVisible( !wrapper.isMarkedForCreation() );
        Subject subject = wrapper.getSubject();
        if ( !inputSubjects.contains( subject ) ) {
            subjectLabel.add( new AttributeModifier(
                    "class",
                    true,
                    new Model<String>( "to-be-deleted" ) ) );
        }
        if ( subject.getLabel( MAX_INFO_LENGTH ).length() < subject.toString().length() ) {
            subjectLabel.add( new AttributeModifier(
                    "title",
                    true,
                    new Model<String>( subject.toString() ) ) );
        }
        item.addOrReplace( subjectLabel );
    }


    private boolean isReadOnly() {
        return !isLockedByUser( getFlow() ) || !getFlow().canSetNameAndElements();
    }

    private void addRemove( ListItem<SubjectWrapper> item ) {
        final SubjectWrapper wrapper = item.getModelObject();
        AjaxFallbackLink deleteLink = new AjaxFallbackLink(
                "remove" ) {
            public void onClick( AjaxRequestTarget target ) {
                wrapper.removeSubject();
                addSubjectsList();
                target.addComponent( subjectsContainer );
                update( target,
                        new Change( Change.Type.Updated, getFlow(), "eois" ) );
            }
        };
        makeVisible( deleteLink, !isReadOnly() && !wrapper.isMarkedForCreation() );
        item.addOrReplace( deleteLink );

    }

    private String cssClasses( ListItem<SubjectWrapper> item, int count ) {
        int index = item.getIndex();
        String cssClasses = index % 2 == 0 ? "even" : "odd";
        if ( index == count - 1 ) cssClasses += " last";
        return cssClasses;
    }

    private List<SubjectWrapper> getSubjectWrappers() {
        List<SubjectWrapper> wrappers = new ArrayList<SubjectWrapper>();
        List<Subject> subjects = getTransformation().getSubjects();
        for ( int i = 0; i < subjects.size(); i++ ) {
            SubjectWrapper wrapper = new SubjectWrapper( i );
            wrappers.add( wrapper );
        }
        if ( !isReadOnly() ) wrappers.add( new SubjectWrapper() );
        return wrappers;
    }

    private void adjustFields() {
        makeVisible( subjectsContainer, !getTransformation().isNone() );
        typeChoice.setEnabled( !isReadOnly() );
    }

    public String getTypeLabel() {
        return getTransformation().getType().getLabel();
    }

    public void setTypeLabel( String val ) {
        Transformation.Type type = Transformation.Type.valueOfLabel( val );
        doCommand(
                UpdateObject.makeCommand(
                        getFlow(),
                        "eois[" + getEoiIndex() + "].transformation.type",
                        type,
                        UpdateObject.Action.Set
                )
        );
    }

    private Transformation getTransformation() {
        return getElementOfInformation().getTransformation();

    }

    private ElementOfInformation getElementOfInformation() {
        return getFlow().getEois().get( getEoiIndex() );
    }

    private Flow getFlow() {
        return flowModel.getObject();
    }

    private int getEoiIndex() {
        return eoiIndex;
    }

    public class SubjectWrapper implements Serializable {

        private int subjectIndex;
        private boolean markedForCreation;

        public SubjectWrapper( int subjectIndex ) {
            this.subjectIndex = subjectIndex;
            markedForCreation = false;
        }

        public SubjectWrapper() {
            subjectIndex = -1;
            markedForCreation = true;
        }

        public boolean isMarkedForCreation() {
            return markedForCreation;
        }

        public void removeSubject() {
            assert !isMarkedForCreation();
            doCommand(
                    UpdateObject.makeCommand(
                            getFlow(),
                            "eois[" + getEoiIndex() + "].transformation.subjects",
                            getSubject(),
                            UpdateObject.Action.Remove
                    )
            );
        }

        public Subject getSubject() {
            return subjectIndex >= 0
                    ? getTransformation().getSubjects().get( subjectIndex )
                    : getTransformation().newSubject();
        }

        public void setSubject( Subject subject ) {
            assert isMarkedForCreation();
            if ( subject != null )
                doCommand(
                        UpdateObject.makeCommand(
                                getFlow(),
                                "eois[" + getEoiIndex() + "].transformation.subjects",
                                subject,
                                UpdateObject.Action.Add
                        )
                );
        }

        public String getSubjectLabel() {
            return getSubject().getLabel( MAX_INFO_LENGTH );
        }
    }

}
