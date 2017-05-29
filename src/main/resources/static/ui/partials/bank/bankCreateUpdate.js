app.controller('bankCreateUpdateCtrl', ['BankService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'title', 'action', 'bank',
    function (BankService, $scope, $rootScope, $timeout, $log, $uibModalInstance, title, action, bank) {

        $scope.title = title;

        $scope.action = action;

        $scope.clear = function () {
            $scope.bank = {};
            if ($scope.form) {
                $scope.form.$setPristine()
            }
        };

        if (bank) {
            $scope.bank = bank;
        } else {
            $scope.clear();
        }

        $scope.submit = function () {
            switch ($scope.action) {
                case 'create' :
                    BankService.create($scope.bank).then(function (data) {
                        $scope.clear();
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