package dsa.personal.notespsqlv04;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Controller
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @GetMapping("/loginPage")
    public String loginPage() throws Exception {
        logger.debug("redirecting to login page");
        return "plain-login";
    }
    
}
