app.controller("bankCtrl", ['BankService', 'BranchService', 'ModalProvider', '$rootScope', '$scope', '$window', '$timeout', '$log', '$state',
    function (BankService, BranchService, ModalProvider, $rootScope, $scope, $window, $timeout, $log, $state) {

        //
        $scope.items = [];
        $scope.items.push({'id': 1, 'type': 'link', 'name': 'البرامج', 'link': 'menu'});
        $scope.items.push({'id': 2, 'type': 'title', 'name': 'الحسابات البنكية'});
        //

        $scope.buffer = {};

        $scope.selected = {};

        $scope.banks = [];

        $timeout(function () {
            BranchService.fetchBranchCombo().then(function (data) {
                $scope.branches = data;
                $scope.buffer.branch = $scope.branches[0];
            });
        }, 1500);

        $scope.setSelected = function (object) {
            if (object) {
                angular.forEach($scope.banks, function (bank) {
                    if (object.id === bank.id) {
                        $scope.selected = bank;
                        return bank.isSelected = true;
                    } else {
                        return bank.isSelected = false;
                    }
                });
            }
        };

        $scope.filter = function () {
            var modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: '/ui/partials/billBuy/billBuyFilter.html',
                controller: 'billBuyFilterCtrl',
                scope: $scope,
                backdrop: 'static',
                keyboard: false,
                resolve: {
                    title: function () {
                        return 'البحث فى الحسابات البنكية';
                    }
                }
            });

            modalInstance.result.then(function (buffer) {
                var search = [];
                if (buffer.code) {
                    search.push('code=');
                    search.push(buffer.code);
                    search.push('&');
                }
                if (buffer.name) {
                    search.push('name=');
                    search.push(buffer.name);
                    search.push('&');
                }
                if (buffer.branchName) {
                    search.push('branchName=');
                    search.push(buffer.branchName);
                    search.push('&');
                }
                if (buffer.stockFrom) {
                    search.push('stockFrom=');
                    search.push(buffer.stockFrom);
                    search.push('&');
                }
                if (buffer.stockTo) {
                    search.push('stockTo=');
                    search.push(buffer.stockTo);
                    search.push('&');
                }
                if (buffer.branch) {
                    search.push('branchId=');
                    search.push(buffer.branch.id);
                    search.push('&');
                }

                BankService.filter(search.join("")).then(function (data) {
                    $scope.banks = data;
                    $scope.setSelected(data[0]);
                    $scope.items = [];
                    $scope.items.push({'id': 1, 'type': 'link', 'name': 'البرامج', 'link': 'menu'});
                    $scope.items.push({'id': 2, 'type': 'title', 'name': 'الحسابات البنكية', 'style': 'font-weight:bold'});
                    $scope.items.push({'id': 3, 'type': 'title', 'name': 'فرع', 'style': 'font-weight:bold'});
                    $scope.items.push({
                        'id': 4,
                        'type': 'title',
                        'name': ' [ ' + buffer.branch.code + ' ] ' + buffer.branch.name
                    });
                });

            }, function () {
                console.info('BankFilterModel Closed.');
            });
        };

        $scope.newBank = function () {
            ModalProvider.openBankCreateModel().result.then(function (data) {
                $scope.banks.splice(0,0,data);
            }, function () {
                console.info('BankCreateModel Closed.');
            });
        };

        $scope.openDepositModel = function (bank) {
            if (bank) {
                ModalProvider.openDepositCreateModel(bank);
                return;
            }
            ModalProvider.openDepositCreateModel($scope.selected);
        };

        $scope.openWithdrawModel = function (bank) {
            if (bank) {
                ModalProvider.openWithdrawCreateModel(bank);
                return;
            }
            ModalProvider.openWithdrawCreateModel($scope.selected);
        };

        $scope.rowMenu = [
            {
                html: '<div class="drop-menu">انشاء حساب جديد<span class="fa fa-pencil fa-lg"></span></div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    ModalProvider.openBankCreateModel();
                }
            },
            {
                html: '<div class="drop-menu">تعديل بيانات الحساب<span class="fa fa-edit fa-lg"></span></div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    ModalProvider.openBankUpdateModel($itemScope.bank);
                }
            },
            {
                html: '<div class="drop-menu">حذف الحساب<span class="fa fa-trash fa-lg"></span></div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.delete($itemScope.bank);
                }
            }
        ];

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 1500);

    }]);
