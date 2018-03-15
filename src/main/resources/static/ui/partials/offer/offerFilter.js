app.controller('offerFilterCtrl', ['PersonService', 'BranchService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'title',
    function (PersonService, BranchService, $scope, $rootScope, $timeout, $log, $uibModalInstance, title) {

        $scope.modalTitle = title;

        $scope.addSortBy = function () {
            var sortBy = {};
            $scope.pageOffer.sorts.push(sortBy);
        };

        $scope.submit = function () {
            $scope.pageOffer.page = $scope.pageOffer.currentPage - 1;
            $uibModalInstance.close($scope.paramOffer);
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 600);

    }]);