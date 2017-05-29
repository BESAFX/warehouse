app.controller("companyCtrl", ['CompanyService', 'ModalProvider', 'FileService', '$scope', '$rootScope', '$log', '$http', '$state', '$timeout',
    function (CompanyService, ModalProvider, FileService, $scope, $rootScope, $log, $http, $state, $timeout) {

        $scope.selected = {};

        $scope.setSelected = function (object) {
            if (object) {
                angular.forEach($scope.companies, function (company) {
                    if (object.id == company.id) {
                        $scope.selected = company;
                        return company.isSelected = true;
                    } else {
                        return company.isSelected = false;
                    }
                });
            }
        };

        $scope.fetchTableData = function () {
            CompanyService.fetchTableData().then(function (data) {
                $scope.companies = data;
                $scope.setSelected(data[0]);
            });
        };

        $scope.delete = function (company) {
            if (company) {
                $rootScope.showConfirmNotify("حذف البيانات", "هل تود حذف الشركة فعلاً؟", "error", "fa-ban", function () {
                    CompanyService.remove(company.id).then(function () {

                    });
                });
                return;
            }
            $rootScope.showConfirmNotify("حذف البيانات", "هل تود حذف الشركة فعلاً؟", "error", "fa-ban", function () {
                CompanyService.remove($scope.selected.id).then(function () {

                });
            });
        };

        $scope.rowMenu = [
            {
                html: '<div class="drop-menu"> اضافة <span class="fa fa-plus-square-o fa-lg"></span></div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    ModalProvider.openCompanyCreateModel();
                }
            },
            {
                html: '<div class="drop-menu"> تعديل <span class="fa fa-edit fa-lg"></span></div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    ModalProvider.openCompanyUpdateModel($itemScope.company);
                }
            },
            {
                html: '<div class="drop-menu"> حذف <span class="fa fa-minus-square-o fa-lg"></span></div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.delete($itemScope.company);
                }
            }
        ];

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 1500);

    }]);