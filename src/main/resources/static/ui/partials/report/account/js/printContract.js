app.controller('printContractCtrl', ['BranchService' ,'AccountService', '$scope', '$rootScope', '$timeout', '$uibModalInstance',
    function (BranchService, AccountService, $scope, $rootScope, $timeout, $uibModalInstance) {

        $scope.buffer = {};
        $scope.buffer.reportFileName = "";
        $scope.buffer.contractType = "";
        $scope.buffer.reportType = "";

        $scope.accounts = [];

        $scope.searchAccount = function ($select, $event) {

            // no event means first load!
            if (!$event) {
                $scope.page = 0;
                $scope.accounts = [];
            } else {
                $event.stopPropagation();
                $event.preventDefault();
                $scope.page++;
            }

            var search = [];

            search.push('size=');
            search.push(10);
            search.push('&');

            search.push('page=');
            search.push($scope.page);
            search.push('&');

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
                $scope.buffer.last = data.last;
                return $scope.accounts = $scope.accounts.concat(data.content);
            });

        };

        $scope.submit = function () {
            var selectedAccountsIds = [];
            angular.forEach($scope.buffer.accountsList, function (account) {
                selectedAccountsIds.push(account.id);
            });
            switch ($scope.buffer.reportType){
                case "ZIP":
                    window.open('/report/account/contract/zip?accountIds=' + selectedAccountsIds + "&&contractType=" + $scope.buffer.contractType + "&&reportFileName="  + $scope.buffer.reportFileName);
                    break;
                case "PDF":
                    window.open('/report/account/contract/pdf?accountIds=' + selectedAccountsIds + "&&contractType=" + $scope.buffer.contractType + "&&reportFileName="  + $scope.buffer.reportFileName + "&&hijriDate=" + $scope.buffer.hijriDate);
                    break;
            }
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $timeout(function () {
            BranchService.fetchBranchCombo().then(function (data) {
                $scope.branches = data;
            });
            window.componentHandler.upgradeAllRegistered();
        }, 600);

    }]);