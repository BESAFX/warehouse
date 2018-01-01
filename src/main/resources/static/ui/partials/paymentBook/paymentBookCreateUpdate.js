app.controller('paymentBookCreateUpdateCtrl', ['PaymentBookService', 'BranchService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'title', 'action', 'paymentBook',
    function (PaymentBookService, BranchService, $scope, $rootScope, $timeout, $log, $uibModalInstance, title, action, paymentBook) {

        $scope.paymentBook = paymentBook;

        $scope.title = title;

        $scope.action = action;

        $timeout(function () {
            BranchService.fetchBranchCombo().then(function (data) {
                $scope.branches = data;
            });
        }, 600);

        $scope.submit = function () {
            switch ($scope.action) {
                case 'create' :
                    PaymentBookService.create($scope.paymentBook).then(function (data) {
                        $uibModalInstance.close(data);
                    });
                    break;
                case 'update' :
                    PaymentBookService.update($scope.paymentBook).then(function (data) {
                        $uibModalInstance.close(data);
                    });
                    break;
            }
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

    }]);