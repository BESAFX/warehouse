app.controller('confirmModalCtrl', [
    '$uibModalInstance',
    '$scope',
    'title',
    'icon',
    'message',
    function ($uibModalInstance,
              $scope,
              title,
              icon,
              message) {

        $scope.modalTitle = title;
        $scope.modalIcon = icon;
        $scope.modalMessage = message;

        $scope.submit = function () {
            $uibModalInstance.close(true);
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

       setTimeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 1500);

    }]);