app.controller('offerByPersonCtrl', ['PersonService', '$scope', '$rootScope', '$timeout', '$uibModalInstance',
    function (PersonService, $scope, $rootScope, $timeout, $uibModalInstance) {

        $scope.buffer = {};

        $scope.buffer.personsList = [];

        $scope.persons = [];

        $scope.buffer.sorts = [];

        $scope.addSortBy = function () {
            var sortBy = {};
            $scope.buffer.sorts.push(sortBy);
        };

        $scope.submit = function () {
            var param = [];
            //
            if ($scope.buffer.startDate) {
                param.push('startDate=');
                param.push($scope.buffer.startDate.getTime());
                param.push('&');
            }
            if ($scope.buffer.endDate) {
                param.push('endDate=');
                param.push($scope.buffer.endDate.getTime());
                param.push('&');
            }
            //
            var personIds = [];
            angular.forEach($scope.buffer.personsList, function (person) {
                personIds.push(person.id);
            });
            if (personIds.length > 0) {
                param.push('personIds=');
                param.push(personIds);
                param.push('&');
            }
            //
            param.push('exportType=');
            param.push($scope.buffer.exportType);
            param.push('&');
            //
            param.push('registerOption=');
            param.push($scope.buffer.registerOption);
            param.push('&');
            //
            param.push('title=');
            param.push($scope.buffer.title);
            param.push('&');
            //

            angular.forEach($scope.buffer.sorts, function (sortBy) {
                param.push('sort=');
                param.push(sortBy.name + ',' + sortBy.direction);
                param.push('&');
            });

            window.open('/report/OfferByPersons?' + param.join(""));
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $timeout(function () {
            PersonService.findAllCombo().then(function (data) {
                $scope.persons = data;
            });
            window.componentHandler.upgradeAllRegistered();
        }, 600);


    }]);