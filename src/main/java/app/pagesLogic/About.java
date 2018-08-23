package app.pagesLogic;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

@Service("getAbout")
public class About extends Page {
    @Override
    public ModelAndView build() {
        ModelAndView mav = new ModelAndView("about");
        return mav;
    }
}