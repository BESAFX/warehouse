app.factory("SupplierService",
    ['$http', '$log', function ($http, $log) {
        return {
            findOne: function (id) {
                return $http.get("/api/supplier/findOne/" + id).then(function (response) {
                    return response.data;
                });
            },
            create: function (supplier, openCash) {
                return $http.post("/api/supplier/create/" + openCash, supplier).then(function (response) {
                    return response.data;
                });
            },
            remove: function (id) {
                return $http.delete("/api/supplier/delete/" + id);
            },
            update: function (supplier) {
                return $http.put("/api/supplier/update", supplier).then(function (response) {
                    return response.data;
                });
            },
            findSupplierBalance: function (id) {
                return $http.get("/api/supplier/findSupplierBalance/" + id).then(function (response) {
                    return response.data;
                });
            },
            findAllSupplierBalance: function () {
                return $http.get("/api/supplier/findAllSupplierBalance").then(function (response) {
                    return response.data;
                });
            },
            findAllCombo: function () {
                return $http.get("/api/supplier/findAllCombo").then(function (response) {
                    return response.data;
                });
            },
            filter: function (search) {
                return $http.get("/api/supplier/filter?" + search).then(function (response) {
                    return response.data;
                });
            }
        };
    }]);