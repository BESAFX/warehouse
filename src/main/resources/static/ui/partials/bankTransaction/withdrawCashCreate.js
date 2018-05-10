app.controller('withdrawCashCreateCtrl', ['SellerService', 'BankTransactionService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance',
    function (SellerService, BankTransactionService, $scope, $rootScope, $timeout, $log, $uibModalInstance) {

        $scope.buffer = {};

        $scope.submit = function () {
            BankTransactionService.createWithdrawCash($scope.buffer.amount, $scope.buffer.note).then(function (data) {
                $uibModalInstance.close(data);
            });
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 600);

    }]);