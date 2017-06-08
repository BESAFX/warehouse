app.controller('offerByBranchCtrl', ['BranchService', '$scope', '$rootScope', '$timeout', '$uibModalInstance',
    function (BranchService, $scope, $rootScope, $timeout, $uibModalInstance) {
        $scope.buffer = {};
        $timeout(function () {
            BranchService.fetchTableDataSummery().then(function (data) {
                $scope.branches = data;
            });
        }, 1500);

        $scope.submit = function () {
            if ($scope.buffer.startDate && $scope.buffer.endDate) {
                window.open('/report/OfferByBranch/'
                    + $scope.buffer.branch.id + "?"
                    + 'exportType=' + $scope.buffer.exportType + '&'
                    + "startDate=" + $scope.buffer.startDate.getTime() + "&"
                    + "endDate=" + $scope.buffer.endDate.getTime());
            } else {
                window.open('/report/OfferByBranch/' + $scope.buffer.branch.id + '?exportType=' + $scope.buffer.exportType);
            }
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

    }]);