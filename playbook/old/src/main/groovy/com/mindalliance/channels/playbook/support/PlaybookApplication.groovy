package com.mindalliance.channels.playbook.support

import com.mindalliance.channels.playbook.ifm.Channels
import com.mindalliance.channels.playbook.ifm.project.Project
import com.mindalliance.channels.playbook.ifm.taxonomy.Taxonomy
import com.mindalliance.channels.playbook.mem.ApplicationMemory
import com.mindalliance.channels.playbook.mem.NoSessionCategory
import com.mindalliance.channels.playbook.mem.Store
import com.mindalliance.channels.playbook.pages.LoginPage
import com.mindalliance.channels.playbook.pages.PlaybookPage
import com.mindalliance.channels.playbook.query.Query
import com.mindalliance.channels.playbook.query.QueryCache
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.ref.impl.RefImpl
import com.mindalliance.channels.playbook.support.drools.RuleBaseSession
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import org.apache.wicket.Application
import org.apache.wicket.Page
import javax.servlet.ServletContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.apache.wicket.authentication.AuthenticatedWebApplication
import org.apache.wicket.authentication.AuthenticatedWebSession
import org.apache.wicket.markup.html.WebPage
import org.apache.wicket.Session;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 21, 2008
 * Time: 11:01:36 AM
 */
class PlaybookApplication extends AuthenticatedWebApplication implements Serializable, PropertyChangeListener {

    private static final long serialVersionUID = -1L;
    static final String FORM_PACKAGE = 'com.mindalliance.channels.playbook.pages.forms'
    static final String FORM_SUFFIX = 'Form'

    ApplicationMemory appMemory
    String message
    boolean initial = true
    Channels cachedChannels

    PlaybookApplication() {
        super()
        appMemory = new ApplicationMemory(this)
    }

    void init() {
        super.init();
        logConfig();
    }

    static PlaybookApplication current() {
        return (PlaybookApplication) Application.get()
    }

    ApplicationMemory getMemory() {
        return appMemory
    }

    RuleBaseSession getRuleBaseSession() {
        return appMemory.ruleBaseSession
    }

    //----------------------
    @Override
    public Class<? extends Page> getHomePage() {
       return PlaybookPage.class
       // return FormTest.class
    }

    @Override
    protected Class<? extends AuthenticatedWebSession> getWebSessionClass() {
        return PlaybookSession.class
    }

    @Override
    protected Class<? extends WebPage> getSignInPageClass() {
        return LoginPage.class;
    }

    public QueryCache getQueryCache() {
        return appMemory.queryCache
    }

    // ------------- Initialization

    void initializeContents() {
        TestData.load()
        // TODO - implement minimal initialization for clean slate
    }



        /**
         * Log current configuration
         */
        void logConfig() {
            Logger log = LoggerFactory.getLogger( getClass() )

            if ( log.isInfoEnabled() ) {
                ServletContext ctx = PlaybookApplication.get().servletContext
                log.info( "**** Starting Channels" );
                log.info( "current dir = " +  java.lang.System.properties[ "user.dir" ] )
                log.info( "user dir = " +  java.lang.System.properties[ "user.home" ] )
                log.info( "persistence-dir = " + ctx.getInitParameter("persistence-dir"))
                log.info( "trained-data = " + ctx.getInitParameter("trained-data"))
                log.info( "wordnet-data = " + ctx.getInitParameter("wordnet-data"))
                log.info( "wicket configuration = " + ctx.getInitParameter("configuration"))
                log.info( "dot executable = " + ctx.getInitParameter("dot"))
            }
        }

    // ----------------------- Data access

    Ref getChannels() {  // Load memory from file if needed
        RefImpl channels = (RefImpl)getRoot()
        if (cachedChannels && !channels.isAttached()) {
            channels.attach(cachedChannels)
        }
        if (!channels.isAttached() && !isStored(channels)) {   // don't try loading Channels unless is it stored, else deadlock possible on second try
            use(NoSessionCategory) { // bypass session transactions
                if (!load(channels)) {  // if no export file, bootstrap memory and export
                    initializeContents()
                    channels.save()
                }
            }
            appMemory.fireAllRules()
            initial = false
        }
        else {
            Channels rootElement = (Channels)channels.deref()
            if (rootElement != cachedChannels) {
                cachedChannels = rootElement
                cachedChannels.addPropertyChangeListener(this)
            }
            if (initial) {
                appMemory.insertFact(channels)  // with references if not already done
                initial = false
            }
        }
        return channels
    }

    void propertyChange(PropertyChangeEvent evt) {
        Referenceable referenceable = (Referenceable)evt.source
        if (referenceable.getReference() == getRoot()) {
            cachedChannels = (Channels)referenceable   // cache the latest version of Channels
        }
    }

    boolean load(Ref ref) {
        int count = memory.importRef(ref.toString())
        return count > 0
    }

    Ref findUser(String id) {
        return this.channels.findUser(id)
    }

    List<Ref> findProjectsForUser(Ref user) {
        return (List<Ref>)Query.execute(Project.class, "findProjectsOfUser", user)
        // return this.channels.findProjectsForUser(user)
    }

    List<Ref> findTaxonomiesForUser(Ref user) {
        return (List<Ref>)Query.execute(Taxonomy.class,"findTaxonomiesOfUser", user)
        // return this.channels.findTaxonomiesForUser(user)
    }

    public Ref findParticipation(Ref project, Ref user) {
        return project.findParticipation(user)
    }

    void storeAll(Collection<Referenceable> referenceables) {
        appMemory.storeAll(referenceable)
    }

    Ref store(Referenceable referenceable) {
        return appMemory.store(referenceable)
    }

    Referenceable retrieve(Ref ref) {
        return appMemory.retrieve(ref)
    }

    boolean isStored(Ref ref) {
        return appMemory.isStored(ref)
    }

    void clearAll() {
        appMemory.clearAll()
    }

    Ref getRoot() {
        ApplicationMemory.getRoot()
    }

    static Store locateStore() {
        PlaybookSession session = (PlaybookSession) Session.get()
        return session.memory
    }

    void save() {
        this.channels.save()
    }

    void sessionTimedOut(PlaybookSession session) {
        appMemory.sessionTimedOut(session)
    }

    // Util

    Ref createNewElement(String type) {
        Class clazz = (Class) Eval.me("${type}.class")
        return (Ref) clazz.newInstance()
    }

    Class formClassFor(String type) {
       String className = "${FORM_PACKAGE}.${type}${FORM_SUFFIX}"
        try {
            return Class.forName(className)
        }
        catch (Exception e) {
            LoggerFactory.getLogger(this.class.name).warn("No form class $className", e)
            return null
        }
    }

}