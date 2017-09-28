app.controller("accountCtrl", ['AccountService', 'BranchService', 'MasterService', 'CourseService', 'ModalProvider', '$rootScope', '$scope', '$timeout', '$log', '$state', '$uibModal',
    function (AccountService, BranchService, MasterService, CourseService, ModalProvider, $rootScope, $scope, $timeout, $log, $state, $uibModal) {

        $scope.buffer = {};

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
            window.open('/report/account/contract/' + account.id);
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

                var search = [];
                if (buffer.firstName) {
                    search.push('firstName=');
                    search.push(buffer.firstName);
                    search.push('&');
                }
                if (buffer.secondName) {
                    search.push('secondName=');
                    search.push(buffer.secondName);
                    search.push('&');
                }
                if (buffer.thirdName) {
                    search.push('thirdName=');
                    search.push(buffer.thirdName);
                    search.push('&');
                }
                if (buffer.forthName) {
                    search.push('forthName=');
                    search.push(buffer.forthName);
                    search.push('&');
                }
                if (buffer.dateFrom) {
                    search.push('dateFrom=');
                    search.push(moment(buffer.dateFrom).valueOf());
                    search.push('&');
                }
                if (buffer.dateTo) {
                    search.push('dateTo=');
                    search.push(moment(buffer.dateTo).valueOf());
                    search.push('&');
                }
                if (buffer.studentIdentityNumber) {
                    search.push('studentIdentityNumber=');
                    search.push(buffer.studentIdentityNumber);
                    search.push('&');
                }
                if (buffer.studentMobile) {
                    search.push('studentMobile=');
                    search.push(buffer.studentMobile);
                    search.push('&');
                }
                if (buffer.coursePriceFrom) {
                    search.push('coursePriceFrom=');
                    search.push(buffer.coursePriceFrom);
                    search.push('&');
                }
                if (buffer.coursePriceTo) {
                    search.push('coursePriceTo=');
                    search.push(buffer.coursePriceTo);
                    search.push('&');
                }
                if (buffer.branch) {
                    search.push('branch=');
                    search.push(buffer.branch.id);
                    search.push('&');
                }
                if (buffer.master) {
                    search.push('master=');
                    search.push(buffer.master.id);
                    search.push('&');
                }
                if (buffer.course) {
                    search.push('course=');
                    search.push(buffer.course.id);
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
                        'name': ' [ ' + buffer.branch.code + ' ] ' + buffer.branch.name
                    });
                    if (buffer.master) {
                        $scope.items.push({'id': 5, 'type': 'title', 'name': 'تخصص', 'style': 'font-weight:bold'});
                        $scope.items.push({
                            'id': 6,
                            'type': 'title',
                            'name': ' [ ' + buffer.master.code + ' ] ' + buffer.master.name
                        });
                    }
                    if (buffer.course) {
                        $scope.items.push({'id': 7, 'type': 'title', 'name': 'رقم الدورة', 'style': 'font-weight:bold'});
                        $scope.items.push({
                            'id': 8,
                            'type': 'title',
                            'name': ' [ ' + buffer.course.code + ' ] '
                        });
                    }
                });

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
            }
        ];

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 1500);


    }]);
