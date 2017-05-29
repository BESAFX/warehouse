app.controller("accountCtrl", ['AccountService', 'BranchService', 'MasterService', 'CourseService', 'ModalProvider', '$rootScope', '$scope', '$timeout', '$log', '$state',
    function (AccountService, BranchService, MasterService, CourseService, ModalProvider, $rootScope, $scope, $timeout, $log, $state) {

        $timeout(function () {

            $scope.sideOpacity = 1;

            $scope.buffer = {};

            BranchService.fetchTableData().then(function (data) {
                $scope.branches = data;
                $scope.buffer.branch = $scope.branches[0];
            });

        }, 2000);

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 1500);

        $scope.setSelected = function (object) {
            if (object) {
                angular.forEach($scope.accounts, function (account) {
                    if (object.id == account.id) {
                        account.isSelected = true;
                        object = account;
                    } else {
                        return account.isSelected = false;
                    }
                });
                $scope.selected = object;
            }
        };

        $scope.openCreateModel = function () {
            ModalProvider.openAccountCreateModel();
        };

        $scope.openUpdateModel = function (account) {
            if (account) {
                ModalProvider.openAccountUpdateModel(account);
                return;
            }
            ModalProvider.openAccountUpdateModel($scope.selected);
        };

        $scope.openPaymentCreateFromAccountModel = function (account) {
            var payment = {};
            if (account) {
                payment.account = account;
                ModalProvider.openPaymentCreateModel(payment);
            } else {
                payment.account = $scope.selected;
                ModalProvider.openPaymentCreateModel(payment);
            }
        };

        $scope.reload = function () {
            $state.reload();
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
            });
        };

        $scope.rowMenu = [
            {
                html: '<div style="cursor: pointer;padding: 10px;text-align: right"> اضافة تسجيل جديد <span class="fa fa-plus-square-o fa-lg"></span></div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.openCreateModel();
                }
            },
            {
                html: '<div style="cursor: pointer;padding: 10px;text-align: right"> تعديل بيانات الطالب <span class="fa fa-edit fa-lg"></span></div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.openUpdateModel($itemScope.account);
                }
            },
            {
                html: '<div style="cursor: pointer;padding: 10px;text-align: right"> حذف التسجيل <span class="fa fa-minus-square-o fa-lg"></span></div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {

                }
            },
            {
                html: '<div style="cursor: pointer;padding: 10px;text-align: right"> اضافة سند جديد <span class="fa fa-file-text fa-lg"></span></div>',
                enabled: function () {
                    return true;
                },
                click: function ($itemScope, $event, value) {
                    $scope.openPaymentCreateFromAccountModel($itemScope.account);
                }
            },
            {
                html: '<div style="cursor: pointer;padding: 10px;text-align: right"> طباعة عقد <span class="fa fa-file-text fa-lg"></span></div>',
                enabled: function () {
                    return true;
                },
                click: function ($itemScope, $event, value) {
                    $scope.print($itemScope.account);

                }
            }
        ];

    }]);
