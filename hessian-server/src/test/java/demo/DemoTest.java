package demo;

import com.caucho.hessian.client.HessianProxyFactory;
import org.junit.Before;
import org.junit.Test;

import java.net.MalformedURLException;

public class DemoTest {


    @Test
    public void test(){
        HessianProxyFactory factory = new HessianProxyFactory();

        String url = "http://localhost:8080/service/demo";
        try {
            DemoService service = (DemoService) factory.create(DemoService.class, url);

            String hello = service.sayHello("hello world !");

            System.out.println(hello);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

}
