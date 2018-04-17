app.controller('accountDebtByCourseCtrl', ['CourseService', '$scope', '$rootScope', '$timeout', '$uibModalInstance',
    function (CourseService, $scope, $rootScope, $timeout, $uibModalInstance) {

        $scope.buffer = {};

        $scope.buffer.coursesList = [];

        $scope.courses = [];

        $scope.buffer.sorts = [];

        $scope.addSortBy = function () {
            var sortBy = {};
            $scope.buffer.sorts.push(sortBy);
        };

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
            if(courseIds.length > 0){
                param.push('courseIds=');
                param.push(courseIds);
                param.push('&');
            }
            //
            param.push('exportType=');
            param.push($scope.buffer.exportType);
            param.push('&');
            //
            param.push('title=');
            param.push($scope.buffer.title);
            param.push('&');
            //

            angular.forEach($scope.buffer.sorts, function (sortBy) {
                param.push('sort=');
                param.push(sortBy.name + ',' + sortBy.direction);
                param.push('&');
            });

            window.open('/report/AccountDebtByCourses?' + param.join(""));
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $timeout(function () {
            CourseService.fetchCourseMasterBranchCombo().then(function (data) {
                $scope.courses = data;
            });
            window.componentHandler.upgradeAllRegistered();
        }, 600);

    }]);