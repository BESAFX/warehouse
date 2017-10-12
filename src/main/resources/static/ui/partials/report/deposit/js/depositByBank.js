app.controller('depositByBankCtrl', ['BankService', '$scope', '$rootScope', '$timeout', '$uibModalInstance',
    function (BankService, $scope, $rootScope, $timeout, $uibModalInstance) {

        $scope.buffer = {};
        $scope.buffer.banksList = [];
        $scope.banks = [];

        $timeout(function () {
            BankService.fetchTableDataCombo().then(function (data) {
                $scope.banks = data;
            });
        }, 1500);

        $scope.submit = function () {
            var param = [];
            //
            if ($scope.buffer.startDate) {
                param.push('startDate=');
                param.push($scope.buffer.startDate.getTime());
                param.push('&');
            }
            if ($scope.buffer.endDate) {
                param.push('endDate=');
                param.push($scope.buffer.endDate.getTime());
                param.push('&');
            }
            //
            var bankIds = [];
            angular.forEach($scope.buffer.banksList, function (bank) {
                bankIds.push(bank.id);
            });
            param.push('bankIds=');
            param.push(bankIds);
            param.push('&');
            //
            param.push('exportType=');
            param.push($scope.buffer.exportType);
            param.push('&');
            //
            param.push('title=');
            param.push($scope.buffer.title);
            param.push('&');
            //

            window.open('/report/DepositByBanks?' + param.join(""));
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

    }]);