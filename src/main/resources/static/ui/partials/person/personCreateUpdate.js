app.controller('personCreateUpdateCtrl', ['TeamService', 'PersonService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'title', 'action', 'person',
    function (TeamService, PersonService, $scope, $rootScope, $timeout, $log, $uibModalInstance, title, action, person) {

        $scope.buffer = {};
        $scope.buffer.branchList = [];
        $scope.person = person;

        if ($scope.person.id) {
            angular.forEach($scope.person.branchAccesses, function (branchAccess) {
                $scope.buffer.branchList.push(branchAccess.branch);
            });
        }

        $scope.title = title;

        $scope.action = action;

        $scope.submit = function () {
            $scope.person.branchAccesses = [];
            angular.forEach($scope.buffer.branchList, function (branch) {
                var branchAccess = {};
                branchAccess.branch = branch;
                $scope.person.branchAccesses.push(branchAccess);
            });
            switch ($scope.action) {
                case 'create' :
                    PersonService.create($scope.person).then(function (data) {
                        $uibModalInstance.close(data);
                    });
                    break;
                case 'update' :
                    PersonService.update($scope.person).then(function (data) {
                        $scope.person = data;
                    });
                    break;
            }
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $timeout(function () {
            TeamService.findAll().then(function (data) {
                $scope.teams = data;
            });
            window.componentHandler.upgradeAllRegistered();
        }, 800);

    }]);