app.controller("courseCtrl", ['CourseService', 'MasterService', 'BranchService', 'AccountService', 'PaymentService', 'ModalProvider', '$rootScope', '$scope', '$log', '$timeout', '$state',
    function (CourseService, MasterService, BranchService, AccountService, PaymentService, ModalProvider, $rootScope, $scope, $log, $timeout, $state) {

        $scope.selected = {};

        $scope.courses = [];

        $scope.setSelected = function (object) {
            if (object) {
                angular.forEach($scope.courses, function (course) {
                    if (object.id == course.id) {
                        $scope.selected = course;
                        return course.isSelected = true;
                    } else {
                        return course.isSelected = false;
                    }
                });
            }
        };

        $scope.fetchTableData = function () {
            CourseService.fetchTableData().then(function (data) {
                $scope.courses = data;
                $scope.setSelected(data[0]);
            });
        };

        $scope.newCourse = function () {
            ModalProvider.openCourseCreateModel().result.then(function (data) {
                $scope.courses.splice(0,0,data);
            }, function () {
                $log.info('CourseCreateModel Closed.');
            });
        };

        $scope.delete = function (course) {
            if (course) {
                $rootScope.showConfirmNotify("حذف البيانات", "هل تود حذف الدورة فعلاً؟", "error", "fa-ban", function () {
                    CourseService.remove(course.id).then(function () {
                        var index = $scope.courses.indexOf(course);
                        $scope.courses.splice(index, 1);
                        $scope.setSelected($scope.courses[0]);
                    });
                });
                return;
            }
            $rootScope.showConfirmNotify("حذف البيانات", "هل تود حذف الدورة فعلاً؟", "error", "fa-ban", function () {
                CourseService.remove($scope.selected.id).then(function () {
                    var index = $scope.courses.indexOf($scope.selected);
                    $scope.courses.splice(index, 1);
                    $scope.setSelected($scope.courses[0]);
                });
            });
        };

        $scope.deleteAccounts = function (course) {
            if (course) {
                $rootScope.showConfirmNotify("حذف البيانات", "هل تود حذف طلاب الدورة فعلاً؟", "error", "fa-ban", function () {
                    AccountService.removeByCourse(course.id).then(function () {
                    });
                });
                return;
            }
            $rootScope.showConfirmNotify("حذف البيانات", "هل تود حذف طلاب الدورة فعلاً؟", "error", "fa-ban", function () {
                AccountService.removeByCourse($scope.selected.id).then(function () {
                });
            });
        };

        $scope.deletePayments = function (course) {
            if (course) {
                $rootScope.showConfirmNotify("حذف البيانات", "هل تود حذف جميع سندات طلاب الدورة فعلاً؟", "error", "fa-ban", function () {
                    PaymentService.removeByCourse(course.id).then(function () {

                    });
                });
                return;
            }
            $rootScope.showConfirmNotify("حذف البيانات", "هل تود حذف جميع سندات طلاب الدورة فعلاً؟", "error", "fa-ban", function () {
                PaymentService.removeByCourse($scope.selected.id).then(function () {

                });
            });
        };

        $scope.rowMenu = [
            {
                html: '<div class="drop-menu">انشاء دورة جديد<span class="fa fa-pencil fa-lg"></span></div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.newCourse();
                }
            },
            {
                html: '<div class="drop-menu">تعديل بيانات الدورة<span class="fa fa-edit fa-lg"></span></div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    ModalProvider.openCourseUpdateModel($itemScope.course);
                }
            },
            {
                html: '<div class="drop-menu">حذف الدورة<span class="fa fa-trash fa-lg"></span></div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.delete($itemScope.course);
                }
            },
            {
                html: '<div class="drop-menu">حذف طلاب الدورة<span class="fa fa-trash fa-lg"></span></div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.deleteAccounts($itemScope.course);
                }
            },
            {
                html: '<div class="drop-menu">حذف جميع سندات طلاب الدورة<span class="fa fa-trash fa-lg"></span></div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.deletePayments($itemScope.course);
                }
            }
        ];

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
            $scope.fetchTableData();
        }, 1500);

    }]);
