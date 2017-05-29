app.controller('accountByMasterCtrl', ['MasterService', '$scope', '$rootScope', '$timeout', '$uibModalInstance',
    function (MasterService, $scope, $rootScope, $timeout, $uibModalInstance) {
        $timeout(function () {
            $scope.buffer = {};
            $scope.masters = [];
            MasterService.fetchTableData().then(function (data) {
                $scope.masters = data;
            })
        }, 1500);

        $scope.submit = function () {
            if ($scope.buffer.startDate && $scope.buffer.endDate) {
                window.open('/report/AccountByMaster/'
                    + $scope.buffer.master.id + "?"
                    + "startDate=" + $scope.buffer.startDate.getTime() + "&"
                    + "endDate=" + $scope.buffer.endDate.getTime());
            } else {
                window.open('/report/AccountByMaster/' + $scope.buffer.master.id);
            }
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };
    }]);