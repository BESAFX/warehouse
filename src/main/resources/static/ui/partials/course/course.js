app.controller("courseCtrl", ['CourseService', 'MasterService', 'BranchService', 'ModalProvider', '$rootScope', '$scope', '$log', '$timeout', '$state',
    function (CourseService, MasterService, BranchService, ModalProvider, $rootScope, $scope, $log, $timeout, $state) {

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 1500);

        $scope.setSelected = function (object) {
            if (object) {
                angular.forEach($scope.courses, function (course) {
                    if (object.id == course.id) {
                        course.isSelected = true;
                        object = course;
                    } else {
                        return course.isSelected = false;
                    }
                });
                $scope.selected = object;
            }
        };

        $scope.read = function () {
            CourseService.fetchTableData().then(function (data) {
                $scope.courses = data;
                $scope.setSelected(data[0]);
            });
        };

        $scope.reload = function () {
            $state.reload();
        };

        $scope.openCreateModel = function () {
            ModalProvider.openCourseCreateModel();
        };

        $scope.openUpdateModel = function (course) {
            if (course) {
                ModalProvider.openCourseUpdateModel(course);
                return;
            }
            var coursesSelected = $scope.courses.filter(function (item) {
                return item.isSelected === true;
            });
            if (coursesSelected.length == 0) {
                $rootScope.showNotify("خطأ", "فضلاً اختر العنصر أولاً", "error", 'fa-exclamation-triangle');
                return;
            }
            ModalProvider.openCourseUpdateModel(coursesSelected[0]);
        };
    }]);
