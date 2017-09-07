app.factory("CourseService",
    ['$http', '$log', function ($http, $log) {
        return {
            findAll: function () {
                return $http.get("/api/course/findAll").then(function (response) {
                    return response.data;
                });
            },
            findOne: function (id) {
                return $http.get("/api/course/findOne/" + id).then(function (response) {
                    return response.data;
                });
            },
            create: function (course) {
                return $http.post("/api/course/create", course).then(function (response) {
                    return response.data;
                });
            },
            remove: function (id) {
                return $http.delete("/api/course/delete/" + id).then(function (response) {
                    return response.data;
                });
            },
            update: function (course) {
                return $http.put("/api/course/update", course).then(function (response) {
                    return response.data;
                });
            },
            count: function () {
                return $http.get("/api/course/count").then(function (response) {
                    return response.data;
                });
            },
            findByBranch: function (branchId) {
                return $http.get("/api/course/findByBranch/" + branchId).then(function (response) {
                    return response.data;
                });
            },
            findByMaster: function (masterId) {
                return $http.get("/api/course/findByMaster/" + masterId).then(function (response) {
                    return response.data;
                });
            },
            fetchTableData: function () {
                return $http.get("/api/course/fetchTableData").then(function (response) {
                    return response.data;
                });
            }
        };
    }]);