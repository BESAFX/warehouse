app.controller('paymentFilterCtrl', ['PersonService', 'BranchService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'title',
    function (PersonService, BranchService, $scope, $rootScope, $timeout, $log, $uibModalInstance, title) {

        $scope.modalTitle = title;

        $scope.submit = function () {
            $uibModalInstance.close($scope.buffer);
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 600);

    }]);