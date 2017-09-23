app.controller("paymentOutCtrl", ['PaymentOutService', 'BranchService', 'ModalProvider', '$rootScope', '$scope', '$uibModal', '$timeout', '$log', '$state',
    function (PaymentOutService, BranchService, ModalProvider, $rootScope, $scope, $uibModal, $timeout, $log, $state) {

        $scope.paymentOuts = [];

        //
        $scope.items = [];
        $scope.items.push({'id': 1, 'type': 'link', 'name': 'البرامج', 'link': 'menu'});
        $scope.items.push({'id': 2, 'type': 'title', 'name': 'سندات الصرف'});
        //

        $scope.buffer = {};

        $scope.selected = {};

        $timeout(function () {
            BranchService.fetchBranchCombo().then(function (data) {
                $scope.branches = data;
                $scope.buffer.branch = $scope.branches[0];
            });
        }, 2000);

        $scope.setSelected = function (object) {
            if (object) {
                angular.forEach($scope.paymentOuts, function (paymentOut) {
                    if (object.id == paymentOut.id) {
                        $scope.selected = paymentOut;
                        return paymentOut.isSelected = true;
                    } else {
                        return paymentOut.isSelected = false;
                    }
                });
            }
        };

        $scope.newPaymentOut = function () {
            ModalProvider.openPaymentOutCreateModel().result.then(function (data) {
                $scope.paymentOuts.splice(0, 0, data);
            }, function () {
                console.info('PaymentOutCreateModel Closed.');
            });
        };

        $scope.delete = function (paymentOut) {
            if (paymentOut) {
                $rootScope.showConfirmNotify("حذف البيانات", "هل تود حذف السند فعلاً؟", "error", "fa-ban", function () {
                    PaymentOutService.remove(paymentOut.id).then(function () {
                        var index = $scope.paymentOuts.indexOf(paymentOut);
                        $scope.paymentOuts.splice(index, 1);
                        $scope.setSelected($scope.paymentOuts[0]);
                    });
                });
                return;
            }
            $rootScope.showConfirmNotify("حذف البيانات", "هل تود حذف السند فعلاً؟", "error", "fa-ban", function () {
                PaymentOutService.remove($scope.selected.id).then(function () {
                    var index = $scope.paymentOuts.indexOf($scope.selected);
                    $scope.paymentOuts.splice(index, 1);
                    $scope.setSelected($scope.paymentOuts[0]);
                });
            });
        };

        $scope.filter = function () {
            var modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: '/ui/partials/paymentOut/paymentOutFilter.html',
                controller: 'paymentOutFilterCtrl',
                scope: $scope,
                backdrop: 'static',
                keyboard: false,
                resolve: {
                    title: function () {
                        return 'البحث فى سندات الصرف';
                    }
                }
            });

            modalInstance.result.then(function (buffer) {

                var search = [];
                if (buffer.codeFrom) {
                    search.push('codeFrom=');
                    search.push(buffer.codeFrom);
                    search.push('&');
                }
                if (buffer.codeTo) {
                    search.push('codeTo=');
                    search.push(buffer.codeTo);
                    search.push('&');
                }
                if (buffer.dateFrom) {
                    search.push('dateFrom=');
                    search.push(moment(buffer.dateFrom).valueOf());
                    search.push('&');
                }
                if (buffer.dateTo) {
                    search.push('dateTo=');
                    search.push(moment(buffer.dateTo).valueOf());
                    search.push('&');
                }
                if (buffer.amountFrom) {
                    search.push('amountFrom=');
                    search.push(buffer.amountFrom);
                    search.push('&');
                }
                if (buffer.amountTo) {
                    search.push('amountTo=');
                    search.push(buffer.amountTo);
                    search.push('&');
                }
                if (buffer.branch) {
                    search.push('branchId=');
                    search.push(buffer.branch.id);
                    search.push('&');
                }

                PaymentOutService.filter(search.join("")).then(function (data) {
                    $scope.paymentOuts = data;
                    $scope.setSelected(data[0]);
                    $scope.items = [];
                    $scope.items.push({'id': 1, 'type': 'link', 'name': 'البرامج', 'link': 'menu'});
                    $scope.items.push({'id': 2, 'type': 'title', 'name': 'السندات', 'style': 'font-weight:bold'});
                    $scope.items.push({'id': 3, 'type': 'title', 'name': 'فرع', 'style': 'font-weight:bold'});
                    $scope.items.push({
                        'id': 4,
                        'type': 'title',
                        'name': ' [ ' + buffer.branch.code + ' ] ' + buffer.branch.name
                    });
                });

            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        };

        $scope.rowMenu = [
            {
                html: '<div class="drop-menu">انشاء سند جديد<span class="fa fa-pencil fa-lg"></span></div>',
                enabled: function () {
                    return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_PAYMENT_OUT_CREATE']);
                },
                click: function ($itemScope, $event, value) {
                    $scope.newPaymentOut();
                }
            },
            {
                html: '<div class="drop-menu">حذف السند<span class="fa fa-trash fa-lg"></span></div>',
                enabled: function () {
                    return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_PAYMENT_OUT_DELETE']);
                },
                click: function ($itemScope, $event, value) {
                    $scope.delete($itemScope.paymentOut);
                }
            }
        ];

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 1500);

    }]);