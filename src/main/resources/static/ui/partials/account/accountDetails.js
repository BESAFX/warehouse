app.controller('accountDetailsCtrl', ['AccountConditionService', 'AccountNoteService', 'AccountService', 'OfferService', 'PaymentService', 'AccountAttachService', 'ModalProvider', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', '$uibModal', 'account',
    function (AccountConditionService, AccountNoteService, AccountService, OfferService, PaymentService, AccountAttachService, ModalProvider, $scope, $rootScope, $timeout, $log, $uibModalInstance, $uibModal, account) {

        $scope.account = account;

        $scope.refreshAccount = function () {
          AccountService.findOne($scope.account.id).then(function (data) {
              $scope.account = data;
          })
        };

        $scope.refreshOffersByAccountMobile = function () {
            OfferService.findByCustomerMobile(account.student.contact.mobile).then(function (data) {
                $scope.offers = data;
            });
        };

        $scope.refreshAccountPayments = function () {
            PaymentService.findByAccount($scope.account.id).then(function (data) {
                $scope.account.payments = data;
            });
        };

        $scope.refreshAccountAttaches = function () {
            AccountAttachService.findByAccount($scope.account).then(function (data) {
                $scope.account.accountAttaches = data;
            })
        };

        $scope.findConditionByAccount = function () {
            AccountConditionService.findByAccount($scope.account).then(function (data) {
                $scope.account.accountConditions = data;
            });
        };

        $scope.findNotesByAccount = function () {
            AccountNoteService.findByAccount($scope.account).then(function (data) {
                $scope.account.accountNotes = data;
            });
        };

        $scope.openAccountConditionCreate = function () {
            var modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: '/ui/partials/account/accountConditionCreate.html',
                controller: 'accountConditionCreateCtrl',
                scope: $scope,
                backdrop: 'static',
                keyboard: false,
                resolve: {
                    account: function () {
                        return $scope.account;
                    }
                }
            });

            modalInstance.result.then(function (condition) {
                $scope.account.accountConditions.push(condition);
            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        };

        $scope.openAccountNoteCreate = function () {
            var modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: '/ui/partials/account/accountNoteCreate.html',
                controller: 'accountNoteCreateCtrl',
                scope: $scope,
                backdrop: 'static',
                keyboard: false,
                resolve: {
                    account: function () {
                        return $scope.account;
                    }
                }
            });

            modalInstance.result.then(function (accountNote) {
                $scope.account.accountNotes.push(accountNote);
            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        };

        $scope.deleteFile = function (accountAttach) {
            $rootScope.showConfirmNotify("حذف البيانات", "هل تود حذف المستند فعلاً؟", "error", "fa-ban", function () {
                AccountAttachService.remove(accountAttach).then(function (data) {
                    if(data){
                        var index = $scope.account.accountAttaches.indexOf(accountAttach);
                        $scope.account.accountAttaches.splice(index, 1);
                    }else{
                        $rootScope.showConfirmNotify("حذف البيانات", "هل تود حذف المستند نهائياً؟", "error", "fa-ban", function () {
                            AccountAttachService.removeWhatever(accountAttach);
                        });
                    }
                });
            });
        };

        //////////////////////////File Manager///////////////////////////////////
        $scope.uploadFiles = function () {
            document.getElementById('uploader').click();
        };

        $scope.uploadAll = function (files) {

            var wrappers = [];

            angular.forEach(files, function (file) {
                var wrapper = {};
                wrapper.src = file;
                wrapper.name = file.name;
                wrappers.push(wrapper);
            });

            var modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: '/ui/partials/account/accountAttachUpload.html',
                controller: 'accountAttachUploadCtrl',
                scope: $scope,
                backdrop: 'static',
                keyboard: false,
                resolve: {
                    title: function () {
                        return 'رفع المستندات';
                    },
                    wrappers : function () {
                        return wrappers;
                    }
                }
            });

            modalInstance.result.then(function (wrappers) {
                angular.forEach(wrappers, function (wrapper) {
                    AccountAttachService.upload(account, wrapper.attachType, wrapper.name, wrapper.src).then(function () {
                        $scope.refreshAccountAttaches();
                    });
                });
            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });

        };
        //////////////////////////File Manager///////////////////////////////////

        //////////////////////////Scan Manager///////////////////////////////////
        $scope.scanToJpg = function() {
            scanner.scan(displayImagesOnPage,
                {
                    "output_settings" :
                        [
                            {
                                "type": "return-base64",
                                "format": "jpg"
                            }
                        ]
                }
            );
        };

        function dataURItoBlob(dataURI) {
            // convert base64/URLEncoded data component to raw binary data held in a string
            var byteString;
            if (dataURI.split(',')[0].indexOf('base64') >= 0)
                byteString = atob(dataURI.split(',')[1]);
            else
                byteString = unescape(dataURI.split(',')[1]);

            // separate out the mime component
            var mimeString = dataURI.split(',')[0].split(':')[1].split(';')[0];

            // write the bytes of the string to a typed array
            var ia = new Uint8Array(byteString.length);
            for (var i = 0; i < byteString.length; i++) {
                ia[i] = byteString.charCodeAt(i);
            }

            return new Blob([ia], {type:mimeString});
        }

        /** Processes the scan result */
        function displayImagesOnPage(successful, mesg, response) {
            var scannedImages = scanner.getScannedImages(response, true, false); // returns an array of ScannedImage

            var wrappers = [];

            for(var i = 0; (scannedImages instanceof Array) && i < scannedImages.length; i++) {
                var wrapper = {};
                wrapper.src = scannedImages[i].src;
                console.info(wrapper.src);
                wrappers.push(wrapper);
            }

            var modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: '/ui/partials/account/accountAttachUpload.html',
                controller: 'accountAttachUploadCtrl',
                scope: $scope,
                backdrop: 'static',
                keyboard: false,
                resolve: {
                    title: function () {
                        return 'رفع المستندات';
                    },
                    wrappers : function () {
                        return wrappers;
                    }
                }
            });

            modalInstance.result.then(function (wrappers) {
                angular.forEach(wrappers, function (wrapper) {
                    var blob = dataURItoBlob(wrapper.src);
                    var file = new File([blob], wrapper.name + '.jpg');
                    AccountAttachService.upload(account, wrapper.attachType, wrapper.name, file).then(function () {
                        $scope.refreshAccountAttaches();
                    });
                });
            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        }
        //////////////////////////Scan Manager///////////////////////////////////

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 1500);

    }]);