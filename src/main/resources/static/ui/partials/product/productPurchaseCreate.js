app.controller('productPurchaseCreateCtrl', ['SellerService', 'ProductService', 'ProductPurchaseService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance',
    function (SellerService, ProductService, ProductPurchaseService, $scope, $rootScope, $timeout, $log, $uibModalInstance) {

        $scope.buffer = {};

        $scope.buffer.searchBy = 'name';

        $scope.productPurchases = [];

        $scope.sellers = [];

        $scope.searchSellers = function ($select, $event) {

            // no event means first load!
            if (!$event) {
                $scope.page = 0;
                $scope.sellers = [];
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

            return SellerService.filter(search.join("")).then(function (data) {
                $scope.buffer.last = data.last;
                return $scope.sellers = $scope.sellers.concat(data.content);
            });

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
                tempProductPurchase.seller = $scope.buffer.seller;
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
            ProductService.findParents().then(function (value) {
                $scope.parents = value;
            });
            window.componentHandler.upgradeAllRegistered();
        }, 600);

    }]);