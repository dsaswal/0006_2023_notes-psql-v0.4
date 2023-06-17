package dsa.personal.notespsqlv04;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Controller
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @GetMapping("/")
    public String homePage() throws Exception {
        logger.debug("redirecting to home page");
        return "home";
    }

    @GetMapping("/loginPage")
    public String loginPage() throws Exception {
        logger.debug("redirecting to login page");
        return "plain-login";
    }
    
    @GetMapping("/error")
    public String errorPage() throws Exception {
        logger.debug("redirecting to error page");
        return "error";
    }

    @GetMapping("/access-denied")
    public String accessDenied() throws Exception {
        logger.debug("redirecting to login page");
        return "no-access";
    }
}
