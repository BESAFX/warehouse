app.factory("AccountConditionService",
    ['$http', '$log', function ($http, $log) {
        return {
            findAll: function () {
                return $http.get("/api/accountCondition/findAll").then(function (response) {
                    return response.data;
                });
            },
            findOne: function (id) {
                return $http.get("/api/accountCondition/findOne/" + id).then(function (response) {
                    return response.data;
                });
            },
            findByAccount: function (account) {
                return $http.get("/api/accountCondition/findByAccount/" + account.id).then(function (response) {
                    return response.data;
                });
            },
            create: function (accountCondition) {
                return $http.post("/api/accountCondition/create", accountCondition).then(function (response) {
                    return response.data;
                });
            }
        };
    }]);