app.controller('depositCreateCtrl', ['SupplierService', 'BankTransactionService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance',
    function (SupplierService, BankTransactionService, $scope, $rootScope, $timeout, $log, $uibModalInstance) {

        $scope.buffer = {};

        $scope.buffer.searchBy = 'name';

        $scope.bankTransaction = {};

        $scope.suppliers = [];

        $scope.searchSuppliers = function ($select, $event) {

            // no event means first load!
            if (!$event) {
                $scope.page = 0;
                $scope.suppliers = [];
            } else {
                $event.stopPropagation();
                $event.preventDefault();
                $scope.page++;
            }

            var search = [];

            search.push('size=');
            search.push(10);
            search.push('&');

            search.push('page=');
            search.push($scope.page);
            search.push('&');

            switch ($scope.buffer.searchBy) {
                case "name": {
                    search.push('name=');
                    search.push($select.search);
                    search.push('&');
                    break;
                }
                case "identityNumber":
                    search.push('identityNumber=');
                    search.push($select.search);
                    search.push('&');
                    break;
                case "mobile":
                    search.push('mobile=');
                    search.push($select.search);
                    search.push('&');
                    break;
            }

            return SupplierService.filter(search.join("")).then(function (data) {
                $scope.buffer.last = data.last;
                return $scope.suppliers = $scope.suppliers.concat(data.content);
            });

        };

        $scope.findSupplierBalance = function () {
            SupplierService.findSupplierBalance($scope.bankTransaction.supplier.id).then(function (value) {
                return $scope.bankTransaction.supplier = value;
            });
        };

        $scope.submit = function () {
            BankTransactionService.createDeposit($scope.bankTransaction).then(function (data) {
                $uibModalInstance.close(data);
            });
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 600);

    }]);