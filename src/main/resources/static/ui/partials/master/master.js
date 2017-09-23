app.controller("masterCtrl", ['MasterCategoryService', 'MasterService', 'BranchService', 'ModalProvider', '$scope', '$rootScope', '$log', '$state', '$timeout',
    function (MasterCategoryService ,MasterService, BranchService, ModalProvider, $scope, $rootScope, $log, $state, $timeout) {

        $scope.fetchTableData = function () {
            $scope.refreshMasterCategory();
            $scope.refreshMaster();
        };

        $scope.selected = {};

        $scope.masters = [];

        $scope.refreshMasterCategory = function () {
            MasterCategoryService.findAll().then(function (data) {
                $scope.masterCategories = data;
            });
        };

        $scope.refreshMaster = function () {
            MasterService.fetchTableData().then(function (data) {
                $scope.masters = data;
                $scope.setSelected(data[0]);
            });
        };

        $scope.setSelected = function (object) {
            if (object) {
                angular.forEach($scope.masters, function (master) {
                    if (object.id == master.id) {
                        $scope.selected = master;
                        return master.isSelected = true;
                    } else {
                        return master.isSelected = false;
                    }
                });
            }
        };

        $scope.newMaster = function () {
            ModalProvider.openMasterCreateModel().result.then(function (data) {
                $scope.masters.splice(0, 0, data);
            }, function () {
                console.info('MasterCreateModel Closed.');
            });
        };

        $scope.newMasterCategory = function () {
            ModalProvider.openMasterCategoryCreateModel().result.then(function (data) {
                if ($scope.masterCategories) {
                    $scope.masterCategories.splice(0, 0, data);
                }
            }, function () {
                console.info('MasterCategoryCreateModel Closed.');
            });
        };

        $scope.deleteMasterCategory = function (masterCategory) {
            $rootScope.showConfirmNotify("حذف البيانات", "هل تود حذف التصنيف فعلاً؟", "error", "fa-ban", function () {
                MasterCategoryService.remove(masterCategory.id).then(function () {
                    var index = $scope.masterCategories.indexOf(masterCategory);
                    $scope.masterCategories.splice(index, 1);
                });
            });
        };

        $scope.delete = function (master) {
            if (master) {
                $rootScope.showConfirmNotify("حذف البيانات", "هل تود حذف التخصص فعلاً؟", "error", "fa-ban", function () {
                    MasterService.remove(master.id).then(function () {
                        var index = $scope.masters.indexOf(master);
                        $scope.masters.splice(index, 1);
                        $scope.setSelected($scope.masters[0]);
                    });
                });
                return;
            }
            $rootScope.showConfirmNotify("حذف البيانات", "هل تود حذف التخصص فعلاً؟", "error", "fa-ban", function () {
                MasterService.remove($scope.selected.id).then(function () {
                    var index = $scope.masters.indexOf(selected);
                    $scope.masters.splice(index, 1);
                    $scope.setSelected($scope.masters[0]);
                });
            });
        };

        $scope.rowMenu = [
            {
                html: '<div class="drop-menu"> انشاء تخصص جديد <span class="fa fa-plus-square-o fa-lg"></span></div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    ModalProvider.openMasterCreateModel();
                }
            },
            {
                html: '<div class="drop-menu"> تعديل بيانات التخصص <span class="fa fa-edit fa-lg"></span></div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    ModalProvider.openMasterUpdateModel($itemScope.master);
                }
            },
            {
                html: '<div class="drop-menu"> حذف التخصص <span class="fa fa-minus-square-o fa-lg"></span></div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.delete($itemScope.master);
                }
            }
        ];

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
            $scope.fetchTableData();
        }, 1500);

    }]);