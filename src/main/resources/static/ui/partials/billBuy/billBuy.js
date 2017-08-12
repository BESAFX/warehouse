app.controller("billBuyCtrl", ['BranchService', 'BillBuyService', 'BillBuyTypeService', 'ModalProvider', '$scope', '$rootScope', '$timeout', '$state', '$uibModal',
    function (BranchService, BillBuyService, BillBuyTypeService, ModalProvider, $scope, $rootScope, $timeout, $state, $uibModal) {

        //
        $scope.items = [];
        $scope.items.push({'id': 1, 'type': 'link', 'name': 'البرامج', 'link': 'menu'});
        $scope.items.push({'id': 2, 'type': 'title', 'name': 'فواتير الشراء'});
        //

        $scope.buffer = {};

        $scope.selected = {};

        $scope.buffer.exportType = 'PDF';

        $scope.buffer.orientation = 'Portrait';

        $scope.buffer.reportTitle = 'كشف الفواتير';

        $scope.billBuys = [];

        $scope.columns = [
            {'name': 'رقم الفاتورة', 'view': false, 'groupBy': false, 'sortBy': false},
            {'name': 'تاريخ الفاتورة', 'view': false, 'groupBy': false, 'sortBy': false},
            {'name': 'قيمة الفاتورة', 'view': false, 'groupBy': false, 'sortBy': false},
            {'name': 'نوع الحساب', 'view': false, 'groupBy': false, 'sortBy': false},
            {'name': 'مصدر الفاتورة', 'view': false, 'groupBy': false, 'sortBy': false},
            {'name': 'بيان الفاتورة', 'view': false, 'groupBy': false, 'sortBy': false},
            {'name': 'مدخل الفاتورة', 'view': false, 'groupBy': false, 'sortBy': false}
        ];

        $scope.variables = [
            {'name': 'المتوسط الحسابي للفواتير', 'expression': 'amount', 'operation': 'Average'},
            {'name': 'المجموع الكلي للفواتير', 'expression': 'amount', 'operation': 'Sum'},
            {'name': 'أكبر قيمة للفواتير', 'expression': 'amount', 'operation': 'Highest'},
            {'name': 'أقل قيمة للفواتير', 'expression': 'amount', 'operation': 'Lowest'}
        ];

        $scope.buffer.groupVariables = [];

        $scope.buffer.tableVariables = [];

        $timeout(function () {
            BranchService.fetchTableDataSummery().then(function (data) {
                $scope.branches = data;
                $scope.buffer.branch = $scope.branches[0];
            });
        }, 2000);

        $scope.setSelected = function (object) {
            if (object) {
                angular.forEach($scope.billBuys, function (billBuy) {
                    if (object.id == billBuy.id) {
                        $scope.selected = billBuy;
                        return billBuy.isSelected = true;
                    } else {
                        return billBuy.isSelected = false;
                    }
                });
            }
        };

        $scope.delete = function (billBuy) {
            if (billBuy) {
                $rootScope.showConfirmNotify("حذف البيانات", "هل تود حذف الفاتورة فعلاً؟", "error", "fa-ban", function () {
                    BillBuyService.remove(billBuy.id).then(function () {
                        var index = $scope.billBuys.indexOf(billBuy);
                        $scope.billBuys.splice(index, 1);
                        $scope.setSelected($scope.billBuys[0]);
                    });
                });
                return;
            }
            $rootScope.showConfirmNotify("حذف البيانات", "هل تود حذف الفاتورة فعلاً؟", "error", "fa-ban", function () {
                BillBuyService.remove($scope.selected.id).then(function () {
                    var index = $scope.billBuys.indexOf($scope.selected);
                    $scope.billBuys.splice(index, 1);
                    $scope.setSelected($scope.billBuys[0]);
                });
            });
        };

        $scope.filter = function () {
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
                BillBuyService.filter(search.join("")).then(function (data) {
                    $scope.billBuys = data;
                    $scope.setSelected(data[0]);
                    $scope.items = [];
                    $scope.items.push({'id': 1, 'type': 'link', 'name': 'البرامج', 'link': 'menu'});
                    $scope.items.push({'id': 2, 'type': 'title', 'name': 'فواتير الشراء', 'style': 'font-weight:bold'});
                    $scope.items.push({'id': 3, 'type': 'title', 'name': 'فرع', 'style': 'font-weight:bold'});
                    $scope.items.push({
                        'id': 4,
                        'type': 'title',
                        'name': ' [ ' + buffer.branch.code + ' ] ' + buffer.branch.name
                    });
                });

            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        };

        $scope.print = function () {
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
                search.push(moment($scope.buffer.dateFrom).valueOf());
                search.push('&');
            }
            if ($scope.buffer.dateTo) {
                search.push('dateTo=');
                search.push(moment($scope.buffer.dateTo).valueOf());
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
            if ($scope.buffer.exportType) {
                search.push('exportType=');
                search.push($scope.buffer.exportType);
                search.push('&');
            }
            if ($scope.buffer.reportTitle) {
                search.push('reportTitle=');
                search.push($scope.buffer.reportTitle);
                search.push('&');
            }
            if ($scope.buffer.orientation) {
                search.push('orientation=');
                search.push($scope.buffer.orientation);
                search.push('&');
            }
            if ($scope.buffer.groupVariables) {
                search.push('groupVariables=');
                search.push(JSON.stringify($scope.buffer.groupVariables));
                search.push('&');
            }
            if ($scope.buffer.tableVariables) {
                search.push('tableVariables=');
                search.push(JSON.stringify($scope.buffer.tableVariables));
                search.push('&');
            }
            if ($scope.buffer.branch) {
                search.push('branchId=');
                search.push($scope.buffer.branch.id);
                search.push('&');
            }
            search.push('columns=');
            search.push(JSON.stringify($scope.columns));

            window.open('/report/dynamic/billBuy?' + search.join(""));
        };

        $scope.clear = function () {
            $scope.buffer = {};
            $scope.buffer.branch = $scope.branches[0];
        };

        $scope.rowMenu = [
            {
                html: '<div class="drop-menu"> انشاء فاتورة جديدة <span class="fa fa-plus-square-o fa-lg"></span></div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    ModalProvider.openBillBuyCreateModel();
                }
            },
            {
                html: '<div class="drop-menu"> حذف الفاتورة <span class="fa fa-minus-square-o fa-lg"></span></div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.delete($itemScope.billBuy);
                }
            },
            {
                html: '<div class="drop-menu"> التفاصيل <span class="fa fa-info fa-lg"></span></div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    ModalProvider.openBillBuyDetailsModel($itemScope.billBuy);
                }
            }
        ];

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 1500);

    }]);