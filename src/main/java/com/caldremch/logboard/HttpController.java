package com.caldremch.logboard;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Leon on 2022/9/15
 */
@RestController
@RequestMapping("/requst")
public class HttpController {

    @RequestMapping(value = "/what",  produces = "application/json;charset=UTF-8")
    public void alertManager(@RequestBody String appMetric) throws Exception {
        System.out.println("http请求..");
    }
}
