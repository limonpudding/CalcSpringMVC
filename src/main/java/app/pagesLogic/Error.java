package app.pagesLogic;

import org.springframework.web.servlet.ModelAndView;

public class Error extends Page {

    @Override
    public ModelAndView build() {
        ModelAndView mav = new ModelAndView("error");
        return mav;
    }
}
