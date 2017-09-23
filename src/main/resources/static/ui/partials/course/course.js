app.controller("courseCtrl", ['CourseService', 'BranchService', 'AccountService', 'PaymentService', 'ModalProvider', '$rootScope', '$scope', '$log', '$timeout', '$state', '$uibModal',
    function (CourseService, BranchService, AccountService, PaymentService, ModalProvider, $rootScope, $scope, $log, $timeout, $state, $uibModal) {

        $scope.selected = {};

        $scope.courses = [];

        $scope.branches = [];

        //
        $scope.items = [];
        $scope.items.push({'id': 1, 'type': 'link', 'name': 'البرامج', 'link': 'menu'});
        $scope.items.push({'id': 2, 'type': 'title', 'name': 'الدورات'});
        //

        $timeout(function () {
            BranchService.fetchBranchMaster().then(function (data) {
                $scope.branches = data;
            });
        }, 2000);

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

        $scope.filter = function () {
            var modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: '/ui/partials/course/courseFilter.html',
                controller: 'courseFilterCtrl',
                scope: $scope,
                backdrop: 'static',
                keyboard: false,
                resolve: {
                    title: function () {
                        return 'البحث فى الدورات';
                    }
                }
            });

            modalInstance.result.then(function (buffer) {
                var search = [];
                if (buffer.instructor) {
                    search.push('instructor=');
                    search.push(buffer.instructor);
                    search.push('&');
                }
                if (buffer.codeFrom) {
                    search.push('codeFrom=');
                    search.push(buffer.codeFrom);
                    search.push('&');
                }
                if (buffer.codeTo) {
                    search.push('codeTo=');
                    search.push(buffer.codeTo);
                    search.push('&');
                }
                if (buffer.branch) {
                    search.push('branchId=');
                    search.push(buffer.branch.id);
                    search.push('&');
                }
                if (buffer.master) {
                    search.push('masterId=');
                    search.push(buffer.master.id);
                    search.push('&');
                }

                CourseService.filter(search.join("")).then(function (data) {
                    $scope.courses = data;
                    $scope.setSelected(data[0]);
                    $scope.items = [];
                    $scope.items.push({'id': 1, 'type': 'link', 'name': 'البرامج', 'link': 'menu'});
                    $scope.items.push({'id': 2, 'type': 'title', 'name': 'الدورات', 'style': 'font-weight:bold'});
                    $scope.items.push({'id': 3, 'type': 'title', 'name': 'فرع', 'style': 'font-weight:bold'});
                    $scope.items.push({
                        'id': 4,
                        'type': 'title',
                        'name': ' [ ' + buffer.branch.code + ' ] ' + buffer.branch.name
                    });
                });

            }, function () {
                console.info('CourseFilterModel Closed.');
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
        }, 1500);

    }]);
