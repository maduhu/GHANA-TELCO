package org.motechproject.ghana.mtn.controller;

import org.motechproject.ghana.mtn.parser.InputMessageParser;
import org.motechproject.ghana.mtn.parser.RelativeProgramMessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private InputMessageParser inputMessageParser;
    @Autowired
    private RelativeProgramMessageHandler relativeProgramMessageHandler;

    @RequestMapping("/recompile")
    public void console(HttpServletResponse httpServletResponse) throws IOException {
        relativeProgramMessageHandler.recompilePatterns();
        inputMessageParser.recompilePatterns();
        httpServletResponse.getWriter().append("Recompiled");
    }
}
