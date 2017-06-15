app.controller("billBuyTypeCtrl", ['BillBuyTypeService', 'ModalProvider', '$scope', '$rootScope', '$timeout', '$state',
    function (BillBuyTypeService, ModalProvider, $scope, $rootScope, $timeout, $state) {

        $scope.selected = {};

        $scope.setSelected = function (object) {
            if (object) {
                angular.forEach($scope.billBuyTypes, function (billBuyType) {
                    if (object.id == billBuyType.id) {
                        $scope.selected = billBuyType;
                        return billBuyType.isSelected = true;
                    } else {
                        return billBuyType.isSelected = false;
                    }
                });
            }
        };

        $scope.fetchTableData = function () {
            BillBuyTypeService.findAll().then(function (data) {
                $scope.billBuyTypes = data;
                $scope.setSelected(data[0]);
            })
        };

        $scope.delete = function (billBuyType) {
            if (billBuyType) {
                $rootScope.showConfirmNotify("حذف البيانات", "هل تود حذف النوع فعلاً؟", "error", "fa-ban", function () {
                    BillBuyTypeService.remove(billBuyType.id).then(function () {

                    });
                });
                return;
            }
            $rootScope.showConfirmNotify("حذف البيانات", "هل تود حذف النوع فعلاً؟", "error", "fa-ban", function () {
                BillBuyTypeService.remove($scope.selected.id).then(function () {

                });
            });
        };

        $scope.rowMenu = [
            {
                html: '<div class="drop-menu"> انشاء نوع جديد <span class="fa fa-plus-square-o fa-lg"></span></div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    ModalProvider.openBillBuyTypeCreateModel();
                }
            },
            {
                html: '<div class="drop-menu"> تعديل بيانات النوع <span class="fa fa-edit fa-lg"></span></div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    ModalProvider.openBillBuyTypeUpdateModel($itemScope.billBuyType);
                }
            },
            {
                html: '<div class="drop-menu"> حذف النوع <span class="fa fa-minus-square-o fa-lg"></span></div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.delete($itemScope.billBuyType);
                }
            }
        ];

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
            $scope.fetchTableData();
        }, 1500);

    }]);