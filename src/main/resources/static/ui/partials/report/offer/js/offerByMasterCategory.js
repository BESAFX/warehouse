app.controller('offerByMasterCategoryCtrl', ['BranchService' ,'MasterCategoryService', '$scope', '$rootScope', '$timeout', '$uibModalInstance',
    function (BranchService ,MasterCategoryService, $scope, $rootScope, $timeout, $uibModalInstance) {

        $scope.buffer = {};

        $scope.buffer.branchesList = [];

        $scope.buffer.masterCategoriesList = [];

        $scope.branches = [];

        $scope.masterCategories = [];

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
            if(branchIds.length > 0){
                param.push('branchIds=');
                param.push(branchIds);
                param.push('&');
            }
            //
            var masterCategoryIds = [];
            angular.forEach($scope.buffer.masterCategoriesList, function (masterCategory) {
                masterCategoryIds.push(masterCategory.id);
            });
            if(masterCategoryIds.length > 0){
                param.push('masterCategoryIds=');
                param.push(masterCategoryIds);
                param.push('&');
            }
            //
            param.push('exportType=');
            param.push($scope.buffer.exportType);
            param.push('&');
            //
            param.push('registerOption=');
            param.push($scope.buffer.registerOption);
            param.push('&');
            //
            param.push('title=');
            param.push($scope.buffer.title);
            param.push('&');
            //

            angular.forEach($scope.buffer.sorts, function (sortBy) {
                param.push('sort=');
                param.push(sortBy.name + ',' + sortBy.direction);
                param.push('&');
            });

            window.open('/report/OfferByMasterCategories?' + param.join(""));
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $timeout(function () {
            BranchService.fetchBranchCombo().then(function (data) {
                $scope.branches = data;
            });
            MasterCategoryService.findAllCombo().then(function (data) {
                $scope.masterCategories = data;
            });
            window.componentHandler.upgradeAllRegistered();
        }, 600);


    }]);