app.controller('printContractCtrl', ['BranchService', 'AccountService', '$scope', '$rootScope', '$timeout', '$uibModalInstance',
    function (BranchService, AccountService, $scope, $rootScope, $timeout, $uibModalInstance) {

        $scope.buffer = {};
        $scope.buffer.branchList=[];

        $timeout(function () {
            BranchService.fetchBranchCombo().then(function (data) {
                $scope.branches = data;
            });
        }, 1500);

        $scope.search = function () {
            var search = [];
            //
            if (!$scope.buffer.branchList || $scope.buffer.branchList.length===0) {
                $scope.buffer = {};
                $scope.accounts = [];
                return;
            }
            var branchIds = [];
            angular.forEach($scope.buffer.branchList, function (branch) {
                branchIds.push(branch.id);
            });
            search.push('branchIds=');
            search.push(branchIds);
            search.push('&');
            //
            if ($scope.buffer.firstName) {
                search.push('firstName=');
                search.push($scope.buffer.firstName);
                search.push('&');
            }
            if ($scope.buffer.secondName) {
                search.push('secondName=');
                search.push($scope.buffer.secondName);
                search.push('&');
            }
            if ($scope.buffer.thirdName) {
                search.push('thirdName=');
                search.push($scope.buffer.thirdName);
                search.push('&');
            }
            if ($scope.buffer.forthName) {
                search.push('forthName=');
                search.push($scope.buffer.forthName);
                search.push('&');
            }
            if ($scope.buffer.studentIdentityNumber) {
                search.push('studentIdentityNumber=');
                search.push($scope.buffer.studentIdentityNumber);
                search.push('&');
            }
            if ($scope.buffer.studentMobile) {
                search.push('studentMobile=');
                search.push($scope.buffer.studentMobile);
                search.push('&');
            }
            AccountService.filterWithInfo(search.join("")).then(function (data) {
                $scope.accounts = data;
            });
        };

        $scope.submit = function () {
            var selectedAccountsIds = [];
            angular.forEach($scope.accounts, function (account) {
                if(account.isSelected){
                    selectedAccountsIds.push(account.id);
                }
            });
            window.open('/report/account/contract?accountIds=' + selectedAccountsIds + "&&contractType=" + $scope.buffer.contractType);
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };
    }]);