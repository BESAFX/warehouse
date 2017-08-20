app.controller('paymentByAccountInCtrl', ['BranchService', 'AccountService', '$scope', '$rootScope', '$timeout', '$uibModalInstance',
    function (BranchService, AccountService, $scope, $rootScope, $timeout, $uibModalInstance) {
        $timeout(function () {
            $scope.buffer = {};
            $scope.accounts = [];
            BranchService.fetchBranchCombo().then(function (data) {
                $scope.branches = data;
            });
        }, 1500);

        $scope.findAccountsByBranch = function () {
            AccountService.findByBranch($scope.buffer.branch.id).then(function (data) {
                $scope.accounts = data;
            })
        };

        $scope.submit = function () {
            var listId = [];
            for (var i = 0; i < $scope.buffer.accountsList.length; i++) {
                listId[i] = $scope.buffer.accountsList[i].id;
            }
            if ($scope.buffer.startDate && $scope.buffer.endDate) {
                window.open('/report/PaymentByAccountIn?'
                    + "accountList=" + listId + "&"
                    + "startDate=" + $scope.buffer.startDate.getTime() + "&"
                    + "endDate=" + $scope.buffer.endDate.getTime());
            } else {
                window.open('/report/PaymentByAccountIn?'
                    + "accountList=" + listId);
            }
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };
    }]);