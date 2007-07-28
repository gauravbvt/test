// Copyright (C) 2006 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.rules;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.event.AfterActivationFiredEvent;
import org.drools.event.DefaultAgendaEventListener;
import org.drools.rule.Package;
import org.springframework.context.Lifecycle;

import com.mindalliance.channels.data.models.Scenario;

/**
 * Encapsulation of the DROOLS engine.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public class RulesEngine implements Lifecycle {

    private static Log logger = LogFactory.getLog( RulesEngine.class );

    private List<InputStream> drlPackages;
    private Scenario scenario;

    private WorkingMemory workingMemory;

    /**
     * Default constructor.
     */
    public RulesEngine() {
    }

    /**
     * Return the list of DRL rules to be processed by this engine.
     */
    public List<InputStream> getDrlPackages() {
        return this.drlPackages;
    }

    /**
     * Set the DRL rules for this engine.
     * @param drlPackages a list of input streams.
     */
    public void setDrlPackages( List<InputStream> drlPackages ) {
        this.drlPackages = drlPackages;
    }

    /**
     * Test if rules engine is running.
     */
    public synchronized boolean isRunning() {
        return this.workingMemory != null ;
    }

    /**
     * Return the compiled package.
     */
    private Package getPackage() {

        final PackageBuilder builder = new PackageBuilder();
        for ( InputStream i : getDrlPackages() )
            try {
                builder.addPackageFromDrl( new InputStreamReader( i ) );
            } catch ( IOException e ) {
                logger.error( "Unable to add DRL package", e );
            } catch ( DroolsParserException e ) {
                logger.error( "Unable to add DRL package", e );
            }

        return builder.getPackage();
    }

    /**
     * Start the rules engine.
     */
    public synchronized void start() {

        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        try {
            ruleBase.addPackage( getPackage() );

            this.workingMemory = ruleBase.newWorkingMemory();
            this.workingMemory.addEventListener(
                    new DefaultAgendaEventListener() {
                        public void afterActivationFired(
                                final AfterActivationFiredEvent arg0 ) {
                            super.afterActivationFired( arg0 );
                        }
                    } );

            assertModel();

            this.workingMemory.fireAllRules();
            logger.info( "Rules engine started" );

        } catch ( Exception e ) {
            logger.error( "Unable to start rules engine", e );
        }
    }

    /**
     * Stop the rules engine.
     */
    public synchronized void stop() {
        if ( isRunning() ) {
            getWorkingMemory().dispose();
            this.workingMemory = null ;
            logger.info( "Rules engine stopped" );
        }
    }

    private void assertModel() {
        if ( getModel() == null )
            throw new IllegalStateException( "A scenario is required" );

        // TODO
//        for ( JavaBean b : getModel().getAssertions() )
//            this.workingMemory.assertObject( b, true );
    }

    /**
     * Return the scenario fueling the rules engine.
     */
    public Scenario getModel() {
        return this.scenario;
    }

    /**
     * Set the scenario used by this rules engine.
     * @param scenario the scenario
     */
    public final void setModel( Scenario scenario ) {
        this.scenario = scenario;
    }

    /**
     * Return the working memory behind this rules engine.
     */
    public final WorkingMemory getWorkingMemory() {
        return this.workingMemory;
    }
}
