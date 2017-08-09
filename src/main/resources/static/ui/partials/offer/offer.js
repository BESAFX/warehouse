app.controller("offerCtrl", ['OfferService', 'BranchService', 'PersonService',  'MasterService', 'ModalProvider', '$rootScope', '$scope', '$window', '$timeout', '$log', '$state', '$uibModal',
    function (OfferService, BranchService, PersonService, MasterService, ModalProvider, $rootScope, $scope, $window, $timeout, $log, $state, $uibModal) {

        $scope.buffer = {};

        $scope.selected = {};

        $scope.buffer.registered = false;

        //
        $scope.items = [];
        $scope.items.push({'id': 1, 'type': 'link', 'name': 'البرامج', 'link': 'menu'});
        $scope.items.push({'id': 2, 'type': 'title', 'name': 'العروض'});
        //

        $timeout(function () {
            PersonService.findAllSummery().then(function (data) {
                $scope.persons = data;
            });
            BranchService.fetchBranchMaster().then(function (data) {
                $scope.branches = data;
            });
        }, 2000);

        $scope.setSelected = function (object) {
            if (object) {
                angular.forEach($scope.offers, function (offer) {
                    if (object.id == offer.id) {
                        $scope.selected = offer;
                        return offer.isSelected = true;
                    } else {
                        return offer.isSelected = false;
                    }
                });
            }
        };

        $scope.filter = function () {
            var modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: '/ui/partials/offer/offerFilter.html',
                controller: 'offerFilterCtrl',
                scope: $scope,
                backdrop: 'static',
                keyboard: false,
                resolve: {
                    title: function () {
                        return 'البحث فى العروض';
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
                if (buffer.customerName) {
                    search.push('customerName=');
                    search.push(buffer.customerName);
                    search.push('&');
                }
                if (buffer.customerIdentityNumber) {
                    search.push('customerIdentityNumber=');
                    search.push(buffer.customerIdentityNumber);
                    search.push('&');
                }
                if (buffer.customerMobile) {
                    search.push('customerMobile=');
                    search.push(buffer.customerMobile);
                    search.push('&');
                }
                if (buffer.masterPriceFrom) {
                    search.push('masterPriceFrom=');
                    search.push(buffer.masterPriceFrom);
                    search.push('&');
                }
                if (buffer.masterPriceTo) {
                    search.push('masterPriceTo=');
                    search.push(buffer.masterPriceTo);
                    search.push('&');
                }
                if (buffer.branch) {
                    search.push('branch=');
                    search.push(buffer.branch.id);
                    search.push('&');
                }
                if (buffer.master) {
                    search.push('master=');
                    search.push(buffer.master.id);
                    search.push('&');
                }

                search.push('registered=');
                search.push(buffer.registered);
                search.push('&');

                OfferService.filter(search.join("")).then(function (data) {
                    $scope.offers = data;
                    $scope.setSelected(data[0]);
                    $scope.items = [];
                    $scope.items.push({'id': 1, 'type': 'link', 'name': 'البرامج', 'link': 'menu'});
                    $scope.items.push({'id': 2, 'type': 'title', 'name': 'العروض', 'style': 'font-weight:bold'});
                    $scope.items.push({'id': 3, 'type': 'title', 'name': 'فرع', 'style': 'font-weight:bold'});
                    $scope.items.push({
                        'id': 4,
                        'type': 'title',
                        'name': ' [ ' + buffer.branch.code + ' ] ' + buffer.branch.name
                    });
                    if (buffer.master) {
                        $scope.items.push({'id': 5, 'type': 'title', 'name': 'تخصص', 'style': 'font-weight:bold'});
                        $scope.items.push({
                            'id': 6,
                            'type': 'title',
                            'name': ' [ ' + buffer.master.code + ' ] ' + buffer.master.name
                        });
                    }
                });

            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        };

        $scope.clear = function () {
            $scope.buffer = {};
            $scope.buffer.branch = $scope.branches[0];
        };

        $scope.print = function (offer) {
            if (offer) {
                window.open('/report/OfferById/' + offer.id + '?exportType=PDF');
                return;
            }
            window.open('/report/OfferById/' + $scope.selected.id + '?exportType=PDF');
        };

        $scope.delete = function (offer) {
            if (offer) {
                $rootScope.showConfirmNotify("حذف البيانات", "هل تود حذف العرض فعلاً؟", "error", "fa-ban", function () {
                    OfferService.remove(offer.id).then(function () {
                        var index = $scope.offers.indexOf(offer);
                        $scope.offers.splice(index, 1);
                        $scope.setSelected($scope.offers[0]);
                    });
                });
                return;
            }
            $rootScope.showConfirmNotify("حذف البيانات", "هل تود حذف العرض فعلاً؟", "error", "fa-ban", function () {
                OfferService.remove($scope.selected.id).then(function () {
                    var index = $scope.offers.indexOf($scope.selected);
                    $scope.offers.splice(index, 1);
                    $scope.setSelected($scope.offers[0]);
                });
            });
        };

        $scope.rowMenu = [
            {
                html: '<div class="drop-menu"> انشاء عرض جديد <span class="fa fa-plus-square-o fa-lg"></span></div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    ModalProvider.openOfferCreateModel();
                }
            },
            {
                html: '<div class="drop-menu"> تعديل بيانات العرض <span class="fa fa-edit fa-lg"></span></div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    ModalProvider.openOfferUpdateModel($itemScope.offer);
                }
            },
            {
                html: '<div class="drop-menu"> حذف العرض <span class="fa fa-minus-square-o fa-lg"></span></div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.delete($itemScope.offer);
                }
            },
            {
                html: '<div class="drop-menu"> التفاصيل <span class="fa fa-info fa-lg"></span></div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    ModalProvider.openOfferDetailsModel($itemScope.offer);
                }
            }
        ];

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 1500);

    }]);
