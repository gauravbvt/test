package com.mindalliance.channels.c10n.test

import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper as Context
import com.mindalliance.channels.nk.SessionHelper
import com.mindalliance.channels.c10n.aspects.IAspectContinuation
import com.mindalliance.channels.c10n.util.IContinuation
import com.mindalliance.channels.nk.NetKernelCategory
import org.ten60.netkernel.layer1.representation.NVPAspect
import org.ten60.netkernel.layer1.representation.NVPImpl
import com.mindalliance.channels.c10n.aspects.ContinuationAspect
import com.ten60.netkernel.urii.aspect.IAspectString

/**
 * Created by IntelliJ IDEA.
 * User: jf
 * Date: Feb 12, 2008
 * Time: 1:01:59 PM
 * To change this template use File | Settings | File Templates.
 */
class C10nAccessorTests {

    Context context
    String sessionURI
    SessionHelper session

    C10nAccessorTests(Context context) {
        this.context = context
        sessionURI = context.requestNew("session:null", null); // create session
        session = new SessionHelper(sessionURI, context)
    }

    void continuationCreateExistsDelete() {
        use(NetKernelCategory) {
            IContinuation c10n = create()
            assert c10n.id
            assert c10n.date
            assert c10n.state.size() == 0
            assert !c10n.previous
            assert c10n.followUps.size() == 0
            assert !c10n.aborted
            assert !c10n.committed
            assert exists(c10n)
            delete(c10n)
            assert !exists(c10n)
            deleteSession()
            context.respond(bool(true))
        }
    }

    void continuationUpdateSource() {
        use(NetKernelCategory) {
            IContinuation c10n = create()
            NVPImpl nvp = new NVPImpl()
            nvp.addNVP('a', 'A')
            nvp.addNVP('b', 'B')
            NVPAspect nvpa = new NVPAspect(nvp)
            c10n.state += [message: 'Hello World', truth: true, nvp: map([a: 'A', b: 'B'])]
            update(c10n)
            IContinuation c10n1 = source(c10n.id)
            assert c10n.id == c10n1.id
            deleteSession()
            context.respond(bool(true))
        }
    }

    void continuationCreateFromContinuation() {
        use(NetKernelCategory) {
            IContinuation c10n = create()
            NVPImpl nvp = new NVPImpl()
            nvp.addNVP('a', 'A')
            nvp.addNVP('b', 'B')
            NVPAspect nvpa = new NVPAspect(nvp)
            c10n.state += [message: 'Hello World', truth: true, nvp: map([a: 'A', b: 'B'])]
            update(c10n)
            // Now create a new continuation from this one
            IContinuation c10n1 = followUp(c10n)
            assert c10n.id != c10n1.id
            assert c10n1.previous == c10n.id
            assert c10n.followUps.size() == 1
            assert c10n.followUps[0] == c10n1.id
            assert c10n1.state.message == "Hello World"
            deleteSession()
            context.respond(bool(true))
        }
    }

    void continuationDeleteAndRollup() {
        use(NetKernelCategory) {
            // a -> b -> c
            //   -> d
            IContinuation a = create()
            IContinuation b = followUp(a)
            IContinuation c = followUp(b)
            IContinuation d = followUp(a)
            assert a.followUps.size() == 2
            assert a.followUps.contains(b.id)
            assert a.followUps.contains(d.id)
            assert d.previous == a.id
            // Delete and verify rollup
            delete(d)
            assert !exists(d)
            assert exists(a)
            try {
                delete(a)
                assert false, 'Should not allow deleting c10n with follow-ups'
            }
            catch (Exception e) {
                assert exists(a)
            }
            delete(c)
            assert !exists(c)
            assert !exists(b)
            assert !exists(a)
            deleteSession()
            context.respond(bool(true))
        }

    }

    void continuationTransrept() {
        use(NetKernelCategory) {
            IContinuation a = create()
            NVPImpl nvp = new NVPImpl()
            nvp.addNVP('a', 'A')
            nvp.addNVP('b', 'B')
            NVPAspect nvpa = new NVPAspect(nvp)
            a.state += [message: 'Hello World', truth: true, nvp: map([a: 'A', b: 'B'])]
            update(a)
            IContinuation b = followUp(a)
            IContinuation c = followUp(a)
            IAspectString ias = (IAspectString) context.transrept(new ContinuationAspect(a), IAspectString.class)
            IAspectContinuation iac = (IAspectContinuation) context.transrept(ias, IAspectContinuation.class)
            IContinuation c10n = iac.getContinuation()
            assert c10n.state.message == "Hello World"
            assert c10n.followUps.size() == 2
            assert c10n.followUps.contains(b.id)
            assert c10n.followUps.contains(c.id)
            deleteSession()
            context.respond(bool(true))
        }
    }

    private IContinuation create() {
       IContinuation c10n
        use(NetKernelCategory) {
            IAspectContinuation iac = (IAspectContinuation) context.sourceAspect("active:c10n", [type: 'new', session: sessionURI], IAspectContinuation.class)
            c10n = iac.getContinuation()
        }
        return c10n
    }

    private IContinuation source(String id) {
        IContinuation c10n
        use(NetKernelCategory) {
            IAspectContinuation iac = (IAspectContinuation) context.sourceAspect("active:c10n", [session: sessionURI, id: string(id)], IAspectContinuation.class)
            c10n = iac.getContinuation()
        }
        return c10n
    }

    private void update(IContinuation c10n) {
      context.subrequest("active:c10n", [type: 'sink', session: sessionURI, continuation: new ContinuationAspect(c10n)])
    }

    private IContinuation followUp(IContinuation previous) {
        IContinuation c10n
         use(NetKernelCategory) {
             IAspectContinuation iac = (IAspectContinuation) context.sourceAspect("active:c10n", [type: 'new', session: sessionURI,  id:string(previous.id)], IAspectContinuation.class)
             c10n = iac.getContinuation()
         }
         return c10n

    }

    private void delete(IContinuation c10n) {
        context.subrequest("active:c10n", [type: 'delete', session: sessionURI, id:string(c10n.id)])
    }

    private boolean exists(IContinuation c10n) {
        return context.isTrue("active:c10n", [type: 'exists', session: sessionURI, id:string(c10n.id)])
    }

    private deleteSession() {
        use(NetKernelCategory) {
            context.subrequest(sessionURI, [type:'delete'])
        }
    }
}