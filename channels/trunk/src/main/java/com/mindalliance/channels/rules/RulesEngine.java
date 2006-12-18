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

import com.mindalliance.channels.Model;

/**
 * Encapsulation of the DROOLS engine.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public class RulesEngine implements Lifecycle {

    private static Log logger = LogFactory.getLog( RulesEngine.class );

    private List<InputStream> drlPackages;
    private Model model;

    private WorkingMemory workingMemory;

    /**
     * Default constructor.
     */
    public RulesEngine() {
    }

    public List<InputStream> getDrlPackages() {
        return this.drlPackages;
    }

    public void setDrlPackages(
            List<InputStream> drlPackages ) {
        this.drlPackages = drlPackages;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.context.Lifecycle#isRunning()
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

    /*
     * (non-Javadoc)
     * @see org.springframework.context.Lifecycle#start()
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

            // TODO assert model
            // workingMemory.assertObject( ..., true );

            this.workingMemory.fireAllRules();

            logger.info( "Rules engine started" );
        } catch ( Exception e ) {
            logger.error( "Unable to add compiled package", e );
        }
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.context.Lifecycle#stop()
     */
    public synchronized void stop() {
        if ( isRunning() ) {
            getWorkingMemory().dispose();
            this.workingMemory = null ;
            logger.info( "Rules engine stopped" );
        }
    }

    /**
     * Return the model fueling the rules engine.
     */
    public Model getModel() {
        return this.model;
    }

    /**
     * Set the model used by this rules engine.
     * @param model the model
     */
    public final void setModel( Model model ) {
        this.model = model;
    }

    public final WorkingMemory getWorkingMemory() {
        return workingMemory;
    }
}
