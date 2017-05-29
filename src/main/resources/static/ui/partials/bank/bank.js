app.controller("bankCtrl", ['BankService', 'ModalProvider', '$rootScope', '$scope', '$window', '$timeout', '$log', '$state',
    function (BankService, ModalProvider, $rootScope, $scope, $window, $timeout, $log, $state) {

        $timeout(function () {
            $scope.sideOpacity = 1;

            $scope.buffer = {};

            $scope.selected = {};
        }, 2000);

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 1500);

        $scope.setSelected = function (object) {
            if (object) {
                angular.forEach($scope.banks, function (bank) {
                    if (object.id == bank.id) {
                        bank.isSelected = true;
                        object = bank;
                    } else {
                        return bank.isSelected = false;
                    }
                });
                $scope.selected = object;
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
            if ($scope.buffer.branch) {
                search.push('branch=');
                search.push($scope.buffer.branch);
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

            BankService.filter(search.join("")).then(function (data) {
                $scope.banks = data;
                $scope.setSelected(data[0]);
            });
        };

        $scope.clear = function () {
            $scope.buffer = {};
        };

        $scope.reload = function () {
            $state.reload();
        };

        $scope.openCreateModel = function () {
            ModalProvider.openBankCreateModel();
        };

        $scope.openUpdateModel = function (bank) {
            if (bank) {
                ModalProvider.openBankUpdateModel(bank);
                return;
            }
            ModalProvider.openBankUpdateModel($scope.selected);
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
