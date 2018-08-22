package app;

import app.pagesLogic.Answer;
import app.pagesLogic.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
public class MainController {

/*    @RequestMapping(path ="/*", method = RequestMethod.GET)
    public String getIndex(@RequestParam(value="name", required=true) String name, Model model){

        System.out.println(name);
        return "index";
    }*/
    @Autowired
    AnnotationConfigWebApplicationContext context;

    //TODO привязать через Autowired и Qualifier реализации созданного абстрактного класса для каждого представления свою.

    @RequestMapping(path = "/")
    public String getHome() {
        return "home";
    }

    @RequestMapping(path = "/calc")
    public String getCalc() {
        return "input";
    }

    @RequestMapping(path = "/ophistory")
    public String getOperationHistory() {
        return "ophistory";
    }

    @RequestMapping(path = "/answer")
    public ModelAndView getAnswer(
            @RequestParam(value = "a") String a,
            @RequestParam(value = "b") String b,
            @RequestParam(value = "operation") String operation,
            HttpSession session) throws Exception {
        //TODO убрать получение экземпляра напрямую из контекста.
        Page page = context.getBean(Page.class,"getAnswer");
        Map<String, Object> params = new HashMap<>();
        params.put("a", a);
        params.put("b", b);
        params.put("operation", operation);
        params.put("session", session);
        page.setParams(params);
        return page.build();
    }

    @RequestMapping(path = "/about")
    public String getAbout() {
        return "about";
    }

}