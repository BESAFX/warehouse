app.factory("BranchAccessService",
    ['$http', '$log', function ($http, $log) {
        return {
            findAll: function () {
                return $http.get("/api/branchAccess/findAll").then(function (response) {
                    return response.data;
                })
            },
            findOne: function (id) {
                return $http.get("/api/branchAccess/findOne/" + id).then(function (response) {
                    return response.data;
                });
            },
            findByPerson: function (id) {
                return $http.get("/api/branchAccess/findByPerson/" + id).then(function (response) {
                    return response.data;
                });
            },
            create: function (branchAccess) {
                return $http.post("/api/branchAccess/create", branchAccess).then(function (response) {
                    return response.data;
                });
            },
            remove: function (id) {
                return $http.delete("/api/branchAccess/delete/" + id);
            }
        };
    }]);