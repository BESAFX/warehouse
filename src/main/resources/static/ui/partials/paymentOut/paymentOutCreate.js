app.controller('paymentOutCreateCtrl', ['BranchService', 'PaymentOutService', '$rootScope', '$scope', '$timeout', '$log', '$uibModalInstance', 'title',
    function (BranchService, PaymentOutService, $rootScope, $scope, $timeout, $log, $uibModalInstance, title) {

        $scope.title = title;

        $scope.paymentOut = {};

        $timeout(function () {
            BranchService.fetchBranchCombo().then(function (data) {
                $scope.branches = data;
                $scope.paymentOut.branch = $scope.branches[0];
            });
        }, 2000);

        $scope.submit = function () {
            PaymentOutService.create($scope.paymentOut).then(function (data) {
                $uibModalInstance.close(data);
            });
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

    }]);