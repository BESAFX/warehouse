app.controller('paymentCreateCtrl', ['BranchService' ,'AccountService', 'PaymentService', '$rootScope', '$scope', '$timeout', '$log', '$uibModalInstance', 'title',
    function (BranchService, AccountService, PaymentService, $rootScope, $scope, $timeout, $log, $uibModalInstance, title) {

        $scope.payment = {};

        $scope.branches = [];

        $scope.buffer = {};
        $scope.buffer.branchesList = [];

        $scope.buffer.hijriDate = true;

        $scope.accounts = [];

        $scope.title = title;

        $timeout(function () {
            BranchService.fetchBranchCombo().then(function (data) {
                $scope.branches = data;
            });
        }, 1000);

        $scope.refreshAccounts = function () {
            var branchIds = [];
            angular.forEach($scope.buffer.branchesList, function (branch) {
                console.info(branch);
                branchIds.push(branch.id);
            });
            AccountService.findByBranches(branchIds).then(function (data) {
                $scope.accounts = data;
            });
        };

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

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 1500);

    }]);