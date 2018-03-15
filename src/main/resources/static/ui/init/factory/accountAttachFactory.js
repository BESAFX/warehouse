app.factory("AccountAttachService",
    ['$http', '$log', function ($http, $log) {
        return {
            upload: function (account, files) {
                return $http.post("/api/accountAttach/upload",
                    {account: JSON.stringify(account), files: files},
                    {
                        transformRequest: function () {
                            var formData = new FormData();
                            formData.append("account", JSON.stringify(account));
                            angular.forEach(files, function (file) {
                                formData.append('files', file);
                            });
                            return formData;
                        },
                        headers: {
                            'Content-Type': undefined
                        }
                    }
                ).then(function (response) {
                    return response.data;
                });

            },
            setType: function (accountAttach, attachType) {
                return $http.get("/api/accountAttach/setType/" + accountAttach.id + "/" + attachType.id).then(function (response) {
                    return response.data;
                });
            },
            setName: function (accountAttach, name) {
                return $http.get("/api/accountAttach/setName/" + accountAttach.id + "/" + name).then(function (response) {
                    return response.data;
                });
            },
            remove: function (accountAttach) {
                return $http.delete("/api/accountAttach/delete/" + accountAttach.id).then(function (response) {
                    return response.data;
                });
            },
            removeWhatever: function (accountAttach) {
                return $http.delete("/api/accountAttach/deleteWhatever/" + accountAttach.id);
            },
            findByAccount: function (account) {
                return $http.get("/api/accountAttach/findByAccount/" + account.id).then(function (response) {
                    return response.data;
                });
            }
        };
    }]);