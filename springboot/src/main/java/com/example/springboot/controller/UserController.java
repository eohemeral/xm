package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.User;
import com.example.springboot.service.UserService;
import com.example.springboot.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");

        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return Result.error("用户名或密码不能为空");
        }
        User user = userService.findByUsername(username);
        if (user == null) {
            return Result.error("用户不存在");
        }
        if (!user.getPassword().equals(md5(password))) {
            return Result.error("密码错误");
        }
        if (user.getStatus() == 0) {
            return Result.error("账号已禁用");
        }
        String token = TokenUtils.genToken(user.getId(), user.getPassword());
        return Result.success(Map.of("token", token, "user", user));
    }

    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        System.out.println("收到注册请求，用户信息：" + user);
        if (user.getUsername() == null || user.getUsername().trim().isEmpty() || user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            System.out.println("用户名或密码不能为空");
            return Result.error("用户名或密码不能为空");
        }
        User existingUser = userService.findByUsername(user.getUsername());
        System.out.println("查询用户是否存在：" + existingUser);
        if (existingUser != null) {
            System.out.println("用户名已存在");
            return Result.error("用户名已存在");
        }
        user.setPassword(md5(user.getPassword()));
        user.setStatus(1);
        System.out.println("保存用户信息：" + user);
        userService.save(user);
        System.out.println("注册成功");
        return Result.success();
    }

    @GetMapping("/info")
    public Result getInfo(@RequestHeader("token") String token) {
        Integer userId = TokenUtils.getUserId(token);
        User user = userService.findById(userId);
        return Result.success(user);
    }

    @PutMapping("/update")
    public Result update(@RequestBody User user, @RequestHeader("token") String token) {
        Integer userId = TokenUtils.getUserId(token);
        user.setId(userId);
        userService.update(user);
        return Result.success();
    }

    @PutMapping("/updatePassword")
    public Result updatePassword(@RequestBody Map<String, String> params, @RequestHeader("token") String token) {
        Integer userId = TokenUtils.getUserId(token);
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        if (oldPassword == null || oldPassword.trim().isEmpty() || newPassword == null || newPassword.trim().isEmpty()) {
            return Result.error("密码不能为空");
        }
        User user = userService.findById(userId);
        if (!user.getPassword().equals(md5(oldPassword))) {
            return Result.error("原密码错误");
        }
        user.setPassword(md5(newPassword));
        userService.update(user);
        return Result.success();
    }

    @GetMapping("/list")
    public Result list() {
        return Result.success(userService.list());
    }

    @PutMapping("/status")
    public Result updateStatus(@RequestBody Map<String, Object> params) {
        Integer id = (Integer) params.get("id");
        Integer status = (Integer) params.get("status");
        User user = userService.findById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setStatus(status);
        userService.update(user);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        User user = userService.findById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        userService.delete(id);
        return Result.success();
    }

    private String md5(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes("UTF-8"));
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return str;
        } catch (Exception e) {
            e.printStackTrace();
            return str;
        }
    }
}
