app.controller('contractCreateCtrl', ['ContractService', 'CustomerService', 'SellerService', 'ProductPurchaseService', 'ModalProvider', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance',
    function (ContractService, CustomerService, SellerService, ProductPurchaseService, ModalProvider, $scope, $rootScope, $timeout, $log, $uibModalInstance) {

        $scope.buffer = {};

        $scope.contract = {};

        $scope.contract.discount = 0;

        $scope.customers = [];

        $scope.sellers = [];

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

        $scope.newSeller = function () {
            ModalProvider.openSellerCreateModel().result.then(function (data) {
                $scope.sellers.splice(0, 0, data);
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

        $scope.searchSellers = function ($select, $event) {

            // no event means first load!
            if (!$event) {
                $scope.pageSeller = 0;
                $scope.sellers = [];
            } else {
                $event.stopPropagation();
                $event.preventDefault();
                $scope.pageSeller++;
            }

            var search = [];

            search.push('size=');
            search.push(10);
            search.push('&');

            search.push('page=');
            search.push($scope.pageSeller);
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

            return SellerService.filter(search.join("")).then(function (data) {
                $scope.buffer.lastSeller = data.last;
                return $scope.sellers = $scope.sellers.concat(data.content);
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

        $scope.findProductPurchasesBySeller = function (seller) {
            ProductPurchaseService.findBySellerAndRemainFull(seller.id).then(function (value) {
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