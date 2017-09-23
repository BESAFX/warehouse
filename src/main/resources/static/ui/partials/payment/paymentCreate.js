app.controller('paymentCreateCtrl', ['AccountService', 'PaymentService', '$rootScope', '$scope', '$timeout', '$log', '$uibModalInstance', 'title',
    function (AccountService, PaymentService, $rootScope, $scope, $timeout, $log, $uibModalInstance, title) {

        $scope.payment = {};

        $scope.accounts = [];

        $scope.title = title;

        $timeout(function () {
            AccountService.fetchTableDataAccountComboBox().then(function (data) {
               $scope.accounts = data;
            });
        }, 1000);

        $scope.submit = function () {
            PaymentService.create($scope.payment).then(function (data) {
                AccountService.findOne(data.account.id).then(function (data) {
                    var index = $scope.accounts.indexOf(data.account);
                    $scope.accounts[index] = data;
                    $scope.payment = {};
                    $scope.payment.account = data;
                    $scope.form.$setPristine();
                });
            });
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

    }]);