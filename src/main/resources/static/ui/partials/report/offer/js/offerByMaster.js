app.controller('offerByMasterCtrl', ['MasterService', '$scope', '$rootScope', '$timeout', '$uibModalInstance',
    function (MasterService, $scope, $rootScope, $timeout, $uibModalInstance) {

        $scope.buffer = {};
        $scope.buffer.mastersList = [];
        $scope.masters = [];

        $timeout(function () {
            MasterService.fetchMasterBranchCombo().then(function (data) {
                $scope.masters = data;
            })
        }, 1500);

        $scope.submit = function () {
            var ids = [];
            angular.forEach($scope.buffer.mastersList, function (master) {
                ids.push(master.id);
            });
            if ($scope.buffer.startDate && $scope.buffer.endDate) {
                window.open('/report/OfferByMasters?'
                    + 'ids=' + ids + '&'
                    + 'title=' + $scope.buffer.title + '&'
                    + 'exportType=' + $scope.buffer.exportType + '&'
                    + "startDate=" + $scope.buffer.startDate.getTime() + "&"
                    + "endDate=" + $scope.buffer.endDate.getTime());
            } else {
                window.open('/report/OfferByMasters?'
                    + 'ids=' + ids + '&'
                    + 'title=' + $scope.buffer.title + '&'
                    + 'exportType=' + $scope.buffer.exportType);
            }
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };
    }]);