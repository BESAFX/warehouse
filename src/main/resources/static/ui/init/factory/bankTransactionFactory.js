app.factory("BankTransactionService",
    ['$http', '$log', function ($http, $log) {
        return {
            createDeposit: function (bankTransaction) {
                return $http.post("/api/bankTransaction/createDeposit", bankTransaction).then(function (response) {
                    return response.data;
                });
            },
            createWithdraw: function (bankTransaction) {
                return $http.post("/api/bankTransaction/createWithdraw", bankTransaction).then(function (response) {
                    return response.data;
                });
            },
            createTransfer: function (amount, fromSellerId, toSellerId, note) {
                return $http
                    .get("/api/bankTransaction/createTransfer/" + amount + "/" + fromSellerId + "/" + toSellerId + "/" + note)
                    .then(function (response) {
                    return response.data;
                });
            },
            createWithdrawCash: function (amount, note) {
                return $http.get("/api/bankTransaction/createWithdrawCash/" + amount + "/" + note)
                    .then(function (response) {
                    return response.data;
                });
            },
            findOne: function (id) {
                return $http.get("/api/bankTransaction/findOne/" + id).then(function (response) {
                    return response.data;
                });
            },
            filter: function (search) {
                return $http.get("/api/bankTransaction/filter?" + search).then(function (response) {
                    return response.data;
                });
            }
        };
    }]);