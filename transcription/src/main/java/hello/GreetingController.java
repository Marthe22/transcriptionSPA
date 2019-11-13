package hello;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.atomic.AtomicLong;

@RestController
public class GreetingController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @CrossOrigin()
    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) {
        return new Greeting(counter.incrementAndGet(),
                String.format(template, name));
    }

    @CrossOrigin()
    @PostMapping(value = "/transcript1", consumes = "multipart/form-data")
    public Transcripter transcript(@RequestParam("file") MultipartFile file) {
        Transcripter transcripter = new Transcripter(file);
        return transcripter;
    }
}
