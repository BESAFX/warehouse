app.controller('accountNoteCreateCtrl', ['AccountNoteService', 'ModalProvider', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'account',
    function (AccountNoteService, ModalProvider, $scope, $rootScope, $timeout, $log, $uibModalInstance, account) {

        $scope.accountNote = {};
        $scope.accountNote.account = account;

        $scope.submit = function () {
            AccountNoteService.create($scope.accountNote).then(function (data) {
                $uibModalInstance.close(data);
            });
        };


        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 1500);

    }]);