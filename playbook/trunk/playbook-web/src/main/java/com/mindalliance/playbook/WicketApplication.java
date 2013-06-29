/*
 * Copyright (c) 2011. Mind-Alliance Systems LLC.
 * All rights reserved.
 * CONFIDENTIAL
 */

package com.mindalliance.playbook;

import com.mindalliance.playbook.pages.AckPage;
import com.mindalliance.playbook.pages.ContactPic;
import com.mindalliance.playbook.pages.EditPlay;
import com.mindalliance.playbook.pages.EditStep;
import com.mindalliance.playbook.pages.MessagesPage;
import com.mindalliance.playbook.pages.PlaysPage;
import com.mindalliance.playbook.pages.Settings;
import com.mindalliance.playbook.pages.TodoPage;
import com.mindalliance.playbook.pages.login.Confirm;
import com.mindalliance.playbook.pages.login.JCaptchaImage;
import com.mindalliance.playbook.pages.login.Login;
import com.mindalliance.playbook.pages.login.Register;
import com.mindalliance.playbook.pages.login.Reset;
import com.mindalliance.playbook.pages.login.Thanks;
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.settings.IRequestCycleSettings.RenderStrategy;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Application object for your web application. If you want to run this application without deploying, run the Start
 * class.
 */
@Component
public class WicketApplication extends WebApplication implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    protected void init() {
        super.init();
        getMarkupSettings().setStripWicketTags( true );
        getComponentInstantiationListeners().add( new SpringComponentInjector( this, applicationContext ) );

        // Allow placing html templates under src/webapp, for easier fine-tuning
        getResourceSettings().addResourceFolder( "" );

        getRequestCycleSettings().setRenderStrategy( RenderStrategy.ONE_PASS_RENDER );

        mountPage( "login.html", Login.class );
        mountPage( "todo.html", TodoPage.class );
        mountPage( "plays.html", PlaysPage.class );
        mountPage( "plays/${id}", EditPlay.class );
        mountPage( "steps/${id}", EditStep.class );
        mountPage( "register.html", Register.class );
        mountPage( "confirm.html", Confirm.class );
        mountPage( "reset.html", Reset.class );
        mountPage( "thanks.html", Thanks.class );
        mountPage( "jcaptcha.jpg", JCaptchaImage.class );
        mountPage( "contacts/${id}", ContactPic.class );
        mountPage( "collaborate.html", MessagesPage.class );
        mountPage( "confirm/${id}", AckPage.class );

        mountPage( "settings.html", Settings.class );
    }

    @Override
    public Class<? extends Page> getHomePage() {
        return PlaysPage.class;
    }

    @Override
    public void setApplicationContext( ApplicationContext applicationContext ) {
        this.applicationContext = applicationContext;
    }
}
