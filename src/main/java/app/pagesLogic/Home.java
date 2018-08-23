package app.pagesLogic;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

@Service
public class Home extends Page {
    @Override
    public ModelAndView build() {
        ModelAndView mav = new ModelAndView("home");
        return mav;
    }
}