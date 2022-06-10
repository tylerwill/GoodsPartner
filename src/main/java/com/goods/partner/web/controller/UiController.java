package com.goods.partner.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UiController {
    @GetMapping("/")
    public String getLoginPage() {
        return "index.html";
    }
}