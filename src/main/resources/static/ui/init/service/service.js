app.service('ModalProvider', ['$uibModal', '$log', function ($uibModal, $log) {
    /**************************************************************
     *                                                            *
     * Account Model                                              *
     *                                                            *
     *************************************************************/
    this.openAccountCreateModel = function () {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/account/accountCreateUpdate.html',
            controller: 'accountCreateUpdateCtrl',
            backdrop: 'static',
            keyboard: false,
            size: 'lg',
            resolve: {
                title: function () {
                    return 'تسجيل طالب جديد';
                },
                action: function () {
                    return 'create';
                },
                account: function () {
                    return undefined;
                }
            }
        });
    };

    this.openAccountUpdateModel = function (account) {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/account/accountCreateUpdate.html',
            controller: 'accountCreateUpdateCtrl',
            backdrop: 'static',
            keyboard: false,
            size: 'lg',
            resolve: {
                title: function () {
                    return 'تعديل بيانات طالب';
                },
                action: function () {
                    return 'update';
                },
                account: function () {
                    return account;
                }
            }
        });
    };

    this.openAccountHeavyWorkModel = function () {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/account/accountHeavyWork.html',
            controller: 'accountHeavyWorkCtrl',
            backdrop: 'static',
            keyboard: false,
            resolve: {
                title: function () {
                    return 'تسجيل دفعة من الطلاب';
                }
            }
        });
    };


    /**************************************************************
     *                                                            *
     * Branch Model                                               *
     *                                                            *
     *************************************************************/
    this.openBranchCreateModel = function () {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/branch/branchCreateUpdate.html',
            controller: 'branchCreateUpdateCtrl',
            backdrop: 'static',
            keyboard: false,
            size: 'lg',
            resolve: {
                title: function () {
                    return 'انشاء فرع جديد';
                },
                action: function () {
                    return 'create';
                },
                branch: function () {
                    return undefined;
                }
            }
        });
    };

    this.openBranchUpdateModel = function (branch) {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/branch/branchCreateUpdate.html',
            controller: 'branchCreateUpdateCtrl',
            backdrop: 'static',
            keyboard: false,
            size: 'lg',
            resolve: {
                title: function () {
                    return 'تعديل بيانات فرع';
                },
                action: function () {
                    return 'update';
                },
                branch: function () {
                    return branch;
                }
            }
        });
    };

    /**************************************************************
     *                                                            *
     * Bank Model                                                 *
     *                                                            *
     *************************************************************/
    this.openBankCreateModel = function () {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/bank/bankCreateUpdate.html',
            controller: 'bankCreateUpdateCtrl',
            backdrop: 'static',
            keyboard: false,
            resolve: {
                title: function () {
                    return 'انشاء حساب بنكي جديد';
                },
                action: function () {
                    return 'create';
                },
                bank: function () {
                    return undefined;
                }
            }
        });
    };

    this.openBankUpdateModel = function (bank) {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/bank/bankCreateUpdate.html',
            controller: 'bankCreateUpdateCtrl',
            backdrop: 'static',
            keyboard: false,
            resolve: {
                title: function () {
                    return 'تعديل بيانات حساب بنكي';
                },
                action: function () {
                    return 'update';
                },
                bank: function () {
                    return bank;
                }
            }
        });
    };

    this.openDepositCreateModel = function (bank) {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/bank/depositCreate.html',
            controller: 'depositCreateCtrl',
            backdrop: 'static',
            keyboard: false,
            resolve: {
                title: function () {
                    return 'إيداع جديد';
                },
                bank: function () {
                    return bank;
                }
            }
        });
    };

    this.openWithdrawCreateModel = function (bank) {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/bank/withdrawCreate.html',
            controller: 'withdrawCreateCtrl',
            backdrop: 'static',
            keyboard: false,
            resolve: {
                title: function () {
                    return 'سحب جديد';
                },
                bank: function () {
                    return bank;
                }
            }
        });
    };

    /**************************************************************
     *                                                            *
     * BillBuyType Model                                          *
     *                                                            *
     *************************************************************/
    this.openBillBuyTypeCreateModel = function () {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/billBuyType/billBuyTypeCreateUpdate.html',
            controller: 'billBuyTypeCreateUpdateCtrl',
            backdrop: 'static',
            keyboard: false,
            resolve: {
                title: function () {
                    return 'انشاء حساب فواتير شراء جديد';
                },
                action: function () {
                    return 'create';
                },
                billBuyType: function () {
                    return undefined;
                }
            }
        });
    };

    this.openBillBuyTypeUpdateModel = function (billBuyType) {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/billBuyType/billBuyTypeCreateUpdate.html',
            controller: 'billBuyTypeCreateUpdateCtrl',
            backdrop: 'static',
            keyboard: false,
            resolve: {
                title: function () {
                    return 'تعديل بيانات حساب فواتير شراء';
                },
                action: function () {
                    return 'update';
                },
                billBuyType: function () {
                    return billBuyType;
                }
            }
        });
    };

    this.openBillBuyCreateModel = function () {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/billBuy/billBuyCreate.html',
            controller: 'billBuyCreateCtrl',
            backdrop: 'static',
            keyboard: false,
            resolve: {
                title: function () {
                    return 'انشاء فاتورة شراء جديدة';
                }
            }
        });
    };

    /**************************************************************
     *                                                            *
     * Course Model                                               *
     *                                                            *
     *************************************************************/
    this.openCourseCreateModel = function () {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/course/courseCreateUpdate.html',
            controller: 'courseCreateUpdateCtrl',
            backdrop: 'static',
            keyboard: false,
            resolve: {
                title: function () {
                    return 'انشاء دورة جديد';
                },
                action: function () {
                    return 'create';
                },
                course: function () {
                    return undefined;
                }
            }
        });
    };

    this.openCourseUpdateModel = function (course) {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/course/courseCreateUpdate.html',
            controller: 'courseCreateUpdateCtrl',
            backdrop: 'static',
            keyboard: false,
            resolve: {
                title: function () {
                    return 'تعديل بيانات دورة';
                },
                action: function () {
                    return 'update';
                },
                course: function () {
                    return course;
                }
            }
        });
    };
    /**************************************************************
     *                                                            *
     * Master Model                                               *
     *                                                            *
     *************************************************************/
    this.openMasterCreateModel = function () {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/master/masterCreateUpdate.html',
            controller: 'masterCreateUpdateCtrl',
            backdrop: 'static',
            keyboard: false,
            resolve: {
                title: function () {
                    return 'انشاء تخصص جديد';
                },
                action: function () {
                    return 'create';
                },
                master: function () {
                    return undefined;
                }
            }
        });
    };

    this.openMasterUpdateModel = function (master) {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/master/masterCreateUpdate.html',
            controller: 'masterCreateUpdateCtrl',
            backdrop: 'static',
            keyboard: false,
            resolve: {
                title: function () {
                    return 'تعديل بيانات تخصص';
                },
                action: function () {
                    return 'update';
                },
                master: function () {
                    return master;
                }
            }
        });
    };

    /**************************************************************
     *                                                            *
     * Offer Model                                                *
     *                                                            *
     *************************************************************/
    this.openOfferCreateModel = function () {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/offer/offerCreateUpdate.html',
            controller: 'offerCreateUpdateCtrl',
            backdrop: 'static',
            keyboard: false,
            size: 'lg',
            resolve: {
                title: function () {
                    return 'انشاء عرض جديد';
                },
                action: function () {
                    return 'create';
                },
                offer: function () {
                    return undefined;
                }
            }
        });
    };

    this.openOfferUpdateModel = function (offer) {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/offer/offerCreateUpdate.html',
            controller: 'offerCreateUpdateCtrl',
            backdrop: 'static',
            keyboard: false,
            size: 'lg',
            resolve: {
                title: function () {
                    return 'تعديل بيانات عرض';
                },
                action: function () {
                    return 'update';
                },
                offer: function () {
                    return offer;
                }
            }
        });
    };

    this.openOfferHeavyWorkModel = function () {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/offer/offerHeavyWork.html',
            controller: 'offerHeavyWorkCtrl',
            backdrop: 'static',
            keyboard: false,
            resolve: {
                title: function () {
                    return 'انشاء دفعة من العروض';
                }
            }
        });
    };

    /**************************************************************
     *                                                            *
     * Payment Model                                              *
     *                                                            *
     *************************************************************/
    this.openPaymentCreateModel = function (payment) {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/payment/paymentCreate.html',
            controller: 'paymentCreateCtrl',
            backdrop: 'static',
            keyboard: false,
            resolve: {
                title: function () {
                    return 'انشاء سند قبض جديد';
                },
                payment: function () {
                    return payment;
                }
            }
        });
    };

    /**************************************************************
     *                                                            *
     * PaymentOut Model                                           *
     *                                                            *
     *************************************************************/
    this.openPaymentOutCreateModel = function () {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/paymentOut/paymentOutCreate.html',
            controller: 'paymentOutCreateCtrl',
            backdrop: 'static',
            keyboard: false,
            resolve: {
                title: function () {
                    return 'انشاء سند صرف جديد';
                }
            }
        });
    };

    /**************************************************************
     *                                                            *
     * Person Model                                               *
     *                                                            *
     *************************************************************/
    this.openPersonCreateModel = function () {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/person/personCreateUpdate.html',
            controller: 'personCreateUpdateCtrl',
            backdrop: 'static',
            keyboard: false,
            size: 'lg',
            resolve: {
                title: function () {
                    return 'انشاء مستخدم جديد';
                },
                action: function () {
                    return 'create';
                },
                person: function () {
                    return undefined;
                }
            }
        });
    };

    this.openPersonUpdateModel = function (person) {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/person/personCreateUpdate.html',
            controller: 'personCreateUpdateCtrl',
            backdrop: 'static',
            keyboard: false,
            size: 'lg',
            resolve: {
                title: function () {
                    return 'تعديل بيانات مستخدم';
                },
                action: function () {
                    return 'update';
                },
                person: function () {
                    return person;
                }
            }
        });
    };
    /**************************************************************
     *                                                            *
     * Student Model                                              *
     *                                                            *
     *************************************************************/
    this.openStudentCreateModel = function () {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/student/studentCreateUpdate.html',
            controller: 'studentCreateUpdateCtrl',
            backdrop: 'static',
            keyboard: false,
            size: 'lg',
            resolve: {
                title: function () {
                    return 'انشاء طالب جديد';
                },
                action: function () {
                    return 'create';
                },
                student: function () {
                    return undefined;
                }
            }
        });
    };

    this.openStudentUpdateModel = function (student) {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/student/studentCreateUpdate.html',
            controller: 'studentCreateUpdateCtrl',
            backdrop: 'static',
            keyboard: false,
            size: 'lg',
            resolve: {
                title: function () {
                    return 'تعديل بيانات طالب';
                },
                action: function () {
                    return 'update';
                },
                student: function () {
                    return student;
                }
            }
        });
    };
    /**************************************************************
     *                                                            *
     * Team Model                                                 *
     *                                                            *
     *************************************************************/
    this.openTeamCreateModel = function () {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/team/teamCreateUpdate.html',
            controller: 'teamCreateUpdateCtrl',
            backdrop: 'static',
            keyboard: false,
            size: 'lg',
            resolve: {
                title: function () {
                    return 'انشاء مجموعة جديدة';
                },
                action: function () {
                    return 'create';
                },
                team: function () {
                    return undefined;
                }
            }
        });
    };

    this.openTeamUpdateModel = function (team) {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/team/teamCreateUpdate.html',
            controller: 'teamCreateUpdateCtrl',
            backdrop: 'static',
            keyboard: false,
            size: 'lg',
            resolve: {
                title: function () {
                    return 'تعديل بيانات مجموعة';
                },
                action: function () {
                    return 'update';
                },
                team: function () {
                    return team;
                }
            }
        });
    };

}]);

