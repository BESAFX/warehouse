app.controller('accountPaymentCtrl', ['AccountService', 'PaymentService', '$rootScope', '$scope', '$timeout', '$log', '$uibModalInstance', 'account',
    function (AccountService, PaymentService, $rootScope, $scope, $timeout, $log, $uibModalInstance, account) {

        $scope.payment = {};
        $scope.payment.account = account;

        $scope.buffer = {};

        $timeout(function () {
            $scope.fetchPrices();
        }, 1000);

        $scope.fetchPrices = function () {
            AccountService.findRequiredPrice($scope.payment.account.id).then(function (data) {
                $scope.buffer.requiredPrice = data;
                AccountService.findRemainPrice($scope.payment.account.id).then(function (data) {
                    $scope.buffer.remainPrice = data;
                    AccountService.findPaidPrice($scope.payment.account.id).then(function (data) {
                        $scope.buffer.paidPrice = data;
                    });
                });
            });
        };

        $scope.submit = function () {
            PaymentService.create($scope.payment).then(function (data) {
                $scope.payment = {};
                $scope.buffer = {};
                $scope.form.$setPristine();
                $uibModalInstance.dismiss('cancel');
            });
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

    }]);