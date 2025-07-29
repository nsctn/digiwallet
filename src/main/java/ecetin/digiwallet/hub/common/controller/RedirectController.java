package ecetin.digiwallet.hub.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Controller to handle redirects to Swagger UI.
 */
@Controller
public class RedirectController {

    /**
     * Redirects the root path to the Swagger UI.
     *
     * @return RedirectView to Swagger UI
     */
    @GetMapping("/")
    public RedirectView redirectToSwaggerUi() {
        return new RedirectView("/swagger-ui.html");
    }
}