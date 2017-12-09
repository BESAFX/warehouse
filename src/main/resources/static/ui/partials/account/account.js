app.controller("accountCtrl", ['AccountService', 'BranchService', 'MasterService', 'CourseService', 'ModalProvider', '$rootScope', '$scope', '$timeout', '$log', '$state', '$uibModal',
    function (AccountService, BranchService, MasterService, CourseService, ModalProvider, $rootScope, $scope, $timeout, $log, $state, $uibModal) {

        $scope.buffer = {};
        $scope.account = {};
        $scope.accounts = [];

        //
        $scope.items = [];
        $scope.items.push({'id': 1, 'type': 'link', 'name': 'البرامج', 'link': 'menu'});
        $scope.items.push({'id': 2, 'type': 'title', 'name': 'تسجيل الطلاب'});
        //

        $timeout(function () {
            BranchService.fetchBranchMasterCourse().then(function (data) {
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

        $scope.newAccount = function () {
            ModalProvider.openAccountCreateModel($scope.selected);
        };

        $scope.createFastAccount = function () {
            AccountService.create($scope.account).then(function (data) {
                AccountService.findOne(data.id).then(function (data) {
                    $scope.accounts.splice(0, 0, data);
                    $scope.account = {};
                    $scope.form1.$setPristine();
                });
            });
        };

        $scope.newPayment = function () {
            ModalProvider.openAccountPaymentModel($scope.selected).result.then(function (data) {
                AccountService.findOne($scope.selected.id).then(function (data) {
                    var index = $scope.accounts.indexOf($scope.selected);
                    $scope.accounts[index] = data;
                    $scope.setSelected(data);
                });
            });
        };

        $scope.print = function (account) {
            var modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: '/ui/partials/account/accountContract.html',
                controller: 'accountContractCtrl',
                backdrop: 'static',
                keyboard: false,
                resolve: {
                    account: function () {
                        return account;
                    }
                }
            });

            modalInstance.result.then(function (buffer) {

            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        };

        $scope.search = function () {

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
                search.push('branchIds=');
                var branchIds = [];
                branchIds.push($scope.buffer.branch.id);
                search.push(branchIds);
                search.push('&');
            }
            if ($scope.buffer.master) {
                search.push('masterIds=');
                var masterIds = [];
                masterIds.push($scope.buffer.master.id);
                search.push(masterIds);
                search.push('&');
            }
            if ($scope.buffer.course) {
                search.push('courseIds=');
                var courseIds = [];
                courseIds.push($scope.buffer.course.id);
                search.push(courseIds);
                search.push('&');
            }
            AccountService.filterWithInfo(search.join("")).then(function (data) {
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

                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 600);

            });

        };

        $scope.clearBuffer = function () {
            $scope.buffer = {};
            $scope.buffer.branch = $scope.branches[0];
        };

        $scope.filter = function () {
            var modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: '/ui/partials/account/accountFilter.html',
                controller: 'accountFilterCtrl',
                scope: $scope,
                backdrop: 'static',
                keyboard: false,
                resolve: {
                    title: function () {
                        return 'البحث فى التسجيل';
                    }
                }
            });

            modalInstance.result.then(function (buffer) {

                $scope.buffer = buffer;

                $scope.search();

            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        };

        $scope.delete = function (account) {
            if (account) {
                $rootScope.showConfirmNotify("حذف البيانات", "هل تود حذف التسجيل فعلاً؟", "error", "fa-ban", function () {
                    AccountService.remove(account.id).then(function () {
                        var index = $scope.accounts.indexOf(account);
                        $scope.accounts.splice(index, 1);
                        $scope.setSelected($scope.accounts[0]);
                    });
                });
                return;
            }
            $rootScope.showConfirmNotify("حذف البيانات", "هل تود حذف التسجيل فعلاً؟", "error", "fa-ban", function () {
                AccountService.remove($scope.selected.id).then(function () {
                    var index = $scope.accounts.indexOf($scope.selected);
                    $scope.accounts.splice(index, 1);
                    $scope.setSelected($scope.accounts[0]);
                });
            });
        };

        $scope.rowMenu = [
            {
                html: '<div class="drop-menu">انشاء تسجيل جديد<span class="fa fa-pencil fa-lg"></span></div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.newAccount();
                }
            },
            {
                html: '<div class="drop-menu">تعديل بيانات التسجيل<span class="fa fa-edit fa-lg"></span></div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    ModalProvider.openAccountUpdateModel($itemScope.account);
                }
            },
            {
                html: '<div class="drop-menu">حذف التسجيل<span class="fa fa-trash fa-lg"></span></div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.delete($itemScope.account);
                }
            },
            {
                html: '<div class="drop-menu">سند قبض<span class="fa fa-money fa-lg"></span></div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.newPayment($itemScope.account);
                }
            },
            {
                html: '<div class="drop-menu"> التفاصيل <span class="fa fa-info fa-lg"></span></div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    ModalProvider.openAccountDetailsModel($itemScope.account);
                }
            },
            {
                html: '<div class="drop-menu">طباعة عقد<span class="fa fa-print fa-lg"></span></div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.print($itemScope.account);
                }
            }
        ];

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 1500);


    }]);
