package com.besafx.app.rest;
import com.besafx.app.config.CustomException;
import com.besafx.app.entity.*;
import com.besafx.app.service.*;
import com.besafx.app.util.WrapperUtil;
import com.besafx.app.ws.Notification;
import com.besafx.app.ws.NotificationService;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.*;

@RestController
@RequestMapping(value = "/api/account/")
public class AccountRest {

    @Autowired
    private PersonService personService;

    @Autowired
    private BranchService branchService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private ContactService contactService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private MasterService masterService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private NotificationService notificationService;

    @RequestMapping(value = "create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_ACCOUNT_CREATE')")
    public Account create(@RequestBody Account account, Principal principal) {
        Person person = personService.findByEmail(principal.getName());
        Account topAccount = accountService.findTopByCourseMasterBranchOrderByCodeDesc(account.getCourse().getMaster().getBranch());
        if (topAccount == null) {
            account.setCode(1);
        } else {
            account.setCode(topAccount.getCode() + 1);
        }
        account.setRegisterDate(new Date());
        account.setLastUpdate(new Date());
        account.setLastPerson(person);
        Student student = studentService.findByContactIdentityNumber(account.getStudent().getContact().getIdentityNumber().toLowerCase().trim());
        if (student == null) {
            account.getStudent().setContact(contactService.save(account.getStudent().getContact()));
            account.setStudent(studentService.save(account.getStudent()));
            notificationService.notifyOne(Notification
                    .builder()
                    .title("العمليات على تسجيل الطلاب")
                    .message("تم انشاء حساب جديد لهذا الطالب")
                    .type("success")
                    .icon("fa-plus-square")
                    .build(), principal.getName());
        } else {
            account.setStudent(student);
            notificationService.notifyOne(Notification
                    .builder()
                    .title("العمليات على تسجيل الطلاب")
                    .message("تم استخدام بيانات الطالب المسجلة سابقاً")
                    .type("success")
                    .icon("fa-plus-square")
                    .build(), principal.getName());
        }
        account = accountService.save(account);
        notificationService.notifyOne(Notification
                .builder()
                .title("العمليات على تسجيل الطلاب")
                .message("تم اضافة تسجيل جديد بنجاح")
                .type("success")
                .icon("fa-plus-square")
                .build(), principal.getName());
        return account;
    }

    @RequestMapping(value = "update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_ACCOUNT_UPDATE')")
    public Account update(@RequestBody Account account, Principal principal) {
        Account object = accountService.findOne(account.getId());
        if (object != null) {
            Person person = personService.findByEmail(principal.getName());
            account.setRegisterDate(new Date());
            account.setLastUpdate(new Date());
            account.setLastPerson(person);
            account.getStudent().setContact(contactService.save(account.getStudent().getContact()));
            account.setStudent(studentService.save(account.getStudent()));
            account = accountService.save(account);
            notificationService.notifyOne(Notification
                    .builder()
                    .title("العمليات على تسجيل الطلاب")
                    .message("تم تعديل بيانات التسجيل بنجاح")
                    .type("success")
                    .icon("fa-edit")
                    .build(), principal.getName());
            return account;
        } else {
            return null;
        }
    }

    @RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_ACCOUNT_DELETE')")
    public void delete(@PathVariable Long id, Principal principal) {
        Account object = accountService.findOne(id);
        if (object != null) {
            accountService.delete(id);
            notificationService.notifyOne(Notification
                    .builder()
                    .title("العمليات على تسجيل الطلاب")
                    .message("تم حذف الاشتراك وكل ما يتعلق به من سندات وحسابات بنجاح")
                    .type("success")
                    .icon("fa-trash")
                    .build(), principal.getName());
        }
    }

