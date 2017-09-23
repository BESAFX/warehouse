app.controller("masterCtrl", ['MasterCategoryService', 'MasterService', 'BranchService', 'ModalProvider', '$scope', '$rootScope', '$log', '$state', '$timeout', '$uibModal',
    function (MasterCategoryService ,MasterService, BranchService, ModalProvider, $scope, $rootScope, $log, $state, $timeout, $uibModal) {

        //
        $scope.items = [];
        $scope.items.push({'id': 1, 'type': 'link', 'name': 'البرامج', 'link': 'menu'});
        $scope.items.push({'id': 2, 'type': 'title', 'name': 'التخصصات'});
        //

        $scope.selected = {};

        $scope.branches = [];

        $scope.masters = [];

        $scope.masterCategories = [];

        $scope.fetchTableData = function () {
            $scope.refreshMasterCategory();
            BranchService.fetchBranchCombo().then(function (data) {
                $scope.branches = data;
            })
        };

        $scope.refreshMasterCategory = function () {
            MasterCategoryService.findAll().then(function (data) {
                $scope.masterCategories = data;
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
                $scope.masterCategories.splice(0, 0, data);
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

        $scope.filter = function () {
            var modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: '/ui/partials/master/masterFilter.html',
                controller: 'masterFilterCtrl',
                scope: $scope,
                backdrop: 'static',
                keyboard: false,
                resolve: {
                    title: function () {
                        return 'البحث فى التخصصات';
                    }
                }
            });

            modalInstance.result.then(function (buffer) {
                var search = [];
                if (buffer.name) {
                    search.push('name=');
                    search.push(buffer.name);
                    search.push('&');
                }
                if (buffer.codeFrom) {
                    search.push('codeFrom=');
                    search.push(buffer.codeFrom);
                    search.push('&');
                }
                if (buffer.codeTo) {
                    search.push('codeTo=');
                    search.push(buffer.codeTo);
                    search.push('&');
                }
                if (buffer.branch) {
                    search.push('branchId=');
                    search.push(buffer.branch.id);
                    search.push('&');
                }

                MasterService.filter(search.join("")).then(function (data) {
                    $scope.masters = data;
                    $scope.setSelected(data[0]);
                    $scope.items = [];
                    $scope.items.push({'id': 1, 'type': 'link', 'name': 'البرامج', 'link': 'menu'});
                    $scope.items.push({'id': 2, 'type': 'title', 'name': 'التخصصات', 'style': 'font-weight:bold'});
                    $scope.items.push({'id': 3, 'type': 'title', 'name': 'فرع', 'style': 'font-weight:bold'});
                    $scope.items.push({
                        'id': 4,
                        'type': 'title',
                        'name': ' [ ' + buffer.branch.code + ' ] ' + buffer.branch.name
                    });
                });

            }, function () {
                console.info('MasterFilterModel Closed.');
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