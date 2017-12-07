app.controller("paymentCtrl", ['AccountService', 'PaymentService', 'BranchService', 'MasterService', 'CourseService', 'ModalProvider', '$rootScope', '$scope', '$timeout', '$log', '$state', '$uibModal',
    function (AccountService, PaymentService, BranchService, MasterService, CourseService, ModalProvider, $rootScope, $scope, $timeout, $log, $state, $uibModal) {

        //
        $scope.items = [];
        $scope.items.push({'id': 1, 'type': 'link', 'name': 'البرامج', 'link': 'menu'});
        $scope.items.push({'id': 2, 'type': 'title', 'name': 'السندات'});
        //

        $scope.buffer = {};

        $timeout(function () {
            BranchService.fetchBranchMasterCourse().then(function (data) {
                $scope.branches = data;
                $scope.buffer.branch = $scope.branches[0];
                $scope.buffer.type = 'ايرادات اساسية';
            });
        }, 2000);

        $scope.setSelected = function (object) {
            if (object) {
                angular.forEach($scope.payments, function (payment) {
                    if (object.id == payment.id) {
                        $scope.selected = payment;
                        return payment.isSelected = true;
                    } else {
                        return payment.isSelected = false;
                    }
                });
            }
        };

        $scope.clear = function () {
            $scope.buffer = {};
            $scope.buffer.branch = $scope.branches[0];
            $scope.buffer.type = 'ايرادات اساسية';
        };

        $scope.delete = function (payment) {
            if (payment) {
                $rootScope.showConfirmNotify("حذف البيانات", "هل تود حذف السند فعلاً؟", "error", "fa-ban", function () {
                    PaymentService.remove(payment.id).then(function () {
                        var index = $scope.payments.indexOf(payment);
                        $scope.payments.splice(index, 1);
                        $scope.setSelected($scope.payments[0]);
                    });
                });
                return;
            }
            $rootScope.showConfirmNotify("حذف البيانات", "هل تود حذف السند فعلاً؟", "error", "fa-ban", function () {
                PaymentService.remove($scope.selected.id).then(function () {
                    var index = $scope.payments.indexOf($scope.selected);
                    $scope.payments.splice(index, 1);
                    $scope.setSelected($scope.payments[0]);
                });
            });
        };

        $scope.filter = function () {
            var modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: '/ui/partials/payment/paymentFilter.html',
                controller: 'paymentFilterCtrl',
                scope: $scope,
                backdrop: 'static',
                keyboard: false,
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
                    $scope.setSelected(data[0]);
                    $scope.items = [];
                    $scope.items.push({'id': 1, 'type': 'link', 'name': 'البرامج', 'link': 'menu'});
                    $scope.items.push({'id': 2, 'type': 'title', 'name': 'السندات', 'style': 'font-weight:bold'});
                    $scope.items.push({'id': 3, 'type': 'title', 'name': 'فرع', 'style': 'font-weight:bold'});
                    $scope.items.push({
                        'id': 4,
                        'type': 'title',
                        'name': ' [ ' + buffer.branch.code + ' ] ' + buffer.branch.name
                    });
                    if (buffer.master) {
                        $scope.items.push({'id': 5, 'type': 'title', 'name': 'تخصص', 'style': 'font-weight:bold'});
                        $scope.items.push({
                            'id': 6,
                            'type': 'title',
                            'name': ' [ ' + buffer.master.code + ' ] ' + buffer.master.name
                        });
                    }
                    if (buffer.course) {
                        $scope.items.push({'id': 7, 'type': 'title', 'name': 'رقم الدورة', 'style': 'font-weight:bold'});
                        $scope.items.push({
                            'id': 8,
                            'type': 'title',
                            'name': ' [ ' + buffer.course.code + ' ] '
                        });
                    }
                });

            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        };

        $scope.newPayment = function () {
            ModalProvider.openPaymentCreateModel();
        };

        $scope.printList = function () {
            var paymentIds = [];
            angular.forEach($scope.payments, function (payment) {
               paymentIds.push(payment.id);
            });
            window.open('/report/PaymentsByList?' + "paymentIds=" + paymentIds + "&isSummery=" + false);
        };

        $scope.printListSummery = function () {
            var paymentIds = [];
            angular.forEach($scope.payments, function (payment) {
                paymentIds.push(payment.id);
            });
            window.open('/report/PaymentsByList?' + "paymentIds=" + paymentIds + "&isSummery=" + true);
        };

        $scope.rowMenu = [
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
                    return true
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
                    $scope.delete($itemScope.payment);
                }
            },
            {
                html: '<div class="drop-menu">طباعة تقرير بالقائمة<span class="fa fa-print fa-lg"></span></div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.printList();
                }
            },
            {
                html: '<div class="drop-menu">طباعة تقرير مختصر بالقائمة<span class="fa fa-print fa-lg"></span></div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.printListSummery();
                }
            }
        ];

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 1500);

    }]);