app.controller('billPurchaseFilterCtrl', ['$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance',
    function ($scope, $rootScope, $timeout, $log, $uibModalInstance) {

        $scope.addSortBy = function () {
            var sortBy = {};
            $scope.pageBillPurchase.sorts.push(sortBy);
        };

        $scope.submit = function () {
            $scope.pageBillPurchase.page = $scope.pageBillPurchase.currentPage - 1;
            $uibModalInstance.close($scope.paramBillPurchase);
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 800);

    }]);