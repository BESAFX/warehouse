app.controller('masterDetailsCtrl', ['MasterService', '$scope', '$rootScope', '$timeout', '$uibModalInstance',
    function (MasterService, $scope, $rootScope, $timeout, $uibModalInstance) {
        $scope.buffer = {};
        $timeout(function () {
            MasterService.fetchMasterBranchCombo().then(function (data) {
                $scope.masters = data;
            })
        }, 1500);

        $scope.submit = function () {
            var listId = [];
            for (var i = 0; i < $scope.buffer.mastersList.length; i++) {
                listId[i] = $scope.buffer.mastersList[i].id;
            }
            window.open('/report/MasterDetails?'
                + "mastersList=" + listId);
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };
    }]);