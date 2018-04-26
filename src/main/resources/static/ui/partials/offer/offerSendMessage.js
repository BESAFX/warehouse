app.controller('offerSendMessageCtrl', ['AccountService', '$scope', '$rootScope', '$timeout', '$uibModalInstance', 'offers',
    function (AccountService, $scope, $rootScope, $timeout, $uibModalInstance, offers) {

        $scope.offers = offers;

        $scope.buffer = {};

        $scope.submit = function () {
            var mobiles = [];
            angular.forEach($scope.offers, function (offer) {
                mobiles.push(offer.customerMobile);
            });
            AccountService.sendMessage($scope.buffer.message, mobiles).then(function () {
                $uibModalInstance.dismiss('cancel');
            });
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 800);

    }]);