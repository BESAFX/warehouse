app.controller('billBuyByBranchCtrl', ['BranchService', 'BillBuyTypeService','$scope', '$rootScope', '$timeout', '$uibModalInstance',
    function (BranchService, BillBuyTypeService, $scope, $rootScope, $timeout, $uibModalInstance) {

        $scope.buffer = {};
        $scope.buffer.branchesList = [];
        $scope.branches = [];

        $timeout(function () {
            BranchService.fetchBranchCombo().then(function (data) {
                $scope.branches = data;
            });
            BillBuyTypeService.findAll().then(function (data) {
                $scope.billBuyTypes = data;
            });
        }, 1500);

        $scope.submit = function () {
            var param = [];
            //
            if ($scope.buffer.startDate) {
                param.push('startDate=');
                param.push($scope.buffer.startDate.getTime());
                param.push('&');
            }
            if ($scope.buffer.endDate) {
                param.push('endDate=');
                param.push($scope.buffer.endDate.getTime());
                param.push('&');
            }
            //
            var branchIds = [];
            angular.forEach($scope.buffer.branchesList, function (branch) {
                branchIds.push(branch.id);
            });
            param.push('branchIds=');
            param.push(branchIds);
            param.push('&');
            //
            //
            var billBuyTypeIds = [];
            angular.forEach($scope.buffer.billBuyTypesList, function (billBuyType) {
                billBuyTypeIds.push(billBuyType.id);
            });
            param.push('billBuyTypeIds=');
            param.push(billBuyTypeIds);
            param.push('&');
            //
            param.push('exportType=');
            param.push($scope.buffer.exportType);
            param.push('&');
            //
            param.push('title=');
            param.push($scope.buffer.title);
            param.push('&');
            //

            window.open('/report/BillBuyByBranches?' + param.join(""));
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

    }]);