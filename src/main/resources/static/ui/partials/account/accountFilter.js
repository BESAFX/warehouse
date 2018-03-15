app.controller('accountFilterCtrl', ['PersonService', 'BranchService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'title',
    function (PersonService, BranchService, $scope, $rootScope, $timeout, $log, $uibModalInstance, title) {

        $scope.modalTitle = title;

        $scope.addSortBy = function () {
            var sortBy = {};
            $scope.pageAccount.sorts.push(sortBy);
        };

        $scope.submit = function () {
            $scope.pageAccount.page = $scope.pageAccount.currentPage - 1;
            $uibModalInstance.close($scope.paramAccount);
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 600);

    }]);