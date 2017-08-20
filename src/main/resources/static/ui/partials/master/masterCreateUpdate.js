app.controller('masterCreateUpdateCtrl', ['BranchService', 'MasterService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'title', 'action', 'master',
    function (BranchService, MasterService, $scope, $rootScope, $timeout, $log, $uibModalInstance, title, action, master) {

        $timeout(function () {
            BranchService.fetchBranchCombo().then(function (data) {
                $scope.branches = data;
            });
        }, 1500);

        if (master) {
            $scope.master = master;
        } else {
            $scope.master = {};
        }

        $scope.title = title;

        $scope.action = action;

        $scope.submit = function () {
            switch ($scope.action) {
                case 'create' :
                    MasterService.create($scope.master).then(function (data) {
                        $scope.master = {};
                        $scope.form.$setPristine();
                    });
                    break;
                case 'update' :
                    MasterService.update($scope.master).then(function (data) {
                        $scope.master = data;
                    });
                    break;
            }
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

    }]);