app.controller("paymentOutCtrl", ['PaymentService', 'ModalProvider', '$rootScope', '$scope', '$timeout', '$log', '$state',
    function (PaymentService, ModalProvider, $rootScope, $scope, $timeout, $log, $state) {

        $timeout(function () {

            $scope.sideOpacity = 1;

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

        }, 2000);

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 1500);

        $scope.setSelected = function (object) {
            if (object) {
                angular.forEach($scope.payments, function (payment) {
                    if (object.id == payment.id) {
                        payment.isSelected = true;
                        object = payment;
                    } else {
                        return payment.isSelected = false;
                    }
                });
                $scope.selected = object;
            }
        };

        $scope.reload = function () {
            $state.reload();
        };

        $scope.clear = function () {
            $scope.buffer = {};
        };

        $scope.delete = function () {
            PaymentService.remove($scope.selected.id).then(function () {
                noty({text: 'تم الحذف بنجاح', layout: 'topCenter', type: 'error', timeout: 5000});
                $scope.filter();
            })
        };

        $scope.openCreateModel = function () {
            ModalProvider.openPaymentOutCreateModel();
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
            search.push('type=');
            search.push('مصروفات');

            PaymentService.filter(search.join("")).then(function (data) {
                $scope.payments = data;
                $scope.setSelected(data[0]);
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