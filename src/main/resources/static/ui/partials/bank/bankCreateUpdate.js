app.controller('bankCreateUpdateCtrl', ['BankService', 'BranchService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'title', 'action', 'bank',
    function (BankService, BranchService, $scope, $rootScope, $timeout, $log, $uibModalInstance, title, action, bank) {

        $scope.title = title;

        $scope.action = action;

        if (bank) {
            $scope.bank = bank;
        } else {
            $scope.bank = {};
        }

        $timeout(function () {
            BranchService.fetchTableDataSummery().then(function (data) {
                $scope.branches = data;
            });
        }, 1500);

        $scope.submit = function () {
            switch ($scope.action) {
                case 'create' :
                    BankService.create($scope.bank).then(function (data) {
                        $scope.bank = {};
                        $scope.form.$setPristine();
                    });
                    break;
                case 'update' :
                    BankService.update($scope.bank).then(function (data) {
                        $scope.bank = data;
                    });
                    break;
            }
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

    }]);