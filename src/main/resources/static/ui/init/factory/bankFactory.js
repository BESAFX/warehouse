app.factory("BankService",
    ['$http', '$log', function ($http, $log) {
        return {
            findAll: function () {
                return $http.get("/api/bank/findAll").then(function (response) {
                    return response.data;
                });
            },
            findMyBanks: function () {
                return $http.get("/api/bank/findMyBanks").then(function (response) {
                    return response.data;
                });
            },
            findBySupplier: function (supplierId) {
                return $http.get("/api/bank/findBySupplier/" + supplierId).then(function (response) {
                    return response.data;
                });
            }
        };
    }]);