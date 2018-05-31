app.factory("BillPurchasePaymentService",
    ['$http', '$log', function ($http, $log) {
        return {
            findOne: function (id) {
                return $http.get("/api/billPurchasePayment/findOne/" + id).then(function (response) {
                    return response.data;
                });
            },
            create: function (billPurchasePayment) {
                return $http.post("/api/billPurchasePayment/create", billPurchasePayment).then(function (response) {
                    return response.data;
                });
            },
            remove: function (id) {
                return $http.delete("/api/billPurchasePayment/delete/" + id);
            },
            findByContract: function (id) {
                return $http.get("/api/billPurchasePayment/findByContract/" + id).then(function (response) {
                    return response.data;
                });
            },
            findByDateBetween: function (startDate, endDate) {
                return $http.get("/api/billPurchasePayment/findByDateBetween/" + startDate + "/" + endDate)
                    .then(function (response) {
                    return response.data;
                });
            },
            filter: function (search) {
                return $http.get("/api/billPurchasePayment/filter?" + search).then(function (response) {
                    return response.data;
                });
            }
        };
    }]);