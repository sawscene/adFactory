/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo.controller;

import com.example.demo.model.ProcessInfo;
import com.example.demo.model.ResponseMessage;
import com.example.demo.model.SerialInfo;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author nar-nakamura
 */
@RestController
@RequestMapping("/api")
public class ApiController {

    @RequestMapping(value = "/process", method = {RequestMethod.POST}, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus
    public ResponseEntity<ResponseMessage> postProcess(@RequestBody ProcessInfo body) {
        System.out.println(body);
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setMessage("postProcess Successful");
        return ResponseEntity.ok(responseMessage);
    }

    @RequestMapping(value = "/serial", method = {RequestMethod.POST}, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus
    public ResponseEntity<ResponseMessage> postSerial(@RequestBody SerialInfo body) {
        System.out.println(body);
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setMessage("postSerial Successful");
        return ResponseEntity.ok(responseMessage);
    }
}
