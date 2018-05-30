app.controller('productPurchaseCreateCtrl', ['SupplierService', 'ProductService', 'ProductPurchaseService', 'ModalProvider', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance',
    function (SupplierService, ProductService, ProductPurchaseService, ModalProvider, $scope, $rootScope, $timeout, $log, $uibModalInstance) {

        $scope.buffer = {};

        $scope.buffer.searchBy = 'name';

        $scope.productPurchases = [];

        $scope.suppliers = [];

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
            if(isOpen){
                ProductService.findParents().then(function (value) {
                    $scope.parents = value;
                });
            }
        };

        $scope.refreshChilds = function (isOpen) {
            if(isOpen){
                ProductService.findChilds($scope.buffer.parent.id).then(function (value) {
                    return $scope.buffer.parent.childs = value;
                });
            }
        };

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

        $scope.findSupplierBalance = function (supplier) {
            SupplierService.findSupplierBalance(supplier.id).then(function (value) {
               return $scope.buffer.supplier = value;
            })
        };

        $scope.addProductPurchase = function () {
            $scope.productPurchases.push({});
        };

        $scope.removeProductPurchase = function (index) {
            $scope.productPurchases.splice(index, 1);
        };

        $scope.submit = function () {
            var tempProductPurchases = [];
            angular.forEach($scope.productPurchases, function (productPurchase) {
                var tempProductPurchase = JSON.parse(JSON.stringify(productPurchase));
                tempProductPurchase.supplier = $scope.buffer.supplier;
                tempProductPurchase.note = $scope.buffer.note;
                ProductPurchaseService.create(tempProductPurchase).then(function (data) {
                    tempProductPurchases.push(data);
                });
            });
            $uibModalInstance.close({});
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 600);

    }]);