app.controller('accountPaymentCtrl', ['AccountService', 'PaymentService', 'PaymentBookService', '$rootScope', '$scope', '$timeout', '$log', '$uibModalInstance', 'account',
    function (AccountService, PaymentService, PaymentBookService, $rootScope, $scope, $timeout, $log, $uibModalInstance, account) {

        $scope.buffer = {};
        $scope.payment = {};

        AccountService.findOne(account.id).then(function (data) {
            $scope.payment.account = data;
            PaymentBookService.findByBranchCombo($scope.payment.account.course.master.branch.id).then(function (data) {
                $scope.paymentBooks = data;
            });
        });

        $scope.submit = function () {
            PaymentService.create($scope.payment).then(function (data) {
                $rootScope.showConfirmNotify("السندات", "هل تود طباعة السند ؟", "notification", "fa-info", function () {
                    window.open('report/CashReceipt/' + data.id);
                });
                $uibModalInstance.close(data);
            });
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

    }]);