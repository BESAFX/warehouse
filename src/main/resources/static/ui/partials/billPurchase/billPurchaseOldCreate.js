app.controller('billPurchaseOldCreateCtrl', ['BillPurchaseService', 'CustomerService', 'SupplierService', 'ProductService', 'ProductPurchaseService', 'BillPurchasePaymentService', 'ModalProvider', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance',
    function (BillPurchaseService, CustomerService, SupplierService, ProductService, ProductPurchaseService, BillPurchasePaymentService, ModalProvider, $scope, $rootScope, $timeout, $log, $uibModalInstance) {

        $scope.buffer = {};

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

        $scope.clear = function () {

            $scope.billPurchase = {};

            $scope.billPurchase.discount = 0;

            $scope.billPurchase.paid = 0;

            $scope.customers = [];

            $scope.suppliers = [];

            $scope.productPurchases = [];

            $scope.form.$setPristine();

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
            wrapperUtil.obj1 = $scope.billPurchase;
            wrapperUtil.obj2 = $scope.productPurchases;
            BillPurchaseService.createOld(wrapperUtil).then(function (value) {
                $scope.clear();
            });
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $timeout(function () {
            $scope.clear();
            window.componentHandler.upgradeAllRegistered();
        }, 600);

    }]);