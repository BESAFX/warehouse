app.factory("StudentService",
    ['$http', '$log', function ($http, $log) {
        return {
            findAll: function () {
                return $http.get("/api/student/findAll").then(function (response) {
                    return response.data;
                });
            },
            findUnRegistered: function (branchId, masterId, courseId) {
                return $http.get("/api/student/findUnRegistered/" + branchId + "/" + masterId + "/" + courseId).then(function (response) {
                    return response.data;
                });
            },
            findOne: function (id) {
                return $http.get("/api/student/findOne/" + id).then(function (response) {
                    return response.data;
                });
            },
            findByCode: function (code) {
                return $http.get("/api/student/findByCode?" + "code=" + code).then(function (response) {
                    return response.data;
                });
            },
            create: function (student) {
                return $http.post("/api/student/create", student).then(function (response) {
                    return response.data;
                });
            },
            remove: function (id) {
                return $http.delete("/api/student/delete/" + id).then(function (response) {
                    return response.data;
                });
            },
            update: function (student) {
                return $http.put("/api/student/update", student).then(function (response) {
                    return response.data;
                });
            }
        };
    }]);