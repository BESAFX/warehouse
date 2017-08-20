app.factory("BranchService",
    ['$http', '$log', function ($http, $log) {
        return {
            findAll: function () {
                return $http.get("/api/branch/findAll").then(function (response) {
                    return response.data;
                })
            },
            findOne: function (id) {
                return $http.get("/api/branch/findOne/" + id).then(function (response) {
                    return response.data;
                });
            },
            create: function (branch) {
                return $http.post("/api/branch/create", branch).then(function (response) {
                    return response.data;
                });
            },
            remove: function (id) {
                return $http.delete("/api/branch/delete/" + id);
            },
            update: function (branch) {
                return $http.put("/api/branch/update", branch).then(function (response) {
                    return response.data;
                });
            },
            findMaxId: function () {
                return $http.get("/api/branch/findMaxId").then(function (response) {
                    return response.data;
                });
            },
            findNextCode: function () {
                return $http.get("/api/branch/findNextCode").then(function (response) {
                    return response.data;
                });
            },
            count: function () {
                return $http.get("/api/branch/count").then(function (response) {
                    return response.data;
                });
            },
            findLast: function () {
                return $http.get("/api/branch/findLast").then(function (response) {
                    return response.data;
                });
            },
            findByName: function (name) {
                return $http.get("/api/branch/findByName/" + name).then(function (response) {
                    return response.data;
                });
            },
            findByCode: function (code) {
                return $http.get("/api/branch/findByCode/" + code).then(function (response) {
                    return response.data;
                });
            },
            fetchTableData: function () {
                return $http.get("/api/branch/fetchTableData").then(function (response) {
                    return response.data;
                });
            },
            fetchTableDataSummery: function () {
                return $http.get("/api/branch/fetchTableDataSummery").then(function (response) {
                    return response.data;
                });
            },
            fetchBranchCombo: function () {
                return $http.get("/api/branch/fetchBranchCombo").then(function (response) {
                    return response.data;
                });
            },
            fetchBranchMaster: function () {
                return $http.get("/api/branch/fetchBranchMaster").then(function (response) {
                    return response.data;
                });
            },
            fetchBranchMasterCourse: function () {
                return $http.get("/api/branch/fetchBranchMasterCourse").then(function (response) {
                    return response.data;
                });
            }
        };
    }]);