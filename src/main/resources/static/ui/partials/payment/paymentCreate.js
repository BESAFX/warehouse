app.controller('paymentCreateCtrl', ['BranchService', 'PaymentBookService', 'AccountService', 'PaymentService', '$rootScope', '$scope', '$timeout', '$log', '$uibModalInstance', 'title',
    function (BranchService, PaymentBookService, AccountService, PaymentService, $rootScope, $scope, $timeout, $log, $uibModalInstance, title) {

        $scope.payment = {};

        $scope.buffer = {};

        $scope.accounts = [];

        $scope.title = title;

        $timeout(function () {
            BranchService.fetchBranchCombo().then(function (data) {
                $scope.branches = data;
            });
        }, 1000);

        $scope.onBranchSelect = function () {
            AccountService.findByBranchWithKey($scope.buffer.branch.id).then(function (data) {
                $scope.accounts = data;
            });
            PaymentBookService.findByBranchCombo($scope.buffer.branch.id).then(function (data) {
                $scope.paymentBooks = data;
            });
        };

        $scope.submit = function () {
            PaymentService.create($scope.payment).then(function (data) {
                $rootScope.showConfirmNotify("السندات", "هل تود طباعة السند ؟", "notification", "fa-info", function () {
                    window.open('report/CashReceipt/' + data.id);
                });
                PaymentBookService.findByBranchCombo($scope.buffer.branch.id).then(function (data) {
                    $scope.paymentBooks = data;
                    /**REFRESH ACCOUNT OBJECT**/
                    AccountService.findOne(data.account.id).then(function (data) {
                        var index = $scope.accounts.indexOf(data.account);
                        $scope.accounts[index] = data;
                        $scope.payment = {};
                        $scope.payment.paymentMethod='Cash';
                        $scope.payment.account = data;
                        $scope.form.$setPristine();
                    });
                });
            });
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 1500);

    }]);