app.controller('paymentFilterCtrl', ['PersonService', 'BranchService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'title',
    function (PersonService, BranchService, $scope, $rootScope, $timeout, $log, $uibModalInstance, title) {

        $scope.modalTitle = title;

        $scope.addSortBy = function () {
            var sortBy = {};
            $scope.pagePayment.sorts.push(sortBy);
        };

        $scope.submit = function () {
            $scope.pagePayment.page = $scope.pagePayment.currentPage - 1;
            $uibModalInstance.close($scope.paramPaymentIn);
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 600);

    }]);