app.factory("BillBuyTypeService", ['$http', '$log',
    function ($http, $log) {
        return {
            findAll: function () {
                return $http.get("/api/billBuyType/findAll").then(function (response) {
                    return response.data;
                })
            },
            findOne: function (id) {
                return $http.get("/api/billBuyType/findOne/" + id).then(function (response) {
                    return response.data;
                });
            },
            create: function (billBuyType) {
                return $http.post("/api/billBuyType/create", billBuyType).then(function (response) {
                    return response.data;
                });
            },
            remove: function (id) {
                return $http.delete("/api/billBuyType/delete/" + id);
            },
            update: function (billBuyType) {
                return $http.put("/api/billBuyType/update", billBuyType).then(function (response) {
                    return response.data;
                });
            }
        };
    }]);