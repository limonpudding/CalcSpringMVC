package app.pagesLogic;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
@Service("getError")
public class Error extends Page {

    @Override
    public ModelAndView build() {
        ModelAndView mav = new ModelAndView("error");
        return mav;
    }
}
