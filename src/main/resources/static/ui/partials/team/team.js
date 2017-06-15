app.controller("teamCtrl", ['TeamService', 'PersonService', 'ModalProvider', '$rootScope', '$scope', '$timeout', '$state',
    function (TeamService, PersonService, ModalProvider, $rootScope, $scope, $timeout, $state) {

        $scope.selected = {};

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

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
            $scope.fetchTableData();
        }, 1500);


    }]);
