app.controller('accountContractCtrl', ['$scope', '$rootScope', '$timeout', '$uibModalInstance', 'account',
    function ($scope, $rootScope, $timeout, $uibModalInstance, account) {

        $scope.buffer = {};

        $scope.account = account;

        $scope.submit = function () {
            var accountIds = [];
            accountIds.push(account.id);
            switch ($scope.buffer.reportType) {
                case "ZIP":
                    window.open('/report/account/contract/zip?accountIds=' + accountIds + "&&contractType=" + $scope.buffer.contractType + "&&reportFileName=" + $scope.buffer.reportFileName);
                    break;
                case "PDF":
                    window.open('/report/account/contract/pdf?accountIds=' + accountIds + "&&contractType=" + $scope.buffer.contractType + "&&reportFileName=" + $scope.buffer.reportFileName + "&&hijriDate=" + $scope.buffer.hijriDate);
                    break;
            }
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 600);

    }]);