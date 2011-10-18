package org.motechproject.ghana.mtn.controller;

import org.motechproject.ghana.mtn.parser.CompositeInputMessageParser;
import org.motechproject.ghana.mtn.parser.RelativeProgramMessageParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private CompositeInputMessageParser compositeParser;
    @Autowired
    private RelativeProgramMessageParser relativeProgramMessageParser;

    @RequestMapping("/recompile")
    public void console(HttpServletResponse httpServletResponse) throws IOException {
        relativeProgramMessageParser.recompilePatterns();
        compositeParser.recompilePatterns();
        httpServletResponse.getWriter().append("Recompiled");
    }



}
