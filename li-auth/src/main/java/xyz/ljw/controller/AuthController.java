package xyz.ljw.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    @GetMapping("/test/{name}")
    public String test(@PathVariable String name) {
        log.info("测试日志， 日志内容为：{}","我是你爹");
        throw new RuntimeException("测试异常");
//        System.out.println(1 / 0);
//        return "Hello" + name + "!!!!!";
    }

    @GetMapping("test1")
    public String test1() {
        System.out.println("成功！！！！！");
        return "返回成功";
    }

}
