app.controller('billPurchaseCreateCtrl', ['BillPurchaseService', 'SupplierService', 'ModalProvider', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance',
    function (BillPurchaseService, SupplierService, ModalProvider, $scope, $rootScope, $timeout, $log, $uibModalInstance) {

        $scope.buffer = {};

        $scope.billPurchase = {};

        $scope.billPurchase.discount = 0;

        $scope.suppliers = [];

        $scope.capitalCash = 0;

        $scope.profitPercentage = 0;

        $scope.totalPrice = 0;

        $scope.totalPriceAfterDiscountAndVat = 0;

        $scope.reaminPrice = 0;

        $scope.newSupplier = function () {
            ModalProvider.openSupplierCreateModel().result.then(function (data) {
                $scope.suppliers.splice(0, 0, data);
                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 300);
            });
        };

        $scope.searchSuppliers = function ($select, $event) {

            // no event means first load!
            if (!$event) {
                $scope.pageSupplier = 0;
                $scope.suppliers = [];
            } else {
                $event.stopPropagation();
                $event.preventDefault();
                $scope.pageSupplier++;
            }

            var search = [];

            search.push('size=');
            search.push(10);
            search.push('&');

            search.push('page=');
            search.push($scope.pageSupplier);
            search.push('&');

            search.push('name=');
            search.push($select.search);
            search.push('&');

            search.push('identityNumber=');
            search.push($select.search);
            search.push('&');

            search.push('mobile=');
            search.push($select.search);
            search.push('&');

            search.push('filterCompareType=or');

            return SupplierService.filter(search.join("")).then(function (data) {
                $scope.buffer.lastSupplier = data.last;
                return $scope.suppliers = $scope.suppliers.concat(data.content);
            });

        };

        //رأس المال
        $scope.findCapitalCash = function () {
            $scope.capitalCash = 0;
            angular.forEach($scope.productPurchases, function (productPurchase) {
                $scope.capitalCash = $scope.capitalCash + (productPurchase.requiredQuantity * productPurchase.unitPurchasePrice);
            });
        };

        //إجمالي العقد قبل (الخصم + القيمة المضافة)
        //إجمالي العقد بعد (الخصم + القيمة المضافة)
        $scope.findTotalPrice = function () {
            $scope.totalPrice = 0;
            $scope.totalPriceAfterDiscountAndVat = 0;
            angular.forEach($scope.productPurchases, function (productPurchase) {
                $scope.totalPrice = $scope.totalPrice + (productPurchase.requiredQuantity * productPurchase.unitSellPrice);
                $scope.totalPriceAfterDiscountAndVat = $scope.totalPriceAfterDiscountAndVat +
                    (productPurchase.requiredQuantity * productPurchase.unitSellPrice) +
                    (productPurchase.requiredQuantity * (productPurchase.unitSellPrice * $rootScope.selectedCompany.vatFactor)) -
                    $scope.billPurchase.discount;
            });
            return $scope.totalPrice;
        };

        //نسبة الربح
        $scope.findProfitPercentage = function () {
            $scope.findCapitalCash();
            $scope.findTotalPrice();
            $scope.profitPercentage = (($scope.totalPrice - $scope.capitalCash) / $scope.capitalCash) * 100;
        };

        //حساب الباقي من اجمالي العقد عند اضافة الأقساط
        $scope.findRemainPrice = function () {
            $scope.findTotalPrice();
            //يتم حساب الباقي بعد (الخصم + القيمة المضافة)
            $scope.remainPrice = $scope.totalPriceAfterDiscountAndVat - $scope.findBillPurchasePremiumsSum();
        };

        $scope.submit = function () {
            //ربط الأصناف بالعقد
            $scope.billPurchase.billPurchaseProducts = [];
            angular.forEach($scope.productPurchases, function (productPurchase) {
                if (productPurchase.requiredQuantity > 0) {
                    var billPurchaseProduct = {};
                    billPurchaseProduct.quantity = productPurchase.requiredQuantity;
                    billPurchaseProduct.unitSellPrice = productPurchase.unitSellPrice;
                    billPurchaseProduct.unitVat = productPurchase.unitVat;
                    billPurchaseProduct.productPurchase = productPurchase;
                    $scope.billPurchase.billPurchaseProducts.push(billPurchaseProduct);
                }
            });
            BillPurchaseService.create($scope.billPurchase).then(function (data) {
                BillPurchaseService.findOne(data.id).then(function (value) {
                    $uibModalInstance.close(value);
                });
            });
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 600);

    }]);