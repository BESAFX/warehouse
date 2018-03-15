app.controller('accountDetailsCtrl', [
    'AccountConditionService',
    'AccountNoteService',
    'AccountService',
    'OfferService',
    'PaymentService',
    'AccountAttachService',
    'ModalProvider',
    '$scope',
    '$rootScope',
    '$timeout',
    '$uibModalInstance',
    '$uibModal',
    'account',
    'attachTypes',
    function (AccountConditionService,
              AccountNoteService,
              AccountService,
              OfferService,
              PaymentService,
              AccountAttachService,
              ModalProvider,
              $scope,
              $rootScope,
              $timeout,
              $uibModalInstance,
              $uibModal,
              account,
              attachTypes) {

        $scope.account = account;
        $scope.attachTypes = attachTypes;
        $scope.refreshAccount = function () {
            AccountService.findOne($scope.account.id).then(function (data) {
                $scope.account = data;
            })
        };
        $scope.refreshOffers = function () {
            OfferService.findByCustomerMobile(account.student.contact.mobile).then(function (data) {
                $scope.offers = data;
            });
        };
        $scope.refreshPayments = function () {
            PaymentService.findByAccount($scope.account.id).then(function (data) {
                $scope.account.payments = data;
            });
        };
        $scope.refreshAttaches = function () {
            AccountAttachService.findByAccount($scope.account).then(function (data) {
                $scope.account.accountAttaches = data;
            })
        };
        $scope.refreshConditions = function () {
            AccountConditionService.findByAccount($scope.account).then(function (data) {
                $scope.account.accountConditions = data;
            });
        };
        $scope.refreshNotes = function () {
            AccountNoteService.findByAccount($scope.account).then(function (data) {
                $scope.account.accountNotes = data;
            });
        };

        $scope.updateAccount = function () {
            ModalProvider.openAccountUpdateModel($scope.account).result.then(function (data) {
                return $scope.account = data;
            });
        };
        $scope.newNote = function () {
            ModalProvider.openAccountNoteCreateModel($scope.account).result.then(function (data) {
                return $scope.account.accountNotes.push(data);
            });
        };
        $scope.newCondition = function () {
            ModalProvider.openAccountConditionCreateModel($scope.account).result.then(function (data) {
                return $scope.account.accountConditions.push(data);
            });
        };
        $scope.newPayment = function () {
            ModalProvider.openAccountPaymentModel($scope.account).result.then(function (data) {
                return $scope.account.payments.push(data);
            });
        };
        $scope.setAccountAttachType = function (accountAttach) {
            AccountAttachService.setType(accountAttach, accountAttach.attachType);
        };
        $scope.deleteAttach = function (accountAttach) {
            $rootScope.showConfirmNotify("حذف البيانات", "هل تود حذف المستند فعلاً؟", "error", "fa-ban", function () {
                AccountAttachService.remove(accountAttach).then(function (data) {
                    if (data) {
                        var index = $scope.account.accountAttaches.indexOf(accountAttach);
                        $scope.account.accountAttaches.splice(index, 1);
                    } else {
                        $rootScope.showConfirmNotify("حذف البيانات", "هل تود حذف المستند نهائياً؟", "error", "fa-ban", function () {
                            AccountAttachService.removeWhatever(accountAttach);
                        });
                    }
                });
            });
        };

        $scope.files = [];
        $scope.uploadFiles = function () {
            document.getElementById('uploader').click();
        };
        $scope.$watch('files', function (newVal, oldVal) {
            if ($scope.files.length > 0) {
                AccountAttachService.upload($scope.account, newVal).then(function (data) {
                    return Array.prototype.push.apply($scope.account.accountAttaches, data);
                });
            }
        });

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 600);

    }]);