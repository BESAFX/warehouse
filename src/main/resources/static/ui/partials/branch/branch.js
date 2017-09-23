app.controller("branchCtrl", ['BranchService', 'PersonService', 'ModalProvider', '$scope', '$rootScope', '$state', '$timeout',
    function (BranchService, PersonService, ModalProvider, $scope, $rootScope, $state, $timeout) {

        $scope.selected = {};

        $scope.branches = [];

        $scope.fetchTableData = function () {
            BranchService.fetchTableData().then(function (data) {
                $scope.branches = data;
                $scope.setSelected(data[0]);
            });
        };

        $scope.setSelected = function (object) {
            if (object) {
                angular.forEach($scope.branches, function (branch) {
                    if (object.id == branch.id) {
                        $scope.selected = branch;
                        return branch.isSelected = true;
                    } else {
                        return branch.isSelected = false;
                    }
                });
            }
        };

        $scope.newBranch = function () {
            ModalProvider.openBranchCreateModel().result.then(function (data) {
                $scope.branches.splice(0, 0, data);
            }, function () {
                console.info('BranchCreateModel Closed.');
            });
        };

        $scope.delete = function (branch) {
            if (branch) {
                $rootScope.showConfirmNotify("الفروع", "هل تود حذف الفرع فعلاً؟", "error", "fa-ban", function () {
                    BranchService.remove(branch.id).then(function () {
                        var index = $scope.branches.indexOf(branch);
                        $scope.branches.splice(index, 1);
                        $scope.setSelected($scope.branches[0]);
                    });
                });
                return;
            }
            $rootScope.showConfirmNotify("الفروع", "هل تود حذف الفرع فعلاً؟", "error", "fa-ban", function () {
                BranchService.remove($scope.selected.id).then(function () {
                    var index = $scope.branches.indexOf($scope.selected);
                    $scope.branches.splice(index, 1);
                    $scope.setSelected($scope.branches[0]);
                });
            });
        };

        $scope.rowMenu = [
            {
                html: '<div class="drop-menu"> انشاء فرع جديد <span class="fa fa-plus-square-o fa-lg"></span></div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.newBranch();
                }
            },
            {
                html: '<div class="drop-menu"> تعديل بيانات الفرع <span class="fa fa-edit fa-lg"></span></div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    ModalProvider.openBranchUpdateModel($itemScope.branch);
                }
            },
            {
                html: '<div class="drop-menu"> حذف الفرع <span class="fa fa-minus-square-o fa-lg"></span></div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.delete($itemScope.branch);
                }
            }
        ];

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
            $scope.fetchTableData();
        }, 1500);

    }]);