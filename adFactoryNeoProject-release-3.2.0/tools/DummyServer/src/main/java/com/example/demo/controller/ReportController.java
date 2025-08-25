package com.example.demo.controller;

import com.example.demo.model.OrderReport;
import com.example.demo.model.WorkReport;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/report")
public class ReportController {
    
    // メモ: Shift + Alt + O でインポートを補完

    @PostMapping(path = "/work")
    @ResponseStatus
    public ResponseEntity<String> postWorkReport(@RequestBody WorkReport report) {
        System.out.println(report);
        return ResponseEntity.ok("postWorkReport Successful: " + report);
    }

    @PostMapping(path = "/order")
    @ResponseStatus
    public ResponseEntity<String> postOrderReport(@RequestBody OrderReport report) {
        System.out.println(report);
        return ResponseEntity.ok("postOrderReport Successful: " + report);
    }
}