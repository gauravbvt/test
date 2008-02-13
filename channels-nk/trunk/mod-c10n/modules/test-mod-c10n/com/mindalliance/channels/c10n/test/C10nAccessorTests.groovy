package com.mindalliance.channels.c10n.test

import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper as Context
import com.mindalliance.channels.nk.SessionHelper
import com.mindalliance.channels.c10n.aspects.IAspectContinuation
import com.mindalliance.channels.c10n.util.IContinuation
import com.mindalliance.channels.nk.NetKernelCategory
import org.ten60.netkernel.layer1.representation.NVPAspect
import org.ten60.netkernel.layer1.representation.NVPImpl
import com.mindalliance.channels.c10n.aspects.ContinuationAspect

/**
 * Created by IntelliJ IDEA.
 * User: jf
 * Date: Feb 12, 2008
 * Time: 1:01:59 PM
 * To change this template use File | Settings | File Templates.
 */
class C10nAccessorTests {

    Context context

    C10nAccessorTests(Context context) {
        this.context = context
    }

    void continuationCreateExistsDelete() {
        use(NetKernelCategory) {
            String sessionURI = context.requestNew("session:null", null); // create session
            SessionHelper session = new SessionHelper(sessionURI, context)
            IAspectContinuation iac = (IAspectContinuation) context.sourceAspect("active:c10n", [type: 'new', session: sessionURI], IAspectContinuation.class)
            IContinuation c10n = iac.getContinuation()
            assert c10n.id
            assert c10n.date
            assert c10n.state.size() == 0
            assert !c10n.aborted
            assert !c10n.committed
            boolean exists = context.isTrue("active:c10n", [type: 'exists', session: sessionURI, id: string(c10n.id)])
            assert exists
            context.subrequest("active:c10n", [type: 'delete', session: sessionURI, id:string(c10n.id)])
            exists = context.isTrue("active:c10n", [type: 'exists', session: sessionURI, id:string(c10n.id)])
            assert !exists
            context.subrequest(sessionURI, [type: 'delete'])
            context.respond(bool(true))
        }
    }

    void continuationUpdateSource() {
        use(NetKernelCategory) {
            String sessionURI = context.requestNew("session:null", null); // create session
            SessionHelper session = new SessionHelper(sessionURI, context)
            IAspectContinuation iac = (IAspectContinuation) context.sourceAspect("active:c10n", [type: 'new', session: sessionURI], IAspectContinuation.class)
            IContinuation c10n = iac.getContinuation()
            NVPImpl nvp = new NVPImpl()
            nvp.addNVP('a', 'A')
            nvp.addNVP('b', 'B')
            NVPAspect nvpa = new NVPAspect(nvp)
            c10n.state += [message: 'Hello World', truth: true, nvp: map([a: 'A', b: 'B'])]
            context.subrequest("active:c10n", [type: 'sink', session: sessionURI, continuation: new ContinuationAspect(c10n)])
            IAspectContinuation iac1 = (IAspectContinuation) context.sourceAspect("active:c10n", [session: sessionURI, id: string(c10n.id)], IAspectContinuation.class)
            IContinuation c10n1 = iac1.continuation
            assert c10n.id == c10n1.id
            context.subrequest(sessionURI, [type: 'delete'])
            context.respond(bool(true))
        }
    }

    void continuationCreateFromContinuation() {
        use(NetKernelCategory) {
            String sessionURI = context.requestNew("session:null", null); // create session
            SessionHelper session = new SessionHelper(sessionURI, context)
            IAspectContinuation iac = (IAspectContinuation) context.sourceAspect("active:c10n", [type: 'new', session: sessionURI], IAspectContinuation.class)
            IContinuation c10n = iac.getContinuation()
            NVPImpl nvp = new NVPImpl()
            nvp.addNVP('a', 'A')
            nvp.addNVP('b', 'B')
            NVPAspect nvpa = new NVPAspect(nvp)
            c10n.state += [message: 'Hello World', truth: true, nvp: map([a: 'A', b: 'B'])]
            context.subrequest("active:c10n", [type: 'sink', session: sessionURI, continuation: new ContinuationAspect(c10n)])
            // Now create a new continuation from this one
            iac = (IAspectContinuation) context.sourceAspect("active:c10n", [type: 'new', session: sessionURI, id:string(c10n.id)], IAspectContinuation.class)
            IContinuation c10n1 = iac.getContinuation()
            assert c10n.id != c10n1.id
            assert c10n1.previous == c10n.id
            assert c10n1.state.message == "Hello World"
            context.subrequest(sessionURI, [type: 'delete'])
            context.respond(bool(true))
        }
    }

}