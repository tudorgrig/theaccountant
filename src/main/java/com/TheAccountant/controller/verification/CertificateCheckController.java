package com.TheAccountant.controller.verification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller used for Certificate Generation
 *
 * Created by Florin on 1/14/2017.
 */
@RestController
@RequestMapping(value = "/.well-known/acme-challenge")
public class CertificateCheckController {

    @Value("${certification.check.string1}")
    private String securityString1;

    @Value("${certification.check.string2}")
    private String securityString2;

    @RequestMapping(value = "/UXNzr1IeUQYhaKqZ5DGN8I4oJGkO-zJxuWTgjRlIalQ", method = RequestMethod.GET)
    public ResponseEntity<String> check1() {

        String securityCheckString1 = securityString1;
        return new ResponseEntity<>(securityCheckString1, HttpStatus.OK);
    }

    @RequestMapping(value = "/95qSp_olDPIKMXCKFRBdN9uJJMTp3RHeHOTZNCmrhI4", method = RequestMethod.GET)
    public ResponseEntity<String> check2() {

        String securityCheckString2 = securityString2;
        return new ResponseEntity<>(securityCheckString2, HttpStatus.OK);
    }
}
