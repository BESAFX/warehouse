app.controller('accountAttachUploadCtrl', ['AttachTypeService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'title', 'wrappers',
    function (AttachTypeService, $scope, $rootScope, $timeout, $log, $uibModalInstance, title, wrappers) {

        $scope.modalTitle = title;

        $scope.wrappers = wrappers;

        $scope.submit = function () {
            $uibModalInstance.close($scope.wrappers);
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 1500);

        $timeout(function () {
            AttachTypeService.findAll().then(function (data) {
               $scope.attachTypes = data;
            });
        }, 2000);

    }]);