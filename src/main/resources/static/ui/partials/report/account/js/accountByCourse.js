app.controller('accountByCourseCtrl', ['MasterService', 'CourseService', '$scope', '$rootScope', '$timeout', '$uibModalInstance',
    function (MasterService, CourseService, $scope, $rootScope, $timeout, $uibModalInstance) {

        $scope.buffer = {};

        $timeout(function () {
            MasterService.fetchMasterCourseCombo().then(function (data) {
                $scope.masters = data;
            })
        }, 1500);

        $scope.submit = function () {
            if ($scope.buffer.startDate && $scope.buffer.endDate) {
                window.open('/report/AccountByCourse/'
                    + $scope.buffer.course.id + "?"
                    + 'exportType=' + $scope.buffer.exportType + '&'
                    + "startDate=" + $scope.buffer.startDate.getTime() + "&"
                    + "endDate=" + $scope.buffer.endDate.getTime());
            } else {
                window.open('/report/AccountByCourse/' + $scope.buffer.course.id + '?exportType=' + $scope.buffer.exportType);
            }
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

    }]);