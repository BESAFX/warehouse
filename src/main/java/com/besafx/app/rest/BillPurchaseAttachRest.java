package com.besafx.app.rest;

import com.besafx.app.config.DropboxManager;
import com.besafx.app.entity.Attach;
import com.besafx.app.entity.BillPurchase;
import com.besafx.app.entity.BillPurchaseAttach;
import com.besafx.app.service.AttachService;
import com.besafx.app.service.BillPurchaseAttachService;
import com.besafx.app.service.BillPurchaseService;
import com.besafx.app.service.PersonService;
import com.besafx.app.ws.Notification;
import com.besafx.app.ws.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bohnman.squiggly.Squiggly;
import com.github.bohnman.squiggly.util.SquigglyUtils;
import com.google.common.collect.Lists;
import org.apache.commons.io.FilenameUtils;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RestController
@RequestMapping(value = "/api/billPurchaseAttach/")
public class BillPurchaseAttachRest {

    private final static Logger log = LoggerFactory.getLogger(BillPurchaseAttachRest.class);

    public static final String FILTER_TABLE = "" +
            "id," +
            "attach[**,person[id,contact[id,shortName]]]," +
            "billPurchase[id]";

    @Autowired
    private PersonService personService;

    @Autowired
    private BillPurchaseService billPurchaseService;

    @Autowired
    private BillPurchaseAttachService billPurchaseAttachService;

    @Autowired
    private AttachService attachService;

    @Autowired
    private DropboxManager dropboxManager;

    @Autowired
    private NotificationService notificationService;

    @PostMapping(value = "upload", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_BILL_PURCHASE_CREATE')")
    public String upload(
            @RequestParam(value = "billPurchaseId") Long billPurchaseId,
            @RequestParam(value = "files") MultipartFile[] files,
            Principal principal) throws Exception {

        List<BillPurchaseAttach> billPurchaseAttaches = new ArrayList<>();
        BillPurchase billPurchase = billPurchaseService.findOne(billPurchaseId);

        ListIterator<MultipartFile> multipartFileListIterator = Lists.newArrayList(files).listIterator();
        while (multipartFileListIterator.hasNext()) {
            MultipartFile file = multipartFileListIterator.next();
            Attach attach = new Attach();
            attach.setName(FilenameUtils.removeExtension(file.getOriginalFilename()));
            attach.setMimeType(FilenameUtils.getExtension(file.getOriginalFilename()));
            attach.setDescription("");
            attach.setSize(file.getSize());
            attach.setDate(new DateTime().toDate());
            attach.setPerson(personService.findByEmail(principal.getName()));

            String path = "/Shield/BillPurchase_Attaches/" + billPurchaseId + "/" + attach.getName() + "." + attach.getMimeType();

            Future<Boolean> uploadBillPurchase = dropboxManager.uploadFile(file, path);
            if (uploadBillPurchase.get()) {
                Future<String> shareBillPurchase = dropboxManager.shareFile(path);
                attach.setLink(shareBillPurchase.get());
                attach = attachService.save(attach);

                BillPurchaseAttach billPurchaseAttach = new BillPurchaseAttach();
                billPurchaseAttach.setAttach(attach);
                billPurchaseAttach.setBillPurchase(billPurchase);
                billPurchaseAttaches.add(billPurchaseAttachService.save(billPurchaseAttach));

                notificationService.notifyOne(Notification.builder()
                                                          .message("تم رفع الملف بنجاح")
                                                          .type("success")
                                                          .build(), principal.getName());
            }
        }
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), billPurchaseAttaches);
    }

    @DeleteMapping(value = "delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_BILL_PURCHASE_DELETE')")
    public Boolean delete(@PathVariable Long id, Principal principal) throws ExecutionException, InterruptedException {
        BillPurchaseAttach billPurchaseAttach = billPurchaseAttachService.findOne(id);
        if (billPurchaseAttach != null) {

            String path = "/Shield/BillPurchase_Attaches/" +
                    billPurchaseAttach.getBillPurchase().getId() +
                    "/" + billPurchaseAttach.getAttach().getName() +
                    "." +
                    billPurchaseAttach.getAttach().getMimeType();

            Future<Boolean> deleteBillPurchase = dropboxManager.deleteFile(path);
            if (deleteBillPurchase.get()) {
                billPurchaseAttachService.delete(billPurchaseAttach);
                attachService.delete(billPurchaseAttach.getAttach());
                notificationService.notifyOne(Notification.builder()
                                                          .message("تم حذف الملف بنجاح")
                                                          .type("error").build(), principal.getName());
                return true;
            } else {
                notificationService.notifyOne(Notification.builder()
                                                          .message("لا يمكن حذف الملف حيث يبدو غير موجود")
                                                          .type("error").build(), principal.getName());
                return false;
            }
        } else {
            return false;
        }
    }

    @DeleteMapping(value = "deleteWhatever/{id}")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_BILL_PURCHASE_DELETE')")
    public void deleteWhatever(@PathVariable Long id, Principal principal) {
        BillPurchaseAttach billPurchaseAttach = billPurchaseAttachService.findOne(id);
        if (billPurchaseAttach != null) {
            billPurchaseAttachService.delete(billPurchaseAttach);
            attachService.delete(billPurchaseAttach.getAttach());
            notificationService.notifyOne(Notification.builder()
                                                      .message("تم حذف الملف بنجاح")
                                                      .type("success").build(), principal.getName());
        }
    }

    @GetMapping(value = "findByBillPurchase/{billPurchaseId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findByBillPurchase(@PathVariable(value = "billPurchaseId") Long billPurchaseId) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), billPurchaseAttachService.findByBillPurchaseId(billPurchaseId));
    }
}
