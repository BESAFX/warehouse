app.controller('accountFilterCtrl', ['PersonService', 'BranchService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'title',
    function (PersonService, BranchService, $scope, $rootScope, $timeout, $log, $uibModalInstance, title) {

        $scope.modalTitle = title;

        $scope.submit = function () {
            $uibModalInstance.close($scope.bufferAccount);
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 1500);

    }]);