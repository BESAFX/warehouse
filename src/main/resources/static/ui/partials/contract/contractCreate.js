app.controller('contractCreateCtrl', ['ContractService', 'CustomerService', 'SupplierService', 'ProductPurchaseService', 'ModalProvider', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance',
    function (ContractService, CustomerService, SupplierService, ProductPurchaseService, ModalProvider, $scope, $rootScope, $timeout, $log, $uibModalInstance) {

        $scope.buffer = {};

        $scope.contract = {};

        $scope.contract.discount = 0;

        $scope.customers = [];

        $scope.suppliers = [];

        $scope.productPurchases = [];

        $scope.contractPremiums = [];

        $scope.contractPremiumsSum = 0;

        $scope.capitalCash = 0;

        $scope.profitPercentage = 0;

        $scope.totalPrice = 0;

        $scope.totalPriceAfterDiscountAndVat = 0;

        $scope.reaminPrice = 0;

        $scope.newCustomer = function () {
            ModalProvider.openCustomerCreateModel().result.then(function (data) {
                $scope.customers.splice(0, 0, data);
                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 300);
            });
        };

        $scope.newSupplier = function () {
            ModalProvider.openSupplierCreateModel().result.then(function (data) {
                $scope.suppliers.splice(0, 0, data);
                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 300);
            });
        };

        $scope.newProductPurchase = function () {
            ModalProvider.openProductPurchaseCreateModel().result.then(function (data) {
                $scope.productPurchases.splice(0, 0, data);
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

        $scope.searchCustomers = function ($select, $event) {

            // no event means first load!
            if (!$event) {
                $scope.pageCustomer = 0;
                $scope.customers = [];
            } else {
                $event.stopPropagation();
                $event.preventDefault();
                $scope.pageCustomer++;
            }

            var search = [];

            search.push('size=');
            search.push(10);
            search.push('&');

            search.push('page=');
            search.push($scope.pageCustomer);
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

            return CustomerService.filter(search.join("")).then(function (data) {
                $scope.buffer.lastCustomer = data.last;
                return $scope.customers = $scope.customers.concat(data.content);
            });

        };

        $scope.findProductPurchasesBySupplier = function (supplier) {
            ProductPurchaseService.findBySupplierAndRemainFull(supplier.id).then(function (value) {
                $scope.productPurchases = [];
                $scope.capitalCash = 0;
                $scope.profitPercentage = 0;
                $scope.totalPrice = 0;
                angular.forEach(value, function (productPurchase) {
                    productPurchase.requiredQuantity = 0;
                    productPurchase.unitSellPrice = 0;
                    $scope.productPurchases.push(productPurchase);
                });
            })
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
                    $scope.contract.discount;
            });
            return $scope.totalPrice;
        };

        //نسبة الربح
        $scope.findProfitPercentage = function () {
            $scope.findCapitalCash();
            $scope.findTotalPrice();
            $scope.profitPercentage = (($scope.totalPrice - $scope.capitalCash) / $scope.capitalCash) * 100;
        };

        //اضافة قسط
        $scope.addContractPremium = function () {
            var contractPremium = {};
            contractPremium.amount = 0;
            contractPremium.dueDate = new Date();
            $scope.contractPremiums.push(contractPremium);
        };

        //إزالة قسط
        $scope.removeContractPremium = function (index) {
            $scope.contractPremiums.splice(index, 1);
        };

        //حساب إجمالي الاقساط
        $scope.findContractPremiumsSum = function () {
            $scope.contractPremiumsSum = 0;
            angular.forEach($scope.contractPremiums, function (contractPremium) {
                $scope.contractPremiumsSum = $scope.contractPremiumsSum + contractPremium.amount;
            });
            return $scope.contractPremiumsSum;
        };

        //حساب الباقي من اجمالي العقد عند اضافة الأقساط
        $scope.findRemainPrice = function () {
            $scope.findTotalPrice();
            //يتم حساب الباقي بعد (الخصم + القيمة المضافة)
            $scope.remainPrice = $scope.totalPriceAfterDiscountAndVat - $scope.findContractPremiumsSum();
        };

        $scope.submit = function () {
            //ربط الأصناف بالعقد
            $scope.contract.contractProducts = [];
            angular.forEach($scope.productPurchases, function (productPurchase) {
                if (productPurchase.requiredQuantity > 0) {
                    var contractProduct = {};
                    contractProduct.quantity = productPurchase.requiredQuantity;
                    contractProduct.unitSellPrice = productPurchase.unitSellPrice;
                    contractProduct.unitVat = productPurchase.unitVat;
                    contractProduct.productPurchase = productPurchase;
                    $scope.contract.contractProducts.push(contractProduct);
                }
            });
            //ربط الأقساط بالعقد
            $scope.contract.contractPremiums = [];
            angular.forEach($scope.contractPremiums, function (contractPremium) {
                if (contractPremium.amount > 0) {
                    $scope.contract.contractPremiums.push(contractPremium);
                }
            });
            ContractService.create($scope.contract).then(function (data) {
                ContractService.findOne(data.id).then(function (value) {
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