app.controller('accountUpdatePriceCtrl', ['AccountService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', '$uibModal', 'account',
    function (AccountService, $scope, $rootScope, $timeout, $log, $uibModalInstance, $uibModal, account) {

        $scope.account = account;

        $scope.submit = function () {
            AccountService.update($scope.account).then(function (data) {
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