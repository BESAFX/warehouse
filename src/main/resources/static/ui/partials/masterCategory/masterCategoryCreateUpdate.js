app.controller('masterCategoryCreateUpdateCtrl', ['MasterCategoryService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'title', 'action', 'masterCategory',
    function (MasterCategoryService, $scope, $rootScope, $timeout, $log, $uibModalInstance, title, action, masterCategory) {

        $scope.masterCategory = masterCategory;

        $scope.title = title;

        $scope.action = action;

        $scope.submit = function () {
            switch ($scope.action) {
                case 'create' :
                    MasterCategoryService.create($scope.masterCategory).then(function (data) {
                        $uibModalInstance.close(data);
                    });
                    break;
                case 'update' :
                    MasterCategoryService.update($scope.masterCategory).then(function (data) {
                        $scope.masterCategory = data;
                    });
                    break;
            }
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

    }]);