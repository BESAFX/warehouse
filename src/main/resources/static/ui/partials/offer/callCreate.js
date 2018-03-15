app.controller('callCreateCtrl', ['CallService', 'ModalProvider', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'offer',
    function (CallService, ModalProvider, $scope, $rootScope, $timeout, $log, $uibModalInstance, offer) {

        $scope.call = {};
        $scope.call.offer = offer;

        $scope.submit = function () {
            CallService.create($scope.call).then(function (data) {
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