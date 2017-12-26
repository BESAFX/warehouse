app.controller('depositCreateCtrl', ['BankService', 'DepositService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'title', 'bank',
    function (BankService, DepositService, $scope, $rootScope, $timeout, $log, $uibModalInstance, title, bank) {

        $scope.title = title;
        $scope.deposit = {};
        $scope.deposit.bank = bank;

        $timeout(function () {
            BankService.fetchTableData().then(function (data) {
                $scope.banks = data;
            });
        }, 2000);

        $scope.submit = function () {
            DepositService.create($scope.deposit).then(function (data) {
                $uibModalInstance.close(data);
            });
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

    }]);