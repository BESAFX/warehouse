app.factory("CallService",
    ['$http', '$log', function ($http, $log) {
        return {
            findAll: function () {
                return $http.get("/api/call/findAll").then(function (response) {
                    return response.data;
                });
            },
            findOne: function (id) {
                return $http.get("/api/call/findOne/" + id).then(function (response) {
                    return response.data;
                });
            },
            findByOffer: function (offerId) {
                return $http.get("/api/call/findByOffer/" + offerId).then(function (response) {
                    return response.data;
                });
            },
            create: function (call) {
                return $http.post("/api/call/create", call).then(function (response) {
                    return response.data;
                });
            }
        };
    }]);