app.controller("masterCtrl", ['MasterService', 'BranchService', 'ModalProvider', '$scope', '$rootScope', '$log', '$state', '$timeout',
    function (MasterService, BranchService, ModalProvider, $scope, $rootScope, $log, $state, $timeout) {

        $scope.selected = {};

        $scope.fetchTableData = function () {
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

        $scope.delete = function (master) {
            if (master) {
                $rootScope.showConfirmNotify("حذف البيانات", "هل تود حذف التخصص فعلاً؟", "error", "fa-ban", function () {
                    MasterService.remove(master.id).then(function () {

                    });
                });
                return;
            }
            $rootScope.showConfirmNotify("حذف البيانات", "هل تود حذف التخصص فعلاً؟", "error", "fa-ban", function () {
                MasterService.remove($scope.selected.id).then(function () {

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