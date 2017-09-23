app.controller("personCtrl", ['PersonService', 'ModalProvider', '$rootScope', '$scope', '$timeout', '$state', '$log',
    function (PersonService, ModalProvider, $rootScope, $scope, $timeout, $state, $log) {

        $scope.selected = {};

        $scope.persons = [];

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

        $scope.fetchTableData = function () {
            PersonService.findAll().then(function (data) {
                $scope.persons = data;
                $scope.setSelected(data[0]);
            });
        };

        $scope.newPerson = function () {
            ModalProvider.openPersonCreateModel().result.then(function (data) {
                $scope.persons.splice(0, 0, data);
            });
        };

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

        $scope.rowMenu = [
            {
                html: '<div class="drop-menu">انشاء مستخدم جديد<span class="fa fa-pencil fa-lg"></span></div>',
                enabled: function () {
                    return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_PERSON_CREATE']);
                },
                click: function ($itemScope, $event, value) {
                    $scope.newPerson();
                }
            },
            {
                html: '<div class="drop-menu">تعديل بيانات المستخدم<span class="fa fa-edit fa-lg"></span></div>',
                enabled: function () {
                    return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_PERSON_UPDATE']);
                },
                click: function ($itemScope, $event, value) {
                    ModalProvider.openPersonUpdateModel($itemScope.person);
                }
            }
        ];

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
            $scope.fetchTableData();
        }, 1500);

    }]);
