app.controller('billPurchaseProductCreateCtrl', ['BillPurchaseService', 'BillPurchaseProductService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'billPurchase',
    function (BillPurchaseService, BillPurchaseProductService, $scope, $rootScope, $timeout, $log, $uibModalInstance, billPurchase) {

        $scope.buffer = {};

        $scope.billPurchase = billPurchase;

        $scope.capitalCash = 0;

        $scope.profitPercentage = 0;

        $scope.totalPrice = 0;

        $scope.totalPriceAfterVat = 0;

        //رأس المال
        $scope.findCapitalCash = function () {
            $scope.capitalCash = 0;
            angular.forEach($scope.billPurchase.supplier.productPurchases, function (productPurchase) {
                $scope.capitalCash = $scope.capitalCash + (productPurchase.requiredQuantity * productPurchase.unitPurchasePrice);
            });
        };

        //إجمالي الاصناف قبل (القيمة المضافة)
        //إجمالي الاصناف بعد (القيمة المضافة)
        $scope.findTotalPrice = function () {
            $scope.totalPrice = 0;
            $scope.totalPriceAfterVat = 0;
            angular.forEach($scope.billPurchase.supplier.productPurchases, function (productPurchase) {
                $scope.totalPrice = $scope.totalPrice + (productPurchase.requiredQuantity * productPurchase.unitSellPrice);
                $scope.totalPriceAfterVat = $scope.totalPriceAfterVat +
                    (productPurchase.requiredQuantity * productPurchase.unitSellPrice) +
                    (productPurchase.requiredQuantity * (productPurchase.unitSellPrice * $rootScope.selectedCompany.vatFactor));
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
            $scope.billPurchase.billPurchaseProducts = [];
            angular.forEach($scope.billPurchase.supplier.productPurchases, function (productPurchase) {
                if (productPurchase.requiredQuantity > 0) {
                    var billPurchaseProduct = {};
                    billPurchaseProduct.quantity = productPurchase.requiredQuantity;
                    billPurchaseProduct.unitSellPrice = productPurchase.unitSellPrice;
                    billPurchaseProduct.unitVat = productPurchase.unitVat;
                    billPurchaseProduct.productPurchase = productPurchase;
                    billPurchaseProduct.billPurchase = {}; //هامة جداًَ لربط السلعة مع العقد
                    billPurchaseProduct.billPurchase.id = $scope.billPurchase.id; //هامة جداًَ لربط السلعة مع العقد
                    $scope.billPurchase.billPurchaseProducts.push(billPurchaseProduct);
                }
            });
            BillPurchaseProductService.createBatch($scope.billPurchase.billPurchaseProducts).then(function (data) {
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