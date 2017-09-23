app.controller('personCreateUpdateCtrl', ['TeamService', 'BranchService', 'PersonService', 'FileUploader', 'FileService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'title', 'action', 'person',
    function (TeamService, BranchService, PersonService, FileUploader, FileService, $scope, $rootScope, $timeout, $log, $uibModalInstance, title, action, person) {

        $timeout(function () {
            TeamService.findAll().then(function (data) {
                $scope.teams = data;
            });
            BranchService.findAllCombo().then(function (data) {
                $scope.branches = data;
            });
        }, 2000);

        $scope.person = person;

        $scope.title = title;

        $scope.action = action;

        $scope.submit = function () {
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

    }]);