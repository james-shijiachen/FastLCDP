package com.fastlcdp.login.controller;

// import com.fastlcdp.login.annotation.ApiVersion; // 暂时注释掉，演示其他版本控制方法
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器 - 演示使用自定义注解进行版本控制
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    /**
     * 获取用户信息 - 使用请求头版本控制
     */
    @GetMapping("/profile")
    public String getUserProfile(@RequestHeader(value = "API-Version", defaultValue = "v1") String version) {
        switch (version) {
            case "v2":
                return "{\"id\": 1, \"name\": \"张三\", \"email\": \"zhangsan@example.com\", \"role\": \"admin\", \"createdAt\": \"2024-01-01T00:00:00Z\"}";
            case "v1":
            default:
                return "{\"id\": 1, \"name\": \"张三\", \"email\": \"zhangsan@example.com\"}";
        }
    }

    /**
     * 获取用户列表 - 使用请求头版本控制
     */
    @GetMapping("/list")
    public String getUserList(@RequestHeader(value = "API-Version", defaultValue = "v1") String version,
                             @RequestParam(defaultValue = "10") int limit,
                             @RequestParam(defaultValue = "0") int offset) {
        switch (version) {
            case "v2":
                return "{\"data\": [{\"id\": 1, \"name\": \"张三\", \"email\": \"zhangsan@example.com\"}], \"total\": 1, \"limit\": " + limit + ", \"offset\": " + offset + "}";
            case "v1":
            default:
                return "[{\"id\": 1, \"name\": \"张三\"}]";
        }
    }

    /**
     * 创建用户 - 支持多版本
     */
    @PostMapping("/create")
    public String createUser(@RequestHeader(value = "API-Version", defaultValue = "v1") String version,
                           @RequestBody String userData) {
        switch (version) {
            case "v2":
                return "{\"message\": \"用户创建成功 (v2)\", \"id\": 123, \"validation\": \"enhanced\"}";
            case "v1":
            default:
                return "{\"message\": \"用户创建成功 (v1)\", \"id\": 123}";
        }
    }
}