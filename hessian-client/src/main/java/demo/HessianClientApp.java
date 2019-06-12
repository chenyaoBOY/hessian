package demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chenyao
 * @date 2019/6/12 16:01
 * @description
 */
@SpringBootApplication
@RestController
@ImportResource(locations = {"classpath*:applicationContext.xml"})
public class HessianClientApp {

    @Autowired
    DemoService demoService;

    @Autowired
    OrderService orderService;

    @RequestMapping("/demo")
    public String demo(){
        return demoService.sayHello("heihei");
    }
    @RequestMapping("/order")
    public String order(){
        return orderService.getMsg("order");
    }

    public static void main(String[] args) {
        SpringApplication.run(HessianClientApp.class,args);
    }
}
