app.controller("bankCtrl", ['BankService', 'BranchService', 'ModalProvider', '$rootScope', '$scope', '$window', '$timeout', '$log', '$state',
    function (BankService, BranchService, ModalProvider, $rootScope, $scope, $window, $timeout, $log, $state) {

        //
        $scope.items = [];
        $scope.items.push({'id': 1, 'type': 'link', 'name': 'البرامج', 'link': 'menu'});
        $scope.items.push({'id': 2, 'type': 'title', 'name': 'الحسابات البنكية'});
        //

        $scope.buffer = {};

        $scope.selected = {};

        $timeout(function () {
            $scope.sideOpacity = 1;
            BranchService.fetchTableDataSummery().then(function (data) {
                $scope.branches = data;
                $scope.buffer.branch = $scope.branches[0];
            });
        }, 1500);

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 1500);

        $scope.setSelected = function (object) {
            if (object) {
                angular.forEach($scope.banks, function (bank) {
                    if (object.id == bank.id) {
                        $scope.selected = bank;
                        return bank.isSelected = true;
                    } else {
                        return bank.isSelected = false;
                    }
                });
            }
        };

        $scope.filter = function () {
            var search = [];
            if ($scope.buffer.code) {
                search.push('code=');
                search.push($scope.buffer.code);
                search.push('&');
            }
            if ($scope.buffer.name) {
                search.push('name=');
                search.push($scope.buffer.name);
                search.push('&');
            }
            if ($scope.buffer.branchName) {
                search.push('branchName=');
                search.push($scope.buffer.branchName);
                search.push('&');
            }
            if ($scope.buffer.stockFrom) {
                search.push('stockFrom=');
                search.push($scope.buffer.stockFrom);
                search.push('&');
            }
            if ($scope.buffer.stockTo) {
                search.push('stockTo=');
                search.push($scope.buffer.stockTo);
                search.push('&');
            }
            if ($scope.buffer.branch) {
                search.push('branchId=');
                search.push($scope.buffer.branch.id);
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
                    'name': ' [ ' + $scope.buffer.branch.code + ' ] ' + $scope.buffer.branch.name
                });
            });
        };

        $scope.clear = function () {
            $scope.buffer = {};
            $scope.buffer.branch = $scope.branches[0];
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
                html: '<div style="cursor: pointer;padding: 10px"><span class="fa fa-plus-square-o fa-lg"></span> اضافة</div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.openCreateModel();
                }
            },
            {
                html: '<div style="cursor: pointer;padding: 10px"><span class="fa fa-edit fa-lg"></span> تعديل</div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.openUpdateModel($itemScope.bank);
                }
            },
            {
                html: '<div style="cursor: pointer;padding: 10px"><span class="fa fa-minus-square-o fa-lg"></span> حذف</div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $log.info($itemScope.bank);
                }
            },
            {
                html: '<div style="cursor: pointer;padding: 10px"><span class="fa fa-upload fa-lg"></span> إيداع</div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.openDepositModel($itemScope.bank);
                }
            },
            {
                html: '<div style="cursor: pointer;padding: 10px"><span class="fa fa-download fa-lg"></span> سحب</div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.openWithdrawModel($itemScope.bank);
                }
            }
        ];

    }]);
