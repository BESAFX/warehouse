app.controller('masterFilterCtrl', ['$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'title',
    function ($scope, $rootScope, $timeout, $log, $uibModalInstance, title) {

        $scope.modalTitle = title;

        $scope.buffer = {};

        $scope.buffer.branch = $scope.branches[0];

        $scope.submit = function () {
            $uibModalInstance.close($scope.buffer);
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 1500);

    }]);