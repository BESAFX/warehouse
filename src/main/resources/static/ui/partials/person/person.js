app.controller("personCtrl", ['PersonService', 'ModalProvider', '$rootScope', '$scope', '$timeout', '$state', '$log',
    function (PersonService, ModalProvider, $rootScope, $scope, $timeout, $state, $log) {

        $scope.selected = {};

        $scope.fetchTableData = function () {
            PersonService.findAllSummery().then(function (data) {
                $scope.persons = data;
                $scope.setSelected(data[0]);
            });
        };

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
            $scope.fetchTableData();
        }, 1500);

        $scope.enable = function () {
            PersonService.enable($scope.selected).then(function (data) {
               $scope.selected.enabled = true;
            });
        };

        $scope.disable = function () {
            PersonService.disable($scope.selected).then(function (data) {
                $scope.selected.enabled = false;
            });
        };

        $scope.setSelected = function (object) {
            if (object) {
                angular.forEach($scope.persons, function (person) {
                    if (object.id == person.id) {
                        $scope.selected = person;
                        return person.isSelected = true;
                    } else {
                        return person.isSelected = false;
                    }
                });
            }
        };

    }]);
