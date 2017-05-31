app.controller("teamCtrl", ['TeamService', 'PersonService', 'ModalProvider', '$rootScope', '$scope', '$timeout', '$state',
    function (TeamService, PersonService, ModalProvider, $rootScope, $scope, $timeout, $state) {

        $scope.selected = {};

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 1500);

        $scope.setSelected = function (object) {
            if (object) {
                angular.forEach($scope.teams, function (team) {
                    if (object.id == team.id) {
                        $scope.selected = team;
                        return team.isSelected = true;
                    } else {
                        return team.isSelected = false;
                    }
                });
            }
        };

        $scope.fetchTableData = function () {
            TeamService.findAll().then(function (data) {
                $scope.teams = data;
                $scope.setSelected(data[0]);
            });
        };


    }]);
