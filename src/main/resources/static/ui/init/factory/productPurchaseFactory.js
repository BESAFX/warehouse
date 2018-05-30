app.factory("ProductPurchaseService",
    ['$http', '$log', function ($http, $log) {
        return {
            findOne: function (id) {
                return $http.get("/api/productPurchase/findOne/" + id).then(function (response) {
                    return response.data;
                });
            },
            findMyProductPurchases: function () {
                return $http.get("/api/productPurchase/findMyProductPurchases").then(function (response) {
                    return response.data;
                });
            },
            findBySupplier: function (id) {
                return $http.get("/api/productPurchase/findBySupplier/" + id).then(function (response) {
                    return response.data;
                });
            },
            findBySupplierAndRemainFull: function (id) {
                return $http.get("/api/productPurchase/findBySupplierAndRemainFull/" + id).then(function (response) {
                    return response.data;
                });
            },
            findBySupplierAndRemainEmpty: function (id) {
                return $http.get("/api/productPurchase/findBySupplierAndRemainEmpty/" + id).then(function (response) {
                    return response.data;
                });
            },
            create: function (productPurchase) {
                return $http.post("/api/productPurchase/create", productPurchase).then(function (response) {
                    return response.data;
                });
            },
            remove: function (id) {
                return $http.delete("/api/productPurchase/delete/" + id);
            },
            filter: function (search) {
                return $http.get("/api/productPurchase/filter?" + search).then(function (response) {
                    return response.data;
                });
            }
        };
    }]);