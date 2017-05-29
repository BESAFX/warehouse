app.controller('studentCreateUpdateCtrl', ['StudentService', '$scope', '$timeout', '$log', '$uibModalInstance', 'title', 'action', 'student',
    function (StudentService, $scope, $timeout, $log, $uibModalInstance, title, action, student) {

        if (student) {
            $scope.student = student;
            $scope.student.contact = student.contact;
        } else {
            $scope.student = {};
            $scope.student.contact = {};
            $scope.student.contact.identityStartDate = new Date().getTime();
            $scope.student.contact.birthDate = new Date().getTime();
        }

        $scope.title = title;

        $scope.action = action;

        $timeout(function () {
            $('#name').focus();
        }, 500);

        $scope.submit = function () {
            switch ($scope.action) {
                case 'create' :
                    StudentService.create($scope.student).then(function (data) {
                        $scope.student = {};
                        $scope.student.contact = {};
                        $scope.student.contact.identityStartDate = new Date().getTime();
                        $scope.student.contact.birthDate = new Date().getTime();
                        $scope.from.$setPristine();
                    });
                    break;
                case 'update' :
                    StudentService.update($scope.student).then(function (data) {
                        $scope.student = data;
                    });
                    break;
            }
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

    }]);