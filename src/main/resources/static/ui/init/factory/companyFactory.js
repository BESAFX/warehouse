app.factory("CompanyService",
    ['$http', '$log', function ($http, $log) {
        return {
            findAll: function () {
                return $http.get("/api/company/findAll").then(function (response) {
                    return response.data;
                });
            },
            findAllCombo: function () {
                return $http.get("/api/company/findAllCombo").then(function (response) {
                    return response.data;
                });
            },
            update: function (company) {
                return $http.put("/api/company/update", company).then(function (response) {
                    return response.data;
                });
            }
        };
    }]);