app.controller('billBuyByBranchCtrl', ['BranchService', 'BillBuyTypeService', '$scope', '$rootScope', '$timeout', '$uibModalInstance',
    function (BranchService, BillBuyTypeService, $scope, $rootScope, $timeout, $uibModalInstance) {

        $scope.buffer = {};

        $scope.buffer.sorts = [];

        $scope.addSortBy = function () {
            var sortBy = {};
            $scope.buffer.sorts.push(sortBy);
        };

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
            if (branchIds.length > 0) {
                param.push('branchIds=');
                param.push(branchIds);
                param.push('&');
            }
            //
            //
            var billBuyTypeIds = [];
            angular.forEach($scope.buffer.billBuyTypesList, function (billBuyType) {
                billBuyTypeIds.push(billBuyType.id);
            });
            if (billBuyTypeIds.length > 0) {
                param.push('billBuyTypeIds=');
                param.push(billBuyTypeIds);
                param.push('&');
            }
            //
            param.push('exportType=');
            param.push($scope.buffer.exportType);
            param.push('&');
            //
            param.push('title=');
            param.push($scope.buffer.title);
            param.push('&');
            //

            param.push('sort=');
            param.push('billBuyType.name,asc');
            param.push('&');

            angular.forEach($scope.buffer.sorts, function (sortBy) {
                param.push('sort=');
                param.push(sortBy.name + ',' + sortBy.direction);
                param.push('&');
            });

            window.open('/report/BillBuyByBranches?' + param.join(""));
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $timeout(function () {
            BranchService.fetchBranchCombo().then(function (data) {
                $scope.branches = data;
            });
            BillBuyTypeService.findAll().then(function (data) {
                $scope.billBuyTypes = data;
            });
            window.componentHandler.upgradeAllRegistered();
        }, 600);

    }]);