    @RequestMapping(value = "deleteByCourse/{courseId}", method = RequestMethod.DELETE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_ACCOUNT_DELETE')")
    public void deleteByCourse(@PathVariable Long courseId, Principal principal) {
        List<Account> accounts = accountService.findByCourseId(courseId);
        if (!accounts.isEmpty()) {
            List<Payment> payments = paymentService.findByAccountIn(accounts);
            paymentService.delete(payments);
            accountService.delete(accounts);
            notificationService.notifyOne(Notification
                    .builder()
                    .title("العمليات على تسجيل الطلاب")
                    .message("تم حذف الاشتراكات وكل ما يتعلق بها من سندات وحسابات بنجاح")
                    .type("success")
                    .icon("fa-trash")
                    .build(), principal.getName());
        }
    }

    @RequestMapping(value = "findAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Account> findAll() {
        return Lists.newArrayList(accountService.findAll());
    }

    @RequestMapping(value = "findOne/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Account findOne(@PathVariable Long id) {
        return accountService.findOne(id);
    }

    @RequestMapping(value = "count", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Long count() {
        return accountService.count();
    }

    @RequestMapping(value = "findByStudent/{studentId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Account> findByStudent(@PathVariable Long studentId) {
        List<Account> list = accountService.findByStudent(studentService.findOne(studentId));
        return list;
    }

    @RequestMapping(value = "findByCourse/{courseId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Account> findByCourse(@PathVariable Long courseId) {
        List<Account> list = accountService.findByCourse(courseService.findOne(courseId));
        return list;
    }

    @RequestMapping(value = "findByMaster/{masterId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Account> findByMaster(@PathVariable Long masterId) {
        List<Account> list = accountService.findByCourseMaster(masterService.findOne(masterId));
        return list;
    }

    @RequestMapping(value = "findByBranch/{branchId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Account> findByBranch(@PathVariable Long branchId) {
        List<Account> list = accountService.findByCourseMasterBranch(branchService.findOne(branchId));
        return list;
    }

    @RequestMapping(value = "findRequiredPrice/{accountId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Double findRequiredPrice(@PathVariable Long accountId) {
        Account account = findOne(accountId);
        return account.getRequiredPrice();
    }

    @RequestMapping(value = "findRemainPrice/{accountId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Double findRemainPrice(@PathVariable Long accountId) {
        Account account = findOne(accountId);
        return account.getRequiredPrice() - findPaidPrice(accountId);
    }

    @RequestMapping(value = "findPaidPrice/{accountId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Double findPaidPrice(@PathVariable Long accountId) {
        Account account = findOne(accountId);
        Double paid = paymentService.sumByAccountAndType(account, "ايرادات اساسية");
        return paid == null ? 0.0 : paid;
    }

    @RequestMapping(value = "fetchTableData", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Account> fetchTableData(Principal principal) {
        Person person = personService.findByEmail(principal.getName());
        return accountService.findByCourseMasterBranch(person.getBranch());
    }

    @RequestMapping(value = "fetchAccountsCountByBranch/{branchId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<WrapperUtil> fetchAccountsCountByBranch(@PathVariable(value = "branchId") Long branchId) {
        Branch branch = branchService.findOne(branchId);
        List<WrapperUtil> list = new ArrayList<>();
        YearMonth thisMonth = YearMonth.now();
        for (int i = 0; i <= 4; i++) {
            YearMonth month = thisMonth.minusMonths(i);
            String monthLabel = month.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault());
            Date startDate = Date.from(month.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date endDate = Date.from(month.atEndOfMonth().atStartOfDay(ZoneId.systemDefault()).toInstant());
            Long monthData = accountService.countByCourseMasterBranchAndRegisterDateBetween(branch, startDate, endDate);
            WrapperUtil wrapperUtil = new WrapperUtil();
            wrapperUtil.setObj1(monthLabel);
            wrapperUtil.setObj2(monthData);
            list.add(wrapperUtil);

        }
        return list;

    }

    @RequestMapping(value = "fetchAccountsCountByMaster/{masterId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<WrapperUtil> fetchAccountsCountByMaster(@PathVariable(value = "masterId") Long masterId) {
        Master master = masterService.findOne(masterId);
        List<WrapperUtil> list = new ArrayList<>();
        YearMonth thisMonth = YearMonth.now();
        for (int i = 0; i <= 4; i++) {
            YearMonth month = thisMonth.minusMonths(i);
            String monthLabel = month.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault());
            Date startDate = Date.from(month.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date endDate = Date.from(month.atEndOfMonth().atStartOfDay(ZoneId.systemDefault()).toInstant());
            Long monthData = accountService.countByCourseMasterAndRegisterDateBetween(master, startDate, endDate);
            WrapperUtil wrapperUtil = new WrapperUtil();
            wrapperUtil.setObj1(monthLabel);
            wrapperUtil.setObj2(monthData);
            list.add(wrapperUtil);

        }
        return list;

    }

    @RequestMapping(value = "fetchAccountsCountByCourse/{courseId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<WrapperUtil> fetchAccountsCountByCourse(@PathVariable(value = "courseId") Long courseId) {
        Course course = courseService.findOne(courseId);
        List<WrapperUtil> list = new ArrayList<>();
        YearMonth thisMonth = YearMonth.now();
        for (int i = 0; i <= 4; i++) {
            YearMonth month = thisMonth.minusMonths(i);
            String monthLabel = month.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault());
            Date startDate = Date.from(month.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date endDate = Date.from(month.atEndOfMonth().atStartOfDay(ZoneId.systemDefault()).toInstant());
            Long monthData = accountService.countByCourseAndRegisterDateBetween(course, startDate, endDate);
            WrapperUtil wrapperUtil = new WrapperUtil();
            wrapperUtil.setObj1(monthLabel);
            wrapperUtil.setObj2(monthData);
            list.add(wrapperUtil);

        }
        return list;

    }

    @RequestMapping(value = "filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Account> filter(
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
            @RequestParam(value = "branch", required = false) final Long branch) {
        List<Specification> predicates = new ArrayList<>();
        Optional.ofNullable(firstName).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("student").get("contact").get("firstName"), "%" + value + "%")));
        Optional.ofNullable(secondName).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("student").get("contact").get("secondName"), "%" + value + "%")));
        Optional.ofNullable(thirdName).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("student").get("contact").get("thirdName"), "%" + value + "%")));
        Optional.ofNullable(forthName).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("student").get("contact").get("forthName"), "%" + value + "%")));
        Optional.ofNullable(dateFrom).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("registerDate"), new Date(value))));
        Optional.ofNullable(dateTo).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("registerDate"), new Date(value))));
        Optional.ofNullable(studentIdentityNumber).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("student").get("contact").get("identityNumber"), "%" + value + "%")));
        Optional.ofNullable(studentMobile).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("student").get("contact").get("mobile"), "%" + value + "%")));
        Optional.ofNullable(coursePriceFrom).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("coursePrice"), value)));
        Optional.ofNullable(coursePriceTo).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("coursePrice"), value)));
        Optional.ofNullable(course).ifPresent(value -> predicates.add((root, cq, cb) -> cb.equal(root.get("course").get("id"), value)));
        Optional.ofNullable(master).ifPresent(value -> predicates.add((root, cq, cb) -> cb.equal(root.get("course").get("master").get("id"), value)));
        Optional.ofNullable(branch).ifPresent(value -> predicates.add((root, cq, cb) -> cb.equal(root.get("course").get("master").get("branch").get("id"), value)));
        if (!predicates.isEmpty()) {
            Specification result = predicates.get(0);
            for (int i = 1; i < predicates.size(); i++) {
                result = Specifications.where(result).and(predicates.get(i));
            }
            List<Account> list = Lists.newArrayList(accountService.findAll(result));
            Comparator<Account> comparator = Comparator.comparing(account -> account.getCourse().getId());
            list.sort(comparator);
            return list;
        } else {
            throw new CustomException("فضلاً ادخل على الاقل عنصر واحد للبحث");
        }
    }
}
