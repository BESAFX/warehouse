app.controller("billBuyTypeCtrl", ['BillBuyTypeService', 'ModalProvider', '$scope', '$rootScope', '$timeout', '$state', '$log',
    function (BillBuyTypeService, ModalProvider, $scope, $rootScope, $timeout, $state, $log) {

        $scope.selected = {};

        $scope.billBuyTypes = [];

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

        $scope.newBillBuyType = function () {
            ModalProvider.openBillBuyTypeCreateModel().result.then(function (data) {
                $scope.billBuyTypes.splice(0,0,data);
            }, function () {
                $log.info('BillBuyTypeCreateModel Closed.');
            });
        };

        $scope.delete = function (billBuyType) {
            if (billBuyType) {
                $rootScope.showConfirmNotify("حذف البيانات", "هل تود حذف النوع فعلاً؟", "error", "fa-ban", function () {
                    BillBuyTypeService.remove(billBuyType.id).then(function () {
                        var index = $scope.billBuyTypes.indexOf(billBuyType);
                        $scope.billBuyTypes.splice(index, 1);
                        $scope.setSelected($scope.billBuyTypes[0]);
                    });
                });
                return;
            }
            $rootScope.showConfirmNotify("حذف البيانات", "هل تود حذف النوع فعلاً؟", "error", "fa-ban", function () {
                BillBuyTypeService.remove($scope.selected.id).then(function () {
                    var index = $scope.billBuyTypes.indexOf($scope.selected);
                    $scope.billBuyTypes.splice(index, 1);
                    $scope.setSelected($scope.billBuyTypes[0]);
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
                    $scope.newBillBuyType();
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