package kziomek;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author Krzysztof Ziomek
 * @since 28/01/2016.
 */
@RestController
@RequestMapping(value = "/api/v1/hello")
public class PersonController {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @RequestMapping(method = GET)
    public ResponseEntity<String> hello(@RequestParam(value = "name", required = false) String name) {
        logger.info("GET hello method invoked with person name {}.", name);
        String responseMessage = name != null ? "Hello " + name : "Use query parameter 'name' to generate greeting response.";
        return new ResponseEntity<>(responseMessage, OK);

    }

    @RequestMapping(method = POST)
    public ResponseEntity<String> hello(@Valid @RequestBody Person person) {
        logger.info("POST hello method invoked with person name {}.", person.getName());
        String responseMessage = person.getName() != null ? "Hello " + person.getName() : "Use query parameter 'name' to generate greeting response.";
        return new ResponseEntity<>(responseMessage, OK);

    }
}
