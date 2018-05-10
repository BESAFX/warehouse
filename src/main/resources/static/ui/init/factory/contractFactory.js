app.factory("ContractService",
    ['$http', '$log', function ($http, $log) {
        return {
            findOne: function (id) {
                return $http.get("/api/contract/findOne/" + id).then(function (response) {
                    return response.data;
                });
            },
            create: function (contract) {
                return $http.post("/api/contract/create", contract).then(function (response) {
                    return response.data;
                });
            },
            remove: function (id) {
                return $http.delete("/api/contract/delete/" + id);
            },
            filter: function (search) {
                return $http.get("/api/contract/filter?" + search).then(function (response) {
                    return response.data;
                });
            }
        };
    }]);