package com.besafx.app.rest;

import com.besafx.app.config.DropboxManager;
import com.besafx.app.entity.Account;
import com.besafx.app.entity.AccountAttach;
import com.besafx.app.entity.Attach;
import com.besafx.app.entity.AttachType;
import com.besafx.app.service.AccountAttachService;
import com.besafx.app.service.AttachService;
import com.besafx.app.service.AttachTypeService;
import com.besafx.app.service.PersonService;
import com.besafx.app.util.JSONConverter;
import com.besafx.app.ws.Notification;
import com.besafx.app.ws.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bohnman.squiggly.Squiggly;
import com.github.bohnman.squiggly.util.SquigglyUtils;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RestController
@RequestMapping(value = "/api/accountAttach/")
public class AccountAttachRest {

    private final static Logger log = LoggerFactory.getLogger(AccountAttachRest.class);

    private final String FILTER_TABLE = "" +
            "**," +
            "account[id]," +
            "attach[**,person[id,contact[id,shortName]]]," +
            "attachType[**]";

    @Autowired
    private PersonService personService;

    @Autowired
    private AttachService attachService;

    @Autowired
    private AccountAttachService accountAttachService;

    @Autowired
    private AttachTypeService attachTypeService;

    @Autowired
    private DropboxManager dropboxManager;

    @Autowired
    private NotificationService notificationService;

    @RequestMapping(
            value = "upload",
            method = RequestMethod.POST)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_ACCOUNT_ATTACH_CREATE')")
    public String upload(@RequestParam(value = "account") String accountString,
                         @RequestParam(value = "files") MultipartFile[] files,
                         Principal principal) {

        Account account = JSONConverter.toObject(accountString, Account.class);
        List<AccountAttach> accountAttaches = new ArrayList<>();
        Optional.ofNullable(files).ifPresent(value -> {
            ListIterator<MultipartFile> fileListIterator = Lists.newArrayList(files).listIterator();
            while (fileListIterator.hasNext()) {
                MultipartFile file = fileListIterator.next();

                AccountAttach accountAttach = new AccountAttach();
                accountAttach.setAccount(account);

                Attach attach = new Attach();
                attach.setName(file.getOriginalFilename());
                attach.setSize(file.getSize());
                attach.setDate(new DateTime().toDate());
                attach.setPerson(personService.findByEmail(principal.getName()));

                Future<Boolean> uploadTask = dropboxManager.uploadFile(file, "/Smart Offer/Accounts/" + account.getId() + "/" + file.getOriginalFilename());
                Boolean result = null;
                try {
                    result = uploadTask.get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (result) {
                    String link = null;
                    try {
                        link = dropboxManager.shareFile("/Smart Offer/Accounts/" + account.getId() + "/" + file.getOriginalFilename()).get();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    attach.setLink(link);
                    notificationService.notifyAll(Notification.builder().message("تم رفع الملف" + " [ " + file.getOriginalFilename() + " ] " + " بنجاح.").type("success").build());
                    attach = attachService.save(attach);
                    accountAttach.setAttach(attach);
                    accountAttaches.add(accountAttachService.save(accountAttach));
                }
            }
        });
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), accountAttaches);
    }

    @RequestMapping(value = "setType/{accountAttachId}/{attachTypeId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_ACCOUNT_ATTACH_SET_TYPE')")
    public String setType(
            @PathVariable(value = "accountAttachId") Long accountAttachId,
            @PathVariable(value = "attachTypeId") Long attachTypeId,
            Principal principal) {
        AccountAttach accountAttach = accountAttachService.findOne(accountAttachId);
        AttachType attachType = attachTypeService.findOne(attachTypeId);
        if (accountAttach != null) {
            accountAttach.setAttachType(attachType);
            accountAttach = accountAttachService.save(accountAttach);
            notificationService.notifyAll(Notification.builder().message("تم تعديل نوع الملف بنجاح").type("success").build());
            return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), accountAttach);
        } else {
            return null;
        }
    }

    @RequestMapping(value = "setName/{accountAttachId}/{name}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_ACCOUNT_ATTACH_SET_NAME')")
    public String setName(
            @PathVariable(value = "accountAttachId") Long accountAttachId,
            @PathVariable(value = "name") String name,
            Principal principal) throws Exception {
        AccountAttach accountAttach = accountAttachService.findOne(accountAttachId);
        if (accountAttach != null) {
            String oldPath = "/Smart Offer/Accounts/" + accountAttach.getAccount().getId() + "/" + accountAttach.getAttach().getName();
            String newPath = "/Smart Offer/Accounts/" + accountAttach.getAccount().getId() + "/" + name;
            dropboxManager.renameFile(oldPath, newPath).get();
            accountAttach.getAttach().setName(name);
            accountAttach = accountAttachService.save(accountAttach);
            notificationService.notifyAll(Notification.builder().message("تم تعديل اسم الملف بنجاح").type("success").build());
            return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), accountAttach);
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
                notificationService.notifyAll(Notification.builder().message("تم حذف الملف" + " [ " + accountAttach.getAttach().getName() + " ] " + " بنجاح.").type("success").build());
                return true;
            } else {
                notificationService.notifyAll(Notification.builder().message("لا يمكن حذف الملف" + " [ " + accountAttach.getAttach().getName() + " ] ").type("error").build());
                return false;
            }
        } else {
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
            notificationService.notifyAll(Notification.builder().message("تم حذف المرفق" + " [ " + accountAttach.getAttach().getName() + " ] " + " بنجاح.").type("success").build());
        }
    }

    @RequestMapping(value = "findByAccount/{accountId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findByAccount(@PathVariable(value = "accountId") Long accountId) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), accountAttachService.findByAccountId(accountId));
    }
}
