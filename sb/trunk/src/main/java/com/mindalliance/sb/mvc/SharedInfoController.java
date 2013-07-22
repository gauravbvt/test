package com.mindalliance.sb.mvc;

import com.mindalliance.sb.model.SharedInfo;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/lists/sharedinfoes")
@Controller
@RooWebScaffold(path = "lists/sharedinfoes", formBackingObject = SharedInfo.class)
public class SharedInfoController {
}
