app.controller("billBuyCtrl", ['BranchService', 'BillBuyService', 'BillBuyTypeService', 'ModalProvider', '$scope', '$rootScope', '$timeout', '$state',
    function (BranchService, BillBuyService, BillBuyTypeService, ModalProvider, $scope, $rootScope, $timeout, $state) {

        $timeout(function () {

            $scope.sideOpacity = 1;

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

            BranchService.fetchTableDataWithoutMasters().then(function (data) {
                $scope.branches = data;
                $scope.buffer.branch = $scope.branches[0];
            });

        }, 2000);

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 1500);

        $scope.setSelected = function (object) {
            if (object) {
                angular.forEach($scope.billBuys, function (billBuy) {
                    if (object.id == billBuy.id) {
                        billBuy.isSelected = true;
                        object = billBuy;
                    } else {
                        return billBuy.isSelected = false;
                    }
                });
                $scope.selected = object;
            }
        };

        $scope.reload = function () {
            $state.reload();
        };

        $scope.openCreateModel = function () {
            ModalProvider.openBillBuyCreateModel();
        };

        $scope.filter = function () {
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
            if ($scope.buffer.branch) {
                search.push('branchId=');
                search.push($scope.buffer.branch.id);
                search.push('&');
            }
            BillBuyService.filter(search.join("")).then(function (data) {
                $scope.billBuys = data;
                $scope.setSelected(data[0]);
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
        };

    }]);