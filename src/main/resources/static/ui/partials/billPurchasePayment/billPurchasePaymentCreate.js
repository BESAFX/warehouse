app.controller('billPurchasePaymentCreateCtrl', ['BillPurchasePremiumService', 'BillPurchasePaymentService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'billPurchase',
    function (BillPurchasePremiumService, BillPurchasePaymentService, $scope, $rootScope, $timeout, $log, $uibModalInstance, billPurchase) {

        $scope.buffer = {};

        $scope.billPurchase = billPurchase;

        $scope.billPurchasePayment = {};

        $scope.billPurchasePayment.billPurchase = billPurchase;

        $scope.submit = function () {
            BillPurchasePaymentService.create($scope.billPurchasePayment).then(function (data) {
                BillPurchasePaymentService.findOne(data.id).then(function (value) {
                    $uibModalInstance.close(value);
                });
            });
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $timeout(function () {
            BillPurchasePremiumService.findByBillPurchase($scope.billPurchase.id).then(function (value) {
                $scope.billPurchasePremiums = value;
            });
            window.componentHandler.upgradeAllRegistered();
        }, 600);

    }]);