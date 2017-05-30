package com.besafx.app.controller;

import com.besafx.app.entity.Account;
import com.besafx.app.entity.Bank;
import com.besafx.app.entity.BillBuyType;
import com.besafx.app.entity.Person;
import com.besafx.app.search.BillBuySearch;
import com.besafx.app.search.DepositSearch;
import com.besafx.app.search.PaymentSearch;
import com.besafx.app.search.WithdrawSearch;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.*;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.oasis.JROdsExporter;
import net.sf.jasperreports.engine.export.oasis.JROdtExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRPptxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.type.*;
import net.sf.jasperreports.export.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ReportDynamicController {

    @Autowired
    private BillBuySearch billBuySearch;

    @Autowired
    private DepositSearch depositSearch;

    @Autowired
    private WithdrawSearch withdrawSearch;

    @Autowired
    private PaymentSearch paymentSearch;

    private JasperDesign jasperDesign = new JasperDesign();

    private JRDesignStyle style1 = new JRDesignStyle();

    private JRDesignStyle style2 = new JRDesignStyle();

    private JRDesignBand band = new JRDesignBand();

    private JRDesignStaticText staticText = new JRDesignStaticText();

    private JRDesignImage image = new JRDesignImage(jasperDesign);

    private JRDesignFrame frame = new JRDesignFrame();

    private JRDesignField field = new JRDesignField();

    private JRDesignTextField textField = new JRDesignTextField();

    private JasperDesign getJasperDesign(String reportTitle, String orientation, List<ColumnMap> columns, List<Variable> groupVariablesList, List<Variable> tableVariablesList) throws JRException {

        jasperDesign = new JasperDesign();
        jasperDesign.setName("NoXmlDesignReport");
        jasperDesign.setOrientation(OrientationEnum.getByName(orientation));
        jasperDesign.setPageWidth(orientation.equals("Portrait") ? 595 : 842);
        jasperDesign.setPageHeight(orientation.equals("Portrait") ? 842 : 595);
        jasperDesign.setColumnSpacing(0);
        jasperDesign.setLeftMargin(20);
        jasperDesign.setRightMargin(20);
        jasperDesign.setTopMargin(20);
        jasperDesign.setBottomMargin(20);
        jasperDesign.setColumnWidth(jasperDesign.getPageWidth() - jasperDesign.getLeftMargin() - jasperDesign.getRightMargin());
        jasperDesign.setWhenNoDataType(WhenNoDataTypeEnum.ALL_SECTIONS_NO_DETAIL);

        createStyle();

        createPageHeader(reportTitle);

        createBackground();

        createPageFooter();

        createTable(columns, groupVariablesList, tableVariablesList);

        return jasperDesign;
    }

    private void createTable(List<ColumnMap> list, List<Variable> groupVariablesList, List<Variable> tableVariablesList) throws JRException {
        //Column Header
        JRDesignBand columnHeader = new JRDesignBand();
        columnHeader.setHeight(15);

        //Detail
        JRDesignBand detail = new JRDesignBand();
        detail.setHeight(15);

        Iterator<ColumnMap> iterator = list.iterator();

        List<ColumnMap> activeList = list.stream().filter(col -> col.isView()).collect(Collectors.toList());

        while (iterator.hasNext()) {

            ColumnMap columnMap = iterator.next();

            if (!jasperDesign.getFieldsList().stream().filter(field -> field.getName().equals(columnMap.getColumnValue())).findAny().isPresent()) {
                field = new JRDesignField();
                field.setName(columnMap.getColumnValue());
                field.setValueClassName(columnMap.getColumnValueClassName());
                jasperDesign.addField(field);
            }

            if (columnMap.isView()) {

                staticText = new JRDesignStaticText();
                staticText.setWidth(jasperDesign.getColumnWidth() / activeList.size());
                staticText.setHeight(15);
                staticText.setX(((jasperDesign.getColumnWidth() / activeList.size()) * (activeList.indexOf(columnMap) + 1)) - (jasperDesign.getColumnWidth() / activeList.size()));
                staticText.setY(0);
                staticText.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
                staticText.setStyle(style2);
                staticText.getLineBox().getPen().setLineColor(Color.BLACK);
                staticText.getLineBox().getPen().setLineWidth(0.2f);
                staticText.getLineBox().setPadding(1);
                staticText.setMode(ModeEnum.OPAQUE);
                staticText.setBackcolor(Color.LIGHT_GRAY);
                staticText.setText(columnMap.getColumnName());
                columnHeader.addElement(staticText);

                textField = new JRDesignTextField();
                textField.setWidth(jasperDesign.getColumnWidth() / activeList.size());
                textField.setHeight(15);
                textField.setX(((jasperDesign.getColumnWidth() / activeList.size()) * (activeList.indexOf(columnMap) + 1)) - (jasperDesign.getColumnWidth() / activeList.size()));
                textField.setY(0);
                textField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
                textField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
                textField.setStyle(style2);
                textField.getLineBox().getPen().setLineColor(Color.BLACK);
                textField.getLineBox().getPen().setLineWidth(0.2f);
                textField.getLineBox().setPadding(1);
                textField.setExpression(new JRDesignExpression(columnMap.getColumnExpression()));
                textField.setStretchWithOverflow(true);
                textField.setBlankWhenNull(true);
                textField.setStretchType(StretchTypeEnum.ELEMENT_GROUP_HEIGHT);

                detail.addElement(textField);
            }

            if (columnMap.isGroupBy()) {
                createGroupByBand(columnMap, groupVariablesList);
            }

            if (columnMap.isSortBy()) {
                jasperDesign.getSortFieldsList().add(createSortField(columnMap, SortOrderEnum.DESCENDING));
            }

        }


        jasperDesign.setColumnHeader(columnHeader);

        ((JRDesignSection) jasperDesign.getDetailSection()).addBand(detail);

        //START COLUMN FOOTER
        frame = new JRDesignFrame();
        frame.setWidth(jasperDesign.getColumnWidth());
        frame.setHeight(15);
        frame.getLineBox().getBottomPen().setLineColor(Color.BLACK);
        frame.getLineBox().getBottomPen().setLineWidth(1);
        frame.getLineBox().setPadding(0);

        //Create Variables that will depends on this table
        ListIterator<Variable> listIterator = tableVariablesList.listIterator();
        while (listIterator.hasNext()) {
            Variable variable = listIterator.next();
            JRDesignVariable jrDesignVariable = new JRDesignVariable();
            jrDesignVariable.setName(variable.getExpression().toUpperCase() + "_" + variable.getOperation().toUpperCase() + "_ALL");
            jrDesignVariable.setValueClassName(Double.class.getName());
            jrDesignVariable.setCalculation(CalculationEnum.getByName(variable.getOperation()));
            jrDesignVariable.setExpression(new JRDesignExpression("$F{" + variable.getExpression() + "}"));
            jrDesignVariable.setIncrementType(IncrementTypeEnum.NONE);
            jrDesignVariable.setResetType(ResetTypeEnum.REPORT);
            jasperDesign.addVariable(jrDesignVariable);

            textField = new JRDesignTextField();
            textField.setWidth(jasperDesign.getColumnWidth() / tableVariablesList.size());
            textField.setHeight(15);
            textField.setX(((jasperDesign.getColumnWidth() / tableVariablesList.size()) * (tableVariablesList.indexOf(variable) + 1)) - (jasperDesign.getColumnWidth() / tableVariablesList.size()));
            textField.setY(0);
            textField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
            textField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
            textField.setStyle(style1);
            textField.setBackcolor(Color.CYAN);
            textField.setMode(ModeEnum.OPAQUE);
            textField.getLineBox().getPen().setLineColor(Color.BLACK);
            textField.getLineBox().getPen().setLineWidth(0.2f);
            textField.getLineBox().setPadding(1);
            textField.setExpression(new JRDesignExpression("\" " + variable.getName() + " : \" " + "+" + "new java.text.DecimalFormat(\"#.##\").format($V{" + variable.getExpression().toUpperCase() + "_" + variable.getOperation().toUpperCase() + "_ALL" + "})"));
            frame.addElement(textField);
        }

//        JRDesignVariable variable = new JRDesignVariable();
//        variable.setName("AMOUNT_SUM_ALL");
//        variable.setValueClassName(Double.class.getName());
//        variable.setCalculation(CalculationEnum.SUM);
//        variable.setExpression(new JRDesignExpression("$F{amount}"));
//        variable.setIncrementType(IncrementTypeEnum.NONE);
//        variable.setResetType(ResetTypeEnum.REPORT);
//        jasperDesign.addVariable(variable);


        JRDesignBand columnFooter = new JRDesignBand();
        columnFooter.setHeight(15);
        columnFooter.addElement(frame);
        //END COLUMN FOOTER

        jasperDesign.setColumnFooter(columnFooter);
    }

    private void createPageFooter() {
        band = new JRDesignBand();
        band.setHeight(60);

        frame = new JRDesignFrame();
        frame.setWidth(jasperDesign.getColumnWidth());
        frame.setHeight(band.getHeight());
        frame.getLineBox().getTopPen().setLineColor(Color.BLACK);
        frame.getLineBox().getTopPen().setLineWidth(1);
        frame.getLineBox().setPadding(0);

        band.addElement(frame);

        jasperDesign.setPageFooter(band);
    }

    private void createBackground() {
        band = new JRDesignBand();
        band.setHeight(jasperDesign.getPageHeight() - jasperDesign.getBottomMargin() - jasperDesign.getTopMargin());

        JRDesignRectangle rectangle = new JRDesignRectangle();
        rectangle.setWidth(jasperDesign.getColumnWidth());
        rectangle.setHeight(band.getHeight());
        rectangle.setMode(ModeEnum.TRANSPARENT);
        rectangle.setRadius(10);
        rectangle.setX(0);
        rectangle.getLinePen().setLineWidth(2);
        rectangle.getLinePen().setLineColor(Color.BLACK);
        rectangle.getLinePen().setLineStyle(LineStyleEnum.SOLID);
        band.addElement(rectangle);

        jasperDesign.setBackground(band);
    }

    private void createPageHeader(String reportTitle) {
        band = new JRDesignBand();
        band.setHeight(60);

        frame = new JRDesignFrame();
        frame.setWidth(jasperDesign.getColumnWidth());
        frame.setHeight(band.getHeight());
        frame.getLineBox().getBottomPen().setLineColor(Color.BLACK);
        frame.getLineBox().getBottomPen().setLineWidth(1);
        frame.getLineBox().setPadding(0);

        StringBuilder builder = new StringBuilder();
        builder.append("المملكة العربية السعودية");
        builder.append("\n");
        builder.append("المعهد الأهلي العالي للتدريب");
        builder.append("\n");
        builder.append("تحت إشراف المؤسسة العامة للتدريب المهني والتقني");

        staticText = new JRDesignStaticText();
        staticText.setWidth(jasperDesign.getColumnWidth() / 2);
        staticText.setHeight(45);
        staticText.setX((jasperDesign.getColumnWidth() - staticText.getWidth()) / 2);
        staticText.setY(0);
        staticText.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
        staticText.setStyle(style1);
        staticText.setText(builder.toString());
        frame.addElement(staticText);

        staticText = new JRDesignStaticText();
        staticText.setWidth(jasperDesign.getColumnWidth());
        staticText.setHeight(15);
        staticText.setX(0);
        staticText.setY(45);
        staticText.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
        staticText.setStyle(style2);
        staticText.setText(reportTitle);
        frame.addElement(staticText);

        image = new JRDesignImage(jasperDesign);
        JRDesignExpression expression = new JRDesignExpression();
        expression.setText("\"https://www.dropbox.com/s/bwbavw793i1hejf/LOGO.bmp?raw=1\"");
        image.setExpression(expression);
        image.setHorizontalImageAlign(HorizontalImageAlignEnum.CENTER);
        image.setVerticalImageAlign(VerticalImageAlignEnum.MIDDLE);
        image.setUsingCache(true);
        image.setFill(FillEnum.SOLID);
        image.setScaleImage(ScaleImageEnum.FILL_FRAME);
        image.setLazy(true);
        image.setHeight(35);
        image.setWidth(70);
        image.setX(5);
        image.setY(5);
        frame.addElement(image);

        band.addElement(frame);

        jasperDesign.setPageHeader(band);
    }

    private void createStyle() throws JRException {
        style1 = new JRDesignStyle();
        style1.setName("Style1");
        style1.setDefault(true);
        style1.setFontName("Arial");
        style1.setFontSize(11f);
        style1.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
        style1.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
        style1.setRotation(RotationEnum.NONE);
        style1.setBlankWhenNull(true);
        jasperDesign.addStyle(style1);

        style2 = new JRDesignStyle();
        style2.setName("Style2");
        style2.setDefault(true);
        style2.setFontName("Arial");
        style2.setFontSize(10f);
        style2.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
        style2.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
        style2.setRotation(RotationEnum.NONE);
        style2.setBlankWhenNull(true);
        jasperDesign.addStyle(style2);
    }

    private JRDesignGroup createGroup(JRDesignBand header, JRDesignBand footer, List<Variable> variables, String expression) throws JRException {

        //Create Group
        JRDesignGroup group = new JRDesignGroup();

        //Create Variables that will depends on this group
        ListIterator<Variable> listIterator = variables.listIterator();
        while (listIterator.hasNext()) {
            Variable variable = listIterator.next();
            JRDesignVariable jrDesignVariable = new JRDesignVariable();
            jrDesignVariable.setName(variable.getExpression().toUpperCase() + "_" + variable.getOperation().toUpperCase());
            jrDesignVariable.setValueClassName(Double.class.getName());
            jrDesignVariable.setCalculation(CalculationEnum.getByName(variable.getOperation()));
            jrDesignVariable.setExpression(new JRDesignExpression("$F{" + variable.getExpression() + "}"));
            jrDesignVariable.setIncrementType(IncrementTypeEnum.NONE);
            jrDesignVariable.setResetType(ResetTypeEnum.GROUP);
            jrDesignVariable.setResetGroup(group);
            jasperDesign.addVariable(jrDesignVariable);
        }

        group.setName(expression);
        group.setMinHeightToStartNewPage(60);
        group.setExpression(new JRDesignExpression(expression));

        Optional.ofNullable(header).ifPresent(value -> ((JRDesignSection) group.getGroupHeaderSection()).addBand(value));
        Optional.ofNullable(footer).ifPresent(value -> ((JRDesignSection) group.getGroupFooterSection()).addBand(value));

        return group;
    }

    private void createGroupByBand(ColumnMap columnMap, List<Variable> variables) throws JRException {

        //START HEADER
        frame = new JRDesignFrame();
        frame.setWidth(jasperDesign.getColumnWidth());
        frame.setHeight(15);
        frame.getLineBox().getBottomPen().setLineColor(Color.BLACK);
        frame.getLineBox().getBottomPen().setLineWidth(1);
        frame.getLineBox().setPadding(0);

        textField = new JRDesignTextField();
        textField.setWidth(jasperDesign.getColumnWidth());
        textField.setHeight(15);
        textField.setX(0);
        textField.setY(0);
        textField.setHorizontalTextAlign(HorizontalTextAlignEnum.RIGHT);
        textField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
        textField.setStyle(style2);
        textField.getLineBox().getPen().setLineColor(Color.BLACK);
        textField.getLineBox().getPen().setLineWidth(0.2f);
        textField.getLineBox().setPadding(1);
        textField.setExpression(new JRDesignExpression("\" " + columnMap.getColumnName() + " : \" " + "+" + columnMap.getColumnExpression()));
        frame.addElement(textField);

        JRDesignBand header = new JRDesignBand();
        header.setHeight(15);
        header.addElement(frame);
        //END HEADER

        //START FOOTER
        frame = new JRDesignFrame();
        frame.setWidth(jasperDesign.getColumnWidth());
        frame.setHeight(15);
        frame.getLineBox().getBottomPen().setLineColor(Color.BLACK);
        frame.getLineBox().getBottomPen().setLineWidth(1);
        frame.getLineBox().setPadding(0);

        ListIterator<Variable> listIterator = variables.listIterator();
        while (listIterator.hasNext()) {
            Variable variable = listIterator.next();
            textField = new JRDesignTextField();
            textField.setWidth(jasperDesign.getColumnWidth() / variables.size());
            textField.setHeight(15);
            textField.setX(((jasperDesign.getColumnWidth() / variables.size()) * (variables.indexOf(variable) + 1)) - (jasperDesign.getColumnWidth() / variables.size()));
            textField.setY(0);
            textField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
            textField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
            textField.setStyle(style1);
            textField.setBackcolor(Color.LIGHT_GRAY);
            textField.setMode(ModeEnum.OPAQUE);
            textField.getLineBox().getPen().setLineColor(Color.BLACK);
            textField.getLineBox().getPen().setLineWidth(0.2f);
            textField.getLineBox().setPadding(1);
            textField.setExpression(new JRDesignExpression("\" " + variable.getName() + " : \" " + "+" + "new java.text.DecimalFormat(\"#.##\").format($V{" + variable.getExpression().toUpperCase() + "_" + variable.getOperation().toUpperCase() + "})"));
            frame.addElement(textField);
        }

        JRDesignBand footer = new JRDesignBand();
        footer.setHeight(15);
        footer.addElement(frame);
        //END FOOTER

        JRDesignGroup group = createGroup(header, footer, variables, columnMap.getColumnExpression());

        jasperDesign.addGroup(group);

    }

    private JRDesignSortField createSortField(ColumnMap columnMap, SortOrderEnum order) throws JRException {
        JRDesignVariable variable = new JRDesignVariable();
        variable.setName(columnMap.getColumnValue());
        variable.setValueClassName(columnMap.getColumnValueClassName());
        variable.setExpression(new JRDesignExpression(columnMap.getColumnExpression()));
        jasperDesign.getVariablesList().add(variable);

        JRDesignSortField sortField = new JRDesignSortField();
        sortField.setName(variable.getName());
        sortField.setType(SortFieldTypeEnum.VARIABLE);
        sortField.setOrder(order);
        return sortField;
    }

    @RequestMapping(value = "/report/dynamic/billBuy", method = RequestMethod.GET, produces = MediaType.ALL_VALUE)
    public void ReportBillBuy(
            @RequestParam(value = "exportType") final String exportType,
            @RequestParam(value = "reportTitle") final String reportTitle,
            @RequestParam(value = "orientation") final String orientation,
            @RequestParam(value = "columns") final String columns,
            @RequestParam(value = "groupVariables") final String groupVariables,
            @RequestParam(value = "tableVariables") final String tableVariables,
            @RequestParam(value = "codeFrom", required = false) final String codeFrom,
            @RequestParam(value = "codeTo", required = false) final String codeTo,
            @RequestParam(value = "dateFrom", required = false) final Long dateFrom,
            @RequestParam(value = "dateTo", required = false) final Long dateTo,
            @RequestParam(value = "amountFrom", required = false) final Long amountFrom,
            @RequestParam(value = "amountTo", required = false) final Long amountTo,
            @RequestParam(value = "branchId", required = false) final Long branchId,
            HttpServletResponse response
    ) throws JRException, IOException {

        Gson gson = new Gson();

        TypeToken<List<Column>> token = new TypeToken<List<Column>>() {
        };
        List<Column> columnList = gson.fromJson(columns, token.getType());

        TypeToken<List<Variable>> tokenVariables = new TypeToken<List<Variable>>() {
        };
        List<Variable> groupVariablesList = gson.fromJson(groupVariables, tokenVariables.getType());
        List<Variable> tableVariablesList = gson.fromJson(tableVariables, tokenVariables.getType());

        List<ColumnMap> columnMapList = new ArrayList<>();
        columnList.stream().forEach(col -> {
            if (col.getName().equals("رقم الفاتورة")) {
                columnMapList.add(ColumnMap.builder()
                        .columnName(col.getName())
                        .view(col.isView())
                        .columnValue("code")
                        .columnExpression("$F{code}")
                        .columnValueClassName(String.class.getName())
                        .groupBy(col.isGroupBy())
                        .sortBy(col.isSortBy())
                        .build());
            }
            if (col.getName().equals("تاريخ الفاتورة")) {
                columnMapList.add(ColumnMap.builder()
                        .columnName(col.getName())
                        .view(col.isView())
                        .columnValue("date")
                        .columnExpression("com.besafx.app.util.DateConverter.getHijriStringFromDateRTL($F{date})")
                        .columnValueClassName(Date.class.getName())
                        .groupBy(col.isGroupBy())
                        .sortBy(col.isSortBy())
                        .build());
            }
            if (col.getName().equals("قيمة الفاتورة")) {
                columnMapList.add(ColumnMap.builder()
                        .columnName(col.getName())
                        .view(col.isView())
                        .columnValue("amount")
                        .columnExpression("$F{amount}")
                        .columnValueClassName(Double.class.getName())
                        .groupBy(col.isGroupBy())
                        .sortBy(col.isSortBy())
                        .build());
            }
            if (col.getName().equals("نوع الحساب")) {
                columnMapList.add(ColumnMap.builder()
                        .columnName(col.getName())
                        .view(col.isView())
                        .columnValue("billBuyType")
                        .columnExpression("$F{billBuyType}.getName()")
                        .columnValueClassName(BillBuyType.class.getName())
                        .groupBy(col.isGroupBy())
                        .sortBy(col.isSortBy()).build());
            }
            if (col.getName().equals("مصدر الفاتورة")) {
                columnMapList.add(ColumnMap.builder()
                        .columnName(col.getName())
                        .view(col.isView())
                        .columnValue("fromName")
                        .columnExpression("$F{fromName}")
                        .columnValueClassName(String.class.getName())
                        .groupBy(col.isGroupBy())
                        .sortBy(col.isSortBy())
                        .build());
            }
            if (col.getName().equals("مدخل الفاتورة")) {
                columnMapList.add(ColumnMap.builder()
                        .columnName(col.getName())
                        .view(col.isView())
                        .columnValue("lastPerson")
                        .columnExpression("$F{lastPerson}.getContact().getFirstName() + \" \" + $F{lastPerson}.getContact().getForthName()")
                        .columnValueClassName(Person.class.getName())
                        .groupBy(col.isGroupBy())
                        .sortBy(col.isSortBy())
                        .build());
            }
            if (col.getName().equals("بيان الفاتورة")) {
                columnMapList.add(ColumnMap.builder()
                        .columnName(col.getName())
                        .view(col.isView())
                        .columnValue("note")
                        .columnExpression("$F{note}")
                        .columnValueClassName(String.class.getName())
                        .groupBy(col.isGroupBy())
                        .sortBy(col.isSortBy())
                        .build());
            }
        });

        final OutputStream ouputStream = response.getOutputStream();
        JasperDesign jasperDesign = getJasperDesign(reportTitle, orientation, columnMapList, groupVariablesList, tableVariablesList);
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, new JRBeanCollectionDataSource(billBuySearch.search(codeFrom, codeTo, dateFrom, dateTo, amountFrom, amountTo, branchId)));

        export(exportType, response, ouputStream, jasperPrint);
    }

    @RequestMapping(value = "/report/dynamic/deposit", method = RequestMethod.GET, produces = MediaType.ALL_VALUE)
    public void ReportDeposit(
            @RequestParam(value = "exportType") final String exportType,
            @RequestParam(value = "reportTitle") final String reportTitle,
            @RequestParam(value = "orientation") final String orientation,
            @RequestParam(value = "columns") final String columns,
            @RequestParam(value = "groupVariables") final String groupVariables,
            @RequestParam(value = "tableVariables") final String tableVariables,

            @RequestParam(value = "code", required = false) final Long code,
            @RequestParam(value = "amountFrom", required = false) final Long amountFrom,
            @RequestParam(value = "amountTo", required = false) final Long amountTo,
            @RequestParam(value = "fromName", required = false) final String fromName,
            @RequestParam(value = "dateFrom,", required = false) final Long dateFrom,
            @RequestParam(value = "dateTo,", required = false) final Long dateTo,

            @RequestParam(value = "bankCode", required = false) final Long bankCode,
            @RequestParam(value = "bankName", required = false) final String bankName,
            @RequestParam(value = "bankBranch", required = false) final String bankBranch,
            @RequestParam(value = "bankStockFrom", required = false) final Long bankStockFrom,
            @RequestParam(value = "bankStockTo", required = false) final Long bankStockTo,

            HttpServletResponse response
    ) throws JRException, IOException {

        Gson gson = new Gson();

        TypeToken<List<Column>> token = new TypeToken<List<Column>>() {
        };
        List<Column> columnList = gson.fromJson(columns, token.getType());

        TypeToken<List<Variable>> tokenVariables = new TypeToken<List<Variable>>() {
        };
        List<Variable> groupVariablesList = gson.fromJson(groupVariables, tokenVariables.getType());
        List<Variable> tableVariablesList = gson.fromJson(tableVariables, tokenVariables.getType());

        List<ColumnMap> columnMapList = new ArrayList<>();
        columnList.stream().forEach(col -> {
            if (col.getName().equals("رقم الإيداع")) {
                columnMapList.add(ColumnMap.builder()
                        .columnName(col.getName())
                        .view(col.isView())
                        .columnValue("code")
                        .columnExpression("$F{code}")
                        .columnValueClassName(String.class.getName())
                        .groupBy(col.isGroupBy())
                        .sortBy(col.isSortBy())
                        .build());
            }
            if (col.getName().equals("قيمة الإيداع")) {
                columnMapList.add(ColumnMap.builder()
                        .columnName(col.getName())
                        .view(col.isView())
                        .columnValue("amount")
                        .columnExpression("$F{amount}")
                        .columnValueClassName(Double.class.getName())
                        .groupBy(col.isGroupBy())
                        .sortBy(col.isSortBy())
                        .build());
            }
            if (col.getName().equals("اسم المودع")) {
                columnMapList.add(ColumnMap.builder()
                        .columnName(col.getName())
                        .view(col.isView())
                        .columnValue("fromName")
                        .columnExpression("$F{fromName}")
                        .columnValueClassName(String.class.getName())
                        .groupBy(col.isGroupBy())
                        .sortBy(col.isSortBy()).build());
            }
            if (col.getName().equals("تاريخ الإيداع")) {
                columnMapList.add(ColumnMap.builder()
                        .columnName(col.getName())
                        .view(col.isView())
                        .columnValue("date")
                        .columnExpression("com.besafx.app.util.DateConverter.getHijriStringFromDateRTL($F{date})")
                        .columnValueClassName(Date.class.getName())
                        .groupBy(col.isGroupBy())
                        .sortBy(col.isSortBy())
                        .build());
            }
            if (col.getName().equals("مدخل الإيداع")) {
                columnMapList.add(ColumnMap.builder()
                        .columnName(col.getName())
                        .view(col.isView())
                        .columnValue("lastPerson")
                        .columnExpression("$F{lastPerson}.getContact().getFirstName() + \" \" + $F{lastPerson}.getContact().getForthName()")
                        .columnValueClassName(Person.class.getName())
                        .groupBy(col.isGroupBy())
                        .sortBy(col.isSortBy())
                        .build());
            }
            if (col.getName().equals("جهة اصدار الشيك")) {
                columnMapList.add(ColumnMap.builder()
                        .columnName(col.getName())
                        .view(col.isView())
                        .columnValue("checkBankName")
                        .columnExpression("$F{checkBankName}")
                        .columnValueClassName(String.class.getName())
                        .groupBy(col.isGroupBy())
                        .sortBy(col.isSortBy())
                        .build());
            }
            if (col.getName().equals("رقم الشيك")) {
                columnMapList.add(ColumnMap.builder()
                        .columnName(col.getName())
                        .view(col.isView())
                        .columnValue("checkNumber")
                        .columnExpression("$F{checkNumber}")
                        .columnValueClassName(String.class.getName())
                        .groupBy(col.isGroupBy())
                        .sortBy(col.isSortBy())
                        .build());
            }
            if (col.getName().equals("تاريخ اصدار الشيك")) {
                columnMapList.add(ColumnMap.builder()
                        .columnName(col.getName())
                        .view(col.isView())
                        .columnValue("checkDate")
                        .columnExpression("com.besafx.app.util.DateConverter.getHijriStringFromDateRTL($F{checkDate})")
                        .columnValueClassName(Date.class.getName())
                        .groupBy(col.isGroupBy())
                        .sortBy(col.isSortBy())
                        .build());
            }
            if (col.getName().equals("ملاحظات الإيداع")) {
                columnMapList.add(ColumnMap.builder()
                        .columnName(col.getName())
                        .view(col.isView())
                        .columnValue("note")
                        .columnExpression("$F{note}")
                        .columnValueClassName(String.class.getName())
                        .groupBy(col.isGroupBy())
                        .sortBy(col.isSortBy())
                        .build());
            }
            if (col.getName().equals("رقم الحساب البنكي")) {
                columnMapList.add(ColumnMap.builder()
                        .columnName(col.getName())
                        .view(col.isView())
                        .columnValue("bank")
                        .columnExpression("$F{bank}.getCode()")
                        .columnValueClassName(Bank.class.getName())
                        .groupBy(col.isGroupBy())
                        .sortBy(col.isSortBy()).build());
            }
            if (col.getName().equals("اسم البنك")) {
                columnMapList.add(ColumnMap.builder()
                        .columnName(col.getName())
                        .view(col.isView())
                        .columnValue("bank")
                        .columnExpression("$F{bank}.getName()")
                        .columnValueClassName(Bank.class.getName())
                        .groupBy(col.isGroupBy())
                        .sortBy(col.isSortBy()).build());
            }
            if (col.getName().equals("فرع البنك")) {
                columnMapList.add(ColumnMap.builder()
                        .columnName(col.getName())
                        .view(col.isView())
                        .columnValue("bank")
                        .columnExpression("$F{bank}.getBranch()")
                        .columnValueClassName(Bank.class.getName())
                        .groupBy(col.isGroupBy())
                        .sortBy(col.isSortBy()).build());
            }
            if (col.getName().equals("الرصيد الافتتاحي")) {
                columnMapList.add(ColumnMap.builder()
                        .columnName(col.getName())
                        .view(col.isView())
                        .columnValue("bank")
                        .columnExpression("$F{bank}.getStartAmount()")
                        .columnValueClassName(Bank.class.getName())
                        .groupBy(col.isGroupBy())
                        .sortBy(col.isSortBy()).build());
            }
            if (col.getName().equals("تاريخ الرصيد الافتتاحي")) {
                columnMapList.add(ColumnMap.builder()
                        .columnName(col.getName())
                        .view(col.isView())
                        .columnValue("bank")
                        .columnExpression("com.besafx.app.util.DateConverter.getHijriStringFromDateRTL($F{bank}.getStartAmountDate())")
                        .columnValueClassName(Bank.class.getName())
                        .groupBy(col.isGroupBy())
                        .sortBy(col.isSortBy()).build());
            }
            if (col.getName().equals("الرصيد الحالي")) {
                columnMapList.add(ColumnMap.builder()
                        .columnName(col.getName())
                        .view(col.isView())
                        .columnValue("bank")
                        .columnExpression("$F{bank}.getStock()")
                        .columnValueClassName(Bank.class.getName())
                        .groupBy(col.isGroupBy())
                        .sortBy(col.isSortBy()).build());
            }
            if (col.getName().equals("مدخل الحساب البنكي")) {
                columnMapList.add(ColumnMap.builder()
                        .columnName(col.getName())
                        .view(col.isView())
                        .columnValue("bank")
                        .columnExpression("$F{bank}.getLastPerson().getContact().getFirstName() + \"  \" + $F{bank}.getLastPerson().getContact().getForthName()")
                        .columnValueClassName(Bank.class.getName())
                        .groupBy(col.isGroupBy())
                        .sortBy(col.isSortBy()).build());
            }
        });

        final OutputStream ouputStream = response.getOutputStream();
        JasperDesign jasperDesign = getJasperDesign(reportTitle, orientation, columnMapList, groupVariablesList, tableVariablesList);
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, new JRBeanCollectionDataSource(depositSearch.search(code, amountFrom, amountTo, fromName, dateFrom, dateTo, bankCode, bankName, bankBranch, bankStockFrom, bankStockTo)));

        export(exportType, response, ouputStream, jasperPrint);
    }

    @RequestMapping(value = "/report/dynamic/withdraw", method = RequestMethod.GET, produces = MediaType.ALL_VALUE)
    public void ReportWithdraw(
            @RequestParam(value = "exportType") final String exportType,
            @RequestParam(value = "reportTitle") final String reportTitle,
            @RequestParam(value = "orientation") final String orientation,
            @RequestParam(value = "columns") final String columns,
            @RequestParam(value = "groupVariables") final String groupVariables,
            @RequestParam(value = "tableVariables") final String tableVariables,

            @RequestParam(value = "code", required = false) final Long code,
            @RequestParam(value = "amountFrom", required = false) final Long amountFrom,
            @RequestParam(value = "amountTo", required = false) final Long amountTo,
            @RequestParam(value = "fromName", required = false) final String fromName,
            @RequestParam(value = "dateFrom,", required = false) final Long dateFrom,
            @RequestParam(value = "dateTo,", required = false) final Long dateTo,

            @RequestParam(value = "bankCode", required = false) final Long bankCode,
            @RequestParam(value = "bankName", required = false) final String bankName,
            @RequestParam(value = "bankBranch", required = false) final String bankBranch,
            @RequestParam(value = "bankStockFrom", required = false) final Long bankStockFrom,
            @RequestParam(value = "bankStockTo", required = false) final Long bankStockTo,

            HttpServletResponse response
    ) throws JRException, IOException {

        Gson gson = new Gson();

        TypeToken<List<Column>> token = new TypeToken<List<Column>>() {
        };
        List<Column> columnList = gson.fromJson(columns, token.getType());

        TypeToken<List<Variable>> tokenVariables = new TypeToken<List<Variable>>() {
        };
        List<Variable> groupVariablesList = gson.fromJson(groupVariables, tokenVariables.getType());
        List<Variable> tableVariablesList = gson.fromJson(tableVariables, tokenVariables.getType());

        List<ColumnMap> columnMapList = new ArrayList<>();
        columnList.stream().forEach(col -> {
            if (col.getName().equals("رقم السحب")) {
                columnMapList.add(ColumnMap.builder()
                        .columnName(col.getName())
                        .view(col.isView())
                        .columnValue("code")
                        .columnExpression("$F{code}")
                        .columnValueClassName(String.class.getName())
                        .groupBy(col.isGroupBy())
                        .sortBy(col.isSortBy())
                        .build());
            }
            if (col.getName().equals("قيمة السحب")) {
                columnMapList.add(ColumnMap.builder()
                        .columnName(col.getName())
                        .view(col.isView())
                        .columnValue("amount")
                        .columnExpression("$F{amount}")
                        .columnValueClassName(Double.class.getName())
                        .groupBy(col.isGroupBy())
                        .sortBy(col.isSortBy())
                        .build());
            }
            if (col.getName().equals("اسم الساحب")) {
                columnMapList.add(ColumnMap.builder()
                        .columnName(col.getName())
                        .view(col.isView())
                        .columnValue("fromName")
                        .columnExpression("$F{fromName}")
                        .columnValueClassName(String.class.getName())
                        .groupBy(col.isGroupBy())
                        .sortBy(col.isSortBy()).build());
            }
            if (col.getName().equals("تاريخ السحب")) {
                columnMapList.add(ColumnMap.builder()
                        .columnName(col.getName())
                        .view(col.isView())
                        .columnValue("date")
                        .columnExpression("com.besafx.app.util.DateConverter.getHijriStringFromDateRTL($F{date})")
                        .columnValueClassName(Date.class.getName())
                        .groupBy(col.isGroupBy())
                        .sortBy(col.isSortBy())
                        .build());
            }
            if (col.getName().equals("مدخل السحب")) {
                columnMapList.add(ColumnMap.builder()
                        .columnName(col.getName())
                        .view(col.isView())
                        .columnValue("lastPerson")
                        .columnExpression("$F{lastPerson}.getContact().getFirstName() + \" \" + $F{lastPerson}.getContact().getForthName()")
                        .columnValueClassName(Person.class.getName())
                        .groupBy(col.isGroupBy())
                        .sortBy(col.isSortBy())
                        .build());
            }
            if (col.getName().equals("رقم الشيك")) {
                columnMapList.add(ColumnMap.builder()
                        .columnName(col.getName())
                        .view(col.isView())
                        .columnValue("checkNumber")
                        .columnExpression("$F{checkNumber}")
                        .columnValueClassName(String.class.getName())
                        .groupBy(col.isGroupBy())
                        .sortBy(col.isSortBy())
                        .build());
            }
            if (col.getName().equals("تاريخ اصدار الشيك")) {
                columnMapList.add(ColumnMap.builder()
                        .columnName(col.getName())
                        .view(col.isView())
                        .columnValue("checkDate")
                        .columnExpression("com.besafx.app.util.DateConverter.getHijriStringFromDateRTL($F{checkDate})")
                        .columnValueClassName(Date.class.getName())
                        .groupBy(col.isGroupBy())
                        .sortBy(col.isSortBy())
                        .build());
            }
            if (col.getName().equals("ملاحظات السحب")) {
                columnMapList.add(ColumnMap.builder()
                        .columnName(col.getName())
                        .view(col.isView())
                        .columnValue("note")
                        .columnExpression("$F{note}")
                        .columnValueClassName(String.class.getName())
                        .groupBy(col.isGroupBy())
                        .sortBy(col.isSortBy())
                        .build());
            }
            if (col.getName().equals("رقم الحساب البنكي")) {
                columnMapList.add(ColumnMap.builder()
                        .columnName(col.getName())
                        .view(col.isView())
                        .columnValue("bank")
                        .columnExpression("$F{bank}.getCode()")
                        .columnValueClassName(Bank.class.getName())
                        .groupBy(col.isGroupBy())
                        .sortBy(col.isSortBy()).build());
            }
            if (col.getName().equals("اسم البنك")) {
                columnMapList.add(ColumnMap.builder()
                        .columnName(col.getName())
                        .view(col.isView())
                        .columnValue("bank")
                        .columnExpression("$F{bank}.getName()")
                        .columnValueClassName(Bank.class.getName())
                        .groupBy(col.isGroupBy())
                        .sortBy(col.isSortBy()).build());
            }
            if (col.getName().equals("فرع البنك")) {
                columnMapList.add(ColumnMap.builder()
                        .columnName(col.getName())
                        .view(col.isView())
                        .columnValue("bank")
                        .columnExpression("$F{bank}.getBranch()")
                        .columnValueClassName(Bank.class.getName())
                        .groupBy(col.isGroupBy())
                        .sortBy(col.isSortBy()).build());
            }
            if (col.getName().equals("الرصيد الافتتاحي")) {
                columnMapList.add(ColumnMap.builder()
                        .columnName(col.getName())
                        .view(col.isView())
                        .columnValue("bank")
                        .columnExpression("$F{bank}.getStartAmount()")
                        .columnValueClassName(Bank.class.getName())
                        .groupBy(col.isGroupBy())
                        .sortBy(col.isSortBy()).build());
            }
            if (col.getName().equals("تاريخ الرصيد الافتتاحي")) {
                columnMapList.add(ColumnMap.builder()
                        .columnName(col.getName())
                        .view(col.isView())
                        .columnValue("bank")
                        .columnExpression("com.besafx.app.util.DateConverter.getHijriStringFromDateRTL($F{bank}.getStartAmountDate())")
                        .columnValueClassName(Bank.class.getName())
                        .groupBy(col.isGroupBy())
                        .sortBy(col.isSortBy()).build());
            }
            if (col.getName().equals("الرصيد الحالي")) {
                columnMapList.add(ColumnMap.builder()
                        .columnName(col.getName())
                        .view(col.isView())
                        .columnValue("bank")
                        .columnExpression("$F{bank}.getStock()")
                        .columnValueClassName(Bank.class.getName())
                        .groupBy(col.isGroupBy())
                        .sortBy(col.isSortBy()).build());
            }
            if (col.getName().equals("مدخل الحساب البنكي")) {
                columnMapList.add(ColumnMap.builder()
                        .columnName(col.getName())
                        .view(col.isView())
                        .columnValue("bank")
                        .columnExpression("$F{bank}.getLastPerson().getContact().getFirstName() + \"  \" + $F{bank}.getLastPerson().getContact().getForthName()")
                        .columnValueClassName(Bank.class.getName())
                        .groupBy(col.isGroupBy())
                        .sortBy(col.isSortBy()).build());
            }
        });

        final OutputStream ouputStream = response.getOutputStream();
        JasperDesign jasperDesign = getJasperDesign(reportTitle, orientation, columnMapList, groupVariablesList, tableVariablesList);
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, new JRBeanCollectionDataSource(withdrawSearch.search(code, amountFrom, amountTo, fromName, dateFrom, dateTo, bankCode, bankName, bankBranch, bankStockFrom, bankStockTo)));

        export(exportType, response, ouputStream, jasperPrint);
    }

    @RequestMapping(value = "/report/dynamic/payment", method = RequestMethod.GET, produces = MediaType.ALL_VALUE)
    public void ReportPayment(
            @RequestParam(value = "exportType") final String exportType,
            @RequestParam(value = "reportTitle") final String reportTitle,
            @RequestParam(value = "orientation") final String orientation,
            @RequestParam(value = "columns") final String columns,
            @RequestParam(value = "groupVariables") final String groupVariables,
            @RequestParam(value = "tableVariables") final String tableVariables,
            @RequestParam(value = "paymentCodeFrom", required = false) final String paymentCodeFrom,
            @RequestParam(value = "paymentCodeTo", required = false) final String paymentCodeTo,
            @RequestParam(value = "paymentDateFrom", required = false) final Long paymentDateFrom,
            @RequestParam(value = "paymentDateTo", required = false) final Long paymentDateTo,
            @RequestParam(value = "amountFrom", required = false) final Long amountFrom,
            @RequestParam(value = "amountTo", required = false) final Long amountTo,
            @RequestParam(value = "firstName", required = false) final String firstName,
            @RequestParam(value = "secondName", required = false) final String secondName,
            @RequestParam(value = "thirdName", required = false) final String thirdName,
            @RequestParam(value = "forthName", required = false) final String forthName,
            @RequestParam(value = "dateFrom", required = false) final Long dateFrom,
            @RequestParam(value = "dateTo", required = false) final Long dateTo,
            @RequestParam(value = "studentIdentityNumber", required = false) final String studentIdentityNumber,
            @RequestParam(value = "studentMobile", required = false) final String studentMobile,
            @RequestParam(value = "coursePriceFrom", required = false) final Long coursePriceFrom,
            @RequestParam(value = "coursePriceTo", required = false) final Long coursePriceTo,
            @RequestParam(value = "course", required = false) final Long course,
            @RequestParam(value = "master", required = false) final Long master,
            @RequestParam(value = "branch", required = false) final Long branch,
            @RequestParam(value = "type", required = false) final String type,
            HttpServletResponse response
    ) throws JRException, IOException {

        Gson gson = new Gson();

        TypeToken<List<Column>> token = new TypeToken<List<Column>>() {
        };
        List<Column> columnList = gson.fromJson(columns, token.getType());

        TypeToken<List<Variable>> tokenVariables = new TypeToken<List<Variable>>() {
        };
        List<Variable> groupVariablesList = gson.fromJson(groupVariables, tokenVariables.getType());
        List<Variable> tableVariablesList = gson.fromJson(tableVariables, tokenVariables.getType());

        List<ColumnMap> columnMapList = new ArrayList<>();
        columnList.stream().forEach(col -> {
            if (col.getName().equals("رقم السند")) {
                columnMapList.add(ColumnMap.builder()
                        .columnName(col.getName())
                        .view(col.isView())
                        .columnValue("code")
                        .columnExpression("$F{code}")
                        .columnValueClassName(Integer.class.getName())
                        .groupBy(col.isGroupBy())
                        .sortBy(col.isSortBy())
                        .build());
            }
            if (col.getName().equals("تاريخ السند")) {
                columnMapList.add(ColumnMap.builder()
                        .columnName(col.getName())
                        .view(col.isView())
                        .columnValue("date")
                        .columnExpression("com.besafx.app.util.DateConverter.getHijriStringFromDateRTL($F{date})")
                        .columnValueClassName(Date.class.getName())
                        .groupBy(col.isGroupBy())
                        .sortBy(col.isSortBy())
                        .build());
            }
            if (col.getName().equals("قيمة السند")) {
                columnMapList.add(ColumnMap.builder()
                        .columnName(col.getName())
                        .view(col.isView())
                        .columnValue("amountNumber")
                        .columnExpression("$F{amountNumber}")
                        .columnValueClassName(Double.class.getName())
                        .groupBy(col.isGroupBy())
                        .sortBy(col.isSortBy())
                        .build());
            }
            if (col.getName().equals("وجهة الصرف")) {
                columnMapList.add(ColumnMap.builder()
                        .columnName(col.getName())
                        .view(col.isView())
                        .columnValue("toName")
                        .columnExpression("$F{toName}")
                        .columnValueClassName(String.class.getName())
                        .groupBy(col.isGroupBy())
                        .sortBy(col.isSortBy())
                        .build());
            }
            if (col.getName().equals("مدخل السند")) {
                columnMapList.add(ColumnMap.builder()
                        .columnName(col.getName())
                        .view(col.isView())
                        .columnValue("lastPerson")
                        .columnExpression("$F{lastPerson}.getContact().getFirstName() + \" \" + $F{lastPerson}.getContact().getForthName()")
                        .columnValueClassName(Person.class.getName())
                        .groupBy(col.isGroupBy())
                        .sortBy(col.isSortBy())
                        .build());
            }
            if (col.getName().equals("بيان السند")) {
                columnMapList.add(ColumnMap.builder()
                        .columnName(col.getName())
                        .view(col.isView())
                        .columnValue("note")
                        .columnExpression("$F{note}")
                        .columnValueClassName(String.class.getName())
                        .groupBy(col.isGroupBy())
                        .sortBy(col.isSortBy())
                        .build());
            }
            if (col.getName().equals("نوع السند")) {
                columnMapList.add(ColumnMap.builder()
                        .columnName(col.getName())
                        .view(col.isView())
                        .columnValue("type")
                        .columnExpression("$F{type}")
                        .columnValueClassName(String.class.getName())
                        .groupBy(col.isGroupBy())
                        .sortBy(col.isSortBy())
                        .build());
            }
            if (col.getName().equals("اسم الطالب")) {
                columnMapList.add(ColumnMap.builder()
                        .columnName(col.getName())
                        .view(col.isView())
                        .columnValue("account")
                        .columnExpression("$F{account}.getStudent().getContact().getFirstName() + \" \" + $F{account}.getStudent().getContact().getSecondName() + \" \" + $F{account}.getStudent().getContact().getThirdName() + \" \" + $F{account}.getStudent().getContact().getForthName()")
                        .columnValueClassName(Account.class.getName())
                        .groupBy(col.isGroupBy())
                        .sortBy(col.isSortBy())
                        .build());
            }
            if (col.getName().equals("رقم البطاقة")) {
                columnMapList.add(ColumnMap.builder()
                        .columnName(col.getName())
                        .view(col.isView())
                        .columnValue("account")
                        .columnExpression("$F{account}.getStudent().getContact().getIdentityNumber()")
                        .columnValueClassName(Account.class.getName())
                        .groupBy(col.isGroupBy())
                        .sortBy(col.isSortBy())
                        .build());
            }
            if (col.getName().equals("رقم الجوال")) {
                columnMapList.add(ColumnMap.builder()
                        .columnName(col.getName())
                        .view(col.isView())
                        .columnValue("account")
                        .columnExpression("$F{account}.getStudent().getContact().getMobile()")
                        .columnValueClassName(Account.class.getName())
                        .groupBy(col.isGroupBy())
                        .sortBy(col.isSortBy())
                        .build());
            }
            if (col.getName().equals("الجنسية")) {
                columnMapList.add(ColumnMap.builder()
                        .columnName(col.getName())
                        .view(col.isView())
                        .columnValue("account")
                        .columnExpression("$F{account}.getStudent().getContact().getNationality()")
                        .columnValueClassName(Account.class.getName())
                        .groupBy(col.isGroupBy())
                        .sortBy(col.isSortBy())
                        .build());
            }
            if (col.getName().equals("العنوان")) {
                columnMapList.add(ColumnMap.builder()
                        .columnName(col.getName())
                        .view(col.isView())
                        .columnValue("account")
                        .columnExpression("$F{account}.getStudent().getContact().getAddress()")
                        .columnValueClassName(Account.class.getName())
                        .groupBy(col.isGroupBy())
                        .sortBy(col.isSortBy())
                        .build());
            }
            if (col.getName().equals("المبلغ المطلوب")) {
                columnMapList.add(ColumnMap.builder()
                        .columnName(col.getName())
                        .view(col.isView())
                        .columnValue("account")
                        .columnExpression("$F{account}.getCoursePrice()")
                        .columnValueClassName(Account.class.getName())
                        .groupBy(col.isGroupBy())
                        .sortBy(col.isSortBy())
                        .build());
            }
            if (col.getName().equals("الفرع")) {
                columnMapList.add(ColumnMap.builder()
                        .columnName(col.getName())
                        .view(col.isView())
                        .columnValue("account")
                        .columnExpression("$F{account}.getCourse().getMaster().getBranch().getName()")
                        .columnValueClassName(Account.class.getName())
                        .groupBy(col.isGroupBy())
                        .sortBy(col.isSortBy())
                        .build());
            }
            if (col.getName().equals("التخصص")) {
                columnMapList.add(ColumnMap.builder()
                        .columnName(col.getName())
                        .view(col.isView())
                        .columnValue("account")
                        .columnExpression("$F{account}.getCourse().getMaster().getName()")
                        .columnValueClassName(Account.class.getName())
                        .groupBy(col.isGroupBy())
                        .sortBy(col.isSortBy())
                        .build());
            }
        });

        final OutputStream ouputStream = response.getOutputStream();
        JasperDesign jasperDesign = getJasperDesign(reportTitle, orientation, columnMapList, groupVariablesList, tableVariablesList);
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, new JRBeanCollectionDataSource(
                paymentSearch.search(paymentCodeFrom, paymentCodeTo, paymentDateFrom, paymentDateTo, amountFrom, amountTo, firstName, secondName, thirdName, forthName, dateFrom, dateTo, studentIdentityNumber, studentMobile, coursePriceFrom, coursePriceTo, course, master, branch, null, type)));


        export(exportType, response, ouputStream, jasperPrint);
    }

    public void export(final String exportType, final HttpServletResponse response, final OutputStream ouputStream, final JasperPrint jasperPrint) throws JRException {

        Exporter exporter = null;

        switch (exportType) {
            case "PDF":
                response.setContentType("application/pdf");
                response.setHeader("Content-Disposition", "inline; filename=\"report.pdf\"");
                exporter = new JRPdfExporter();
                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(ouputStream));
                SimplePdfExporterConfiguration configurationPdf = new SimplePdfExporterConfiguration();
                configurationPdf.setCreatingBatchModeBookmarks(true);
                exporter.setConfiguration(configurationPdf);
                break;
            case "RTF":
                response.setContentType("application/rtf");
                response.setHeader("Content-Disposition", "inline; filename=\"report.rtf\"");
                exporter = new JRRtfExporter();
                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new SimpleWriterExporterOutput(ouputStream));
                break;
            case "HTML":
                response.setContentType("application/html");
                response.setHeader("Content-Disposition", "inline; filename=\"report.html\"");
                exporter = new HtmlExporter();
                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new SimpleHtmlExporterOutput(ouputStream));
                SimpleHtmlReportConfiguration reportExportConfiguration = new SimpleHtmlReportConfiguration();
                reportExportConfiguration.setWhitePageBackground(false);
                reportExportConfiguration.setRemoveEmptySpaceBetweenRows(true);
                exporter.setConfiguration(reportExportConfiguration);
                break;
            case "XHTML":
                response.setContentType("application/xhtml");
                response.setHeader("Content-Disposition", "inline; filename=\"report.xhtml\"");
                exporter = new HtmlExporter();
                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new SimpleHtmlExporterOutput(ouputStream));
                break;
            case "XLSX":
                response.setContentType("application/xlsx");
                response.setHeader("Content-Disposition", "inline; filename=\"report.xlsx\"");
                exporter = new JRXlsxExporter();
                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(ouputStream));
                SimpleXlsxReportConfiguration configurationXlsx = new SimpleXlsxReportConfiguration();
                configurationXlsx.setOnePagePerSheet(false);
                exporter.setConfiguration(configurationXlsx);
                break;
            case "CSV":
                response.setContentType("application/csv");
                response.setHeader("Content-Disposition", "inline; filename=\"report.csv\"");
                exporter = new JRCsvExporter();
                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new SimpleWriterExporterOutput(ouputStream));
                break;
            case "PPTX":
                response.setContentType("application/pptx");
                response.setHeader("Content-Disposition", "inline; filename=\"report.pptx\"");
                exporter = new JRPptxExporter();
                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(ouputStream));
                break;
            case "DOCX":
                response.setContentType("application/docx");
                response.setHeader("Content-Disposition", "inline; filename=\"report.docx\"");
                exporter = new JRDocxExporter();
                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(ouputStream));
                break;
            case "ODS":
                response.setContentType("application/ods");
                response.setHeader("Content-Disposition", "inline; filename=\"report.ods\"");
                exporter = new JROdsExporter();
                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(ouputStream));
                SimpleOdsReportConfiguration configurationOds = new SimpleOdsReportConfiguration();
                configurationOds.setOnePagePerSheet(true);
                exporter.setConfiguration(configurationOds);
                break;
            case "ODT":
                response.setContentType("application/odt");
                response.setHeader("Content-Disposition", "inline; filename=\"report.odt\"");
                exporter = new JROdtExporter();
                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(ouputStream));
                break;

        }

        exporter.exportReport();
        Optional.ofNullable(ouputStream).ifPresent(outputStream -> {
            try {
                ouputStream.flush();
                ouputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


}
