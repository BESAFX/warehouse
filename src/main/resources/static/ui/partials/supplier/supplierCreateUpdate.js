app.controller('supplierCreateUpdateCtrl', ['SupplierService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'title', 'action', 'supplier',
    function (SupplierService, $scope, $rootScope, $timeout, $log, $uibModalInstance, title, action, supplier) {

        $scope.buffer = {};

        $scope.supplier = supplier;

        $scope.supplier.openCash = 0;

        $scope.title = title;

        $scope.action = action;

        $scope.submit = function () {
            switch ($scope.action) {
                case 'create' :
                    SupplierService.create($scope.supplier, $scope.supplier.openCash).then(function (data) {
                        SupplierService.findOne(data.id).then(function (value) {
                            $uibModalInstance.close(value);
                        });
                    });
                    break;
                case 'update' :
                    SupplierService.update($scope.supplier).then(function (data) {
                        $uibModalInstance.close(data);
                    });
                    break;
            }
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 600);

    }]);