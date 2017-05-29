app.controller('billBuyCreateCtrl', ['BillBuyTypeService', 'BillBuyService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'title',
    function (BillBuyTypeService, BillBuyService, $scope, $rootScope, $timeout, $log, $uibModalInstance, title) {

        $scope.billBuy = {};

        $scope.title = title;

        $timeout(function () {
            BillBuyTypeService.findAll().then(function (data) {
                $scope.billBuyTypes = data;
            })
        }, 2000);

        $scope.submit = function () {
            BillBuyService.create($scope.billBuy).then(function (data) {
                $scope.billBuy = {};
                $scope.form.$setPristine();
            });
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };
    }]);