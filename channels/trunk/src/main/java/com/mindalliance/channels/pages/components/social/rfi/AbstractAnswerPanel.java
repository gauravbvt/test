package com.mindalliance.channels.pages.components.social.rfi;

import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.social.model.rfi.Answer;
import com.mindalliance.channels.social.model.rfi.AnswerSet;
import com.mindalliance.channels.social.model.rfi.Question;
import com.mindalliance.channels.social.model.rfi.RFI;
import com.mindalliance.channels.social.services.AnswerService;
import com.mindalliance.channels.social.services.AnswerSetService;
import com.mindalliance.channels.social.services.QuestionService;
import com.mindalliance.channels.social.services.RFIService;
import com.mindalliance.channels.social.services.SurveysDAO;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * An abstract RFI answer panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/30/12
 * Time: 1:28 PM
 */
abstract public class AbstractAnswerPanel extends AbstractUpdatablePanel {

    private static final boolean SHARED_ONLY = true;

    @SpringBean
    private RFIService rfiService;

    @SpringBean
    private QuestionService questionService;

    @SpringBean
    private AnswerSetService answerSetService;

    @SpringBean
    private AnswerService answerService;

    @SpringBean( name="surveysDao" )
    private SurveysDAO surveysDAO;

    private final IModel<Question> questionModel;
    private final IModel<RFI> rfiModel;
    private AnswerSet answerSet;
    private boolean changed = false;
    private WebMarkupContainer questionAndAnswerContainer;
    private WebMarkupContainer anonymousContainer;
    private Map<String, Set<String>> results;
    private WebMarkupContainer questionContainer;


    public AbstractAnswerPanel( String id, IModel<Question> questionModel, IModel<RFI> rfiModel ) {
        super( id );
        this.questionModel = questionModel;
        this.rfiModel = rfiModel;
        init();
    }

    private void init() {
        processOtherResults();
        addQuestion();
        addComment();
        addPrivacy();
        moreInit();
    }

    private void processOtherResults() {
        results = surveysDAO.processAnswers(
                getCommunityService(),
                getRFI().getRfiSurvey(),
                getQuestion(),
                SHARED_ONLY,
                getUsername() );
    }

    abstract protected void moreInit();

    private void addQuestion() {
        questionAndAnswerContainer = new WebMarkupContainer( "questionAndAnswer" );
        if ( getQuestion().isAnswerRequired() ) {
            questionAndAnswerContainer.add( new AttributeModifier( "class", "required" ) );
        }
        add( questionAndAnswerContainer );
        questionContainer = new WebMarkupContainer( "question" );
        questionContainer.add( new Label( "index", Integer.toString( getQuestionIndex() + 1 ) ) );
        questionContainer.add( new Label( "text", getQuestionText() ) );
        questionAndAnswerContainer.add( questionContainer );
        addOtherAnswersLink();

    }

