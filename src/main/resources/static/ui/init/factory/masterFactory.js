app.factory("MasterService",
    ['$http', '$log', function ($http, $log) {
        return {
            create: function (master) {
                return $http.post("/api/master/create", master).then(function (response) {
                    return response.data;
                });
            },
            remove: function (id) {
                return $http.delete("/api/master/delete/" + id).then(function (response) {
                    return response.data;
                });
            },
            update: function (master) {
                return $http.put("/api/master/update", master).then(function (response) {
                    return response.data;
                });
            },
            findAll: function () {
                return $http.get("/api/master/findAll").then(function (response) {
                    return response.data;
                });
            },
            findOne: function (id) {
                return $http.get("/api/master/findOne/" + id).then(function (response) {
                    return response.data;
                });
            },
            findByBranch: function (branch) {
                return $http.get("/api/master/findByBranch?" + "branchId=" + branch.id).then(function (response) {
                    return response.data;
                });
            },
            count: function () {
                return $http.get("/api/master/count").then(function (response) {
                    return response.data;
                });
            },
            fetchTableData: function () {
                return $http.get("/api/master/fetchTableData").then(function (response) {
                    return response.data;
                });
            },
            fetchTableDataSummery: function () {
                return $http.get("/api/master/fetchTableDataSummery").then(function (response) {
                    return response.data;
                });
            },
            fetchComoBox: function () {
                return $http.get("/api/master/fetchComboBox").then(function (response) {
                    return response.data;
                });
            }
        };
    }]);