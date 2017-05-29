app.controller("depositCtrl", ['DepositService', 'ModalProvider', '$rootScope', '$scope', '$window', '$timeout', '$log', '$state',
    function (DepositService, ModalProvider, $rootScope, $scope, $window, $timeout, $log, $state) {

        $timeout(function () {
            $scope.sideOpacity = 1;

            $scope.buffer = {};

            $scope.selected = {};

            $scope.buffer.exportType = 'PDF';

            $scope.buffer.orientation = 'Portrait';

            $scope.buffer.reportTitle = 'كشف عمليات الإيداع البنكي';

            $scope.deposits = [];

            $scope.columns = [
                {'name': 'رقم الإيداع', 'view': false, 'groupBy': false, 'sortBy': false},
                {'name': 'قيمة الإيداع', 'view': false, 'groupBy': false, 'sortBy': false},
                {'name': 'اسم المودع', 'view': false, 'groupBy': false, 'sortBy': false},
                {'name': 'تاريخ الإيداع', 'view': false, 'groupBy': false, 'sortBy': false},
                {'name': 'جهة اصدار الشيك', 'view': false, 'groupBy': false, 'sortBy': false},
                {'name': 'رقم الشيك', 'view': false, 'groupBy': false, 'sortBy': false},
                {'name': 'تاريخ اصدار الشيك', 'view': false, 'groupBy': false, 'sortBy': false},
                {'name': 'مدخل الإيداع', 'view': false, 'groupBy': false, 'sortBy': false},
                {'name': 'ملاحظات الإيداع', 'view': false, 'groupBy': false, 'sortBy': false},
                {'name': 'رقم الحساب البنكي', 'view': false, 'groupBy': false, 'sortBy': false},
                {'name': 'اسم البنك', 'view': false, 'groupBy': false, 'sortBy': false},
                {'name': 'فرع البنك', 'view': false, 'groupBy': false, 'sortBy': false},
                {'name': 'الرصيد الافتتاحي', 'view': false, 'groupBy': false, 'sortBy': false},
                {'name': 'تاريخ الرصيد الافتتاحي', 'view': false, 'groupBy': false, 'sortBy': false},
                {'name': 'الرصيد الحالي', 'view': false, 'groupBy': false, 'sortBy': false},
                {'name': 'مدخل الحساب البنكي', 'view': false, 'groupBy': false, 'sortBy': false}
            ];

            $scope.variables = [
                {'name': 'المتوسط الحسابي لعمليات الإيداع', 'expression': 'amount', 'operation': 'Average'},
                {'name': 'المجموع الكلي لعمليات الإيداع', 'expression': 'amount', 'operation': 'Sum'},
                {'name': 'أكبر قيمة لعمليات الإيداع', 'expression': 'amount', 'operation': 'Highest'},
                {'name': 'أقل قيمة لعمليات الإيداع', 'expression': 'amount', 'operation': 'Lowest'}
            ];

            $scope.buffer.groupVariables = [];

            $scope.buffer.tableVariables = [];

        }, 2000);

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 1500);

        $scope.setSelected = function (object) {
            if (object) {
                angular.forEach($scope.deposits, function (deposit) {
                    if (object.id == deposit.id) {
                        deposit.isSelected = true;
                        object = deposit;
                    } else {
                        return deposit.isSelected = false;
                    }
                });
                $scope.selected = object;
            }
        };

        $scope.filter = function () {
            var search = [];
            if ($scope.buffer.code) {
                search.push('code=');
                search.push($scope.buffer.code);
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
            if ($scope.buffer.fromName) {
                search.push('fromName=');
                search.push($scope.buffer.fromName);
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

            if ($scope.buffer.bankCode) {
                search.push('bankCode=');
                search.push($scope.buffer.bankCode);
                search.push('&');
            }
            if ($scope.buffer.bankName) {
                search.push('bankName=');
                search.push($scope.buffer.bankName);
                search.push('&');
            }
            if ($scope.buffer.bankBranch) {
                search.push('bankBranch=');
                search.push($scope.buffer.bankBranch);
                search.push('&');
            }
            if ($scope.buffer.bankStockFrom) {
                search.push('bankStockFrom=');
                search.push($scope.buffer.bankStockFrom);
                search.push('&');
            }
            if ($scope.buffer.bankStockTo) {
                search.push('bankStockTo=');
                search.push($scope.buffer.bankStockTo);
                search.push('&');
            }

            DepositService.filter(search.join("")).then(function (data) {
                $scope.deposits = data;
                $scope.setSelected(data[0]);
            });
        };

        $scope.print = function () {
            var search = [];
            if ($scope.buffer.code) {
                search.push('code=');
                search.push($scope.buffer.code);
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
            if ($scope.buffer.fromName) {
                search.push('fromName=');
                search.push($scope.buffer.fromName);
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

            if ($scope.buffer.bankCode) {
                search.push('bankCode=');
                search.push($scope.buffer.bankCode);
                search.push('&');
            }
            if ($scope.buffer.bankName) {
                search.push('bankName=');
                search.push($scope.buffer.bankName);
                search.push('&');
            }
            if ($scope.buffer.bankBranch) {
                search.push('bankBranch=');
                search.push($scope.buffer.bankBranch);
                search.push('&');
            }
            if ($scope.buffer.bankStockFrom) {
                search.push('bankStockFrom=');
                search.push($scope.buffer.bankStockFrom);
                search.push('&');
            }
            if ($scope.buffer.bankStockTo) {
                search.push('bankStockTo=');
                search.push($scope.buffer.bankStockTo);
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
            search.push('columns=');
            search.push(JSON.stringify($scope.columns));

            window.open('/report/dynamic/deposit?' + search.join(""));
        };

        $scope.clear = function () {
            $scope.buffer = {};
        };

        $scope.reload = function () {
            $state.reload();
        };

        $scope.openCreateModel = function () {
            ModalProvider.openDepositCreateModel();
        };

    }]);
