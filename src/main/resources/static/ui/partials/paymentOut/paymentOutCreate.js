app.controller('paymentOutCreateCtrl', ['BranchService', 'PaymentOutService', '$rootScope', '$scope', '$timeout', '$log', '$uibModalInstance', 'title',
    function (BranchService, PaymentOutService, $rootScope, $scope, $timeout, $log, $uibModalInstance, title) {

        $scope.title = title;

        $scope.paymentOut = {};

        $scope.submit = function () {
            PaymentOutService.create($scope.paymentOut).then(function (data) {
                $uibModalInstance.close(data);
            });
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

    }]);