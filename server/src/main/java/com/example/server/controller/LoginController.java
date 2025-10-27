package com.example.server.controller;

import com.example.server.entity.Login;
import com.example.server.entity.LoginResponse;
import com.example.server.entity.Response;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class LoginController {

    private static final String USERNAME = "admin";
    private static final String PASSWORD = "123456";

    @PostMapping("/login")
    public Response<LoginResponse> login(@Valid @RequestBody Login login){
        if (USERNAME.equals(login.getUsername()) && PASSWORD.equals(login.getPassword())){
            String token  = UUID.randomUUID().toString();
            LoginResponse response = new LoginResponse(token,login.getUsername());
            return  Response.success(response);
        }

        return  Response.error("用户名或密码错误");

    }
}
