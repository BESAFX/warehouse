app.factory("MasterCategoryService",
    ['$http', '$log', function ($http, $log) {
        return {
            findAll: function () {
                return $http.get("/api/masterCategory/findAll").then(function (response) {
                    return response.data;
                });
            },
            findAllCombo: function () {
                return $http.get("/api/masterCategory/findAllCombo").then(function (response) {
                    return response.data;
                });
            },
            findOne: function (id) {
                return $http.get("/api/masterCategory/findOne/" + id).then(function (response) {
                    return response.data;
                });
            },
            create: function (masterCategory) {
                return $http.post("/api/masterCategory/create", masterCategory).then(function (response) {
                    return response.data;
                });
            },
            remove: function (id) {
                return $http.delete("/api/masterCategory/delete/" + id);
            },
            update: function (masterCategory) {
                return $http.put("/api/masterCategory/update", masterCategory).then(function (response) {
                    return response.data;
                });
            }
        };
    }]);