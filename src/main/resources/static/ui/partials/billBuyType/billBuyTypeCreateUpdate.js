app.controller('billBuyTypeCreateUpdateCtrl', ['BillBuyTypeService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'title', 'action', 'billBuyType',
    function (BillBuyTypeService, $scope, $rootScope, $timeout, $log, $uibModalInstance, title, action, billBuyType) {

        if (billBuyType) {
            $scope.billBuyType = billBuyType;
        } else {
            $scope.billBuyType = {};
        }

        $scope.title = title;

        $scope.action = action;

        $scope.submit = function () {
            switch ($scope.action) {
                case 'create' :
                    BillBuyTypeService.create($scope.billBuyType).then(function (data) {
                        $scope.billBuyType = {};
                        $scope.form.$setPristine();
                    });
                    break;
                case 'update' :
                    BillBuyTypeService.update($scope.billBuyType).then(function (data) {
                        $scope.billBuyType = data;
                    });
                    break;
            }
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };
    }]);