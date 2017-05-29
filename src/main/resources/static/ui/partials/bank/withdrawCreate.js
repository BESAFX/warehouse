app.controller('withdrawCreateCtrl', ['BankService', 'WithdrawService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'title', 'bank',
    function (BankService, WithdrawService, $scope, $rootScope, $timeout, $log, $uibModalInstance, title, bank) {

        $scope.clear = function () {
            $scope.withdraw = {};
            if ($scope.form) {
                $scope.form.$setPristine()
            }
        };

        $scope.clear();

        $scope.title = title;

        $scope.withdraw.bank = bank;

        $timeout(function () {
            BankService.filter().then(function (data) {
                $scope.banks = data;
            })
        }, 2000);

        $scope.submit = function () {
            WithdrawService.create($scope.withdraw).then(function (data) {
                $scope.clear();
                $scope.form.$setPristine();
                BankService.filter().then(function (data) {
                    $scope.banks = data;
                    $scope.withdraw.bank = data.bank;
                });
            });
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

    }]);