app.factory("PaymentBookService", ['$http', '$log',
    function ($http, $log) {
        return {
            findAll: function () {
                return $http.get("/api/paymentBook/findAll").then(function (response) {
                    return response.data;
                })
            },
            findOne: function (id) {
                return $http.get("/api/paymentBook/findOne/" + id).then(function (response) {
                    return response.data;
                });
            },
            create: function (paymentBook) {
                return $http.post("/api/paymentBook/create", paymentBook).then(function (response) {
                    return response.data;
                });
            },
            remove: function (id) {
                return $http.delete("/api/paymentBook/delete/" + id);
            },
            update: function (paymentBook) {
                return $http.put("/api/paymentBook/update", paymentBook).then(function (response) {
                    return response.data;
                });
            },
            findByBranch: function (branchId) {
                return $http.get("/api/paymentBook/findByBranch/" + branchId).then(function (response) {
                    return response.data;
                });
            },
            findByBranchCombo: function (branchId) {
                return $http.get("/api/paymentBook/findByBranchCombo/" + branchId).then(function (response) {
                    return response.data;
                });
            },
            fetchTableData: function () {
                return $http.get("/api/paymentBook/fetchTableData").then(function (response) {
                    return response.data;
                });
            },
            fetchTableDataCombo: function () {
                return $http.get("/api/paymentBook/fetchTableDataCombo").then(function (response) {
                    return response.data;
                });
            }
        };
    }
]);