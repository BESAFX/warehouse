app.controller('billBuyFilterCtrl', ['PersonService', 'BranchService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'title',
    function (PersonService, BranchService, $scope, $rootScope, $timeout, $log, $uibModalInstance, title) {

        $scope.modalTitle = title;

        $scope.addSortBy = function () {
            var sortBy = {};
            $scope.pageBillBuy.sorts.push(sortBy);
        };

        $scope.submit = function () {
            $scope.pageBillBuy.page = $scope.pageBillBuy.currentPage - 1;
            $uibModalInstance.close($scope.paramBillBuy);
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 600);

    }]);