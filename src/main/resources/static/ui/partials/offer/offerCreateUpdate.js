app.controller('offerCreateUpdateCtrl', ['OfferService', 'MasterService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'title', 'action', 'offer',
    function (OfferService, MasterService, $scope, $rootScope, $timeout, $log, $uibModalInstance, title, action, offer) {

        $scope.title = title;

        $scope.action = action;

        $scope.groupByBranch = function (item) {
            return item.branch.name;
        };

        $scope.clear = function () {
            $scope.offer = {};
            $scope.offer.registered = false;
            $scope.offer.masterPrice = 0.0;
            $scope.offer.masterDiscountAmount = 0.0;
            $scope.offer.masterProfitAmount = 0.0;
            $scope.offer.masterCreditAmount = 0.0;
            $scope.offer.masterPaymentType = 'نقدي';
        };

        if (offer) {
            $scope.offer = offer;
        } else {
            $scope.clear();
        }

        $scope.submit = function () {
            switch ($scope.action) {
                case 'create' :
                    OfferService.create($scope.offer).then(function (data) {
                        $uibModalInstance.close(data);
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

        $timeout(function () {
            MasterService.fetchMasterBranchCombo().then(function (data) {
                $scope.masters = data;
            });
            window.componentHandler.upgradeAllRegistered();
        }, 800);

    }]);