    private void addComment() {
        WebMarkupContainer moreContainer = new WebMarkupContainer( "more" );
        moreContainer.setVisible( getQuestion().isOpenEnded() );
        questionAndAnswerContainer.add( moreContainer );
        TextArea<String> commentText = new TextArea<String>(
                "comment",
                new PropertyModel<String>( this, "comment" ) );
        commentText.add( new AjaxFormComponentUpdatingBehavior( "onkeyup" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                setChanged( true );
            }
        } );
        moreContainer.add( commentText );
    }

    public void setComment( String val ) {
        getAnswerSet().setComment( val );
    }

    public String getComment() {
        String comment = getAnswerSet().getComment();
        return comment == null ? "" : comment;
    }

    private void addOtherAnswersLink( ) {
        final String answerersLabel = getOtherAnswersLabel();
        AjaxLink<String> otherAnswersLink = new AjaxLink<String>(
                "others",
                new Model<String>( answerersLabel )
        ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                if ( !results.isEmpty() ) {
                    QuestionResultsPanel questionResultsPanel = new QuestionResultsPanel(
                            getModalableParent().getModalContentId(),
                            results );
                    getModalableParent().showDialog(
                            "What others answered",
                            300,
                            500,
                            questionResultsPanel,
                            AbstractAnswerPanel.this,
                            target );
                }
            }
        };
        otherAnswersLink.setOutputMarkupId( true );
        makeVisible( otherAnswersLink, !answerersLabel.isEmpty() );
        questionContainer.addOrReplace( otherAnswersLink );
    }

    private String getOtherAnswersLabel() {
        if ( !getRFI().isPersisted() || results.isEmpty() )
            return "";
        else {
            Set<String> answerers = new HashSet<String>();
            for ( String text : results.keySet() ) {
                answerers.addAll( results.get( text ) );
            }
            int n = answerers.size();
            return "What "
                    + n
                    + ( n > 1 ? " others " : " other " )
                    + "answered";
        }
    }

    private void addPrivacy() {
        WebMarkupContainer privacyContainer = new WebMarkupContainer( "privacyContainer" );
        privacyContainer.setVisible( getRFI().isPersisted() && getQuestion().isAnswerable() );
        questionAndAnswerContainer.add( privacyContainer );
        privacyContainer.add( new AjaxCheckBox(
                "shared",
                new PropertyModel<Boolean>( this, "shared" )

        ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                changed = true;
                makeVisible( anonymousContainer, isShared() );
                target.add( anonymousContainer );
            }
        } );
        anonymousContainer = new WebMarkupContainer( "anonymousContainer" );
        anonymousContainer.setOutputMarkupId( true );
        makeVisible( anonymousContainer, isShared() );
        privacyContainer.add( anonymousContainer );
        AjaxCheckBox anonymousCheckBox = new AjaxCheckBox(
                "anonymous",
                new PropertyModel<Boolean>( this, "anonymous" )
        ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                changed = true;
            }
        };
        anonymousContainer.add( anonymousCheckBox );
    }

    public boolean isShared() {
        return getRFI().isPersisted() && getQuestion().isAnswerable() && getAnswerSet().isShared();
    }

    public void setShared( boolean val ) {
        if ( getQuestion().isAnswerable() ) getAnswerSet().setShared( val );
    }

    public boolean isAnonymous() {
        return getRFI().isPersisted() && getQuestion().isAnswerable() && getAnswerSet().isAnonymous();
    }

    public void setAnonymous( boolean val ) {
        if ( getQuestion().isAnswerable() ) getAnswerSet().setAnonymous( val );
    }

    protected Question getQuestion() {
        return questionModel.getObject();
    }

    protected RFI getRFI() {
        return rfiModel.getObject();
    }

    protected int getQuestionIndex() {
        return getQuestion().getIndex();
    }

    protected String getQuestionText() {
        Map<String, Object> extraContext = new HashMap<String, Object>();
        extraContext.put( "user", getUserFullName( getUsername() ) );
        extraContext.put( "planner", getUserFullName( getRFI().getRfiSurvey().getUsername() ) );
        return ChannelsUtils.convertTemplate(
                getQuestion().getText(),
                getRFI().getRfiSurvey().getAbout( getCommunityService() ),
                extraContext );
    }

    protected AnswerSet getAnswerSet() {
        RFI rfi = getRFI();
        if ( getQuestion().isAnswerable() && answerSet == null && rfi.isPersisted() ) {
            answerSet = answerSetService.findAnswerSet( rfi, getQuestion() );
        }
        if ( answerSet == null ) {
            answerSet = new AnswerSet( getPlanCommunity(), getUser(), getRFI(), getQuestion() );
        }
        return answerSet;
    }

    protected Answer getAnswer() {
        return getAnswerSet().getOrCreateAnswer();
    }

    protected boolean isChanged() {
        return getQuestion().isAnswerable() && getAnswerSet() != null && changed;
    }

    protected void setChanged( boolean changed ) {
        this.changed = changed;
    }

    protected WebMarkupContainer getContainer() {
        return questionAndAnswerContainer;
    }

    public void saveChanges() {
        if ( isChanged() ) {
            getSurveysDAO().saveAnswerSet( getAnswerSet() );
        }
    }

    protected SurveysDAO getSurveysDAO() {
        return surveysDAO;
    }
}
