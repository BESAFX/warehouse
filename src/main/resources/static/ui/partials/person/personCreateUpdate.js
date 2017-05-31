app.controller('personCreateUpdateCtrl', ['TeamService', 'BranchService', 'PersonService', 'FileUploader', 'FileService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'title', 'action', 'person',
    function (TeamService, BranchService, PersonService, FileUploader, FileService, $scope, $rootScope, $timeout, $log, $uibModalInstance, title, action, person) {

        $timeout(function () {

            $scope.teams = [];

            TeamService.findAll().then(function (data) {
                $scope.teams = data;
            });

            BranchService.fetchTableDataSummery().then(function (data) {
                $scope.branches = data;
            });

        }, 2000);

        if (person) {
            $scope.person = person;
        } else {
            $scope.person = {};
        }

        $scope.title = title;

        $scope.action = action;

        $scope.submit = function () {
            switch ($scope.action) {
                case 'create' :
                    PersonService.create($scope.person).then(function (data) {
                        $scope.person = {};
                        $scope.from.$setPristine();
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

    }]);