package com.mindalliance.channels.pages.components.social.rfi;

import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.social.services.SurveysDAO;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Question results panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/3/12
 * Time: 3:43 PM
 */
public class QuestionResultsPanel extends AbstractUpdatablePanel {


    @SpringBean( name="surveysDao" )
    private SurveysDAO surveysDAO;

    private boolean excludedSelf;
    private boolean sharedOnly;
    private List<SurveyAnswer> results;
    private Map<String, Set<String>> answers;

    public QuestionResultsPanel(
            String id,
            Map<String, Set<String>> answers) {
        super( id );
        this.answers = answers;
        init();
    }

    private void init() {
        processResults();
        addResultList();
    }

    private void addResultList() {
        WebMarkupContainer resultsContainer = new WebMarkupContainer( "resultsContainer" );
        add( resultsContainer );
        ListView<SurveyAnswer> answerList = new ListView<SurveyAnswer>(
                "results",
                results
        ) {
            @Override
            protected void populateItem( ListItem<SurveyAnswer> item ) {
                SurveyAnswer answer = item.getModelObject();
                item.add( new Label( "count", answer.getFullNames().size() + "" ) );
                item.add( new Label( "text", answer.getText() ) );
                WebMarkupContainer participantsContainer = new WebMarkupContainer( "participantsContainer" );
                item.add( participantsContainer );
                ListView<String> fullNamesList = new ListView<String>(
                        "participants",
                        answer.getFullNames()
                ) {
                    @Override
                    protected void populateItem( ListItem<String> subItem ) {
                        subItem.add( new Label( "fullName", subItem.getModelObject() ) );
                    }
                };
                participantsContainer.add( fullNamesList );
            }
        };
        resultsContainer.add( answerList );
    }

    private void processResults() {
        results = new ArrayList<SurveyAnswer>();
        List<String> sortedAnswers = new ArrayList<String>( answers.keySet() );
        Collections.sort(
                sortedAnswers,
                new Comparator<String>() {
                    @Override
                    public int compare( String answer1, String answer2 ) {
                        int s1 = answers.get( answer1 ).size();
                        int s2 = answers.get( answer2 ).size();
                        return s1 > s2 ? -1 : s1 < s2 ? 1 : 0;
                    }
                }
        );
        for ( String text : sortedAnswers ) {
            List<String> fullNames = new ArrayList<String>();
            for ( String username : answers.get( text ) ) {
                fullNames.add( getUserFullName( username ) );
            }
            Collections.sort( fullNames );
            results.add( new SurveyAnswer( text, fullNames ) );
        }
    }

    private class SurveyAnswer implements Serializable {

        private String text;
        private List<String> fullNames;

        private SurveyAnswer( String text, List<String> fullNames ) {
            this.text = text;
            this.fullNames = fullNames;
        }

        public String getText() {
            return text;
        }

        public void setText( String text ) {
            this.text = text;
        }

        public List<String> getFullNames() {
            return fullNames;
        }

        public void setFullNames( List<String> fullNames ) {
            this.fullNames = fullNames;
        }
    }
}
