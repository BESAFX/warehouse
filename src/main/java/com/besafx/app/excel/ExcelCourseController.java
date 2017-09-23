package com.besafx.app.excel;

import com.besafx.app.entity.Course;
import com.besafx.app.entity.Master;
import com.besafx.app.entity.Person;
import com.besafx.app.service.CourseService;
import com.besafx.app.service.MasterService;
import com.besafx.app.service.PersonService;
import com.besafx.app.util.DateConverter;
import com.besafx.app.ws.Notification;
import com.besafx.app.ws.NotificationService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.*;
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
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@RestController
public class ExcelCourseController {

    private final static Logger log = LoggerFactory.getLogger(ExcelCourseController.class);

    private SecureRandom random;

    @Autowired
    private MasterService masterService;

    @Autowired
    private CourseService courseService;

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

    @RequestMapping(value = "/api/heavy-work/course/write/{rowCount}", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void writeExcelFile(@PathVariable(value = "rowCount") Integer rowCount, HttpServletResponse response, Principal principal) {
        log.info("فحص المستخدم");
        Person person = personService.findByEmail(principal.getName());
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
        cell.setCellValue("رقم الدورة");
        cell.setCellType(CellType.STRING);
        cell.setCellStyle(styleColumnHeader);
        sheet.setColumnWidth(0, 20 * 256);
        //
        cell = row.createCell(1);
        cell.setCellValue("رقم التخصص");
        cell.setCellType(CellType.STRING);
        cell.setCellStyle(styleColumnHeader);
        sheet.setColumnWidth(1, 20 * 256);
        //
        cell = row.createCell(2);
        cell.setCellValue("رقم الفرع");
        cell.setCellType(CellType.STRING);
        cell.setCellStyle(styleColumnHeader);
        sheet.setColumnWidth(2, 20 * 256);
        //
        cell = row.createCell(3);
        cell.setCellValue("المدرب");
        cell.setCellType(CellType.STRING);
        cell.setCellStyle(styleColumnHeader);
        sheet.setColumnWidth(3, 20 * 256);
        //
        cell = row.createCell(4);
        cell.setCellValue("الشركة");
        cell.setCellType(CellType.STRING);
        cell.setCellStyle(styleColumnHeader);
        sheet.setColumnWidth(4, 20 * 256);
        //
        cell = row.createCell(5);
        cell.setCellValue("الحد الأقصى لعدد الطلاب");
        cell.setCellType(CellType.STRING);
        cell.setCellStyle(styleColumnHeader);
        sheet.setColumnWidth(5, 20 * 256);
        //
        cell = row.createCell(6);
        cell.setCellValue("التاريخ من");
        cell.setCellType(CellType.STRING);
        cell.setCellStyle(styleColumnHeader);
        sheet.setColumnWidth(6, 20 * 256);
        //
        cell = row.createCell(7);
        cell.setCellValue("التاريخ إلى");
        cell.setCellType(CellType.STRING);
        cell.setCellStyle(styleColumnHeader);
        sheet.setColumnWidth(7, 20 * 256);
        //
        for (int i = 1; i <= rowCount; i++) {
            row = sheet.createRow(i);
            row.setHeightInPoints((short) 25);
            //
            for (int j = 0; j <= 7; j++) {
                cell = row.createCell(j);
                cell.setCellType(CellType.STRING);
                cell.setCellValue("---");
                cell.setCellStyle(styleCellDate);
            }
        }
        try {
            response.setContentType("application/xlsx");
            response.setHeader("Content-Disposition", "attachment; filename=\"SmartManager-HeavyWork-Course-Insert.xlsx\"");
            OutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            workbook.close();
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            log.info(e.getMessage());
        }

    }

    @RequestMapping(value = "/api/heavy-work/course/read", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void readExcelFile(@RequestParam("file") MultipartFile multipartFile, Principal principal) {
        Person person = personService.findByEmail(principal.getName());
        try {
            List<Course> courseList = new ArrayList<>();
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
                Integer courseCode = null, masterCode = null, branchCode = null;
                Course course = new Course();
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
                                courseCode = Integer.parseInt((String) excelCellHelper.getCellValue(nextCell));
                            } catch (Exception ex) {
                                accept = false;
                                break;
                            }
                            break;
                        case 1:
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
                            break;
                        case 2:
                            if (excelCellHelper.getCellValue(nextCell) == null) {
                                accept = false;
                            }
                            nextCell.setCellType(CellType.STRING);
                            try {
                                branchCode = Integer.parseInt((String) excelCellHelper.getCellValue(nextCell));
                            } catch (Exception ex) {
                                accept = false;
                                break;
                            }
                            break;
                        case 3:
                            if (excelCellHelper.getCellValue(nextCell) == null) {
                                accept = false;
                            }
                            nextCell.setCellType(CellType.STRING);
                            course.setInstructor((String) excelCellHelper.getCellValue(nextCell));
                            break;
                        case 4:
                            if (excelCellHelper.getCellValue(nextCell) == null) {
                                accept = false;
                            }
                            nextCell.setCellType(CellType.STRING);
                            course.setCompanyName((String) excelCellHelper.getCellValue(nextCell));
                            break;
                        case 5:
                            if (excelCellHelper.getCellValue(nextCell) == null) {
                                accept = false;
                            }
                            nextCell.setCellType(CellType.STRING);
                            Integer masterStudentCount;
                            try {
                                masterStudentCount = Integer.parseInt((String) excelCellHelper.getCellValue(nextCell));
                            } catch (Exception ex) {
                                accept = false;
                                break;
                            }
                            course.setMaxStudentCount(masterStudentCount);
                            break;
                        case 6:
                            if (excelCellHelper.getCellValue(nextCell) == null) {
                                accept = false;
                            }
                            nextCell.setCellType(CellType.STRING);
                            Date startDate;
                            try {
                                startDate = DateConverter.parseHijriDateStringWithFormat((String) excelCellHelper.getCellValue(nextCell), "dd/MM/yyyy");
                            } catch (Exception ex) {
                                accept = false;
                                break;
                            }
                            course.setStartDate(startDate);
                            break;
                        case 7:
                            if (excelCellHelper.getCellValue(nextCell) == null) {
                                accept = false;
                            }
                            nextCell.setCellType(CellType.STRING);
                            Date endDate;
                            try {
                                endDate = DateConverter.parseHijriDateStringWithFormat((String) excelCellHelper.getCellValue(nextCell), "dd/MM/yyyy");
                            } catch (Exception ex) {
                                accept = false;
                                break;
                            }
                            course.setEndDate(endDate);
                            break;
                    }

                }
                if (accept) {
                    try {
                        log.info("التأكد من وجود التخصص بالنسبة الى الفرع");
                        Master master = masterService.findByCodeAndBranchCode(masterCode, branchCode);
                        if (master == null) {
                            log.info("تجاهل هذا الصف لعدم وجود التخصص المطلوب");
                            continue;
                        }
                        if (courseService.findByCodeAndMaster(courseCode, master) != null) {
                            log.info("تجاهل هذا الصف لتكرار الكود بالنسبة الي التخصص");
                            continue;
                        }
                        course.setCode(courseCode);
                        course.setMaster(master);
                        course.setLastUpdate(new Date());
                        course.setLastPerson(person);
                        courseList.add(course);
                        courseService.save(course);
                    } catch (Exception ex) {
                        log.info(ex.getMessage());
                    }
                }
            }
            notificationService.notifyOne(Notification
                    .builder()
                    .title("العمليات على الدورات")
                    .message("تم اضافة عدد " + courseList.size() + " من الدورات بنجاح.")
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
