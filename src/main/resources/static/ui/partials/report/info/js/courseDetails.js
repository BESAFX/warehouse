app.controller('courseDetailsCtrl', ['MasterService', 'CourseService', '$scope', '$rootScope', '$timeout', '$uibModalInstance',
    function (MasterService, CourseService, $scope, $rootScope, $timeout, $uibModalInstance) {
        $scope.buffer = {};
        $timeout(function () {
            MasterService.fetchBranchMasterCourseCombo().then(function (data) {
                $scope.masters = data;
            })
        }, 1500);

        $scope.submit = function () {
            var listId = [];
            for (var i = 0; i < $scope.buffer.coursesList.length; i++) {
                listId[i] = $scope.buffer.coursesList[i].id;
            }
            window.open('/report/CourseDetails?'
                + "coursesList=" + listId);
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };
    }]);