app.factory("WithdrawService",
    ['$http', '$log', function ($http, $log) {
        return {
            findAll: function () {
                return $http.get("/api/withdraw/findAll").then(function (response) {
                    return response.data;
                })
            },
            findOne: function (id) {
                return $http.get("/api/withdraw/findOne/" + id).then(function (response) {
                    return response.data;
                });
            },
            create: function (withdraw) {
                return $http.post("/api/withdraw/create", withdraw).then(function (response) {
                    return response.data;
                });
            },
            remove: function (id) {
                return $http.delete("/api/withdraw/delete/" + id);
            },
            update: function (withdraw) {
                return $http.put("/api/withdraw/update", withdraw).then(function (response) {
                    return response.data;
                });
            },
            filter: function (search) {
                return $http.get("/api/withdraw/filter?" + search).then(function (response) {
                    return response.data;
                });
            }
        };
    }]);