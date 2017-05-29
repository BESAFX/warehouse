app.factory("DepositService",
    ['$http', '$log', function ($http, $log) {
        return {
            findAll: function () {
                return $http.get("/api/deposit/findAll").then(function (response) {
                    return response.data;
                })
            },
            findOne: function (id) {
                return $http.get("/api/deposit/findOne/" + id).then(function (response) {
                    return response.data;
                });
            },
            create: function (deposit) {
                return $http.post("/api/deposit/create", deposit).then(function (response) {
                    return response.data;
                });
            },
            remove: function (id) {
                return $http.delete("/api/deposit/delete/" + id);
            },
            update: function (deposit) {
                return $http.put("/api/deposit/update", deposit).then(function (response) {
                    return response.data;
                });
            },
            filter: function (search) {
                return $http.get("/api/deposit/filter?" + search).then(function (response) {
                    return response.data;
                });
            }
        };
    }]);