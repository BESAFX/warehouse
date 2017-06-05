package com.besafx.app.excel;
import com.besafx.app.entity.Master;
import com.besafx.app.entity.Offer;
import com.besafx.app.entity.Person;
import com.besafx.app.service.MasterService;
import com.besafx.app.service.OfferService;
import com.besafx.app.service.PersonService;
import com.besafx.app.ws.Notification;
import com.besafx.app.ws.NotificationService;
import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ExcelOfferController {

    private final static Logger log = LoggerFactory.getLogger(ExcelOfferController.class);

    private SecureRandom random;

    @Autowired
    private MasterService masterService;

    @Autowired
    private OfferService offerService;

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

    @RequestMapping(value = "/api/heavy-work/offer/write", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void writeBranchFile(HttpServletResponse response, Principal principal) {
        log.info("فحص المستخدم");
        Person person = personService.findByEmail(principal.getName());
        List<Master> masters = masterService.findByBranch(person.getBranch());
        masters.sort((m1, m2) -> m1.getCode().compareTo(m2.getCode()));
        //
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(person.getContact().getFirstName() + " " + person.getContact().getForthName() + " - " + " فرع " + person.getBranch().getName());
        Row row = sheet.createRow(0);
        row.setHeightInPoints((short) 25);
        //
        CreationHelper createHelper = workbook.getCreationHelper();
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
        XSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd"));
        cellStyle.setFont(fontCellDate);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setFillForegroundColor(new XSSFColor(Color.WHITE));
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        //
        Cell cell = row.createCell(0);
        cell.setCellValue("اسم الطالب");
        cell.setCellType(CellType.STRING);
        cell.setCellStyle(styleColumnHeader);
        sheet.setColumnWidth(0, 20 * 256);
        //
        cell = row.createCell(1);
        cell.setCellValue("رقم البطاقة / الإقامة");
        cell.setCellType(CellType.STRING);
        cell.setCellStyle(styleColumnHeader);
        sheet.setColumnWidth(1, 20 * 256);
        //
        cell = row.createCell(2);
        cell.setCellValue("رقم الجوال");
        cell.setCellType(CellType.STRING);
        cell.setCellStyle(styleColumnHeader);
        sheet.setColumnWidth(2, 20 * 256);
        //
        cell = row.createCell(3);
        cell.setCellValue("الحالة");
        cell.setCellType(CellType.STRING);
        cell.setCellStyle(styleColumnHeader);
        sheet.setColumnWidth(3, 20 * 256);
        //
        cell = row.createCell(4);
        cell.setCellValue("ملاحظات");
        cell.setCellType(CellType.STRING);
        cell.setCellStyle(styleColumnHeader);
        sheet.setColumnWidth(4, 20 * 256);
        //
        cell = row.createCell(5);
        cell.setCellValue("طريقة الدفع");
        cell.setCellType(CellType.STRING);
        cell.setCellStyle(styleColumnHeader);
        sheet.setColumnWidth(5, 20 * 256);
        //
        cell = row.createCell(6);
        cell.setCellValue("المبلغ نقداً");
        cell.setCellType(CellType.STRING);
        cell.setCellStyle(styleColumnHeader);
        sheet.setColumnWidth(6, 20 * 256);
        //
        cell = row.createCell(7);
        cell.setCellValue("رقم فهرس التخصص");
        cell.setCellType(CellType.STRING);
        cell.setCellStyle(styleColumnHeader);
        sheet.setColumnWidth(7, 20 * 256);
        //
        cell = row.createCell(8);
        cell.setCellValue("تاريخ العرض");
        cell.setCellType(CellType.STRING);
        cell.setCellStyle(styleColumnHeader);
        sheet.setColumnWidth(8, 20 * 256);
        //
        for (int i = 1; i <= 8; i++) {
            row = sheet.createRow(i);
            row.setHeightInPoints((short) 25);
            //
            for (int j = 0; j <= 8; j++) {
                cell = row.createCell(j);
                if (j == 8) {
                    cell.setCellType(CellType.NUMERIC);
                    cell.setCellValue(new Date());
                    cell.setCellStyle(cellStyle);
                    continue;
                }
                cell.setCellType(CellType.STRING);
                cell.setCellValue("---");
                cell.setCellStyle(styleCellDate);
            }
        }
        //
        XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(sheet);
        XSSFDataValidationConstraint dvConstraint = (XSSFDataValidationConstraint) dvHelper.createExplicitListConstraint(Lists.newArrayList(masters).stream().map(company -> company.getCode().toString()).collect(Collectors.toList()).stream().toArray(String[]::new));
        CellRangeAddressList addressList = new CellRangeAddressList(1, 8, 7, 7);
        XSSFDataValidation validation = (XSSFDataValidation) dvHelper.createValidation(dvConstraint, addressList);
        validation.setShowErrorBox(true);
        validation.setErrorStyle(DataValidation.ErrorStyle.STOP);
        validation.createErrorBox("العروض الذكية", "فضلاً اختر رقم فهرس التخصص دون تعديل من القائمة.");
        sheet.addValidationData(validation);
        //
        dvHelper = new XSSFDataValidationHelper(sheet);
        dvConstraint = (XSSFDataValidationConstraint) dvHelper.createExplicitListConstraint(new String[]{"مسجل", "غير مسجل"});
        addressList = new CellRangeAddressList(1, 8, 3, 3);
        validation = (XSSFDataValidation) dvHelper.createValidation(dvConstraint, addressList);
        validation.setShowErrorBox(true);
        validation.setErrorStyle(DataValidation.ErrorStyle.STOP);
        validation.createErrorBox("العروض الذكية", "فضلاً اختر الحالة دون تعديل من القائمة.");
        sheet.addValidationData(validation);
        //
        dvHelper = new XSSFDataValidationHelper(sheet);
        dvConstraint = (XSSFDataValidationConstraint) dvHelper.createExplicitListConstraint(new String[]{"نقدي", "قسط شهري"});
        addressList = new CellRangeAddressList(1, 8, 5, 5);
        validation = (XSSFDataValidation) dvHelper.createValidation(dvConstraint, addressList);
        validation.setShowErrorBox(true);
        validation.setErrorStyle(DataValidation.ErrorStyle.STOP);
        validation.createErrorBox("العروض الذكية", "فضلاً اختر طريقة الدفع دون تعديل من القائمة.");
        sheet.addValidationData(validation);
        //
        try {
            response.setContentType("application/xlsx");
            response.setHeader("Content-Disposition", "attachment; filename=\"SmartManager-HeavyWork-Offer-Insert.xlsx\"");
            OutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            workbook.close();
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            log.info(e.getMessage());
        }

    }

    @RequestMapping(value = "/api/heavy-work/offer/read", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void readBranchFile(@RequestParam("file") MultipartFile multipartFile, Principal principal) {
        Person person = personService.findByEmail(principal.getName());
        try {
            List<Offer> offerList = new ArrayList<>();
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
                Offer offer = new Offer();
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
                            offer.setCustomerName((String) excelCellHelper.getCellValue(nextCell));
                            log.info((String) excelCellHelper.getCellValue(nextCell));
                            break;
                        case 1:
                            if (excelCellHelper.getCellValue(nextCell) == null) {
                                accept = false;
                            }
                            nextCell.setCellType(CellType.STRING);
                            offer.setCustomerIdentityNumber((String) excelCellHelper.getCellValue(nextCell));
                            log.info((String) excelCellHelper.getCellValue(nextCell));
                            break;
                        case 2:
                            if (excelCellHelper.getCellValue(nextCell) == null) {
                                accept = false;
                            }
                            nextCell.setCellType(CellType.STRING);
                            offer.setCustomerMobile((String) excelCellHelper.getCellValue(nextCell));
                            log.info((String) excelCellHelper.getCellValue(nextCell));
                            break;
                        case 3:
                            if (excelCellHelper.getCellValue(nextCell) == null) {
                                accept = false;
                            }
                            nextCell.setCellType(CellType.STRING);
                            offer.setRegistered(excelCellHelper.getCellValue(nextCell).equals("مسجل") ? true : false);
                            log.info((String) excelCellHelper.getCellValue(nextCell));
                            break;
                        case 4:
                            if (excelCellHelper.getCellValue(nextCell) == null) {
                                accept = false;
                            }
                            nextCell.setCellType(CellType.STRING);
                            offer.setNote((String) excelCellHelper.getCellValue(nextCell));
                            log.info((String) excelCellHelper.getCellValue(nextCell));
                            break;
                        case 5:
                            if (excelCellHelper.getCellValue(nextCell) == null) {
                                accept = false;
                            }
                            nextCell.setCellType(CellType.STRING);
                            offer.setMasterPaymentType((String) excelCellHelper.getCellValue(nextCell));
                            log.info((String) excelCellHelper.getCellValue(nextCell));
                            break;
                        case 6:
                            if (excelCellHelper.getCellValue(nextCell) == null) {
                                accept = false;
                            }
                            nextCell.setCellType(CellType.STRING);
                            Double masterPrice;
                            try {
                                masterPrice = Double.parseDouble((String) excelCellHelper.getCellValue(nextCell));
                            } catch (Exception ex) {
                                accept = false;
                                break;
                            }
                            offer.setMasterPrice(masterPrice);
                            log.info((String) excelCellHelper.getCellValue(nextCell));
                            break;
                        case 7:
                            if (excelCellHelper.getCellValue(nextCell) == null) {
                                accept = false;
                                break;
                            }
                            nextCell.setCellType(CellType.STRING);
                            Integer masterCode;
                            try {
                                masterCode = Integer.parseInt((String) excelCellHelper.getCellValue(nextCell));
                            } catch (Exception ex) {
                                accept = false;
                                break;
                            }
                            Master master = masterService.findByCodeAndBranch(masterCode, person.getBranch());
                            if (master == null) {
                                accept = false;
                                break;
                            }
                            offer.setMaster(master);
                            log.info((String) excelCellHelper.getCellValue(nextCell));
                            break;
                        case 8:
                            if (excelCellHelper.getCellValue(nextCell) == null) {
                                accept = false;
                            }
                            nextCell.setCellType(CellType.NUMERIC);
                            Date date;
                            try {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                date = dateFormat.parse((String) excelCellHelper.getCellValue(nextCell));
                            } catch (Exception ex) {
                                log.error(ex.getMessage(), ex);
                                accept = false;
                                break;
                            }
                            offer.setLastUpdate(date);
                            log.info(date.toString());
                            break;
                    }

                }
                if (accept) {
                    Offer topOffer = offerService.findTopByMasterBranchOrderByCodeDesc(offer.getMaster().getBranch());
                    if (topOffer == null) {
                        offer.setCode(1);
                    } else {
                        offer.setCode(topOffer.getCode() + 1);
                    }
                    offer.setLastPerson(person);
                    offer.setMasterCreditAmount(0.0);
                    offer.setMasterDiscountAmount(0.0);
                    offer.setMasterProfitAmount(0.0);
                    offerList.add(offer);
                }
            }
            offerService.save(offerList);
            notificationService.notifyOne(Notification
                    .builder()
                    .title("العمليات على العروض")
                    .message("تم اضافة عدد " + offerList.size() + " من العروض بنجاح.")
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
