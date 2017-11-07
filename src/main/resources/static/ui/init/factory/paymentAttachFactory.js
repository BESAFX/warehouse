app.factory("PaymentAttachService",
    ['$http', '$log', function ($http, $log) {
        return {
            upload: function (payment, file) {
                var fd = new FormData();
                fd.append('file', file);
                return $http.post("/api/paymentAttach/upload/" + payment.id,
                    fd, {transformRequest: angular.identity, headers: {'Content-Type': undefined}}).then(function (response) {
                    return response.data;
                });
            }
        };
    }]);