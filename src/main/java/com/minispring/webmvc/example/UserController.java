package com.minispring.webmvc.example;

import com.minispring.webmvc.ModelAndView;
import com.minispring.webmvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器示例
 * 展示Spring MVC的各种功能：路径变量、请求参数、JSON响应等
 * 体现Spring MVC的声明式编程和RESTful设计理念
 */
@Controller
@RequestMapping("/users")
public class UserController {
    
    /**
     * 获取所有用户列表
     * 演示基本的GET请求处理
     */
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView getAllUsers() {
        ModelAndView mv = new ModelAndView("user/list");
        
        // 模拟用户数据
        Map<String, Object> users = new HashMap<>();
        users.put("user1", new User(1, "张三", "zhangsan@example.com"));
        users.put("user2", new User(2, "李四", "lisi@example.com"));
        users.put("user3", new User(3, "王五", "wangwu@example.com"));
        
        mv.addObject("users", users);
        mv.addObject("title", "用户列表");
        
        return mv;
    }
    
    /**
     * 根据ID获取用户信息
     * 演示路径变量(@PathVariable)的使用
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ModelAndView getUserById(@PathVariable("id") Long id) {
        ModelAndView mv = new ModelAndView("user/detail");
        
        // 模拟根据ID查找用户
        User user = findUserById(id);
        if (user != null) {
            mv.addObject("user", user);
        } else {
            mv.addObject("error", "用户不存在，ID: " + id);
        }
        
        return mv;
    }
    
    /**
     * JSON格式返回用户信息
     * 演示@ResponseBody注解的使用
     */
    @RequestMapping(value = "/{id}/json", method = RequestMethod.GET)
    @ResponseBody
    public User getUserByIdJson(@PathVariable("id") Long id) {
        return findUserById(id);
    }
    
    /**
     * 创建用户
     * 演示POST请求和请求参数(@RequestParam)的使用
     */
    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView createUser(@RequestParam("name") String name, 
                                  @RequestParam("email") String email,
                                  @RequestParam(value = "age", defaultValue = "18") Integer age) {
        
        // 模拟创建用户
        User newUser = new User(System.currentTimeMillis(), name, email);
        newUser.setAge(age);
        
        System.out.println("创建新用户: " + newUser);
        
        // 重定向到用户详情页
        return new ModelAndView("redirect:/users/" + newUser.getId());
    }
    
    /**
     * 搜索用户
     * 演示请求参数处理和条件匹配
     */
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ModelAndView searchUsers(@RequestParam(value = "keyword", required = false) String keyword,
                                   @RequestParam(value = "page", defaultValue = "1") Integer page) {
        
        ModelAndView mv = new ModelAndView("user/search");
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            // 模拟搜索结果
            Map<String, Object> searchResults = new HashMap<>();
            searchResults.put("keyword", keyword);
            searchResults.put("page", page);
            searchResults.put("results", "搜索结果：找到包含 '" + keyword + "' 的用户");
            
            mv.addObject("searchResults", searchResults);
        }
        
        return mv;
    }
    
    /**
     * 获取用户统计信息
     * 演示JSON响应和复杂数据结构
     */
    @RequestMapping(value = "/stats", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getUserStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", 150);
        stats.put("activeUsers", 120);
        stats.put("newUsersToday", 5);
        stats.put("timestamp", System.currentTimeMillis());
        
        return stats;
    }
    
    /**
     * 演示原生Servlet API的使用
     */
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public ModelAndView getRequestInfo(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new ModelAndView("user/info");
        
        Map<String, String> requestInfo = new HashMap<>();
        requestInfo.put("method", request.getMethod());
        requestInfo.put("uri", request.getRequestURI());
        requestInfo.put("remoteAddr", request.getRemoteAddr());
        requestInfo.put("userAgent", request.getHeader("User-Agent"));
        
        mv.addObject("requestInfo", requestInfo);
        return mv;
    }
    
    /**
     * 演示内容协商 - 只处理JSON请求
     */
    @RequestMapping(value = "/api/{id}", 
                   method = RequestMethod.GET, 
                   produces = "application/json")
    @ResponseBody
    public User getApiUser(@PathVariable("id") Long id) {
        User user = findUserById(id);
        if (user == null) {
            user = new User(-1L, "用户不存在", "");
        }
        return user;
    }
    
    /**
     * 演示请求头条件匹配
     */
    @RequestMapping(value = "/mobile", 
                   method = RequestMethod.GET,
                   headers = "X-Mobile-Client=true")
    public ModelAndView getMobileVersion() {
        ModelAndView mv = new ModelAndView("user/mobile");
        mv.addObject("message", "这是移动端专用页面");
        return mv;
    }
    
    // 辅助方法
    private User findUserById(Long id) {
        // 模拟数据库查询
        if (id == 1) {
            return new User(1, "张三", "zhangsan@example.com");
        } else if (id == 2) {
            return new User(2, "李四", "lisi@example.com");
        } else if (id == 3) {
            return new User(3, "王五", "wangwu@example.com");
        }
        return null;
    }
    
    /**
     * 简单的User模型类
     */
    public static class User {
        private Long id;
        private String name;
        private String email;
        private Integer age;
        
        public User() {}
        
        public User(Long id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public Integer getAge() { return age; }
        public void setAge(Integer age) { this.age = age; }
        
        @Override
        public String toString() {
            return "User{id=" + id + ", name='" + name + "', email='" + email + "', age=" + age + "}";
        }
    }
}