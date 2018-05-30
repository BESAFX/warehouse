app.controller('supplierFilterCtrl', ['$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance',
    function ($scope, $rootScope, $timeout, $log, $uibModalInstance) {

        $scope.addSortBy = function () {
            var sortBy = {};
            $scope.pageSupplier.sorts.push(sortBy);
        };

        $scope.submit = function () {
            $scope.pageSupplier.page = $scope.pageSupplier.currentPage - 1;
            $uibModalInstance.close($scope.paramSupplier);
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 800);

    }]);