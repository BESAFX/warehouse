app.controller('accountConditionCreateCtrl', ['AccountConditionService', 'ModalProvider', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'account',
    function (AccountConditionService, ModalProvider, $scope, $rootScope, $timeout, $log, $uibModalInstance, account) {

        $scope.accountCondition = {};
        $scope.accountCondition.account = account;

        $scope.submit = function () {
            AccountConditionService.create($scope.accountCondition).then(function (data) {
                $uibModalInstance.close(data);
            });
        };


        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 1500);

    }]);