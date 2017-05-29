app.controller("billBuyTypeCtrl", ['BillBuyTypeService', 'ModalProvider', '$scope', '$rootScope', '$timeout', '$state',
    function (BillBuyTypeService, ModalProvider, $scope, $rootScope, $timeout, $state) {

        $scope.selected = {};

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 1500);

        $scope.setSelected = function (object) {
            if (object) {
                angular.forEach($scope.billBuyTypes, function (billBuyType) {
                    if (object.id == billBuyType.id) {
                        billBuyType.isSelected = true;
                        object = billBuyType;
                    } else {
                        return billBuyType.isSelected = false;
                    }
                });
                $scope.selected = object;
            }
        };

        $scope.read = function () {
            BillBuyTypeService.findAll().then(function (data) {
                $scope.billBuyTypes = data;
                $scope.setSelected(data[0]);
            })
        };

        $scope.reload = function () {
            $state.reload();
        };

        $scope.openCreateModel = function () {
            ModalProvider.openBillBuyTypeCreateModel();
        };

        $scope.openUpdateModel = function (billBuyType) {
            if (billBuyType) {
                ModalProvider.openBillBuyTypeUpdateModel(billBuyType);
                return;
            }
            ModalProvider.openBillBuyTypeUpdateModel($scope.selected);
        };

    }]);