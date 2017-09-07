app.factory("PaymentOutService",
    ['$http', '$log', function ($http, $log) {
        return {
            findAll: function () {
                return $http.get("/api/paymentOut/findAll").then(function (response) {
                    return response.data;
                });
            },
            findOne: function (id) {
                return $http.get("/api/paymentOut/findOne/" + id).then(function (response) {
                    return response.data;
                });
            },
            create: function (paymentOut) {
                return $http.post("/api/paymentOut/create", paymentOut).then(function (response) {
                    return response.data;
                });
            },
            remove: function (id) {
                return $http.delete("/api/paymentOut/delete/" + id).then(function (response) {
                    return response.data;
                });
            },
            update: function (paymentOut) {
                return $http.put("/api/paymentOut/update", paymentOut).then(function (response) {
                    return response.data;
                });
            },
            filter: function (search) {
                return $http.get("/api/paymentOut/filter?" + search).then(function (response) {
                    return response.data;
                });
            }
        };
    }]);