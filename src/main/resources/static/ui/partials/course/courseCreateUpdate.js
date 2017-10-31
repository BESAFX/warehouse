app.controller('courseCreateUpdateCtrl', ['MasterService', 'CourseService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'title', 'action', 'course',
    function (MasterService, CourseService, $scope, $rootScope, $timeout, $log, $uibModalInstance, title, action, course) {

        $timeout(function () {
            MasterService.fetchMasterBranchCombo().then(function (data) {
                $scope.masters = data;
            });
        }, 2000);

        $scope.course = course;

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


        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 1500);


    }]);