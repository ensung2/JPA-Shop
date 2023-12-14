package jpabook.jpashop.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j     // 로그 찍기
public class HomeController {


    @RequestMapping("/")
    public String home() {
        log.info("home controller");
        return "home";      // home.html 파일로 찾아가서 오픈
    }

}
