package com.besafx.app.excel;
import com.besafx.app.entity.Account;
import com.besafx.app.entity.Payment;
import com.besafx.app.entity.Person;
import com.besafx.app.service.AccountService;
import com.besafx.app.service.PaymentService;
import com.besafx.app.service.PersonService;
import com.besafx.app.util.ArabicLiteralNumberParser;
import com.besafx.app.util.DateConverter;
import com.besafx.app.ws.Notification;
import com.besafx.app.ws.NotificationService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.Principal;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RestController
public class ExcelPaymentController {

    private final static Logger log = LoggerFactory.getLogger(ExcelPaymentController.class);

    private SecureRandom random;

    private Integer day = null, month = null, year = null, masterCode = null, courseCode = null, accountCode = null;

    @Autowired
    private AccountService accountService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PersonService personService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ExcelCellHelper excelCellHelper;

    @PostConstruct
    public void init() {
        random = new SecureRandom();
    }

    @RequestMapping(value = "/api/heavy-work/payment/write/{rowCount}", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void writeExcelFile(@PathVariable(value = "rowCount") Integer rowCount, HttpServletResponse response, Principal principal) {
        log.info("فحص المستخدم");
        Person person = personService.findByEmail(principal.getName());
        //
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(person.getContact().getFirstName() + " " + person.getContact().getForthName() + " - " + " فرع " + person.getBranch().getName());
        Row row = sheet.createRow(0);
        row.setHeightInPoints((short) 25);
        //
        XSSFFont fontColumnHeader = workbook.createFont();
        fontColumnHeader.setBold(true);
        fontColumnHeader.setColor(IndexedColors.WHITE.getIndex());
        //
        XSSFFont fontCellDate = workbook.createFont();
        fontCellDate.setBold(true);
        fontCellDate.setColor(IndexedColors.BLACK.getIndex());
        //
        XSSFCellStyle styleColumnHeader = workbook.createCellStyle();
        styleColumnHeader.setFont(fontColumnHeader);
        styleColumnHeader.setAlignment(HorizontalAlignment.CENTER);
        styleColumnHeader.setVerticalAlignment(VerticalAlignment.CENTER);
        styleColumnHeader.setBorderTop(BorderStyle.THIN);
        styleColumnHeader.setBorderLeft(BorderStyle.THIN);
        styleColumnHeader.setBorderBottom(BorderStyle.THIN);
        styleColumnHeader.setBorderRight(BorderStyle.THIN);
        styleColumnHeader.setFillForegroundColor(new XSSFColor(Color.decode("#795548")));
        styleColumnHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        //
        XSSFCellStyle styleCellDate = workbook.createCellStyle();
        styleCellDate.setFont(fontCellDate);
        styleCellDate.setAlignment(HorizontalAlignment.CENTER);
        styleCellDate.setVerticalAlignment(VerticalAlignment.CENTER);
        styleCellDate.setBorderTop(BorderStyle.THIN);
        styleCellDate.setBorderLeft(BorderStyle.THIN);
        styleCellDate.setBorderBottom(BorderStyle.THIN);
        styleCellDate.setBorderRight(BorderStyle.THIN);
        styleCellDate.setFillForegroundColor(new XSSFColor(Color.WHITE));
        styleCellDate.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        //
        Cell cell = row.createCell(0);
        cell.setCellValue("التسلسل");
        cell.setCellType(CellType.STRING);
        cell.setCellStyle(styleColumnHeader);
        sheet.setColumnWidth(0, 20 * 256);
        //
        cell = row.createCell(1);
        cell.setCellValue("رقم الدورة");
        cell.setCellType(CellType.STRING);
        cell.setCellStyle(styleColumnHeader);
        sheet.setColumnWidth(1, 20 * 256);
        //
        cell = row.createCell(2);
        cell.setCellValue("رقم التخصص");
        cell.setCellType(CellType.STRING);
        cell.setCellStyle(styleColumnHeader);
        sheet.setColumnWidth(2, 20 * 256);
        //
        cell = row.createCell(3);
        cell.setCellValue("رقم السند");
        cell.setCellType(CellType.STRING);
        cell.setCellStyle(styleColumnHeader);
        sheet.setColumnWidth(3, 20 * 256);
        //
        cell = row.createCell(4);
        cell.setCellValue("نوع الايرادات");
        cell.setCellType(CellType.STRING);
        cell.setCellStyle(styleColumnHeader);
        sheet.setColumnWidth(4, 20 * 256);
        //
        cell = row.createCell(5);
        cell.setCellValue("قيمة السند");
        cell.setCellType(CellType.STRING);
        cell.setCellStyle(styleColumnHeader);
        sheet.setColumnWidth(5, 20 * 256);
        //
        cell = row.createCell(6);
        cell.setCellValue("البيان");
        cell.setCellType(CellType.STRING);
        cell.setCellStyle(styleColumnHeader);
        sheet.setColumnWidth(6, 20 * 256);
        //
        cell = row.createCell(7);
        cell.setCellValue("يوم تاريخ السند");
        cell.setCellType(CellType.STRING);
        cell.setCellStyle(styleColumnHeader);
        sheet.setColumnWidth(7, 20 * 256);
        //
        cell = row.createCell(8);
        cell.setCellValue("شهر تاريخ السند");
        cell.setCellType(CellType.STRING);
        cell.setCellStyle(styleColumnHeader);
        sheet.setColumnWidth(8, 20 * 256);
        //
        cell = row.createCell(9);
        cell.setCellValue("سنة تاريخ السند");
        cell.setCellType(CellType.STRING);
        cell.setCellStyle(styleColumnHeader);
        sheet.setColumnWidth(9, 20 * 256);
        //
        for (int i = 1; i <= rowCount; i++) {
            row = sheet.createRow(i);
            row.setHeightInPoints((short) 25);
            //
            for (int j = 0; j <= 9; j++) {
                cell = row.createCell(j);
                cell.setCellType(CellType.STRING);
                cell.setCellValue("---");
                cell.setCellStyle(styleCellDate);
            }
        }
        //
        XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(sheet);
        XSSFDataValidationConstraint dvConstraint = (XSSFDataValidationConstraint) dvHelper.createExplicitListConstraint(new String[]{"ايرادات اساسية", "ايرادات اضافية"});
        CellRangeAddressList addressList = new CellRangeAddressList(1, 9, 4, 4);
        XSSFDataValidation validation = (XSSFDataValidation) dvHelper.createValidation(dvConstraint, addressList);
        validation.setShowErrorBox(true);
        validation.setErrorStyle(DataValidation.ErrorStyle.STOP);
        validation.createErrorBox("العروض الذكية", "فضلاً اختر نوع المدفوعات (اساسية / اضافية) دون تعديل من القائمة.");
        sheet.addValidationData(validation);
        //
        try {
            response.setContentType("application/xlsx");
            response.setHeader("Content-Disposition", "attachment; filename=\"SmartManager-HeavyWork-Payment-Insert.xlsx\"");
            OutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            workbook.close();
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            log.info(e.getMessage());
        }

    }

    @RequestMapping(value = "/api/heavy-work/payment/read", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void readExcelFile(@RequestParam("file") MultipartFile multipartFile, Principal principal) {
        Person person = personService.findByEmail(principal.getName());
        try {
            List<Payment> paymentList = new ArrayList<>();
            //
            File tempFile = File.createTempFile(new BigInteger(130, random).toString(32), "." + FilenameUtils.getExtension(multipartFile.getOriginalFilename()));
            FileUtils.writeByteArrayToFile(tempFile, multipartFile.getBytes());
            FileInputStream fileInputStream = new FileInputStream(tempFile);
            //
            Workbook workbook = new XSSFWorkbook(fileInputStream);
            Sheet dataTypeSheet = workbook.getSheetAt(0);
            //
            Iterator<Row> iterator = dataTypeSheet.iterator();
            log.info("تجاهل الصف الأول من الجدول والذي يحتوي على رؤوس الأعمدة");
            if (iterator.hasNext()) iterator.next();
            while (iterator.hasNext()) {
                Row nextRow = iterator.next();
                Iterator<Cell> cellIterator = nextRow.cellIterator();
                Payment payment = new Payment();
                boolean accept = true;
                while (cellIterator.hasNext()) {
                    Cell nextCell = cellIterator.next();
                    int columnIndex = nextCell.getColumnIndex();
                    switch (columnIndex) {
                        case 0:
                            if (excelCellHelper.getCellValue(nextCell) == null) {
                                accept = false;
                            }
                            nextCell.setCellType(CellType.STRING);
                            try {
                                accountCode = Integer.parseInt((String) excelCellHelper.getCellValue(nextCell));
                            } catch (Exception ex) {
                                accept = false;
                                break;
                            }
                            log.info("Account Code: " + accountCode);
                            break;
                        case 1:
                            if (excelCellHelper.getCellValue(nextCell) == null) {
                                accept = false;
                            }
                            nextCell.setCellType(CellType.STRING);
                            try {
                                courseCode = Integer.parseInt((String) excelCellHelper.getCellValue(nextCell));
                            } catch (Exception ex) {
                                accept = false;
                                break;
                            }
                            log.info("Course Code: " + courseCode);
                            break;
                        case 2:
                            if (excelCellHelper.getCellValue(nextCell) == null) {
                                accept = false;
                            }
                            nextCell.setCellType(CellType.STRING);
                            try {
                                masterCode = Integer.parseInt((String) excelCellHelper.getCellValue(nextCell));
                            } catch (Exception ex) {
                                accept = false;
                                break;
                            }
                            log.info("Master Code: " + masterCode);
                            break;
                        case 3:
                            if (excelCellHelper.getCellValue(nextCell) == null) {
                                accept = false;
                            }
                            nextCell.setCellType(CellType.STRING);
                            try {
                                payment.setCode(Integer.parseInt((String) excelCellHelper.getCellValue(nextCell)));
                            } catch (Exception ex) {
                                accept = false;
                                break;
                            }
                            log.info("Payment Code: " + payment.getCode());
                            break;
                        case 4:
                            if (excelCellHelper.getCellValue(nextCell) == null) {
                                accept = false;
                            }
                            nextCell.setCellType(CellType.STRING);
                            try {
                                payment.setType((String) excelCellHelper.getCellValue(nextCell));
                            } catch (Exception ex) {
                                accept = false;
                                break;
                            }
                            log.info("Payment Note: " + payment.getNote());
                            break;
                        case 5:
                            if (excelCellHelper.getCellValue(nextCell) == null) {
                                accept = false;
                            }
                            nextCell.setCellType(CellType.STRING);
                            try {
                                payment.setAmountNumber(Double.parseDouble((String) excelCellHelper.getCellValue(nextCell)));
                                payment.setAmountString(ArabicLiteralNumberParser.literalValueOf(payment.getAmountNumber()));
                            } catch (Exception ex) {
                                accept = false;
                                break;
                            }
                            log.info("Payment Amount Number: " + payment.getAmountNumber());
                            log.info("Payment Amount String: " + payment.getAmountString());
                            break;
                        case 6:
                            if (excelCellHelper.getCellValue(nextCell) == null) {
                                accept = false;
                            }
                            nextCell.setCellType(CellType.STRING);
                            try {
                                payment.setNote((String) excelCellHelper.getCellValue(nextCell));
                            } catch (Exception ex) {
                                accept = false;
                                break;
                            }
                            log.info("Payment Note: " + payment.getNote());
                            break;
                        case 7:
                            if (excelCellHelper.getCellValue(nextCell) == null) {
                                accept = false;
                            }
                            nextCell.setCellType(CellType.STRING);
                            try {
                                day = Integer.parseInt((String) excelCellHelper.getCellValue(nextCell));
                            } catch (Exception ex) {
                                accept = false;
                                break;
                            }
                            log.info((String) excelCellHelper.getCellValue(nextCell));
                            break;
                        case 8:
                            if (excelCellHelper.getCellValue(nextCell) == null) {
                                accept = false;
                            }
                            nextCell.setCellType(CellType.STRING);
                            try {
                                month = Integer.parseInt((String) excelCellHelper.getCellValue(nextCell));
                            } catch (Exception ex) {
                                accept = false;
                                break;
                            }
                            log.info((String) excelCellHelper.getCellValue(nextCell));
                            break;
                        case 9:
                            if (excelCellHelper.getCellValue(nextCell) == null) {
                                accept = false;
                            }
                            nextCell.setCellType(CellType.STRING);
                            try {
                                year = Integer.parseInt((String) excelCellHelper.getCellValue(nextCell));
                            } catch (Exception ex) {
                                accept = false;
                                break;
                            }
                            log.info((String) excelCellHelper.getCellValue(nextCell));
                            break;
                    }

                }
                if (accept) {
                    try {
                        Account account = accountService.findByCodeAndCourseCodeAndCourseMasterCodeAndCourseMasterBranchCode(accountCode, courseCode, masterCode, person.getBranch().getCode());
                        if (account == null) {
                            continue;
                        }
                        if (paymentService.findByCodeAndAccountCourseMasterBranch(payment.getCode(), person.getBranch()) != null) {
                            continue;
                        }
                        log.info("تم إيجاد التسجيل بنجاح...");
                        payment.setAccount(account);
                        payment.setLastUpdate(DateConverter.getDateFromHijri(year, month, day));
                        payment.setLastPerson(person);
                        paymentList.add(payment);
                        paymentService.save(payment);
                    } catch (Exception ex) {
                        log.info(ex.getMessage());
                    }
                }
            }
            notificationService.notifyOne(Notification
                    .builder()
                    .title("العمليات على سندات القبض")
                    .message("تم اضافة عدد " + paymentList.size() + " من السندات بنجاح.")
                    .type("success")
                    .icon("fa-plus-circle")
                    .build(), principal.getName());
            workbook.close();
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
