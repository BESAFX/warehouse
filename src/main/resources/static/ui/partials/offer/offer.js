app.controller("offerCtrl", ['OfferService', 'BranchService', 'MasterService', 'ModalProvider', '$rootScope', '$scope', '$window', '$timeout', '$log', '$state',
    function (OfferService, BranchService, MasterService, ModalProvider, $rootScope, $scope, $window, $timeout, $log, $state) {

        $scope.buffer = {};

        $scope.selected = {};

        $scope.buffer.registered = false;

        //
        $scope.items = [];
        $scope.items.push({'id': 1, 'type': 'link', 'name': 'البرامج', 'link': 'menu'});
        $scope.items.push({'id': 2, 'type': 'title', 'name': 'العروض'});
        //

        $timeout(function () {
            $scope.sideOpacity = 1;
            BranchService.fetchTableData().then(function (data) {
                $scope.branches = data;
                $scope.buffer.branch = $scope.branches[0];
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
            var search = [];
            if ($scope.buffer.codeFrom) {
                search.push('codeFrom=');
                search.push($scope.buffer.codeFrom);
                search.push('&');
            }
            if ($scope.buffer.codeTo) {
                search.push('codeTo=');
                search.push($scope.buffer.codeTo);
                search.push('&');
            }
            if ($scope.buffer.dateFrom) {
                search.push('dateFrom=');
                search.push(moment($scope.buffer.dateFrom).valueOf());
                search.push('&');
            }
            if ($scope.buffer.dateTo) {
                search.push('dateTo=');
                search.push(moment($scope.buffer.dateTo).valueOf());
                search.push('&');
            }
            if ($scope.buffer.customerName) {
                search.push('customerName=');
                search.push($scope.buffer.customerName);
                search.push('&');
            }
            if ($scope.buffer.customerIdentityNumber) {
                search.push('customerIdentityNumber=');
                search.push($scope.buffer.customerIdentityNumber);
                search.push('&');
            }
            if ($scope.buffer.customerMobile) {
                search.push('customerMobile=');
                search.push($scope.buffer.customerMobile);
                search.push('&');
            }
            if ($scope.buffer.masterPriceFrom) {
                search.push('masterPriceFrom=');
                search.push($scope.buffer.masterPriceFrom);
                search.push('&');
            }
            if ($scope.buffer.masterPriceTo) {
                search.push('masterPriceTo=');
                search.push($scope.buffer.masterPriceTo);
                search.push('&');
            }
            if ($scope.buffer.branch) {
                search.push('branch=');
                search.push($scope.buffer.branch.id);
                search.push('&');
            }
            if ($scope.buffer.master) {
                search.push('master=');
                search.push($scope.buffer.master.id);
                search.push('&');
            }

            search.push('registered=');
            search.push($scope.buffer.registered);
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
                    'name': ' [ ' + $scope.buffer.branch.code + ' ] ' + $scope.buffer.branch.name
                });
                if ($scope.buffer.master) {
                    $scope.items.push({'id': 5, 'type': 'title', 'name': 'تخصص', 'style': 'font-weight:bold'});
                    $scope.items.push({
                        'id': 6,
                        'type': 'title',
                        'name': ' [ ' + $scope.buffer.master.code + ' ] ' + $scope.buffer.master.name
                    });
                }
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

                    });
                });
                return;
            }
            $rootScope.showConfirmNotify("حذف البيانات", "هل تود حذف العرض فعلاً؟", "error", "fa-ban", function () {
                OfferService.remove($scope.selected.id).then(function () {

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
            }
        ];

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 1500);

    }]);
