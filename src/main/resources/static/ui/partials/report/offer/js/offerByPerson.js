app.controller('offerByPersonCtrl', ['PersonService', '$scope', '$rootScope', '$timeout', '$uibModalInstance',
    function (PersonService, $scope, $rootScope, $timeout, $uibModalInstance) {

        $scope.buffer = {};
        $scope.buffer.personsList = [];
        $scope.persons = [];

        $timeout(function () {
            PersonService.findAllCombo().then(function (data) {
                $scope.persons = data;
            })
        }, 1500);

        $scope.submit = function () {
            var ids = [];
            angular.forEach($scope.buffer.personsList, function (master) {
                ids.push(master.id);
            });
            if ($scope.buffer.startDate && $scope.buffer.endDate) {
                window.open('/report/OfferByPersons?'
                    + 'ids=' + ids + '&'
                    + 'title=' + $scope.buffer.title + '&'
                    + 'exportType=' + $scope.buffer.exportType + '&'
                    + "startDate=" + $scope.buffer.startDate.getTime() + "&"
                    + "endDate=" + $scope.buffer.endDate.getTime());
            } else {
                window.open('/report/OfferByPersons?'
                    + 'ids=' + ids + '&'
                    + 'title=' + $scope.buffer.title + '&'
                    + 'exportType=' + $scope.buffer.exportType);
            }
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };
    }]);