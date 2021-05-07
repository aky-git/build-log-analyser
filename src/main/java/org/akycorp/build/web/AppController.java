package org.akycorp.build.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/*
 * @author  : ajay_kumar_yadav
 * @created : 06/05/21
 */
@Controller
public class AppController {
    @RequestMapping("/main")
    public String mainPage(Model model) {
        return "main-page";
    }

    //redirect if user hits the root
    @RequestMapping("/")
    public String home(Model model) {
        return "redirect:main";
    }
}
