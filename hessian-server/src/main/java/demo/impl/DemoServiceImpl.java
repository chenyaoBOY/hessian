package demo.impl;

import demo.DemoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DemoServiceImpl implements DemoService {

    @Value("${applicationName}")
    private String applicationName;
    @Override
    public String sayHello(String content) {
        System.out.println("content = " + content+applicationName);
        return "hello wolrd";
    }
}
