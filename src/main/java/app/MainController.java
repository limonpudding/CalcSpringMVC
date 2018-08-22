package app;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {

/*    @RequestMapping(path ="/*", method = RequestMethod.GET)
    public String getIndex(@RequestParam(value="name", required=true) String name, Model model){

        System.out.println(name);
        return "index";
    }*/

    @RequestMapping(path ="/*")
    public String getHome(){
        return "home";
    }

    @RequestMapping(path ="/calc")
    public String getCalc(){
        return "input";
    }

    @RequestMapping(path ="/ophistory")
    public String getOperationHistory(){
        return "ophistory";
    }

    @RequestMapping(path ="/answer")
    public String getAnswer(@RequestParam(value="a", required=false) String a, @RequestParam(value="b", required=false)String b, @RequestParam(value="answer", required=false)String answer, Model model){

        return "answer";
    }

    @RequestMapping(path ="/about")
    public String getAbout(){
        return "about";
    }

//    @RequestMapping("/welcome")
//    public String getWelcome(){
//        return "welcome";
//    }
}