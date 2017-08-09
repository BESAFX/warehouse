app.controller('accountDetailsCtrl', ['AccountService', 'PaymentService' ,'ModalProvider', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'account',
    function (AccountService, PaymentService, ModalProvider, $scope, $rootScope, $timeout, $log, $uibModalInstance, account) {

        $scope.account = account;

        $scope.refreshAccount = function () {
          AccountService.findOne($scope.account.id).then(function (data) {
              $scope.account = data;
          })
        };

        $scope.refreshAccountPayments = function () {
            PaymentService.findByAccount($scope.account.id).then(function (data) {
                $scope.account.payments = data;
            });
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 1500);

    }]);