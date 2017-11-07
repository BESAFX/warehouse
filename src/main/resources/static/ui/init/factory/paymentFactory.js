app.factory("PaymentService",
    ['$http', '$log', function ($http, $log) {
        return {
            findAll: function () {
                return $http.get("/api/payment/findAll").then(function (response) {
                    return response.data;
                });
            },
            findOne: function (id) {
                return $http.get("/api/payment/findOne/" + id).then(function (response) {
                    return response.data;
                });
            },
            create: function (payment) {
                return $http.post("/api/payment/create", payment).then(function (response) {
                    return response.data;
                });
            },
            createWrapper: function (wrapper) {
                return $http.post("/api/payment/createWrapper", wrapper).then(function (response) {
                    return response.data;
                });
            },
            remove: function (id) {
                return $http.delete("/api/payment/delete/" + id).then(function (response) {
                    return response.data;
                });
            },
            removeByCourse: function (courseId) {
                return $http.delete("/api/payment/deleteByCourse/" + courseId).then(function (response) {
                    return response.data;
                });
            },
            update: function (payment) {
                return $http.put("/api/payment/update", payment).then(function (response) {
                    return response.data;
                });
            },
            findByAccount: function (accountId) {
                return $http.get("/api/payment/findByAccount/" + accountId).then(function (response) {
                    return response.data;
                });
            },
            filter: function (search) {
                return $http.get("/api/payment/filter?" + search).then(function (response) {
                    return response.data;
                });
            }
        };
    }]);