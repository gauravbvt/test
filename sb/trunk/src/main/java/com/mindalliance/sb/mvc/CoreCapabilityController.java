package com.mindalliance.sb.mvc;

import com.mindalliance.sb.model.CoreCapability;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/lists/corecapabilitys")
@Controller
@RooWebScaffold(path = "lists/corecapabilitys", formBackingObject = CoreCapability.class, create = false, delete = false )
public class CoreCapabilityController extends AbstractController<CoreCapability> {

    @Override
    protected List<CoreCapability> getList() {
        return CoreCapability.findAllCoreCapabilitys();
    }
}
