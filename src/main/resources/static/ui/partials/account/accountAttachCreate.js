app.controller('accountAttachCreateCtrl', [
    'BranchService',
    'AccountService',
    'AccountAttachService',
    '$rootScope',
    '$scope',
    '$timeout',
    '$log',
    '$uibModalInstance',
    'attachTypes',
    function (
        BranchService,
        AccountService,
        AccountAttachService,
        $rootScope,
        $scope,
        $timeout,
        $log,
        $uibModalInstance,
        attachTypes
    ) {

        $scope.buffer = {};

        $scope.buffer.searchBy = 'fullName';

        $scope.accounts = [];

        $scope.attachTypes = attachTypes;

        $scope.files = [];

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

            if($scope.buffer.branch){
                search.push('branchIds=');
                var branchIds = [];
                branchIds.push($scope.buffer.branch.id);
                search.push(branchIds);
                search.push('&');
            }

            search.push('searchType=');
            search.push('and');
            search.push('&');

            return AccountService.filterWithAttaches(search.join("")).then(function (data) {
                return $scope.accounts = data.content;
            });

        };

        $scope.refreshAttaches = function () {
            AccountAttachService.findByAccount($scope.buffer.account).then(function (data) {
                $scope.buffer.account.accountAttaches = data;
            })
        };

        $scope.setAccountAttachType = function (accountAttach) {
            AccountAttachService.setType(accountAttach, accountAttach.attachType);
        };
        
        $scope.deleteAttach = function (accountAttach) {
            $rootScope.showConfirmNotify("حذف البيانات", "هل تود حذف المستند فعلاً؟", "error", "fa-ban", function () {
                AccountAttachService.remove(accountAttach).then(function (data) {
                    if (data) {
                        var index = $scope.buffer.account.accountAttaches.indexOf(accountAttach);
                        $scope.buffer.account.accountAttaches.splice(index, 1);
                    } else {
                        $rootScope.showConfirmNotify("حذف البيانات", "هل تود حذف المستند نهائياً؟", "error", "fa-ban", function () {
                            AccountAttachService.removeWhatever(accountAttach);
                        });
                    }
                });
            });
        };
        
        $scope.uploadFiles = function () {
            document.getElementById('uploader').click();
        };
        
        $scope.$watch('files', function (newVal, oldVal) {
            if ($scope.files.length > 0) {
                AccountAttachService.upload($scope.buffer.account, newVal).then(function (data) {
                    return Array.prototype.push.apply($scope.buffer.account.accountAttaches, data);
                });
            }
        });

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $timeout(function () {
            BranchService.fetchBranchCombo().then(function (data) {
                $scope.branches = data;
                $scope.buffer.branch = data[0];
            });
            window.componentHandler.upgradeAllRegistered();
        }, 600);

    }]);