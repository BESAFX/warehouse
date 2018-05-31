app.factory("BillPurchaseService",
    ['$http', '$log', function ($http, $log) {
        return {
            findOne: function (id) {
                return $http.get("/api/billPurchase/findOne/" + id).then(function (response) {
                    return response.data;
                });
            },
            findMyContracts: function () {
                return $http.get("/api/billPurchase/findMyContracts").then(function (response) {
                    return response.data;
                });
            },
            findBySupplier: function (supplierId) {
                return $http.get("/api/billPurchase/findBySupplier/" + supplierId).then(function (response) {
                    return response.data;
                });
            },
            create: function (contract) {
                return $http.post("/api/billPurchase/create", contract).then(function (response) {
                    return response.data;
                });
            },
            createOld: function (wrapperUtil) {
                return $http.post("/api/billPurchase/createOld", wrapperUtil).then(function (response) {
                    return response.data;
                });
            },
            remove: function (id) {
                return $http.delete("/api/billPurchase/delete/" + id);
            },
            filter: function (search) {
                return $http.get("/api/billPurchase/filter?" + search).then(function (response) {
                    return response.data;
                });
            }
        };
    }]);