app.controller("paymentOutCtrl", ['PaymentService', 'BranchService', 'ModalProvider', '$rootScope', '$scope', '$timeout', '$log', '$state',
    function (PaymentService, BranchService, ModalProvider, $rootScope, $scope, $timeout, $log, $state) {

        //
        $scope.items = [];
        $scope.items.push({'id': 1, 'type': 'link', 'name': 'البرامج', 'link': 'menu'});
        $scope.items.push({'id': 2, 'type': 'title', 'name': 'السندات'});
        //

        $scope.buffer = {};

        $scope.buffer.exportType = 'PDF';

        $scope.buffer.orientation = 'Portrait';

        $scope.buffer.reportTitle = 'كشف سندات الصرف';

        $scope.payments = [];

        $scope.selected = {};

        $scope.columns = [
            {'name': 'رقم السند', 'view': false, 'groupBy': false, 'sortBy': false},
            {'name': 'تاريخ السند', 'view': false, 'groupBy': false, 'sortBy': false},
            {'name': 'قيمة السند', 'view': false, 'groupBy': false, 'sortBy': false},
            {'name': 'وجهة الصرف', 'view': false, 'groupBy': false, 'sortBy': false},
            {'name': 'بيان السند', 'view': false, 'groupBy': false, 'sortBy': false},
            {'name': 'مدخل السند', 'view': false, 'groupBy': false, 'sortBy': false}
        ];

        $scope.variables = [
            {'name': 'المتوسط الحسابي للسندات', 'expression': 'amountNumber', 'operation': 'Average'},
            {'name': 'المجموع الكلي للسندات', 'expression': 'amountNumber', 'operation': 'Sum'},
            {'name': 'أكبر قيمة للسندات', 'expression': 'amountNumber', 'operation': 'Highest'},
            {'name': 'أقل قيمة للسندات', 'expression': 'amountNumber', 'operation': 'Lowest'}
        ];

        $scope.buffer.groupVariables = [];

        $scope.buffer.tableVariables = [];

        $timeout(function () {
            $scope.sideOpacity = 1;
            BranchService.fetchTableDataSummery().then(function (data) {
                $scope.branches = data;
                $scope.buffer.branch = $scope.branches[0];
                $scope.buffer.type = 'ايرادات اساسية';
            });
        }, 2000);

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 1500);

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
        };

        $scope.delete = function (payment) {
            if (payment) {
                $rootScope.showConfirmNotify("حذف البيانات", "هل تود حذف السند فعلاً؟", "error", "fa-ban", function () {
                    PaymentService.remove(payment.id).then(function () {

                    });
                });
                return;
            }
            $rootScope.showConfirmNotify("حذف البيانات", "هل تود حذف السند فعلاً؟", "error", "fa-ban", function () {
                PaymentService.remove($scope.selected.id).then(function () {

                });
            });
        };

        $scope.filter = function () {
            var search = [];
            if ($scope.buffer.paymentCodeFrom) {
                search.push('paymentCodeFrom=');
                search.push($scope.buffer.paymentCodeFrom);
                search.push('&');
            }
            if ($scope.buffer.paymentCodeTo) {
                search.push('paymentCodeTo=');
                search.push($scope.buffer.paymentCodeTo);
                search.push('&');
            }
            if ($scope.buffer.paymentDateFrom) {
                search.push('paymentDateFrom=');
                search.push(moment($scope.buffer.paymentDateFrom).valueOf());
                search.push('&');
            }
            if ($scope.buffer.paymentDateTo) {
                search.push('paymentDateTo=');
                search.push(moment($scope.buffer.paymentDateTo).valueOf());
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
                search.push('personBranch=');
                search.push($scope.buffer.branch.id);
                search.push('&');
            }
            search.push('type=');
            search.push('مصروفات');

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
                    'name': ' [ ' + $scope.buffer.branch.code + ' ] ' + $scope.buffer.branch.name
                });
            });
        };

        $scope.print = function () {
            var search = [];
            if ($scope.buffer.paymentCodeFrom) {
                search.push('paymentCodeFrom=');
                search.push($scope.buffer.paymentCodeFrom);
                search.push('&');
            }
            if ($scope.buffer.paymentCodeTo) {
                search.push('paymentCodeTo=');
                search.push($scope.buffer.paymentCodeTo);
                search.push('&');
            }
            if ($scope.buffer.paymentDateFrom) {
                search.push('paymentDateFrom=');
                search.push(moment($scope.buffer.paymentDateFrom).valueOf());
                search.push('&');
            }
            if ($scope.buffer.paymentDateTo) {
                search.push('paymentDateTo=');
                search.push(moment($scope.buffer.paymentDateTo).valueOf());
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
            search.push('type=');
            search.push('مصروفات');
            search.push('&');
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

            window.open('/report/dynamic/payment?' + search.join(""));
        };

    }]);