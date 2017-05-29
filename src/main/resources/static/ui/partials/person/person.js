app.controller("personCtrl", ['PersonService', 'ModalProvider', '$rootScope', '$scope', '$timeout', '$state', '$log',
    function (PersonService, ModalProvider, $rootScope, $scope, $timeout, $state, $log) {

        $scope.selected = {};

        $scope.read = function () {
            PersonService.fetchTableData().then(function (data) {
                $scope.persons = data;
                $scope.setSelected(data[0]);
            });
        };

        $scope.reload = function () {
            $state.reload();
        };

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 1500);

        $scope.setSelected = function (object) {
            if (object) {
                angular.forEach($scope.persons, function (person) {
                    if (object.id == person.id) {
                        person.isSelected = true;
                        object = person;
                    } else {
                        return person.isSelected = false;
                    }
                });
                $scope.selected = object;
            }
        };

        $scope.openCreateModel = function () {
            ModalProvider.openPersonCreateModel();
        };

        $scope.openUpdateModel = function (person) {
            if (person) {
                ModalProvider.openPersonUpdateModel(person);
                return;
            }
            ModalProvider.openPersonUpdateModel($scope.selected);
        };

    }]);
