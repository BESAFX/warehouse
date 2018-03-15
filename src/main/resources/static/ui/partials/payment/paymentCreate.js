app.controller('paymentCreateCtrl', ['BranchService', 'AccountService', 'PaymentService', '$rootScope', '$scope', '$timeout', '$log', '$uibModalInstance', 'title',
    function (BranchService, AccountService, PaymentService, $rootScope, $scope, $timeout, $log, $uibModalInstance, title) {

        $scope.payment = {};

        $scope.buffer = {};

        $scope.buffer.searchBy = 'fullName';

        $scope.accounts = [];

        $scope.title = title;

        $scope.searchAccount = function ($select, $event) {

            // no event means first load!
            if (!$event) {
                $scope.accounts = [];
            } else {
                $event.stopPropagation();
                $event.preventDefault();
            }

            var search = [];

            switch ($scope.buffer.searchBy){
                case "fullName":{
                    search.push('fullName=');
                    search.push($select.search);
                    search.push('&');
                    break;
                }
                case "studentIdentityNumber":
                    search.push('studentIdentityNumber=');
                    search.push($select.search);
                    search.push('&');
                    break;
                case "studentMobile":
                    search.push('studentMobile=');
                    search.push($select.search);
                    search.push('&');
                    break;

            }

            search.push('branchIds=');
            var branchIds = [];
            branchIds.push($scope.buffer.branch.id);
            search.push(branchIds);
            search.push('&');

            search.push('searchType=');
            search.push('and');
            search.push('&');

            return AccountService.filterWithInfo(search.join("")).then(function (data) {
                return $scope.accounts = data.content;
            });

        };

        $scope.submit = function () {
            PaymentService.create($scope.payment).then(function (data) {
                /**REFRESH ACCOUNT OBJECT**/
                AccountService.findOne(data.account.id).then(function (data) {
                    var index = $scope.accounts.indexOf(data.account);
                    $scope.accounts[index] = data;
                    $scope.payment = {};
                    $scope.payment.paymentMethod='Cash';
                    $scope.payment.account = data;
                    $scope.form.$setPristine();
                });
                $rootScope.showConfirmNotify("السندات", "هل تود طباعة السند ؟", "notification", "fa-info", function () {
                    window.open('report/CashReceipt/' + data.id);
                });
            });
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $timeout(function () {
            BranchService.fetchBranchCombo().then(function (data) {
                $scope.branches = data;
            });
            window.componentHandler.upgradeAllRegistered();
        }, 800);

    }]);