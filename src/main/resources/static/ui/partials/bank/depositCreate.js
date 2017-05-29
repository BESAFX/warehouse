app.controller('depositCreateCtrl', ['BankService', 'DepositService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'title', 'bank',
    function (BankService, DepositService, $scope, $rootScope, $timeout, $log, $uibModalInstance, title, bank) {

        $scope.clear = function () {
            $scope.deposit = {};
            if ($scope.form) {
                $scope.form.$setPristine()
            }
        };

        $scope.clear();

        $scope.title = title;

        $scope.deposit.bank = bank;

        $timeout(function () {
            BankService.filter().then(function (data) {
                $scope.banks = data;
            });
        }, 2000);

        $scope.submit = function () {
            DepositService.create($scope.deposit).then(function (data) {
                $scope.clear();
                $scope.form.$setPristine();
                BankService.filter().then(function (data) {
                    $scope.banks = data;
                    $scope.deposit.bank = data.bank;
                });
            });
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

    }]);