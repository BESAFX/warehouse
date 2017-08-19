app.controller('paymentByCourseCtrl', ['MasterService', 'CourseService', '$scope', '$rootScope', '$timeout', '$uibModalInstance',
    function (MasterService, CourseService, $scope, $rootScope, $timeout, $uibModalInstance) {
        $timeout(function () {
            $scope.buffer = {};
            $scope.masters = [];
            $scope.courses = [];
            MasterService.fetchTableDataSummery().then(function (data) {
                $scope.masters = data;
            })
        }, 1500);

        $scope.setUpCoursesList = function () {
            CourseService.findByMaster($scope.buffer.master.id).then(function (data) {
                $scope.courses = data;
            });
        };

        $scope.submit = function () {
            if ($scope.buffer.startDate && $scope.buffer.endDate) {
                window.open('/report/PaymentByCourse/'
                    + $scope.buffer.course.id + "?"
                    + "startDate=" + $scope.buffer.startDate.getTime() + "&"
                    + "endDate=" + $scope.buffer.endDate.getTime());
            } else {
                window.open('/report/PaymentByCourse/' + $scope.buffer.course.id);
            }
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

    }]);