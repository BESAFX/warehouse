app.controller('printContractCtrl', ['BranchService', 'MasterService', 'CourseService', 'AccountService', '$scope', '$rootScope', '$timeout', '$uibModalInstance',
    function (BranchService, MasterService, CourseService, AccountService, $scope, $rootScope, $timeout, $uibModalInstance) {

        $scope.buffer = {};
        $scope.radio = {};
        $scope.radio.registerOption = '1';
        $scope.buffer.branchList = [];
        $scope.buffer.masterList = [];
        $scope.buffer.courseList = [];

        $timeout(function () {
            BranchService.fetchBranchCombo().then(function (data) {
                $scope.branches = data;
            });
            MasterService.fetchMasterBranchCombo().then(function (data) {
                $scope.masters = data;
            });
            CourseService.fetchCourseMasterBranchCombo().then(function (data) {
                $scope.courses = data;
            });
        }, 1500);

        $scope.search = function () {
            var search = [];
            switch ($scope.radio.registerOption){
                case '1':
                    //
                    if (!$scope.buffer.branchList || $scope.buffer.branchList.length === 0) {
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
                    break;
                case '2':
                    //
                    if (!$scope.buffer.masterList || $scope.buffer.masterList.length === 0) {
                        $scope.buffer = {};
                        $scope.accounts = [];
                        return;
                    }
                    var masterIds = [];
                    angular.forEach($scope.buffer.masterList, function (master) {
                        masterIds.push(master.id);
                    });
                    search.push('masterIds=');
                    search.push(masterIds);
                    search.push('&');
                    //
                    break;
                case '3':
                    //
                    if (!$scope.buffer.courseList || $scope.buffer.courseList.length === 0) {
                        $scope.buffer = {};
                        $scope.accounts = [];
                        return;
                    }
                    var courseIds = [];
                    angular.forEach($scope.buffer.courseList, function (course) {
                        courseIds.push(course.id);
                    });
                    search.push('courseIds=');
                    search.push(courseIds);
                    search.push('&');
                    //
                    break;
            }

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
                if (account.isSelected) {
                    selectedAccountsIds.push(account.id);
                }
            });
            window.open('/report/account/contract?accountIds=' + selectedAccountsIds + "&&contractType=" + $scope.buffer.contractType);
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 800);

    }]);