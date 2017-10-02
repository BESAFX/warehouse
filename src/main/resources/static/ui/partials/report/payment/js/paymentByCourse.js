app.controller('paymentByCourseCtrl', ['CourseService', '$scope', '$rootScope', '$timeout', '$uibModalInstance',
    function (CourseService, $scope, $rootScope, $timeout, $uibModalInstance) {

        $scope.buffer = {};
        $scope.buffer.coursesList = [];

        $timeout(function () {
            CourseService.fetchCourseMasterBranchCombo().then(function (data) {
                $scope.courses = data;
            })
        }, 1500);

        $scope.submit = function () {
            var param = [];
            //
            if ($scope.buffer.startDate) {
                param.push('startDate=');
                param.push($scope.buffer.startDate.getTime());
                param.push('&');
            }
            if ($scope.buffer.endDate) {
                param.push('endDate=');
                param.push($scope.buffer.endDate.getTime());
                param.push('&');
            }
            //
            var courseIds = [];
            angular.forEach($scope.buffer.coursesList, function (course) {
                courseIds.push(course.id);
            });
            param.push('courseIds=');
            param.push(courseIds);
            param.push('&');
            //
            param.push('exportType=');
            param.push($scope.buffer.exportType);
            param.push('&');
            //
            param.push('title=');
            param.push($scope.buffer.title);
            param.push('&');
            //
            console.info(param.join(""));
            window.open('/report/PaymentByCourses?' + param.join(""));
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

    }]);