package com.coderiders.gamificationservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
@RequiredArgsConstructor
public class GamificationServiceController {

    @GetMapping()
    public ResponseEntity<String> gelloGamification() {
        return new ResponseEntity<>("Gamification Service", HttpStatus.OK);
    }

}
