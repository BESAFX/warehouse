app.controller('paymentByMasterCategoryCtrl', ['BranchService' ,'MasterCategoryService', '$scope', '$rootScope', '$timeout', '$uibModalInstance',
    function (BranchService ,MasterCategoryService, $scope, $rootScope, $timeout, $uibModalInstance) {

        $scope.buffer = {};
        $scope.buffer.branchesList = [];
        $scope.buffer.masterCategoriesList = [];
        $scope.branches = [];
        $scope.masterCategories = [];

        $timeout(function () {
            BranchService.fetchBranchCombo().then(function (data) {
                $scope.branches = data;
            });
            MasterCategoryService.findAllCombo().then(function (data) {
                $scope.masterCategories = data;
            })
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
            var masterCategoryIds = [];
            angular.forEach($scope.buffer.masterCategoriesList, function (masterCategory) {
                masterCategoryIds.push(masterCategory.id);
            });
            param.push('masterCategoryIds=');
            param.push(masterCategoryIds);
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
            console.info(param.join(""));
            window.open('/report/PaymentByMasterCategories?' + param.join(""));
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };
    }]);