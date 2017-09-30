package com.besafx.app.rest;

import com.besafx.app.config.DropboxManager;
import com.besafx.app.entity.AccountAttach;
import com.besafx.app.entity.Attach;
import com.besafx.app.service.*;
import com.besafx.app.ws.Notification;
import com.besafx.app.ws.NotificationService;
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
@RequestMapping(value = "/api/accountAttach/")
public class AccountAttachRest {

    private final static Logger log = LoggerFactory.getLogger(AccountAttachRest.class);

    private final String FILTER_TABLE = "**,account[id],attach[**,person[id,contact[firstName,forthName]]],attachType[**]";

    @Autowired
    private PersonService personService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AttachTypeService attachTypeService;

    @Autowired
    private AttachService attachService;

    @Autowired
    private AccountAttachService accountAttachService;

    @Autowired
    private DropboxManager dropboxManager;

    @Autowired
    private NotificationService notificationService;

    @RequestMapping(value = "upload/{accountId}/{attachTypeId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_ACCOUNT_ATTACH_CREATE')")
    public String upload(@PathVariable(value = "accountId") Long accountId,
                                @PathVariable(value = "attachTypeId") Long attachTypeId,
                                @RequestParam(value = "fileName") String fileName,
                                @RequestParam(value = "file") MultipartFile file,
                                Principal principal)
            throws ExecutionException, InterruptedException {

        AccountAttach accountAttach = new AccountAttach();
        accountAttach.setAccount(accountService.findOne(accountId));
        accountAttach.setAttachType(attachTypeService.findOne(attachTypeId));

        Attach attach = new Attach();
        attach.setName(fileName);
        attach.setSize(file.getSize());
        attach.setDate(new DateTime().toDate());
        attach.setPerson(personService.findByEmail(principal.getName()));

        Future<Boolean> uploadTask = dropboxManager.uploadFile(file, "/Smart Offer/Accounts/" + accountId + "/" + fileName);
        if (uploadTask.get()) {
            Future<String> shareTask = dropboxManager.shareFile("/Smart Offer/Accounts/" + accountId + "/" + fileName);
            attach.setLink(shareTask.get());
            notificationService.notifyOne(Notification
                    .builder()
                    .title("تسجيل الطلاب")
                    .message("تم رفع الملف" + " [ " + file.getOriginalFilename() + " ] " + " بنجاح.")
                    .type("success")
                    .icon("fa-upload")
                    .build(), principal.getName());

            attach = attachService.save(attach);
            accountAttach.setAttach(attach);
            return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), accountAttachService.save(accountAttach));
        } else {
            return null;
        }
    }

    @RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_ACCOUNT_ATTACH_DELETE')")
    public Boolean delete(@PathVariable Long id, Principal principal) throws ExecutionException, InterruptedException {
        AccountAttach accountAttach = accountAttachService.findOne(id);
        if (accountAttach != null) {
            Future<Boolean> deleteTask = dropboxManager.deleteFile("/Smart Offer/Accounts/" + accountAttach.getAccount().getId() + "/" + accountAttach.getAttach().getName());
            if (deleteTask.get()) {
                accountAttachService.delete(accountAttach);
                notificationService.notifyOne(Notification
                        .builder()
                        .title("تسجيل الطلاب")
                        .message("تم حذف الملف" + " [ " + accountAttach.getAttach().getName() + " ] " + " بنجاح.")
                        .type("success")
                        .icon("fa-trash")
                        .build(), principal.getName());
                return true;
            } else {
                notificationService.notifyOne(Notification
                        .builder()
                        .title("تسجيل الطلاب")
                        .message("لا يمكن حذف الملف" + " [ " + accountAttach.getAttach().getName() + " ] ")
                        .type("error")
                        .icon("fa-trash")
                        .build(), principal.getName());
                return false;
            }
        }else{
            return false;
        }
    }

    @RequestMapping(value = "deleteWhatever/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_ACCOUNT_ATTACH_DELETE')")
    public void deleteWhatever(@PathVariable Long id, Principal principal) throws ExecutionException, InterruptedException {
        AccountAttach accountAttach = accountAttachService.findOne(id);
        if (accountAttach != null) {
            accountAttachService.delete(accountAttach);
            notificationService.notifyOne(Notification
                    .builder()
                    .title("تسجيل الطلاب")
                    .message("تم حذف المرفق" + " [ " + accountAttach.getAttach().getName() + " ] " + " بنجاح.")
                    .type("success")
                    .icon("fa-trash")
                    .build(), principal.getName());
        }
    }

    @RequestMapping(value = "findByAccount/{accountId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findByAccount(@PathVariable(value = "accountId") Long accountId) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), accountAttachService.findByAccountId(accountId));
    }
}
