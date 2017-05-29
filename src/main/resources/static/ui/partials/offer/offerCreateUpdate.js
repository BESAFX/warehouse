app.controller('offerCreateUpdateCtrl', ['OfferService', 'MasterService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'title', 'action', 'offer',
    function (OfferService, MasterService, $scope, $rootScope, $timeout, $log, $uibModalInstance, title, action, offer) {

        $scope.title = title;

        $scope.action = action;

        $timeout(function () {
            MasterService.fetchTableDataSummery().then(function (data) {
                $scope.masters = data;
            })
        }, 2000);

        $scope.clear = function () {
            $scope.offer = {};
            $scope.offer.registered = false;
            $scope.offer.masterPrice = 0.0;
            $scope.offer.masterDiscountAmount = 0.0;
            $scope.offer.masterProfitAmount = 0.0;
            $scope.offer.masterCreditAmount = 0.0;
            $scope.showBox = true;
            if ($scope.form) {
                $scope.form.$setPristine()
            }
        };

        if (offer) {
            $scope.offer = offer;
            $scope.showBox = (offer.masterPaymentType == 'نقدي');
        } else {
            $scope.clear();
        }

        $scope.submit = function () {
            if ($scope.showBox) {
                $scope.offer.masterPaymentType = 'نقدي';
                $scope.offer.masterProfitAmount = 0.0;
                $scope.offer.masterCreditAmount = 0.0;
            } else {
                $scope.offer.masterPaymentType = 'قسط شهري';
                $scope.offer.masterDiscountAmount = 0.0;
            }
            switch ($scope.action) {
                case 'create' :
                    OfferService.create($scope.offer).then(function (data) {
                        $scope.clear();
                        $scope.form.$setPristine();
                    });
                    break;
                case 'update' :
                    OfferService.update($scope.offer).then(function (data) {
                        $scope.offer = data;
                    });
                    break;
            }
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

    }]);