app.controller("depositCtrl", ['DepositService', 'BranchService', 'ModalProvider', '$rootScope', '$scope', '$window', '$timeout', '$log', '$state',
    function (DepositService, BranchService, ModalProvider, $rootScope, $scope, $window, $timeout, $log, $state) {

        //
        $scope.items = [];
        $scope.items.push({'id': 1, 'type': 'link', 'name': 'البرامج', 'link': 'menu'});
        $scope.items.push({'id': 2, 'type': 'title', 'name': 'الإيداعات البنكية'});
        //

        $scope.buffer = {};

        $scope.selected = {};

        $scope.deposits = [];

        $timeout(function () {
            $scope.sideOpacity = 1;
            BranchService.fetchTableDataSummery().then(function (data) {
                $scope.branches = data;
                $scope.buffer.bankBranch = $scope.branches[0];
            });
        }, 2000);

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 1500);

        $scope.setSelected = function (object) {
            if (object) {
                angular.forEach($scope.deposits, function (deposit) {
                    if (object.id == deposit.id) {
                        $scope.selected = deposit;
                        return deposit.isSelected = true;
                    } else {
                        return deposit.isSelected = false;
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
            if ($scope.buffer.amountFrom) {
                search.push('amountFrom=');
                search.push($scope.buffer.amountFrom);
                search.push('&');
            }
            if ($scope.buffer.amountTo) {
                search.push('amountTo=');
                search.push($scope.buffer.amountTo);
                search.push('&');
            }
            if ($scope.buffer.fromName) {
                search.push('fromName=');
                search.push($scope.buffer.fromName);
                search.push('&');
            }
            if ($scope.buffer.dateFrom) {
                search.push('dateFrom=');
                search.push($scope.buffer.dateFrom.getTime());
                search.push('&');
            }
            if ($scope.buffer.dateTo) {
                search.push('dateTo=');
                search.push($scope.buffer.dateTo.getTime());
                search.push('&');
            }

            if ($scope.buffer.bankCode) {
                search.push('bankCode=');
                search.push($scope.buffer.bankCode);
                search.push('&');
            }
            if ($scope.buffer.bankName) {
                search.push('bankName=');
                search.push($scope.buffer.bankName);
                search.push('&');
            }
            if ($scope.buffer.bankBranch) {
                search.push('bankBranch=');
                search.push($scope.buffer.bankBranch.id);
                search.push('&');
            }
            if ($scope.buffer.bankBranchName) {
                search.push('bankBranchName=');
                search.push($scope.buffer.bankBranchName);
                search.push('&');
            }
            if ($scope.buffer.bankStockFrom) {
                search.push('bankStockFrom=');
                search.push($scope.buffer.bankStockFrom);
                search.push('&');
            }
            if ($scope.buffer.bankStockTo) {
                search.push('bankStockTo=');
                search.push($scope.buffer.bankStockTo);
                search.push('&');
            }

            DepositService.filter(search.join("")).then(function (data) {
                $scope.deposits = data;
                $scope.setSelected(data[0]);
                $scope.items = [];
                $scope.items.push({'id': 1, 'type': 'link', 'name': 'البرامج', 'link': 'menu'});
                $scope.items.push({'id': 2, 'type': 'title', 'name': 'الإيداعات البنكية', 'style': 'font-weight:bold'});
                $scope.items.push({'id': 3, 'type': 'title', 'name': 'فرع', 'style': 'font-weight:bold'});
                $scope.items.push({
                    'id': 4,
                    'type': 'title',
                    'name': ' [ ' + $scope.buffer.bankBranch.code + ' ] ' + $scope.buffer.bankBranch.name
                });
            });
        };

        $scope.clear = function () {
            $scope.buffer = {};
            $scope.buffer.bankBranch = $scope.branches[0];
        };

    }]);
