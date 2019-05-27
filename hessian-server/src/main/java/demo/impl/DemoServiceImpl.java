package demo.impl;

import demo.DemoService;

public class DemoServiceImpl implements DemoService {
    @Override
    public String sayHello(String content) {
        System.out.println("content = " + content);
        return "hello wolrd";
    }
}
