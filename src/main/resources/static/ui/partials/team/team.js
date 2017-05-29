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
                        team.isSelected = true;
                        object = team;
                    } else {
                        return team.isSelected = false;
                    }
                });
                $scope.selected = object;
            }
        };

        $scope.read = function () {
            TeamService.fetchTableData().then(function (data) {
                $scope.teams = data;
                $scope.setSelected(data[0]);
                angular.forEach($scope.teams, function (team) {
                    PersonService.countPersonsByTeam(team).then(function (data) {
                        return team.personsCount = data;
                    })
                });
            });
        };

        $scope.delete = function (team) {
            PersonService.countPersonsByTeam(team).then(function (data) {
                if (data == 0) {
                    TeamService.remove(team.id).then(function () {
                        $rootScope.showSuccessMessageBox(
                            'العمليات على قاعدة البيانات',
                            'تم الحذف بنجاح',
                            'alert',
                            function () {
                                $scope.reload();
                            });
                    });
                } else {
                    $rootScope.showDangerMessageBox(
                        'العمليات على قاعدة البيانات',
                        'لا يمكنك الحذف نظراً لاستخدامها من قبل بعض المستخدمين',
                        'alert',
                        function () {

                        });
                }
            });
        };

        $scope.reload = function () {
            $state.reload();
        };

        $scope.openCreateModel = function () {
            ModalProvider.openTeamCreateModel();
        };

        $scope.openUpdateModel = function (team) {
            if (team) {
                ModalProvider.openTeamUpdateModel(team);
                return;
            }
            ModalProvider.openTeamUpdateModel($scope.selected);
        };

    }]);
