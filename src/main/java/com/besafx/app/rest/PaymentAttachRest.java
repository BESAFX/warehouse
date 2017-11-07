package com.besafx.app.rest;

import com.besafx.app.config.DropboxManager;
import com.besafx.app.entity.Attach;
import com.besafx.app.entity.Payment;
import com.besafx.app.service.AttachService;
import com.besafx.app.service.PaymentService;
import com.besafx.app.service.PersonService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bohnman.squiggly.Squiggly;
import com.github.bohnman.squiggly.util.SquigglyUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RestController
@RequestMapping(value = "/api/paymentAttach/")
public class PaymentAttachRest {

    private final static Logger log = LoggerFactory.getLogger(PaymentAttachRest.class);

    @Autowired
    private PersonService personService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private AttachService attachService;

    @Autowired
    private DropboxManager dropboxManager;

    @RequestMapping(value = "upload/{paymentId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_PAYMENT_CREATE')")
    public String upload(@PathVariable(value = "paymentId") Long paymentId,
                         @RequestParam(value = "file") MultipartFile file,
                         Principal principal)
            throws ExecutionException, InterruptedException {

        Attach attach = new Attach();
        log.info("ORIGINAL_FILE_NAME: " + file.getOriginalFilename());
        attach.setName(file.getOriginalFilename());
        attach.setSize(file.getSize());
        attach.setDate(new DateTime().toDate());
        attach.setPerson(personService.findByEmail(principal.getName()));

        Future<Boolean> uploadTask = dropboxManager.uploadFile(file, "/Smart Offer/Payments/" + paymentId + "/" + file.getOriginalFilename());
        if (uploadTask.get()) {
            Future<String> shareTask = dropboxManager.shareFile("/Smart Offer/Payments/" + paymentId + "/" + file.getOriginalFilename());
            attach.setLink(shareTask.get());
            Payment payment = paymentService.findOne(paymentId);
            payment.setAttach(attachService.save(attach));
            return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), PaymentRest.FILTER_TABLE), paymentService.save(payment));
        } else {
            return null;
        }
    }
}
