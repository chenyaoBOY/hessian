package demo.impl;

import demo.OrderService;
import org.springframework.stereotype.Service;

/**
 * @author chenyao
 * @date 2019/6/11 14:18
 * @description
 */
@Service
public class OrderServiceImpl implements OrderService {
    @Override
    public String getMsg(String msg) {
        System.out.println("msg="+msg);
        return "msg="+msg;
    }
}
