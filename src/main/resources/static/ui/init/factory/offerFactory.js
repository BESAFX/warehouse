app.factory("OfferService",
    ['$http', '$log', function ($http, $log) {
        return {
            create: function (offer) {
                return $http.post("/api/offer/create", offer).then(function (response) {
                    return response.data;
                });
            },
            remove: function (id) {
                return $http.delete("/api/offer/delete/" + id).then(function (response) {
                    return response.data;
                });
            },
            update: function (offer) {
                return $http.put("/api/offer/update", offer).then(function (response) {
                    return response.data;
                });
            },
            findAll: function () {
                return $http.get("/api/offer/findAll").then(function (response) {
                    return response.data;
                });
            },
            findOne: function (id) {
                return $http.get("/api/offer/findOne/" + id).then(function (response) {
                    return response.data;
                });
            },
            findByBranch: function (branch) {
                return $http.get("/api/offer/findByBranch/" + branch.id).then(function (response) {
                    return response.data;
                });
            },
            findByMaster: function (master) {
                return $http.get("/api/offer/findByMaster/" + master.id).then(function (response) {
                    return response.data;
                });
            },
            count: function () {
                return $http.get("/api/offer/count").then(function (response) {
                    return response.data;
                });
            },
            fetchTableData: function () {
                return $http.get("/api/offer/fetchTableData").then(function (response) {
                    return response.data;
                });
            },
            fetchCount: function () {
                return $http.get("/api/offer/fetchCount").then(function (response) {
                    return response.data;
                });
            },
            filter: function (search) {
                return $http.get("/api/offer/filter?" + search).then(function (response) {
                    return response.data;
                });
            }
        };
    }]);