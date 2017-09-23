app.controller('billBuyCreateCtrl', ['BillBuyTypeService', 'BillBuyService', 'BranchService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'title',
    function (BillBuyTypeService, BillBuyService, BranchService, $scope, $rootScope, $timeout, $log, $uibModalInstance, title) {

        $scope.billBuy = {};

        $scope.title = title;

        $timeout(function () {
            BillBuyTypeService.findAll().then(function (data) {
                $scope.billBuyTypes = data;
            });
            BranchService.fetchBranchCombo().then(function (data) {
                $scope.branches = data;
                $scope.billBuy.branch = data[0];
            });
        }, 2000);

        $scope.submit = function () {
            BillBuyService.create($scope.billBuy).then(function (data) {
                $uibModalInstance.close(data);
            });
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };
    }]);