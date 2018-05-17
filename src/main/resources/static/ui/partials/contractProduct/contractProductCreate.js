app.controller('contractProductCreateCtrl', ['ContractService', 'ContractProductService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'contract',
    function (ContractService, ContractProductService, $scope, $rootScope, $timeout, $log, $uibModalInstance, contract) {

        $scope.buffer = {};

        $scope.contract = contract;

        $scope.capitalCash = 0;

        $scope.profitPercentage = 0;

        $scope.totalPrice = 0;

        $scope.reaminPrice = 0;

        //رأس المال
        $scope.findCapitalCash = function () {
            $scope.capitalCash = 0;
            angular.forEach($scope.contract.seller.productPurchases, function (productPurchase) {
                $scope.capitalCash = $scope.capitalCash + (productPurchase.requiredQuantity * productPurchase.unitPurchasePrice);
            });
        };

        //إجمالي العقد
        $scope.findTotalPrice = function () {
            $scope.totalPrice = 0;
            angular.forEach($scope.contract.seller.productPurchases, function (productPurchase) {
                $scope.totalPrice = $scope.totalPrice + (productPurchase.requiredQuantity * productPurchase.unitSellPrice);
            });
            return $scope.totalPrice;
        };

        //نسبة الربح
        $scope.findProfitPercentage = function () {
            $scope.findCapitalCash();
            $scope.findTotalPrice();
            $scope.profitPercentage = (($scope.totalPrice - $scope.capitalCash) / $scope.capitalCash) * 100;
        };

        $scope.submit = function () {
            //ربط الأصناف بالعقد
            $scope.contract.contractProducts = [];
            angular.forEach($scope.contract.seller.productPurchases, function (productPurchase) {
                if (productPurchase.requiredQuantity > 0) {
                    var contractProduct = {};
                    contractProduct.quantity = productPurchase.requiredQuantity;
                    contractProduct.unitSellPrice = productPurchase.unitSellPrice;
                    contractProduct.productPurchase = productPurchase;
                    contractProduct.contract = {}; //هامة جداًَ لربط السلعة مع العقد
                    contractProduct.contract.id = $scope.contract.id; //هامة جداًَ لربط السلعة مع العقد
                    $scope.contract.contractProducts.push(contractProduct);
                }
            });
            ContractProductService.createBatch($scope.contract.contractProducts).then(function (data) {
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