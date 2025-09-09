package com.minispring.webmvc.example;

import com.minispring.webmvc.ModelAndView;
import com.minispring.webmvc.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 简化版控制器示例
 * 展示Spring MVC核心功能，避免过度复杂的业务逻辑
 * 体现清晰的设计意图和学习价值
 */
@Controller
@RequestMapping("/simple")
public class SimpleController {
    
    /**
     * 最基本的请求映射
     */
    @RequestMapping("/hello")
    public ModelAndView hello() {
        ModelAndView mv = new ModelAndView("hello");
        mv.addObject("message", "Hello, Mini Spring MVC!");
        return mv;
    }
    
    /**
     * 带参数的请求处理
     */
    @RequestMapping("/greet")
    public ModelAndView greet(@RequestParam("name") String name) {
        ModelAndView mv = new ModelAndView("greet");
        mv.addObject("greeting", "Hello, " + name + "!");
        return mv;
    }
    
    /**
     * 路径变量示例
     */
    @RequestMapping("/user/{id}")
    public ModelAndView getUser(@PathVariable("id") String id) {
        ModelAndView mv = new ModelAndView("user");
        mv.addObject("userId", id);
        mv.addObject("userName", "User" + id);
        return mv;
    }
    
    /**
     * JSON响应示例
     */
    @RequestMapping("/api/data")
    @ResponseBody
    public Map<String, Object> getData() {
        Map<String, Object> data = new HashMap<>();
        data.put("status", "success");
        data.put("message", "This is JSON response");
        data.put("timestamp", System.currentTimeMillis());
        return data;
    }
    
    /**
     * POST请求处理
     */
    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    public ModelAndView submit(@RequestParam("data") String data) {
        ModelAndView mv = new ModelAndView("result");
        mv.addObject("result", "Received: " + data);
        return mv;
    }
    
    /**
     * 重定向示例
     */
    @RequestMapping("/redirect-test")
    public ModelAndView redirectTest() {
        return new ModelAndView("redirect:/simple/hello");
    }
    
    /**
     * 转发示例
     */
    @RequestMapping("/forward-test")
    public ModelAndView forwardTest() {
        return new ModelAndView("forward:/simple/hello");
    }
}