app.service('ReportModelProvider', ['$uibModal', function ($uibModal) {
    /**************************************************************
     *                                                            *
     * Report.OfferByBranch                                       *
     *                                                            *
     *************************************************************/
    this.openReportOfferByBranchModel = function () {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: "/ui/partials/report/offer/offerByBranch.html",
            controller: "offerByBranchCtrl",
            backdrop: 'static',
            keyboard: false
        });
    };
    /**************************************************************
     *                                                            *
     * Report.OfferByMaster                                       *
     *                                                            *
     *************************************************************/
    this.openReportOfferByMasterModel = function () {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: "/ui/partials/report/offer/offerByMaster.html",
            controller: "offerByMasterCtrl",
            backdrop: 'static',
            keyboard: false
        });
    };
    /**************************************************************
     *                                                            *
     * Report.OfferByPerson                                       *
     *                                                            *
     *************************************************************/
    this.openReportOfferByPersonModel = function () {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: "/ui/partials/report/offer/offerByPerson.html",
            controller: "offerByPersonCtrl",
            backdrop: 'static',
            keyboard: false
        });
    };
    /**************************************************************
     *                                                            *
     * Report.OfferById                                           *
     *                                                            *
     *************************************************************/
    this.openReportOfferByIdModel = function () {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: "/ui/partials/report/offer/offerById.html",
            controller: "offerByIdCtrl",
            backdrop: 'static',
            keyboard: false
        });
    };
    /**************************************************************
     *                                                            *
     * Report.AccountByBranch                                     *
     *                                                            *
     *************************************************************/
    this.openReportAccountByBranchModel = function () {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: "/ui/partials/report/account/accountByBranch.html",
            controller: "accountByBranchCtrl",
            backdrop: 'static',
            keyboard: false
        });
    };
    /**************************************************************
     *                                                            *
     * Report.AccountByMaster                                     *
     *                                                            *
     *************************************************************/
    this.openReportAccountByMasterModel = function () {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: "/ui/partials/report/account/accountByMaster.html",
            controller: "accountByMasterCtrl",
            backdrop: 'static',
            keyboard: false
        });
    };
    /**************************************************************
     *                                                            *
     * Report.AccountByCourse                                     *
     *                                                            *
     *************************************************************/
    this.openReportAccountByCourseModel = function () {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: "/ui/partials/report/account/accountByCourse.html",
            controller: "accountByCourseCtrl",
            backdrop: 'static',
            keyboard: false
        });
    };
    /**************************************************************
     *                                                            *
     * Report.PaymentByBranch                                     *
     *                                                            *
     *************************************************************/
    this.openReportPaymentByBranchModel = function () {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: "/ui/partials/report/payment/paymentByBranch.html",
            controller: "paymentByBranchCtrl",
            backdrop: 'static',
            keyboard: false
        });
    };
    /**************************************************************
     *                                                            *
     * Report.PaymentByMaster                                     *
     *                                                            *
     *************************************************************/
    this.openReportPaymentByMasterModel = function () {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: "/ui/partials/report/payment/paymentByMaster.html",
            controller: "paymentByMasterCtrl",
            backdrop: 'static',
            keyboard: false
        });
    };
    /**************************************************************
     *                                                            *
     * Report.PaymentByCourse                                     *
     *                                                            *
     *************************************************************/
    this.openReportPaymentByCourseModel = function () {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: "/ui/partials/report/payment/paymentByCourse.html",
            controller: "paymentByCourseCtrl",
            backdrop: 'static',
            keyboard: false
        });
    };
    /**************************************************************
     *                                                            *
     * Report.PaymentByAccountIn                                  *
     *                                                            *
     *************************************************************/
    this.openReportPaymentByAccountInModel = function () {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: "/ui/partials/report/payment/paymentByAccountIn.html",
            controller: "paymentByAccountInCtrl",
            backdrop: 'static',
            keyboard: false
        });
    };
    /**************************************************************
     *                                                            *
     * Report.ChartOffersCountAverageByBranch                     *
     *                                                            *
     *************************************************************/
    this.openReportChartOffersCountAverageByBranchModel = function () {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: "/ui/partials/report/chart/offersCountAverageByBranch.html",
            controller: "offersCountAverageByBranchCtrl",
            backdrop: 'static',
            keyboard: false
        });
    };
    /**************************************************************
     *                                                            *
     * Report.ChartOffersCountAverageByMaster                     *
     *                                                            *
     *************************************************************/
    this.openReportChartOffersCountAverageByMasterModel = function () {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: "/ui/partials/report/chart/offersCountAverageByMaster.html",
            controller: "offersCountAverageByMasterCtrl",
            backdrop: 'static',
            keyboard: false
        });
    };
    /**************************************************************
     *                                                            *
     * Report.ChartOffersPriceAverageByBranch                     *
     *                                                            *
     *************************************************************/
    this.openReportChartOffersPriceAverageByBranchModel = function () {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: "/ui/partials/report/chart/offersPriceAverageByBranch.html",
            controller: "offersPriceAverageByBranchCtrl",
            backdrop: 'static',
            keyboard: false
        });
    };
    /**************************************************************
     *                                                            *
     * Report.ChartOffersPriceAverageByMaster                     *
     *                                                            *
     *************************************************************/
    this.openReportChartOffersPriceAverageByMasterModel = function () {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: "/ui/partials/report/chart/offersPriceAverageByMaster.html",
            controller: "offersPriceAverageByMasterCtrl",
            backdrop: 'static',
            keyboard: false
        });
    };
    /**************************************************************
     *                                                            *
     * Report.BranchDetails                                       *
     *                                                            *
     *************************************************************/
    this.openReportBranchDetailsModel = function () {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: "/ui/partials/report/info/branchDetails.html",
            controller: "branchDetailsCtrl",
            backdrop: 'static',
            keyboard: false
        });
    };
    /**************************************************************
     *                                                            *
     * Report.MasterDetails                                       *
     *                                                            *
     *************************************************************/
    this.openReportMasterDetailsModel = function () {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: "/ui/partials/report/info/masterDetails.html",
            controller: "masterDetailsCtrl",
            backdrop: 'static',
            keyboard: false
        });
    };
    /**************************************************************
     *                                                            *
     * Report.CourseDetails                                       *
     *                                                            *
     *************************************************************/
    this.openReportCourseDetailsModel = function () {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: "/ui/partials/report/info/courseDetails.html",
            controller: "courseDetailsCtrl",
            backdrop: 'static',
            keyboard: false
        });
    };
    /**************************************************************
     *                                                            *
     * Report.UnRegisteredStudentDetailsByBranch                  *
     *                                                            *
     *************************************************************/
    this.openReportUnRegisteredStudentDetailsByBranchModel = function () {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: "/ui/partials/report/info/unRegisteredStudentDetailsByBranch.html",
            controller: "unRegisteredStudentDetailsByBranchCtrl",
            backdrop: 'static',
            keyboard: false
        });
    };
    /**************************************************************
     *                                                            *
     * Report.UnRegisteredStudentDetailsByMaster                  *
     *                                                            *
     *************************************************************/
    this.openReportUnRegisteredStudentDetailsByMasterModel = function () {
        $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: "/ui/partials/report/info/unRegisteredStudentDetailsByMaster.html",
            controller: "unRegisteredStudentDetailsByMasterCtrl",
            backdrop: 'static',
            keyboard: false
        });
    };

}]);

app.service('NotificationProvider', ['$http', function ($http) {

    this.notifyOne = function (code, title, message, type, receiver) {
        $http.post("/notifyOne?"
            + 'code=' + code
            + '&'
            + 'title=' + title
            + '&'
            + 'message=' + message
            + '&'
            + 'type=' + type
            + '&'
            + 'receiver=' + receiver);
    };
    this.notifyAll = function (code, title, message, type) {
        $http.post("/notifyAll?"
            + 'code=' + code
            + '&'
            + 'title=' + title
            + '&'
            + 'message=' + message
            + '&'
            + 'type=' + type
        );
    };
    this.notifyOthers = function (code, title, message, type) {
        $http.post("/notifyOthers?"
            + 'code=' + code
            + '&'
            + 'title=' + title
            + '&'
            + 'message=' + message
            + '&'
            + 'type=' + type
        );
    };

}]);

