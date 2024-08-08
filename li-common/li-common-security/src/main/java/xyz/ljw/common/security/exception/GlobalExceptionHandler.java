package xyz.ljw.common.security.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;


@RestControllerAdvice
public class GlobalExceptionHandler {

    public GlobalExceptionHandler() {
        System.out.println("构造方法被执行！！！！！");
    }

    @ExceptionHandler(RuntimeException.class)
    public String exceptionHandler(HttpServletRequest request, RuntimeException e) {
        String requestURI = request.getRequestURI();
//        request.getRequestURL()
        System.out.println(requestURI);
        return e.getMessage();
//        return ResponseEntity.status(300).body(e.getMessage());
//        Map<String, String> map = new HashMap<>();
//        map.put("CODE", "500");
//        map.put("MESSAGE", e.getMessage());
//        return map;
    }

}
