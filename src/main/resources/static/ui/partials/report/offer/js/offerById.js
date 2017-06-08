app.controller('offerByIdCtrl', ['MasterService', 'OfferService', '$scope', '$rootScope', '$timeout', '$uibModalInstance',
    function (MasterService, OfferService, $scope, $rootScope, $timeout, $uibModalInstance) {
        $timeout(function () {
            $scope.buffer = {};
            $scope.masters = [];
            $scope.courses = [];
            MasterService.fetchTableData().then(function (data) {
                $scope.masters = data;
            })
        }, 1500);

        $scope.setUpOffersList = function () {
            OfferService.findByMaster($scope.buffer.master).then(function (data) {
                $scope.offers = data;
            });
        };

        $scope.submit = function () {
            window.open('/report/OfferById/' + $scope.buffer.offer.id + '?exportType=' + $scope.buffer.exportType);
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

    }]);