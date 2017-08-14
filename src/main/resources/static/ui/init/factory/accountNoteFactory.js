app.factory("AccountNoteService",
    ['$http', '$log', function ($http, $log) {
        return {
            findAll: function () {
                return $http.get("/api/accountNote/findAll").then(function (response) {
                    return response.data;
                });
            },
            findOne: function (id) {
                return $http.get("/api/accountNote/findOne/" + id).then(function (response) {
                    return response.data;
                });
            },
            findByAccount: function (account) {
                return $http.get("/api/accountNote/findByAccount/" + account.id).then(function (response) {
                    return response.data;
                });
            },
            create: function (accountNote) {
                return $http.post("/api/accountNote/create", accountNote).then(function (response) {
                    return response.data;
                });
            }
        };
    }]);