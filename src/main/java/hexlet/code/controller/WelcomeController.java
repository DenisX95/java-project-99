package hexlet.code.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.sentry.Sentry;

@RestController
@RequestMapping("/welcome")
public class WelcomeController {

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public String index() {
        return "Welcome to Spring";
    }
}
