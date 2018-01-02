app.controller('paymentMoveToBookCtrl', ['PaymentService', 'PaymentBookService', '$rootScope', '$scope', '$timeout', '$log', '$uibModalInstance',
    function (PaymentService, PaymentBookService, $rootScope, $scope, $timeout, $log, $uibModalInstance) {

        $timeout(function () {
            PaymentBookService.fetchTableDataCombo().then(function (data) {
                return $scope.paymentBooks = data;
            });
        }, 600);

        $scope.submit = function () {
            $scope.paymentWrapper.payments = [];
            angular.forEach($scope.payments, function (payment) {
                if(payment.isSelected){
                    payment.paymentBook = $scope.paymentWrapper.paymentBook;
                    $scope.paymentWrapper.payments.push(payment);
                }
            });
            $uibModalInstance.close($scope.paymentWrapper);
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 1500);

    }]);