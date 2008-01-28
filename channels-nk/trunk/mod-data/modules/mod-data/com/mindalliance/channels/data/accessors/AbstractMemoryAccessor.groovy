package com.mindalliance.channels.data.accessors

import com.mindalliance.channels.nk.NetKernelCategory
import com.mindalliance.channels.nk.accessors.AbstractDataAccessor
import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper as Context
import com.mindalliance.channels.data.BeanRequestContext
import com.mindalliance.channels.data.BeanMemory

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Jan 22, 2008
* Time: 11:15:42 AM
* To change this template use File | Settings | File Templates.
*/
abstract class AbstractMemoryAccessor extends AbstractDataAccessor {

    // Put context and beanGraph into thread local
    void initBeanRequestContext(Context context) {
        use(NetKernelCategory) {
            BeanMemory beanMemory = BeanMemory.getInstance()
            BeanRequestContext.setRequestContext(context)
            BeanRequestContext.setBeanMemory(beanMemory)
        }
    }


}