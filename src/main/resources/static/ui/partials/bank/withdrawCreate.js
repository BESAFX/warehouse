app.controller('withdrawCreateCtrl', ['BankService', 'WithdrawService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'title', 'bank',
    function (BankService, WithdrawService, $scope, $rootScope, $timeout, $log, $uibModalInstance, title, bank) {

        $scope.title = title;

        $scope.withdraw.bank = bank;

        $timeout(function () {
            BankService.fetchTableDataCombo().then(function (data) {
                $scope.banks = data;
            })
        }, 2000);

        $scope.submit = function () {
            WithdrawService.create($scope.withdraw).then(function (data) {
                $uibModalInstance.close(data);
            });
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

    }]);