app.controller("accountCtrl", ['AccountService', 'BranchService', 'MasterService', 'CourseService', 'ModalProvider', '$rootScope', '$scope', '$timeout', '$log', '$state',
    function (AccountService, BranchService, MasterService, CourseService, ModalProvider, $rootScope, $scope, $timeout, $log, $state) {

        $scope.buffer = {};

        //
        $scope.items = [];
        $scope.items.push({'id': 1, 'type': 'link', 'name': 'البرامج', 'link': 'menu'});
        $scope.items.push({'id': 2, 'type': 'title', 'name': 'تسجيل الطلاب'});
        //

        $timeout(function () {
            $scope.sideOpacity = 1;
            BranchService.fetchTableData().then(function (data) {
                $scope.branches = data;
                $scope.buffer.branch = $scope.branches[0];
            });
        }, 2000);

        $scope.setSelected = function (object) {
            if (object) {
                angular.forEach($scope.accounts, function (account) {
                    if (object.id == account.id) {
                        $scope.selected = account;
                        return account.isSelected = true;
                    } else {
                        return account.isSelected = false;
                    }
                });
            }
        };

        $scope.clear = function () {
            $scope.buffer = {};
            $scope.buffer.branch = $scope.branches[0];
        };

        $scope.print = function (account) {
            if (account) {
                window.open('/report/account/contract/' + account.id);
                return;
            }
            window.open('/report/account/contract/' + $scope.selected.id);
        };

        $scope.filter = function () {
            var search = [];
            if ($scope.buffer.firstName) {
                search.push('firstName=');
                search.push($scope.buffer.firstName);
                search.push('&');
            }
            if ($scope.buffer.secondName) {
                search.push('secondName=');
                search.push($scope.buffer.secondName);
                search.push('&');
            }
            if ($scope.buffer.thirdName) {
                search.push('thirdName=');
                search.push($scope.buffer.thirdName);
                search.push('&');
            }
            if ($scope.buffer.forthName) {
                search.push('forthName=');
                search.push($scope.buffer.forthName);
                search.push('&');
            }
            if ($scope.buffer.dateFrom) {
                search.push('dateFrom=');
                search.push(moment($scope.buffer.dateFrom).valueOf());
                search.push('&');
            }
            if ($scope.buffer.dateTo) {
                search.push('dateTo=');
                search.push(moment($scope.buffer.dateTo).valueOf());
                search.push('&');
            }
            if ($scope.buffer.studentIdentityNumber) {
                search.push('studentIdentityNumber=');
                search.push($scope.buffer.studentIdentityNumber);
                search.push('&');
            }
            if ($scope.buffer.studentMobile) {
                search.push('studentMobile=');
                search.push($scope.buffer.studentMobile);
                search.push('&');
            }
            if ($scope.buffer.coursePriceFrom) {
                search.push('coursePriceFrom=');
                search.push($scope.buffer.coursePriceFrom);
                search.push('&');
            }
            if ($scope.buffer.coursePriceTo) {
                search.push('coursePriceTo=');
                search.push($scope.buffer.coursePriceTo);
                search.push('&');
            }
            if ($scope.buffer.branch) {
                search.push('branch=');
                search.push($scope.buffer.branch.id);
                search.push('&');
            }
            if ($scope.buffer.master) {
                search.push('master=');
                search.push($scope.buffer.master.id);
                search.push('&');
            }
            if ($scope.buffer.course) {
                search.push('course=');
                search.push($scope.buffer.course.id);
                search.push('&');
            }
            AccountService.filter(search.join("")).then(function (data) {
                $scope.accounts = data;
                $scope.setSelected(data[0]);
                $scope.items = [];
                $scope.items.push({'id': 1, 'type': 'link', 'name': 'البرامج', 'link': 'menu'});
                $scope.items.push({'id': 2, 'type': 'title', 'name': 'تسجيل الطلاب', 'style': 'font-weight:bold'});
                $scope.items.push({'id': 3, 'type': 'title', 'name': 'فرع', 'style': 'font-weight:bold'});
                $scope.items.push({
                    'id': 4,
                    'type': 'title',
                    'name': ' [ ' + $scope.buffer.branch.code + ' ] ' + $scope.buffer.branch.name
                });
                if ($scope.buffer.master) {
                    $scope.items.push({'id': 5, 'type': 'title', 'name': 'تخصص', 'style': 'font-weight:bold'});
                    $scope.items.push({
                        'id': 6,
                        'type': 'title',
                        'name': ' [ ' + $scope.buffer.master.code + ' ] ' + $scope.buffer.master.name
                    });
                }
                if ($scope.buffer.course) {
                    $scope.items.push({'id': 7, 'type': 'title', 'name': 'رقم الدورة', 'style': 'font-weight:bold'});
                    $scope.items.push({
                        'id': 8,
                        'type': 'title',
                        'name': ' [ ' + $scope.buffer.course.code + ' ] '
                    });
                }
            });
        };

        $scope.delete = function (account) {
            if (account) {
                $rootScope.showConfirmNotify("حذف البيانات", "هل تود حذف التسجيل فعلاً؟", "error", "fa-ban", function () {
                    AccountService.remove(account.id).then(function () {

                    });
                });
                return;
            }
            $rootScope.showConfirmNotify("حذف البيانات", "هل تود حذف التسجيل فعلاً؟", "error", "fa-ban", function () {
                AccountService.remove($scope.selected.id).then(function () {

                });
            });
        };

        $scope.rowMenu = [
            {
                html: '<div class="drop-menu"> انشاء تسجيل جديد <span class="fa fa-plus-square-o fa-lg"></span></div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    ModalProvider.openAccountCreateModel();
                }
            },
            {
                html: '<div class="drop-menu"> تعديل بيانات التسجيل <span class="fa fa-edit fa-lg"></span></div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    ModalProvider.openAccountUpdateModel($itemScope.account);
                }
            },
            {
                html: '<div class="drop-menu"> حذف التسجيل <span class="fa fa-minus-square-o fa-lg"></span></div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.delete($itemScope.account);
                }
            }
        ];

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 1500);


    }]);
