app.controller('contractOldCreateCtrl', ['ContractService', 'CustomerService', 'SellerService', 'ProductService', 'ProductPurchaseService', 'ContractPaymentService', 'ModalProvider', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance',
    function (ContractService, CustomerService, SellerService, ProductService, ProductPurchaseService, ContractPaymentService, ModalProvider, $scope, $rootScope, $timeout, $log, $uibModalInstance) {

        $scope.buffer = {};

        $scope.contract = {};

        $scope.contract.discount = 0;

        $scope.contract.paid = 0;

        $scope.customers = [];

        $scope.sellers = [];

        $scope.productPurchases = [];

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

        $scope.newParent = function () {
            ModalProvider.openParentCreateModel().result.then(function (data) {
                $scope.parents.splice(0, 0, data);
                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 300);
            });
        };

        $scope.newProduct = function () {
            ModalProvider.openProductCreateModel().result.then(function (data) {

            });
        };

        $scope.refreshParents = function (isOpen) {
            if (isOpen) {
                ProductService.findParents().then(function (value) {
                    $scope.parents = value;
                });
            }
        };

        $scope.refreshChilds = function (isOpen) {
            if (isOpen) {
                ProductService.findChilds($scope.buffer.parent.id).then(function (value) {
                    return $scope.buffer.parent.childs = value;
                });
            }
        };

        $scope.addProductPurchase = function () {
            $scope.productPurchases.push({});
        };

        $scope.removeProductPurchase = function (index) {
            $scope.productPurchases.splice(index, 1);
        };

        $scope.submit = function () {
            //فحص هل السلع المطلوبة فارغة
            if($scope.productPurchases.length === 0){
                ModalProvider.openConfirmModel("العقود", "assignment", "فضلاً حدد على الأقل سلعة واحدة تود شرائها")
                    .result
                    .then(function (value) {
                        $scope.form.$setPristine();
                    });
                return;
            }
            var wrapperUtil = {};
            wrapperUtil.obj1 = $scope.contract;
            wrapperUtil.obj2 = $scope.productPurchases;
            ContractService.createOld(wrapperUtil);


            // //شراء السلع المحددة بالكميات المدخلة
            // var tempProductPurchases = [];
            // $scope.contract.contractProducts = [];
            // angular.forEach($scope.productPurchases, function (productPurchase) {
            //     var tempProductPurchase = JSON.parse(JSON.stringify(productPurchase));
            //     tempProductPurchase.seller = $scope.contract.seller;
            //     ProductPurchaseService.create(tempProductPurchase).then(function (data) {
            //         tempProductPurchases.push(data);
            //         //ربط الأصناف بالعقد
            //         var contractProduct = {};
            //         contractProduct.quantity = productPurchase.quantity;
            //         contractProduct.unitSellPrice = productPurchase.unitSellPrice;
            //         contractProduct.unitVat = productPurchase.unitVat;
            //         contractProduct.productPurchase = data;
            //         $scope.contract.contractProducts.push(contractProduct);
            //     });
            // });

            // //ربط الأقساط بالعقد
            // $scope.contract.contractPremiums = [];
            // $scope.contractPremium = {};
            // if ($scope.contract.paid > 0) {
            //     $scope.contractPremium.amount = $scope.contract.paid;
            //     $scope.contractPremium.dueDate = $scope.contract.writtenDate;
            //     $scope.contract.contractPremiums.push($scope.contractPremium);
            // }
            //
            // ContractService.create($scope.contract).then(function (data) {
            //     if ($scope.contract.paid > 0) {
            //         //إنشاء دفعة مالية بالمبالغ الواصلة
            //         var contractPayment = {};
            //         contractPayment.amount = $scope.contractPremium.amount;
            //         contractPayment.date = $scope.contractPremium.dueDate;
            //         contractPayment.contractPremium = $scope.contractPremium;
            //         contractPayment.contract = data;
            //         ContractPaymentService.create(contractPayment);
            //     }
            // });
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 600);

    }]);