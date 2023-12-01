package com.TeamProject.TeamProject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {
    @RequestMapping("/")
    public String test(){
        return "redirect:/restaurant/list";
    }



    @RequestMapping("/modal-test")
    public String modalTest(){
        return "modal_test";
    }
}
