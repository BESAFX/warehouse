app.controller('accountDetailsCtrl', ['AccountService', 'OfferService', 'PaymentService' ,'ModalProvider', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'account',
    function (AccountService, OfferService, PaymentService, ModalProvider, $scope, $rootScope, $timeout, $log, $uibModalInstance, account) {

        $scope.account = account;

        $scope.refreshAccount = function () {
          AccountService.findOne($scope.account.id).then(function (data) {
              $scope.account = data;
          })
        };

        $scope.refreshOffersByAccountMobile = function () {
            OfferService.findByCustomerMobile(account.student.contact.mobile).then(function (data) {
                $scope.offers = data;
            });
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