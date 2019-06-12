package demo;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DemoClient {

    public static void main(String[] args) {


        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");

        DemoService service = (DemoService) context.getBean("demoService");
        String hello = service.sayHello("param参数");
        OrderService orderService = context.getBean(OrderService.class);
        orderService.getMsg("订单发送ongoing");
        System.out.println(hello);
        while (true){

        }

    }
}
