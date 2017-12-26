app.controller('accountPaymentCtrl', ['AccountService', 'PaymentService', 'PaymentAttachService', '$rootScope', '$scope', '$timeout', '$log', '$uibModalInstance', 'account',
    function (AccountService, PaymentService, PaymentAttachService, $rootScope, $scope, $timeout, $log, $uibModalInstance, account) {

        $scope.buffer = {};
        $scope.payment = {};
        $scope.payment.account = account;

        $scope.submit = function () {
            PaymentService.create($scope.payment).then(function (data) {
                $uibModalInstance.close(data);
            });
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

    }]);