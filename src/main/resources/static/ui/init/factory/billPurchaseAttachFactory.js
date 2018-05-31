app.factory("BillPurchaseAttachService",
    ['$http', '$log', function ($http, $log) {
        return {
            upload: function (billPurchase, files) {
                return $http.post("/api/billPurchaseAttach/upload",
                    {billPurchaseId: billPurchase.id, files: files},
                    {
                        transformRequest: function () {
                            var formData = new FormData();
                            formData.append("billPurchaseId", billPurchase.id);
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
            remove: function (billPurchaseAttach) {
                return $http.delete("/api/billPurchaseAttach/delete/" + billPurchaseAttach.id).then(function (response) {
                    return response.data;
                });
            },
            removeWhatever: function (billPurchaseAttach) {
                return $http.delete("/api/billPurchaseAttach/deleteWhatever/" + billPurchaseAttach.id);
            },
            findByBillPurchase: function (billPurchase) {
                return $http.get("/api/billPurchaseAttach/findByBillPurchase/" + billPurchase.id).then(function (response) {
                    return response.data;
                });
            }
        };
    }]);