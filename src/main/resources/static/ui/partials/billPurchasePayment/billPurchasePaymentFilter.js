app.controller('billPurchasePaymentFilterCtrl', ['$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance',
    function ($scope, $rootScope, $timeout, $log, $uibModalInstance) {

        $scope.addSortBy = function () {
            var sortBy = {};
            $scope.pageBillPurchasePayment.sorts.push(sortBy);
        };

        $scope.submit = function () {
            $scope.pageBillPurchasePayment.page = $scope.pageBillPurchasePayment.currentPage - 1;
            $uibModalInstance.close($scope.paramBillPurchasePayment);
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 800);

    }]);