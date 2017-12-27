app.controller('accountUpdateCtrl', ['AccountService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', '$uibModal', 'account',
    function (AccountService, $scope, $rootScope, $timeout, $log, $uibModalInstance, $uibModal, account) {

        $timeout(function () {
            AccountService.findOne(account.id).then(function (data) {
                $scope.account = data;
            });
        }, 600);

        $scope.submit = function () {
            AccountService.update($scope.account).then(function (data) {
                $scope.account = data;
            });
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 600);

    }]);