app.controller('offerByPersonCtrl', ['PersonService', '$scope', '$rootScope', '$timeout', '$uibModalInstance',
    function (PersonService, $scope, $rootScope, $timeout, $uibModalInstance) {
        $timeout(function () {
            $scope.buffer = {};
            $scope.persons = [];
            PersonService.fetchTableData().then(function (data) {
                $scope.persons = data;
            })
        }, 1500);

        $scope.submit = function () {
            if ($scope.buffer.startDate && $scope.buffer.endDate) {
                window.open('/report/OfferByPerson/'
                    + $scope.buffer.person.id + "?"
                    + "startDate=" + $scope.buffer.startDate.getTime() + "&"
                    + "endDate=" + $scope.buffer.endDate.getTime());
            } else {
                window.open('/report/OfferByPerson/' + $scope.buffer.person.id);
            }
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };
    }]);