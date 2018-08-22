package app;

import app.pagesLogic.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

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

    @RequestMapping(path = "/*")
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
            HttpSession session) {
        //TODO убрать получение экземпляра напрямую из контекста.
        Answer answer = context.getBean(Answer.class);
        return createModelAndView(answer, session, a, b, operation);
    }

    private ModelAndView createModelAndView(Answer answer, HttpSession session, @RequestParam(value = "a") String a, @RequestParam(value = "b") String b, @RequestParam(value = "operation") String operation) {
        try {
            return answer.build(session, a, b, operation);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @RequestMapping(path = "/about")
    public String getAbout() {
        return "about";
    }

}