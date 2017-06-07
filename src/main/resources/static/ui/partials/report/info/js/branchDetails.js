app.controller('branchDetailsCtrl', ['BranchService', '$scope', '$rootScope', '$timeout', '$uibModalInstance',
    function (BranchService, $scope, $rootScope, $timeout, $uibModalInstance) {
        $scope.buffer = {};
        $timeout(function () {
            BranchService.fetchTableDataSummery().then(function (data) {
                $scope.branches = data;
            });
        }, 1500);

        $scope.submit = function () {
            var listId = [];
            for (var i = 0; i < $scope.buffer.branchesList.length; i++) {
                listId[i] = $scope.buffer.branchesList[i].id;
            }
            window.open('/report/BranchDetails?'
                + "branchesList=" + listId);
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };
    }]);