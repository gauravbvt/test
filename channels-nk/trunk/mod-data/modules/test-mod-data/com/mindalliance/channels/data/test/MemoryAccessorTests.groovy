package com.mindalliance.channels.data.test
import com.mindalliance.channels.nk.NetKernelCategory
import com.mindalliance.channels.nk.aspects.PersistentBeanAspect
import com.mindalliance.channels.nk.aspects.IAspectPersistentBean
import com.mindalliance.channels.metamodel.TestBean
import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper as Context
import com.mindalliance.channels.metamodel.TestRunComponent

/**
 * Created by IntelliJ IDEA.
 * User: jf
 * Date: Jan 21, 2008
 * Time: 2:48:26 PM
 * To change this template use File | Settings | File Templates.
 */
class MemoryAccessorTests {

   Context context

    MemoryAccessorTests(Context ctx) {
        context = ctx
    }

}