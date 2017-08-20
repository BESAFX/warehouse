app.controller('courseCreateUpdateCtrl', ['MasterService', 'CourseService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'title', 'action', 'course',
    function (MasterService, CourseService, $scope, $rootScope, $timeout, $log, $uibModalInstance, title, action, course) {

        $timeout(function () {
            MasterService.fetchMasterCombo().then(function (data) {
                $scope.masters = data;
            })
        }, 2000);

        if (course) {
            $scope.course = course;
        } else {
            $scope.course = {};
        }

        $scope.title = title;

        $scope.action = action;

        $scope.submit = function () {
            switch ($scope.action) {
                case 'create' :
                    CourseService.create($scope.course).then(function (data) {
                        $uibModalInstance.close(data);
                    });
                    break;
                case 'update' :
                    CourseService.update($scope.course).then(function (data) {
                        $scope.course = data;
                    });
                    break;
            }
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

    }]);