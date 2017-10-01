app.controller('offerByBranchCtrl', ['BranchService', '$scope', '$rootScope', '$timeout', '$uibModalInstance',
    function (BranchService, $scope, $rootScope, $timeout, $uibModalInstance) {

        $scope.buffer = {};
        $scope.buffer.branchesList = [];
        $scope.branches = [];

        $timeout(function () {
            BranchService.fetchBranchCombo().then(function (data) {
                $scope.branches = data;
            });
        }, 1500);

        $scope.submit = function () {
            var ids = [];
            angular.forEach($scope.buffer.branchesList, function (branch) {
                ids.push(branch.id);
            });
            if ($scope.buffer.startDate && $scope.buffer.endDate) {
                window.open('/report/OfferByBranches?'
                    + 'ids=' + ids + '&'
                    + 'title=' + $scope.buffer.title + '&'
                    + 'exportType=' + $scope.buffer.exportType + '&'
                    + "startDate=" + $scope.buffer.startDate.getTime() + "&"
                    + "endDate=" + $scope.buffer.endDate.getTime());
            } else {
                window.open('/report/OfferByBranches?'
                    + 'ids=' + ids + '&'
                    + 'title=' + $scope.buffer.title + '&'
                    + 'exportType=' + $scope.buffer.exportType);
            }
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

    }]);