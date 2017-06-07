var gulp = require('gulp');
var uglify = require('gulp-uglify');
var concat = require('gulp-concat');
var cssMin = require('gulp-css');


gulp.task('css', function () {

    gulp.src([
        './magnific-popup/magnific-popup.css',
        './css/creative.css',

        './css/animate.css',
        './css/animation.css',
        './css/bootstrap.css',
        './css/fonts.css',
        './css/md-icons.css',
        './css/angular-toggle-switch-bootstrap-3.css',
        './css/highlight.css',
        './bootstrap-select/css/nya-bs-select.css',
        './css/font-awesome-animation.css',
        './kdate/css/jquery.calendars.picker.css',
        './chosen/chosen.css',
        './css/mdl-style.css',
        './css/theme-black.css',
        './css/style.css'
    ])
        .pipe(concat('app.css'))
        .pipe(cssMin())
        .pipe(gulp.dest('./'));

});

gulp.task('scripts', function () {

    gulp.src([

        './js/material.js',
        './js/fontawesome.js',
        './js/jquery.js',

        './kdate/js/jquery.calendars.js',
        './kdate/js/jquery.calendars-ar.js',
        './kdate/js/jquery.calendars-ar-EG.js',
        './kdate/js/jquery.plugin.js',
        './kdate/js/jquery.calendars.picker.js',
        './kdate/js/jquery.calendars.picker-ar.js',
        './kdate/js/jquery.calendars.picker-ar-EG.js',
        './kdate/js/jquery.calendars.picker.lang.js',
        './kdate/js/jquery.calendars.ummalqura.js',
        './kdate/js/jquery.calendars.ummalqura-ar.js',
        './kdate/js/jquery.calendars.plus.js',

        './js/jquery-ui.js',
        './js/angular.js',
        './js/angular-locale_ar.js',
        './js/angular-sanitize.js',
        './js/angular-ui-router.js',
        './js/angular-animate.js',
        './js/angular-touch.js',

        './angular-spinner/spin.js',
        './angular-spinner/angular-spinner.js',
        './angular-spinner/angular-loading-spinner.js',

        './js/ui-bootstrap.js',
        './js/ui-bootstrap-tpls.js',

        './sockjs/sockjs.js',
        './stomp-websocket/lib/stomp.js',
        './ng-stomp/ng-stomp.js',
        './kdate/kdate.module.js',
        './kdate/kdate.filter.js',
        './kdate/kdate.picker.js',
        './js/underscore.js',
        './js/lrDragNDrop.js',
        './js/contextMenu.js',
        './js/lrStickyHeader.js',
        './js/smart-table.js',
        './js/stStickyHeader.js',
        './js/angular-pageslide-directive.js',
        './js/elastic.js',
        './js/scrollglue.js',
        './ng-upload/angular-file-upload.js',
        './bootstrap-select/js/nya-bs-select.js',
        './js/angular-css.js',
        './js/angular-timer-all.js',
        './full-screen/angular-fullscreen.js',
        './animate-counter/angular-counter.js',
        './js/angular-focusmanager.js',
        './js/jcs-auto-validate.js',
        './js/angular-toggle-switch.js',
        './js/chosen.jquery.js',
        './chosen/angular-chosen.js',
        './js/sortable.js',
        './js/FileSaver.js',
        './js/jquery.noty.packaged.js',
        './scrollreveal/scrollreveal.js',
        './magnific-popup/jquery.magnific-popup.js',
        './js/creative.js',
        './angular-chart/Chart.js',
        './angular-chart/angular-chart.js',

        './init/config/config.js',
        './init/factory/accountFactory.js',
        './init/factory/bankFactory.js',
        './init/factory/billBuyFactory.js',
        './init/factory/billBuyTypeFactory.js',
        './init/factory/branchFactory.js',
        './init/factory/companyFactory.js',
        './init/factory/contactFactory.js',
        './init/factory/courseFactory.js',
        './init/factory/depositFactory.js',
        './init/factory/fileFactory.js',
        './init/factory/masterFactory.js',
        './init/factory/offerFactory.js',
        './init/factory/paymentFactory.js',
        './init/factory/permissionFactory.js',
        './init/factory/personFactory.js',
        './init/factory/roleFactory.js',
        './init/factory/screenFactory.js',
        './init/factory/studentFactory.js',
        './init/factory/teamFactory.js',
        './init/factory/withdrawFactory.js',

        './init/service/service.js',
        './init/directive/directive.js',
        './init/filter/filter.js',
        './init/run/run.js',

        './partials/home/home.js',
        './partials/menu/menu.js',
        './partials/chat/chat.js',
        './partials/company/company.js',
        './partials/branch/branch.js',
        './partials/branch/branchCreateUpdate.js',
        './partials/master/master.js',
        './partials/master/masterCreateUpdate.js',
        './partials/offer/offer.js',
        './partials/offer/offerCreateUpdate.js',
        './partials/offer/offerHeavyWork.js',
        './partials/course/course.js',
        './partials/course/courseCreateUpdate.js',
        './partials/student/student.js',
        './partials/student/studentCreateUpdate.js',
        './partials/account/account.js',
        './partials/account/accountCreateUpdate.js',
        './partials/account/accountHeavyWork.js',
        './partials/payment/payment.js',
        './partials/payment/paymentCreate.js',
        './partials/paymentOut/paymentOut.js',
        './partials/paymentOut/paymentOutCreate.js',
        './partials/bank/bank.js',
        './partials/bank/bankCreateUpdate.js',
        './partials/bank/deposit.js',
        './partials/bank/depositCreate.js',
        './partials/bank/withdraw.js',
        './partials/bank/withdrawCreate.js',
        './partials/billBuyType/billBuyType.js',
        './partials/billBuyType/billBuyTypeCreateUpdate.js',
        './partials/billBuy/billBuy.js',
        './partials/billBuy/billBuyCreate.js',
        './partials/report/report.js',
        './partials/report/offer/js/offerByBranch.js',
        './partials/report/offer/js/offerByMaster.js',
        './partials/report/offer/js/offerById.js',
        './partials/report/offer/js/offerByPerson.js',
        './partials/report/account/js/accountByBranch.js',
        './partials/report/account/js/accountByCourse.js',
        './partials/report/account/js/accountByMaster.js',
        './partials/report/payment/js/paymentByBranch.js',
        './partials/report/payment/js/paymentByCourse.js',
        './partials/report/payment/js/paymentByMaster.js',
        './partials/report/payment/js/paymentByAccountIn.js',

        './partials/report/chart/js/offersCountAverageByBranch.js',
        './partials/report/chart/js/offersCountAverageByMaster.js',
        './partials/report/chart/js/offersPriceAverageByBranch.js',
        './partials/report/chart/js/offersPriceAverageByMaster.js',

        './partials/report/info/js/branchDetails.js',
        './partials/report/info/js/masterDetails.js',
        './partials/report/info/js/courseDetails.js',
        './partials/report/info/js/unRegisteredStudentDetailsByBranch.js',
        './partials/report/info/js/unRegisteredStudentDetailsByMaster.js',

        './partials/team/team.js',
        './partials/team/teamCreateUpdate.js',

        './partials/person/person.js',
        './partials/person/personCreateUpdate.js',

        './partials/help/help.js',
        './partials/profile/profile.js',
        './partials/about/about.js'

    ])
        .pipe(concat('app.js'))
        .pipe(uglify())
        .pipe(gulp.dest('./'))

});

gulp.task('default', ['css', 'scripts']);