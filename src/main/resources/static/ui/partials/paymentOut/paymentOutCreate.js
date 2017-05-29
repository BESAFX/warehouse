app.controller('paymentOutCreateCtrl', ['PaymentService', '$rootScope', '$scope', '$timeout', '$log', '$uibModalInstance', 'title',
    function (PaymentService, $rootScope, $scope, $timeout, $log, $uibModalInstance, title) {

        $scope.title = title;

        $scope.buffer = {};

        $scope.findPaymentByCode = function () {
            if (!$scope.payment.code) {
                return;
            }
            PaymentService.findByCode($scope.payment.code).then(function (data) {
                if (data) {
                    noty({
                        text: 'هذا الرقم غير متاح حالياَ، فضلاً ادخل رقم آخر للسند',
                        layout: 'topCenter',
                        type: 'danger',
                        timeout: 5000
                    });
                    $scope.payment.code = "";
                    $scope.form.$setPristine();
                    $('#code').focus();
                }
            });
        };

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