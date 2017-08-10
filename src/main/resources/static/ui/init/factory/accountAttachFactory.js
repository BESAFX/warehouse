app.factory("AccountAttachService",
    ['$http', '$log', function ($http, $log) {
        return {
            upload: function (account, attachType, fileName, file) {
                var fd = new FormData();
                fd.append('file', file);
                return $http.post("/api/accountAttach/upload/" + account.id + "/" + attachType.id + "/" + fileName,
                    fd, {transformRequest: angular.identity, headers: {'Content-Type': undefined}}).then(function (response) {
                    return response.data;
                });
            },
            remove: function (accountAttach) {
                return $http.delete("/api/accountAttach/delete/" + accountAttach.id).then(function (response) {
                    return response.data;
                });
            },
            removeWhatever: function (attach) {
                return $http.delete("/api/accountAttach/deleteWhatever/" + attach.id);
            },
            findByAccount: function (account) {
                return $http.get("/api/accountAttach/findByAccount/" + account.id).then(function (response) {
                    return response.data;
                });
            }
        };
    }]);