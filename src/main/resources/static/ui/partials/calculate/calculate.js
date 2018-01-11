function calculateCtrl (
    PersonService,
    BranchService,
    PaymentService,
    PaymentOutService,
    BillBuyTypeService,
    BillBuyService,
    BankService,
    ModalProvider,
    $scope,
    $rootScope,
    $timeout,
    $uibModal) {

    /**************************************************************************************************************
     *                                                                                                            *
     * General                                                                                                    *
     *                                                                                                            *
     **************************************************************************************************************/
    $timeout(function () {
        PersonService.findAllCombo().then(function (data) {
            $scope.persons = data;
        });
        BranchService.fetchBranchMasterCourse().then(function (data) {
            $scope.branches = data;
            $scope.clear();
        });
        $scope.fetchBillBuyTypeTableData();
        window.componentHandler.upgradeAllRegistered();
    }, 1500);
    $scope.clear = function () {
        $scope.buffer = {};
        $scope.buffer.branch = $scope.branches[0];
    };

    /**************************************************************************************************************
     *                                                                                                            *
     * Payment                                                                                                    *
     *                                                                                                            *
     **************************************************************************************************************/
    //
    $scope.itemsPayment = [];
    $scope.itemsPayment.push({'id': 1, 'type': 'link', 'name': 'البرامج', 'link': 'menu'});
    $scope.itemsPayment.push({'id': 2, 'type': 'title', 'name': 'سندات القبض'});
    //
    $scope.deletePayment = function (payment) {
        $rootScope.showConfirmNotify("حذف البيانات", "هل تود حذف السند فعلاً؟", "error", "fa-ban", function () {
            PaymentService.remove(payment.id).then(function () {
                var index = $scope.payments.indexOf(payment);
                $scope.payments.splice(index, 1);
                $scope.setSelectedPayment($scope.payments[0]);
            });
        });
    };
    $scope.filterPayment = function () {
        var modalInstance = $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/payment/paymentFilter.html',
            controller: 'paymentFilterCtrl',
            scope: $scope,
            backdrop: 'static',
            keyboard: false,
            size: 'lg',
            resolve: {
                title: function () {
                    return 'البحث فى سندات القبض';
                }
            }
        });

        modalInstance.result.then(function (buffer) {

            var search = [];
            if (buffer.paymentCodeFrom) {
                search.push('paymentCodeFrom=');
                search.push(buffer.paymentCodeFrom);
                search.push('&');
            }
            if (buffer.paymentCodeTo) {
                search.push('paymentCodeTo=');
                search.push(buffer.paymentCodeTo);
                search.push('&');
            }
            if (buffer.paymentDateFrom) {
                search.push('paymentDateFrom=');
                search.push(moment(buffer.paymentDateFrom).valueOf());
                search.push('&');
            }
            if (buffer.paymentDateTo) {
                search.push('paymentDateTo=');
                search.push(moment(buffer.paymentDateTo).valueOf());
                search.push('&');
            }
            if (buffer.amountFrom) {
                search.push('amountFrom=');
                search.push(buffer.amountFrom);
                search.push('&');
            }
            if (buffer.amountTo) {
                search.push('amountTo=');
                search.push(buffer.amountTo);
                search.push('&');
            }
            if (buffer.firstName) {
                search.push('firstName=');
                search.push(buffer.firstName);
                search.push('&');
            }
            if (buffer.secondName) {
                search.push('secondName=');
                search.push(buffer.secondName);
                search.push('&');
            }
            if (buffer.thirdName) {
                search.push('thirdName=');
                search.push(buffer.thirdName);
                search.push('&');
            }
            if (buffer.forthName) {
                search.push('forthName=');
                search.push(buffer.forthName);
                search.push('&');
            }
            if (buffer.dateFrom) {
                search.push('dateFrom=');
                search.push(moment(buffer.dateFrom).valueOf());
                search.push('&');
            }
            if (buffer.dateTo) {
                search.push('dateTo=');
                search.push(moment(buffer.dateTo).valueOf());
                search.push('&');
            }
            if (buffer.studentIdentityNumber) {
                search.push('studentIdentityNumber=');
                search.push(buffer.studentIdentityNumber);
                search.push('&');
            }
            if (buffer.studentMobile) {
                search.push('studentMobile=');
                search.push(buffer.studentMobile);
                search.push('&');
            }
            if (buffer.coursePriceFrom) {
                search.push('coursePriceFrom=');
                search.push(buffer.coursePriceFrom);
                search.push('&');
            }
            if (buffer.coursePriceTo) {
                search.push('coursePriceTo=');
                search.push(buffer.coursePriceTo);
                search.push('&');
            }
            if (buffer.branch) {
                search.push('branch=');
                search.push(buffer.branch.id);
                search.push('&');
            }
            if (buffer.master) {
                search.push('master=');
                search.push(buffer.master.id);
                search.push('&');
            }
            if (buffer.course) {
                search.push('course=');
                search.push(buffer.course.id);
                search.push('&');
            }
            if (buffer.type) {
                search.push('type=');
                search.push(buffer.type);
                search.push('&');
            }
            PaymentService.filter(search.join("")).then(function (data) {
                $scope.payments = data;
                $scope.totalAmount = 0;
                angular.forEach(data, function (payment) {
                    $scope.totalAmount+=payment.amountNumber;
                });
                $scope.itemsPayment = [];
                $scope.itemsPayment.push({'id': 1, 'type': 'link', 'name': 'البرامج', 'link': 'menu'});
                $scope.itemsPayment.push({'id': 2, 'type': 'title', 'name': 'سندات القبض', 'style': 'font-weight:bold'});
                $scope.itemsPayment.push({'id': 3, 'type': 'title', 'name': 'فرع', 'style': 'font-weight:bold'});
                $scope.itemsPayment.push({
                    'id': 4,
                    'type': 'title',
                    'name': ' [ ' + buffer.branch.code + ' ] ' + buffer.branch.name
                });
                if (buffer.master) {
                    $scope.itemsPayment.push({'id': 5, 'type': 'title', 'name': 'تخصص', 'style': 'font-weight:bold'});
                    $scope.itemsPayment.push({
                        'id': 6,
                        'type': 'title',
                        'name': ' [ ' + buffer.master.code + ' ] ' + buffer.master.name
                    });
                }
                if (buffer.course) {
                    $scope.itemsPayment.push({'id': 7, 'type': 'title', 'name': 'رقم الدورة', 'style': 'font-weight:bold'});
                    $scope.itemsPayment.push({
                        'id': 8,
                        'type': 'title',
                        'name': ' [ ' + buffer.course.code + ' ] '
                    });
                }
            });

        }, function () {
            console.info('Modal dismissed at: ' + new Date());
        });
    };
    $scope.newPayment = function () {
        ModalProvider.openPaymentCreateModel().result.then(function (data) {

        });
    };
    $scope.printList = function () {
        var paymentIds = [];
        angular.forEach($scope.payments, function (payment) {
            if(payment.isSelected){
                paymentIds.push(payment.id);
            }
        });
        window.open('/report/PaymentsByList?' + "paymentIds=" + paymentIds + "&isSummery=" + false);
    };
    $scope.printListSummery = function () {
        var paymentIds = [];
        angular.forEach($scope.payments, function (payment) {
            if(payment.isSelected){
                paymentIds.push(payment.id);
            }
        });
        window.open('/report/PaymentsByList?' + "paymentIds=" + paymentIds + "&isSummery=" + true);
    };
    $scope.rowMenuPayment = [
        {
            html: '<div class="drop-menu"> انشاء سند جديد <span class="fa fa-plus-square-o fa-lg"></span></div>',
            enabled: function () {
                return true
            },
            click: function ($itemScope, $event, value) {
                ModalProvider.openPaymentCreateModel();
            }
        },
        {
            html: '<div class="drop-menu"> تعديل السند <span class="fa fa-edit fa-lg"></span></div>',
            enabled: function () {
                return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_PAYMENT_UPDATE']);
            },
            click: function ($itemScope, $event, value) {
                ModalProvider.openPaymentUpdateModel($itemScope.payment);
            }
        },
        {
            html: '<div class="drop-menu"> حذف السند <span class="fa fa-minus-square-o fa-lg"></span></div>',
            enabled: function () {
                return true
            },
            click: function ($itemScope, $event, value) {
                $scope.deletePayment($itemScope.payment);
            }
        },
        {
            html: '<div class="drop-menu"> طباعة السند <span class="fa fa-print fa-lg"></span></div>',
            enabled: function () {
                return true
            },
            click: function ($itemScope, $event, value) {
                window.open('report/CashReceipt/' + $itemScope.payment.id);
            }
        },
        {
            html: '<div class="drop-menu">طباعة تقرير مفصل<span class="fa fa-print fa-lg"></span></div>',
            enabled: function () {
                return true
            },
            click: function ($itemScope, $event, value) {
                $scope.printList();
            }
        },
        {
            html: '<div class="drop-menu">طباعة تقرير مختصر<span class="fa fa-print fa-lg"></span></div>',
            enabled: function () {
                return true
            },
            click: function ($itemScope, $event, value) {
                $scope.printListSummery();
            }
        },
    ];

    /**************************************************************************************************************
     *                                                                                                            *
     * Payment Out                                                                                                *
     *                                                                                                            *
     **************************************************************************************************************/
    $scope.paymentOuts = [];
    //
    $scope.itemsPaymentOut = [];
    $scope.itemsPaymentOut.push({'id': 1, 'type': 'link', 'name': 'البرامج', 'link': 'menu'});
    $scope.itemsPaymentOut.push({'id': 2, 'type': 'title', 'name': 'سندات الصرف'});
    //
    $scope.selectedPaymentOut = {};
    $scope.setSelectedPaymentOut = function (object) {
        if (object) {
            angular.forEach($scope.paymentOuts, function (paymentOut) {
                if (object.id == paymentOut.id) {
                    $scope.selectedPaymentOut = paymentOut;
                    return paymentOut.isSelected = true;
                } else {
                    return paymentOut.isSelected = false;
                }
            });
        }
    };
    $scope.newPaymentOut = function () {
        ModalProvider.openPaymentOutCreateModel().result.then(function (data) {
            $scope.paymentOuts.splice(0, 0, data);
        }, function () {
            console.info('PaymentOutCreateModel Closed.');
        });
    };
    $scope.deletePaymentOut = function (paymentOut) {
        $rootScope.showConfirmNotify("حذف البيانات", "هل تود حذف السند فعلاً؟", "error", "fa-ban", function () {
            PaymentOutService.remove(paymentOut.id).then(function () {
                var index = $scope.paymentOuts.indexOf(paymentOut);
                $scope.paymentOuts.splice(index, 1);
                $scope.setSelectedPaymentOut($scope.paymentOuts[0]);
            });
        });
    };
    $scope.filterPaymentOut = function () {
        var modalInstance = $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/paymentOut/paymentOutFilter.html',
            controller: 'paymentOutFilterCtrl',
            scope: $scope,
            backdrop: 'static',
            keyboard: false,
            resolve: {
                title: function () {
                    return 'البحث فى سندات الصرف';
                }
            }
        });

        modalInstance.result.then(function (buffer) {

            var search = [];
            if (buffer.codeFrom) {
                search.push('codeFrom=');
                search.push(buffer.codeFrom);
                search.push('&');
            }
            if (buffer.codeTo) {
                search.push('codeTo=');
                search.push(buffer.codeTo);
                search.push('&');
            }
            if (buffer.dateFrom) {
                search.push('dateFrom=');
                search.push(moment(buffer.dateFrom).valueOf());
                search.push('&');
            }
            if (buffer.dateTo) {
                search.push('dateTo=');
                search.push(moment(buffer.dateTo).valueOf());
                search.push('&');
            }
            if (buffer.amountFrom) {
                search.push('amountFrom=');
                search.push(buffer.amountFrom);
                search.push('&');
            }
            if (buffer.amountTo) {
                search.push('amountTo=');
                search.push(buffer.amountTo);
                search.push('&');
            }
            if (buffer.branch) {
                search.push('branchId=');
                search.push(buffer.branch.id);
                search.push('&');
            }

            PaymentOutService.filter(search.join("")).then(function (data) {
                $scope.paymentOuts = data;
                $scope.setSelectedPaymentOut(data[0]);
                $scope.itemsPaymentOut = [];
                $scope.itemsPaymentOut.push({'id': 1, 'type': 'link', 'name': 'البرامج', 'link': 'menu'});
                $scope.itemsPaymentOut.push({'id': 2, 'type': 'title', 'name': 'السندات', 'style': 'font-weight:bold'});
                $scope.itemsPaymentOut.push({'id': 3, 'type': 'title', 'name': 'فرع', 'style': 'font-weight:bold'});
                $scope.itemsPaymentOut.push({
                    'id': 4,
                    'type': 'title',
                    'name': ' [ ' + buffer.branch.code + ' ] ' + buffer.branch.name
                });
            });

        }, function () {
            console.info('Modal dismissed at: ' + new Date());
        });
    };
    $scope.rowMenu = [
        {
            html: '<div class="drop-menu">انشاء سند جديد<span class="fa fa-pencil fa-lg"></span></div>',
            enabled: function () {
                return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_PAYMENT_OUT_CREATE']);
            },
            click: function ($itemScope, $event, value) {
                $scope.newPaymentOut();
            }
        },
        {
            html: '<div class="drop-menu">حذف السند<span class="fa fa-trash fa-lg"></span></div>',
            enabled: function () {
                return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_PAYMENT_OUT_DELETE']);
            },
            click: function ($itemScope, $event, value) {
                $scope.deletePaymentOut($itemScope.paymentOut);
            }
        }
    ];

    /**************************************************************************************************************
     *                                                                                                            *
     * BillBuyType                                                                                                *
     *                                                                                                            *
     **************************************************************************************************************/
    $scope.selectedBillBuyType = {};
    $scope.billBuyTypes = [];
    $scope.setSelectedBillBuyType = function (object) {
        if (object) {
            angular.forEach($scope.billBuyTypes, function (billBuyType) {
                if (object.id == billBuyType.id) {
                    $scope.selectedBillBuyType = billBuyType;
                    return billBuyType.isSelected = true;
                } else {
                    return billBuyType.isSelected = false;
                }
            });
        }
    };
    $scope.fetchBillBuyTypeTableData = function () {
        BillBuyTypeService.findAll().then(function (data) {
            $scope.billBuyTypes = data;
            $scope.setSelectedBillBuyType(data[0]);
        })
    };
    $scope.newBillBuyType = function () {
        ModalProvider.openBillBuyTypeCreateModel().result.then(function (data) {
            $scope.billBuyTypes.splice(0,0,data);
        }, function () {
            console.info('BillBuyTypeCreateModel Closed.');
        });
    };
    $scope.deleteBillBuyType = function (billBuyType) {
        $rootScope.showConfirmNotify("حذف البيانات", "هل تود حذف النوع فعلاً؟", "error", "fa-ban", function () {
            BillBuyTypeService.remove(billBuyType.id).then(function () {
                var index = $scope.billBuyTypes.indexOf(billBuyType);
                $scope.billBuyTypes.splice(index, 1);
                $scope.setSelectedBillBuyType($scope.billBuyTypes[0]);
            });
        });
    };
    $scope.rowMenuBillBuyType = [
        {
            html: '<div class="drop-menu"> انشاء نوع جديد <span class="fa fa-plus-square-o fa-lg"></span></div>',
            enabled: function () {
                return true
            },
            click: function ($itemScope, $event, value) {
                $scope.newBillBuyType();
            }
        },
        {
            html: '<div class="drop-menu"> تعديل بيانات النوع <span class="fa fa-edit fa-lg"></span></div>',
            enabled: function () {
                return true
            },
            click: function ($itemScope, $event, value) {
                ModalProvider.openBillBuyTypeUpdateModel($itemScope.billBuyType);
            }
        },
        {
            html: '<div class="drop-menu"> حذف النوع <span class="fa fa-minus-square-o fa-lg"></span></div>',
            enabled: function () {
                return true
            },
            click: function ($itemScope, $event, value) {
                $scope.deleteBillBuyType($itemScope.billBuyType);
            }
        }
    ];

    /**************************************************************************************************************
     *                                                                                                            *
     * BillBuy                                                                                                    *
     *                                                                                                            *
     **************************************************************************************************************/
    //
    $scope.itemsBillBuy = [];
    $scope.itemsBillBuy.push({'id': 1, 'type': 'link', 'name': 'البرامج', 'link': 'menu'});
    $scope.itemsBillBuy.push({'id': 2, 'type': 'title', 'name': 'فواتير الشراء'});
    //
    $scope.selectedBillBuy = {};
    $scope.billBuys = [];
    $scope.setSelectedBillBuyType = function (object) {
        if (object) {
            angular.forEach($scope.billBuys, function (billBuy) {
                if (object.id == billBuy.id) {
                    $scope.selectedBillBuy = billBuy;
                    return billBuy.isSelected = true;
                } else {
                    return billBuy.isSelected = false;
                }
            });
        }
    };
    $scope.newBillBuy = function () {
        ModalProvider.openBillBuyCreateModel().result.then(function (data) {
            $scope.billBuys.splice(0, 0, data);
            $scope.setSelectedBillBuyType(data);
        }, function () {
            console.info('BillBuyCreateModel Closed.');
        });
    };
    $scope.createFastBill = function () {
        BillBuyService.create($scope.billBuy).then(function (data) {
            $scope.billBuys.splice(0, 0, data);
            $scope.billBuy = {};
            $scope.form.$setPristine();
            $scope.setSelectedBillBuyType(data);
        });
    };
    $scope.deleteBillBuy = function (billBuy) {
        $rootScope.showConfirmNotify("حذف البيانات", "هل تود حذف الفاتورة فعلاً؟", "error", "fa-ban", function () {
            BillBuyService.remove(billBuy.id).then(function () {
                var index = $scope.billBuys.indexOf(billBuy);
                $scope.billBuys.splice(index, 1);
                $scope.setSelectedBillBuyType($scope.billBuys[0]);
            });
        });
    };
    $scope.searchBillBuy = function () {
        var search = [];
        if ($scope.buffer.codeFrom) {
            search.push('codeFrom=');
            search.push($scope.buffer.codeFrom);
            search.push('&');
        }
        if ($scope.buffer.codeTo) {
            search.push('codeTo=');
            search.push($scope.buffer.codeTo);
            search.push('&');
        }
        if ($scope.buffer.dateFrom) {
            search.push('dateFrom=');
            search.push($scope.buffer.dateFrom.getTime());
            search.push('&');
        }
        if ($scope.buffer.dateTo) {
            search.push('dateTo=');
            search.push($scope.buffer.dateTo.getTime());
            search.push('&');
        }
        if ($scope.buffer.amountFrom) {
            search.push('amountFrom=');
            search.push($scope.buffer.amountFrom);
            search.push('&');
        }
        if ($scope.buffer.amountTo) {
            search.push('amountTo=');
            search.push($scope.buffer.amountTo);
            search.push('&');
        }
        if ($scope.buffer.branch) {
            search.push('branchId=');
            search.push($scope.buffer.branch.id);
            search.push('&');
        }
        BillBuyService.filter(search.join("")).then(function (data) {
            $scope.billBuys = data;
            $scope.setSelectedBillBuyType(data[0]);
            $scope.itemsBillBuy = [];
            $scope.itemsBillBuy.push({'id': 1, 'type': 'link', 'name': 'البرامج', 'link': 'menu'});
            $scope.itemsBillBuy.push({'id': 2, 'type': 'title', 'name': 'فواتير الشراء', 'style': 'font-weight:bold'});
            $scope.itemsBillBuy.push({'id': 3, 'type': 'title', 'name': 'فرع', 'style': 'font-weight:bold'});
            $scope.itemsBillBuy.push({
                'id': 4,
                'type': 'title',
                'name': ' [ ' + $scope.buffer.branch.code + ' ] ' + $scope.buffer.branch.name
            });
        });

    };
    $scope.filterBillBuy = function () {
        var modalInstance = $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/billBuy/billBuyFilter.html',
            controller: 'billBuyFilterCtrl',
            scope: $scope,
            backdrop: 'static',
            keyboard: false,
            resolve: {
                title: function () {
                    return 'البحث فى فواتير الشراء';
                }
            }
        });

        modalInstance.result.then(function (buffer) {

            $scope.buffer = buffer;

            $scope.searchBillBuy();

        }, function () {
            console.info('BillBuyFilterModel Closed.');
        });
    };
    $scope.rowMenuBillBuy = [
        {
            html: '<div class="drop-menu"> انشاء فاتورة جديدة <span class="fa fa-plus-square-o fa-lg"></span></div>',
            enabled: function () {
                return true
            },
            click: function ($itemScope, $event, value) {
                $scope.newBillBuy();
            }
        },
        {
            html: '<div class="drop-menu">تعديل بيانات الفاتورة<span class="fa fa-edit fa-lg"></span></div>',
            enabled: function () {
                return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_BILL_BUY_UPDATE']);
            },
            click: function ($itemScope, $event, value) {
                ModalProvider.openBillBuyUpdateModel($itemScope.billBuy);
            }
        },
        {
            html: '<div class="drop-menu"> حذف الفاتورة <span class="fa fa-minus-square-o fa-lg"></span></div>',
            enabled: function () {
                return true
            },
            click: function ($itemScope, $event, value) {
                $scope.deleteBillBuy($itemScope.billBuy);
            }
        }
    ];

    /**************************************************************************************************************
     *                                                                                                            *
     * Bank                                                                                                       *
     *                                                                                                            *
     **************************************************************************************************************/
    //
    $scope.itemsBank = [];
    $scope.itemsBank.push({'id': 1, 'type': 'link', 'name': 'البرامج', 'link': 'menu'});
    $scope.itemsBank.push({'id': 2, 'type': 'title', 'name': 'الحسابات البنكية'});
    //
    $scope.selectedBank = {};
    $scope.banks = [];
    $scope.setSelectedBank = function (object) {
        if (object) {
            angular.forEach($scope.banks, function (bank) {
                if (object.id === bank.id) {
                    $scope.selectedBank = bank;
                    return bank.isSelected = true;
                } else {
                    return bank.isSelected = false;
                }
            });
        }
    };
    $scope.filterBank = function () {
        var modalInstance = $uibModal.open({
            animation: true,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: '/ui/partials/bank/bankFilter.html',
            controller: 'bankFilterCtrl',
            scope: $scope,
            backdrop: 'static',
            keyboard: false,
            resolve: {
                title: function () {
                    return 'البحث فى الحسابات البنكية';
                }
            }
        });

        modalInstance.result.then(function (buffer) {
            var search = [];
            if (buffer.code) {
                search.push('code=');
                search.push(buffer.code);
                search.push('&');
            }
            if (buffer.name) {
                search.push('name=');
                search.push(buffer.name);
                search.push('&');
            }
            if (buffer.branchName) {
                search.push('branchName=');
                search.push(buffer.branchName);
                search.push('&');
            }
            if (buffer.stockFrom) {
                search.push('stockFrom=');
                search.push(buffer.stockFrom);
                search.push('&');
            }
            if (buffer.stockTo) {
                search.push('stockTo=');
                search.push(buffer.stockTo);
                search.push('&');
            }
            if (buffer.branch) {
                search.push('branchId=');
                search.push(buffer.branch.id);
                search.push('&');
            }

            BankService.filter(search.join("")).then(function (data) {
                $scope.banks = data;
                $scope.setSelectedBank(data[0]);
                $scope.itemsBank = [];
                $scope.itemsBank.push({'id': 1, 'type': 'link', 'name': 'البرامج', 'link': 'menu'});
                $scope.itemsBank.push({'id': 2, 'type': 'title', 'name': 'الحسابات البنكية', 'style': 'font-weight:bold'});
                $scope.itemsBank.push({'id': 3, 'type': 'title', 'name': 'فرع', 'style': 'font-weight:bold'});
                $scope.itemsBank.push({
                    'id': 4,
                    'type': 'title',
                    'name': ' [ ' + buffer.branch.code + ' ] ' + buffer.branch.name
                });
            });

        }, function () {
            console.info('BankFilterModel Closed.');
        });
    };
    $scope.newBank = function () {
        ModalProvider.openBankCreateModel().result.then(function (data) {
            $scope.banks.splice(0,0,data);
        }, function () {
            console.info('BankCreateModel Closed.');
        });
    };
    $scope.deleteBank = function (bank) {
        $rootScope.showConfirmNotify("حذف البيانات", "هل تود حذف الفاتورة فعلاً؟", "error", "fa-ban", function () {
            BankService.remove(bank.id).then(function () {
                var index = $scope.banks.indexOf(bank);
                $scope.banks.splice(index, 1);
                $scope.setSelectedBillBuyType($scope.banks[0]);
            });
        });
    };
    $scope.openDepositModel = function (bank) {
        ModalProvider.openDepositCreateModel(bank);
    };
    $scope.openWithdrawModel = function (bank) {
        ModalProvider.openWithdrawCreateModel(bank);
    };
    $scope.rowMenuBank = [
        {
            html: '<div class="drop-menu">انشاء حساب جديد<span class="fa fa-pencil fa-lg"></span></div>',
            enabled: function () {
                return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_BANK_CREATE']);
            },
            click: function ($itemScope, $event, value) {
                $scope.newBank();
            }
        },
        {
            html: '<div class="drop-menu">تعديل بيانات الحساب<span class="fa fa-edit fa-lg"></span></div>',
            enabled: function () {
                return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_BANK_UPDATE']);
            },
            click: function ($itemScope, $event, value) {
                ModalProvider.openBankUpdateModel($itemScope.bank);
            }
        },
        {
            html: '<div class="drop-menu">سحب<span class="fa fa-upload fa-lg"></span></div>',
            enabled: function () {
                return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_WITHDRAW_CREATE']);
            },
            click: function ($itemScope, $event, value) {
                $scope.openWithdrawModel($itemScope.bank);
            }
        },
        {
            html: '<div class="drop-menu">إيداع<span class="fa fa-download fa-lg"></span></div>',
            enabled: function () {
                return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_DEPOSIT_CREATE']);
            },
            click: function ($itemScope, $event, value) {
                $scope.openDepositModel($itemScope.bank);
            }
        },
        {
            html: '<div class="drop-menu">حذف الحساب<span class="fa fa-trash fa-lg"></span></div>',
            enabled: function () {
                return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_BANK_DELETE']);
            },
            click: function ($itemScope, $event, value) {
                $scope.deleteBank($itemScope.bank);
            }
        }
    ];

};

calculateCtrl.$inject = [
    'PersonService',
    'BranchService',
    'PaymentService',
    'PaymentOutService',
    'BillBuyTypeService',
    'BillBuyService',
    'BankService',
    'ModalProvider',
    '$scope',
    '$rootScope',
    '$timeout',
    '$uibModal'];

app.controller("calculateCtrl", calculateCtrl);