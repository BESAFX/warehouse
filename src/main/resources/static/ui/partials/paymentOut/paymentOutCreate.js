app.controller('paymentOutCreateCtrl', ['PaymentService', '$rootScope', '$scope', '$timeout', '$log', '$uibModalInstance', 'title',
    function (PaymentService, $rootScope, $scope, $timeout, $log, $uibModalInstance, title) {

        $scope.title = title;

        $scope.buffer = {};

        $scope.submit = function () {
            $scope.payment.type = 'مصروفات';
            PaymentService.create($scope.payment).then(function (data) {
                $scope.payment = {};
                $scope.form.$setPristine();
            });
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

    }]);